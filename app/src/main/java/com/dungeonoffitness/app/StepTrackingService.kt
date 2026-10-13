package com.dungeonoffitness.app

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import java.util.UUID

/** Les données capteur sont isolées de la sauvegarde RPG pour éviter deux rédacteurs du même JSON. */
internal data class StepSnapshot(
    val id: String = "", val status: String = "idle", val steps: Int = 0,
    val cap: Int = 0, val ready: Boolean = false, val error: String = ""
) {
    val active: Boolean get() = status == "starting" || status == "running" || status == "paused"
}

internal object StepSessionStore {
    private const val PREF_NAME = "dof_steps_v2"
    private fun prefs(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun read(context: Context): StepSnapshot = prefs(context).let { p ->
        val id = p.getString("id", "").orEmpty()
        if (id.isBlank()) StepSnapshot() else StepSnapshot(id, p.getString("status", "idle") ?: "idle",
            p.getInt("steps", 0), p.getInt("cap", 0), p.getBoolean("ready", false),
            p.getString("error", "") ?: "")
    }

    fun begin(context: Context, cap: Int): StepSnapshot {
        val id = UUID.randomUUID().toString()
        val p = prefs(context)
        // Écriture synchrone AVANT de démarrer le service : recovery après fermeture de l'application.
        check(p.edit().clear().putString("id", id).putString("status", "starting")
            .putInt("steps", 0).putInt("cap", cap).putBoolean("ready", false).commit())
        return read(context)
    }

    fun status(context: Context, status: String, ready: Boolean = false, error: String = "") {
        prefs(context).edit().putString("status", status).putBoolean("ready", ready)
            .putString("error", error).apply()
    }

    fun steps(context: Context, total: Int, ready: Boolean) {
        prefs(context).edit().putInt("steps", total).putBoolean("ready", ready).apply()
    }

    fun clear(context: Context) { prefs(context).edit().clear().commit() }

    fun hasSensor(context: Context): Boolean = (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager).let {
        it.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null || it.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null
    }
    fun hasPermission(context: Context): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
        context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
}

/** Foreground service de type HEALTH : lecture capteur et sauvegarde des pas même écran éteint. */
class StepTrackingService : Service(), SensorEventListener {
    companion object {
        const val ACTION_START = "com.dungeonoffitness.steps.START"
        const val ACTION_PAUSE = "com.dungeonoffitness.steps.PAUSE"
        const val ACTION_RESUME = "com.dungeonoffitness.steps.RESUME"
        const val ACTION_STOP = "com.dungeonoffitness.steps.STOP"
        private const val CHANNEL_ID = "dof_step_tracking_v2"
        private const val NOTIFICATION_ID = 2600

        fun dispatch(context: Context, action: String) {
            val intent = Intent(context, StepTrackingService::class.java).setAction(action)
            if (action == ACTION_START || action == ACTION_RESUME) context.startForegroundService(intent)
            else context.startService(intent)
        }
    }

    private lateinit var manager: SensorManager
    private var selectedSensor: Sensor? = null
    private var lastRaw: Float? = null
    private var listening = false
    private var lastNotified = -1

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Marche · Dungeon of Fitness", NotificationManager.IMPORTANCE_LOW)
                .apply { description = "Compteur actif pendant une marche lancée dans le jeu." })
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val snapshot = StepSessionStore.read(this)
        when (intent?.action) {
            ACTION_STOP -> {
                stopListening()
                if (snapshot.active) StepSessionStore.status(this, "stopped")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_PAUSE -> {
                if (snapshot.status == "running" || snapshot.status == "starting") {
                    stopListening()
                    StepSessionStore.status(this, "paused")
                    notifyProgress()
                }
                return START_STICKY
            }
            ACTION_RESUME -> {
                if (snapshot.status == "paused") {
                    StepSessionStore.status(this, "running")
                    startForegroundSafely()
                    if (StepSessionStore.read(this).status != "error") beginListening()
                }
                return START_STICKY
            }
            ACTION_START, null -> {
                if (snapshot.status !in listOf("starting", "running")) { stopSelf(); return START_NOT_STICKY }
                startForegroundSafely()
                if (StepSessionStore.read(this).status == "error") return START_NOT_STICKY
                beginListening()
                return START_STICKY
            }
            else -> return START_STICKY
        }
    }

    private fun startForegroundSafely() {
        try {
            if (!StepSessionStore.hasPermission(this)) {
                fail("Autorisation d'activité physique absente ou retirée.")
                return
            }
            val notification = buildNotification(StepSessionStore.read(this))
            // Le type HEALTH n'existe qu'à partir d'Android 14 (API 34).
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
            } else startForeground(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            fail("Impossible de démarrer le suivi Android : ${e.javaClass.simpleName}")
        }
    }

    private fun beginListening() {
        if (listening) return
        if (!StepSessionStore.hasPermission(this)) { fail("Autorisation d'activité physique manquante."); return }
        val counter = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val detector = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        selectedSensor = counter ?: detector
        if (selectedSensor == null) { fail("Ce téléphone ne possède pas de capteur de pas compatible."); return }
        // Une nouvelle référence évite de compter les pas faits pendant une pause ou un arrêt du processus.
        lastRaw = null
        val ok = try { manager.registerListener(this, selectedSensor, SensorManager.SENSOR_DELAY_NORMAL) }
                 catch (_: SecurityException) { false }
        if (!ok) { fail("Le capteur de pas n'a pas pu être activé."); return }
        listening = true
        StepSessionStore.status(this, "running", ready = selectedSensor?.type == Sensor.TYPE_STEP_DETECTOR)
        notifyProgress()
    }

    private fun stopListening() {
        if (listening) manager.unregisterListener(this)
        listening = false
        selectedSensor = null
        lastRaw = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!listening || event.sensor.type != selectedSensor?.type) return
        val before = StepSessionStore.read(this)
        if (before.status != "running") return
        val incoming = when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val value = event.values[0]
                val increment = stepSensorDelta(lastRaw, value)
                lastRaw = value
                increment
            }
            Sensor.TYPE_STEP_DETECTOR -> event.values[0].toInt().coerceAtLeast(0)
            else -> 0
        }
        val updated = accumulateSessionSteps(before.steps, incoming, before.cap)
        StepSessionStore.steps(this, updated, true)
        if (updated >= before.cap) {
            stopListening()
            StepSessionStore.status(this, "completed")
            stopForeground(STOP_FOREGROUND_REMOVE)
            val notification = buildNotification(StepSessionStore.read(this))
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID, notification)
            stopSelf()
        } else if (updated - lastNotified >= 10 || lastNotified < 0) notifyProgress()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun fail(message: String) {
        stopListening()
        StepSessionStore.status(this, "error", error = message)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun notifyProgress() {
        val snapshot = StepSessionStore.read(this)
        lastNotified = snapshot.steps
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, buildNotification(snapshot))
    }

    private fun buildNotification(s: StepSnapshot): Notification {
        val open = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle(if (s.status == "completed") "Étape atteinte · Dungeon of Fitness" else "Marche · Dungeon of Fitness")
            .setContentText(when (s.status) {
                "paused" -> "En pause : ${s.steps} pas (${s.steps} m)"
                "completed" -> "${s.steps} pas enregistrés. Ouvre le jeu pour la rencontre ou l'étape."
                else -> "${s.steps} / ${s.cap} pas · ${s.steps} m · 1 pas = 1 m"
            })
            .setContentIntent(open).setOnlyAlertOnce(true)
            .setOngoing(s.active).setAutoCancel(!s.active)
        if (s.status == "running" || s.status == "starting") builder.addAction(
            Notification.Action.Builder(android.R.drawable.ic_media_pause, "Pause", serviceAction(ACTION_PAUSE, 1)).build())
        if (s.status == "paused") builder.addAction(
            Notification.Action.Builder(android.R.drawable.ic_media_play, "Reprendre", serviceAction(ACTION_RESUME, 2)).build())
        if (s.active) builder.addAction(
            Notification.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, "Arrêter", serviceAction(ACTION_STOP, 3)).build())
        return builder.build()
    }

    private fun serviceAction(action: String, requestCode: Int): PendingIntent = PendingIntent.getService(
        this, requestCode, Intent(this, StepTrackingService::class.java).setAction(action),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    override fun onDestroy() { stopListening(); super.onDestroy() }
}
