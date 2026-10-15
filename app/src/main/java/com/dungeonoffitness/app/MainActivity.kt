package com.dungeonoffitness.app

import android.os.Bundle
import android.content.Intent
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.delay
import android.content.Context
import org.json.JSONObject
import org.json.JSONArray
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class Character(
    val name: String = "Capitaine",
    val className: String = "Novice",
    val divinity: String = "Tasmina",
    val level: Int = 1,
    val force: Int = 1,
    val endurance: Int = 1,
    val agility: Int = 1,
    val dexterity: Int = 1,
    val mental: Int = 1,
    val po: Int = 10,
    val location: String = "Fontaria",
    val profileType: String = "prototype",
    val pushups30: Int = 0,
    val jumpingJacks180: Int = 0,
    val balanceReps: Int = 0,
    val balanceCapable: Boolean = false,
    val vigour: Int = 0,
    val resistance: Int = 0,
    val adaptation: Int = 0,
    val strengthPercent: String = "",
    val enduranceHours: String = "",
    val enduranceDistance: String = "",
    val agilityMoves: String = "",
    val dexterityPercent: String = "",
    val dexteritySeconds: String = "",
    val mentalDifficulty: String = "",
    val mentalBonus: Int = 0,
    val primaryActivity: String = "",
    val specialAbility: String = "",
    val unlockedClasses: List<String> = emptyList(),
    val completedAchievements: List<String> = emptyList()
)


// Illustrations originales embarquées dans l'APK : aucune connexion requise.
private fun portraitFor(className: String): Int = when (className) {
    "Guerrier" -> R.drawable.portrait_guerrier
    "Acrobate" -> R.drawable.portrait_acrobate
    "Chasseur" -> R.drawable.portrait_chasseur
    "Éclaireur" -> R.drawable.portrait_eclaireur
    "Mage" -> R.drawable.portrait_mage
    "Moine" -> R.drawable.portrait_moine
    "Barde" -> R.drawable.portrait_barde
    "Cavalier" -> R.drawable.portrait_cavalier
    else -> R.drawable.portrait_guerrier
}
private fun classSymbol(className: String): String = when (className) {
    "Guerrier" -> "⚔️"; "Acrobate" -> "🤸"; "Chasseur" -> "🏹"
    "Éclaireur" -> "🧭"; "Mage" -> "🔮"; "Moine" -> "🙏"
    "Barde" -> "🎵"; "Cavalier" -> "🐎"; else -> "🛡️"
}
private fun itemSymbol(name: String): String = when {
    name.startsWith("Shaker") -> "🧪"
    name.startsWith("Parchemin comestible") -> "📜"
    name.contains("Armure", true) -> "🛡️"
    name.contains("Bouclier", true) -> "🛡️"
    name.contains("Bottes", true) -> "🥾"
    name.contains("Casque", true) -> "🪖"
    name.contains("Bracelets", true) || name.contains("Gants", true) -> "🥊"
    name.contains("Arc", true) || name.contains("Arbalète", true) || name.contains("Sarbacane", true) -> "🏹"
    name.contains("Épée", true) || name.contains("Epée", true) || name.contains("Sabre", true) || name.contains("Couteaux", true) -> "⚔️"
    name.contains("Haltère", true) || name.contains("Masse", true) -> "🏋️"
    name.contains("Lance", true) || name.contains("Hallebarde", true) || name.contains("Perche", true) -> "🔱"
    name.contains("Bijou", true) -> "💎"
    name.contains("Manuel", true) || name.contains("Récits", true) -> "📜"
    name.contains("couronne", true) -> "👑"
    name.contains("Pattes", true) -> "🐾"
    name.contains("Tapis", true) -> "🧘"
    else -> "✨"
}
@Composable private fun ClassPortrait(className: String, modifier: Modifier = Modifier) {
    Image(painter = painterResource(portraitFor(className)),
        contentDescription = "Illustration de la classe $className",
        contentScale = ContentScale.Fit,
        modifier = modifier.clip(RoundedCornerShape(10.dp)))
}
@Composable private fun HealthGauge(label: String, current: Int, maximum: Int, modifier: Modifier = Modifier) {
    val fraction = if (maximum > 0) (current.toFloat() / maximum).coerceIn(0f, 1f) else 0f
    val tint = when { fraction > .55f -> Color(0xFF53BD72); fraction > .25f -> Color(0xFFF0B44E); else -> Color(0xFFE65C5C) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("$current / $maximum PV", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(progress = { fraction }, color = tint,
            trackColor = Color(0xFF302B35), modifier = Modifier.fillMaxWidth().height(12.dp))
    }
}
@Composable private fun ClassBanner(c: Character) {
    Card(
        border = BorderStroke(1.dp, Color(0xFF97784A)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF24232F))
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ClassPortrait(c.className, Modifier.size(width = 100.dp, height = 120.dp))
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${classSymbol(c.className)} ${c.className.uppercase()}", color = Color(0xFFEBC582), style = MaterialTheme.typography.titleLarge)
                Text(c.name, style = MaterialTheme.typography.titleMedium)
                Text("Niveau ${c.level} · ${c.divinity}", style = MaterialTheme.typography.bodyMedium)
                Text(if (c.profileType == "advanced") "Profil avancé" else "Profil novice", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

private val classAchievements = linkedMapOf(
    "Éclaireur" to listOf("Survie", "Orientation", "Exploration"),
    "Cavalier" to listOf("Chevauchée", "Obstacle", "Course"),
    "Moine" to listOf("Discipline", "Nutrition", "Invocation"),
    "Chasseur" to listOf("Nuit du chasseur", "Instinct du chasseur", "Expérience du chasseur"),
    "Guerrier" to listOf("Premier sang", "Baptême du feu", "L’art de la guerre"),
    "Mage" to listOf("Initiation aux arcanes magiques", "Diplôme d’école de magie", "Maîtrise des vents magiques"),
    "Barde" to listOf("Gesticuleur", "Figurant", "Vedette"),
    "Acrobate" to listOf("Araignée", "Chat", "Singe")
)

// Catalogue initial transcrit du Guide touristique, pages 163 à 169.
// Les objets modificateurs et les effets complexes seront intégrés séparément.
private data class Weapon(val name: String, val price: Int, val damage: Int,
    val unit: String, val exercise: String, val contextBonus: Int = 0, val ranged: Boolean = false)
private val weapons = listOf(
    Weapon("Gants", 10, 10, "10 sec.", "Punches : coups de poing rapides et fluides"),
    Weapon("Gants de vétéran", 20, 10, "10 sec.", "Punches : coups de poing rapides et fluides", contextBonus = 1),
    Weapon("Haltère", 20, 20, "1 exécution", "Push Up : une pompe, corps droit, bras près du corps"),
    Weapon("Couteaux de lancer", 50, 10, "1 exécution", "Plank Rotation : en planche, rotation sur un bras", ranged = true),
    Weapon("Épée d'échauffement", 50, 50, "30 sec.", "Coups de taille avec une bouteille en plastique vide"),
    Weapon("Épée de bois", 50, 50, "30 sec.", "Coups de pointe avec une bouteille en plastique vide"),
    Weapon("Arbalète", 100, 20, "1 exécution", "Rower Biceps Legs : mouvement de rameur avec les jambes en résistance", ranged = true),
    Weapon("Épée de fonte", 150, 150, "30 sec.", "Coups de pointe avec une bouteille remplie d'eau"),
    Weapon("Arc féérique", 200, 50, "10 sec.", "Maintenir la posture Warrior II (yoga)", ranged = true),
    Weapon("Arc khôl", 200, 100, "1 exécution", "Archer Push Up : pompe avec un bras tendu sur le côté", ranged = true),
    Weapon("Épée longue", 200, 200, "30 sec.", "Coups de taille et pointe, bouteille d'eau tenue à deux mains"),
    Weapon("Culotte de mage", 200, 300, "30 sec.", "Crow : équilibre sur les bras, genoux hauts, regard devant", ranged = true),
    Weapon("Hallebarde lourde", 200, 200, "1 exécution", "One Arm Push Up : pompe sur un seul bras", contextBonus = 2),
    Weapon("Excalibrute", 500, 1000, "30 sec.", "Infinite Push Up : effectuer une seule pompe aussi lentement que possible, sur 30 secondes", contextBonus = 1),
    Weapon("Fléau d’inertie", 200, 100, "1 exécution", "Fly Step + Woodchopper : impulsion sur une jambe, bras levés, puis rotation de bûcheron", contextBonus = 1),
    Weapon("Epée de Tasmina", 0, 500, "30 sec.", "Coups de taille et de pointe avec une petite barre d'haltère", contextBonus = 2),
    Weapon("Lance", 100, 100, "30 sec.", "Coups de pointe avec un manche à balai"),
    Weapon("Masse Gainer", 300, 300, "30 sec.", "Coups de taille avec un manche lesté ou une barre vide"),
    Weapon("Masse haltère", 150, 150, "30 sec.", "Coups de taille avec une bouteille remplie d'eau"),
    Weapon("Perche Lance", 200, 200, "30 sec.", "Coups de pointe avec un manche à balai"),
    Weapon("Sarbacane", 50, 50, "10 sec.", "Vacuum : aspirer l’abdomen vers la colonne pour travailler le transverse"),
    Weapon("Sabre féérique", 200, 50, "1 exécution", "Turkish Get Up : se relever du sol en utilisant une jambe et un bras"),
    Weapon("Sceptre féérique", 200, 200, "10 sec.", "Climbers : ramener les genoux vers la poitrine en position de planche"),
    Weapon("Tapis de mage de guerre", 500, 300, "1 exécution", "Enchaîner Warrior I, II et III, 10 secondes chacune, sans interruption", ranged = true)
)

// Armures officielles (livre p. 164) : emplacement distinct de l'arme et du soutien.
private data class Armor(val name: String, val price: Int, val bonus: Int, val context: Int, val exercise: String, val reroll: Boolean = false)
private val armors = listOf(
    Armor("Armure de force", 300, 100, 2, "Se lester de 10 à 20 kg."),
    Armor("Armure de leste", 200, 50, 1, "Se lester de 5 à 10 kg."),
    Armor("Armure respirante", 100, 0, 0, "Porter un sac à dos vide.", reroll = true)
)

// Equipements de soutien : Guide touristique, pages 165-166.
// Un seul soutien actif ; bonus d'attaque conditionné à l'exercice confirmé.
private data class Support(val name: String, val price: Int, val bonus: Int, val context: Int, val exercise: String)
private val supports = listOf(
    Support("Bottes de Tasmina", 200, 50, 1, "1 High Jump : sauter aussi haut que possible"),
    Support("Bouclier de bois", 50, 10, 1, "1 Lunge : fente, genou à 90°"),
    Support("Bouclier de fonte", 100, 25, 1, "1 Jumping Lunge : fente sautée alternée"),
    Support("Bouclier lourd", 200, 100, 2, "1 Pistol Squat : squat sur une jambe"),
    Support("Bracelets lestés", 50, 10, 0, "Lester les poignets pour l'attaque"),
    Support("Casque de Tasmina", 200, 50, 0, "1 Side Lunge : fente latérale")
)

// Objets d'amélioration : catalogue officiel, pages 170-171.
private data class CombatUpgrade(val name: String, val price: Int, val bonus: Int, val description: String)
private val combatUpgrades = listOf(
    CombatUpgrade("Pattes d’ours", 50, 50, "Compter à voix haute les répétitions ou secondes de chaque attaque."),
    CombatUpgrade("Manuel de bottes de Maître", 1000, 100, "Améliore les dégâts provoqués par une arme."),
    CombatUpgrade("Manuel de combat à mains nues", 500, 50, "Améliore les dégâts des gants et des parties du corps."),
    CombatUpgrade("Manuel de méditation", 1000, 100, "Méditer 60 secondes par adversaire pour +100 PV sur chacune des attaques du combat."),
    CombatUpgrade("Manuel d’agilité", 1000, 0, "Bonus permanent de +2 au jet de contexte des prochaines rencontres (p. 169)."),
    CombatUpgrade("Récits héroïques", 1000, 0, "Livre p. 171 : dégâts ×2 lorsque tu combats à deux contre un (alliés compris). À confirmer à chaque attaque."),
    CombatUpgrade("Réplique de couronne", 1000, 0, "Livre p. 171 : dégâts ×2 lorsque tu combats aux côtés d’un autre joueur réel. À confirmer à chaque attaque.")
)
// Le bonus fixe est ajouté une fois à chaque attaque déclarée, pas à chaque répétition.
private fun attackDamage(weapon: Weapon, count: Int, inventory: List<String>, pattesUsed: Boolean,
    meditationActive: Boolean = false, vigorActive: Boolean = false, supportBonus: Int = 0,
    cardioScroll: Boolean = false, forceScroll: Boolean = false, congestionScroll: Boolean = false): Int {
    // Bonus des AMÉLIORATIONS, ajoutés une fois par attaque (convention confirmée par le joueur).
    val improvementBonus = (if ("Manuel de bottes de Maître" in inventory) 100 else 0) +
        (if ("Manuel de combat à mains nues" in inventory && weapon.name.startsWith("Gants")) 50 else 0) +
        (if (pattesUsed && "Pattes d’ours" in inventory) 50 else 0) +
        (if (meditationActive && "Manuel de méditation" in inventory) 100 else 0)
    // Les bonus des soutiens ne sont pas des « améliorations » dans le catalogue (p. 165–171).
    return ScrollCombatRules.weaponDamage(
        basePerUnit = weapon.damage,
        units = count,
        improvementBonus = improvementBonus,
        supportBonus = supportBonus,
        isTimed = weapon.unit.contains("sec."),
        cardioActive = cardioScroll,
        forceActive = forceScroll,
        congestionActive = congestionScroll,
        vigorActive = vigorActive
    )
}

private data class Encounter(val name: String, val quantity: Int, val hp: Int, val reward: Int, val context: String, val exercise: String, val roll: Int = 0, val remainingHp: Int = hp * quantity, val effect: String = "", val safe: Boolean = false, val contextDie: Int = 0, val contextBonus: Int = 0, val contextWeapon: String = "", val initialQuantity: Int = quantity, val illusionDie: Int = 0, val enemyHp: List<Int> = emptyList(), val hunterUsed: Boolean = false, val mysticUsed: Boolean = false, val mysticDescription: String = "", val typeDice: List<Int> = emptyList(), val quantityDie: Int = 0, val bulkerUsed: Boolean = false,
    val flexibilityScrollUsed: Boolean = false,
    val armorSecondDie: Int = 0, val armorRerollUsed: Boolean = false,
    val contextArmor: String = "",
    val acidTargets: List<Int> = emptyList(), val mageCardioCasts: Int = 0)

private data class AppState(
    val character: Character = Character(),
    val distance: Double = 0.0,
    val destination: String = "Tique-Couenne",
    val destinationKm: Double = 0.0,
    val totalTravelKm: Double = 0.0,
    val encounters: Int = 0,
    val lastEncounter: Encounter? = null,
    val log: List<String> = listOf("Le voyage commence. Le Disque-Fonte vous attend."),
    val inventory: List<String> = emptyList(),
    val equippedWeapon: String = "",
    val equippedSupport: String = "",
    val equippedArmor: String = "",
    val mageJewelSpent: Int = 0, val mageResting: Boolean = false,
    val chevalierProtectionActive: Boolean = false,
    val meditationActive: Boolean = false,
    val cardioScrollActive: Boolean = false,
    val forceScrollActive: Boolean = false,
    val congestionScrollActive: Boolean = false,
    val vigorActive: Boolean = false,
    val quantumMode: String = "",
    val illusionActive: Boolean = false,
    val retreatActive: Boolean = false,
    val retreatOrigin: String = "",
    val abilityDay: String = "",
    val scoutUses: Int = 0,
    val warriorUsed: Boolean = false,
    val warriorActive: Boolean = false,
    val bardUses: Int = 0,
    val bardJourney: Boolean = false,
    val cavalierUsed: Boolean = false,
    // Route guidée : jalons = départ + étapes + arrivée ; tronçons en unités réellement parcourues.
    // L'index indique le tronçon en cours, sans valider automatiquement les chapitres du livre.
    val routeStops: List<String> = emptyList(),
    val routeLegKm: List<Double> = emptyList(),
    val routeLegIndex: Int = 0,
    val visitedPlaces: List<String> = emptyList(),
    val completedChapters: List<String> = emptyList(),
    val manualChapterAccess: List<String> = emptyList(),
    val manualCompanions: List<String> = emptyList(),
    val dismissedCompanions: List<String> = emptyList(),
    val fairyTeleportUsed: Boolean = false,
    val claimedChapterLoot: List<String> = emptyList(),
    // Pouvoirs officiels des compagnons (p. 73, 113 et 129).
    val oracleJourneyActive: Boolean = false,
    val oracleCombatActive: Boolean = false,
    val paladinJourneyArmed: Boolean = false,
    val paladinJourneyUsed: Boolean = false,
    val companionBardJourney: Boolean = false,
    // Curseur de lecture du compteur de pas : sauvegardé avec l'aventure pour garantir l'idempotence.
    val stepSessionId: String = "",
    val stepAppliedSteps: Int = 0,
    val stepLastClosedId: String = "",
    val quests: List<String> = listOf("Créer et suivre votre profil", "Tenir votre journal d'entraînement")
)

class MainActivity : ComponentActivity() {
    companion object { const val EXTRA_OPEN_ADVENTURE = "com.dungeonoffitness.OPEN_ADVENTURE" }
    // Chaque clic sur une alerte ouvre l'onglet Aventure, même si l'activité est déjà lancée.
    private var adventureNavigationToken by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.getBooleanExtra(EXTRA_OPEN_ADVENTURE, false) == true) adventureNavigationToken++
        setContent { DungeonApp(adventureNavigationToken) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_OPEN_ADVENTURE, false)) adventureNavigationToken++
    }
}

@Composable
fun DungeonApp(adventureNavigationToken: Int = 0) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var state by remember { mutableStateOf(loadState(context)) }
    fun update(next: AppState) { state = next; saveState(context, next) }
    var stepSnapshot by remember { mutableStateOf(StepSessionStore.read(context)) }
    var stepNotice by remember { mutableStateOf("") }
    val latestState by rememberUpdatedState(state)
    // Synchronisation non destructive : les capteurs n'écrivent JAMAIS dans le JSON RPG.
    // Chaque pas est crédité au maximum une fois, même si Android recrée l'activité.
    LaunchedEffect(context) {
        while (true) {
            val fresh = StepSessionStore.read(context)
            stepSnapshot = fresh
            val game = latestState
            val sameSession = fresh.id.isNotBlank() && fresh.id == game.stepSessionId
            val pendingSteps = sameSession && fresh.steps > game.stepAppliedSteps
            val justClosed = sameSession && fresh.status in listOf("completed", "stopped", "error") &&
                game.stepLastClosedId != fresh.id
            if (pendingSteps || justClosed) {
                val delta = (fresh.steps - game.stepAppliedSteps).coerceAtLeast(0)
                var moved = if (delta > 0) travel(game, stepsToJourneyKm(delta), viaSteps = true) else game
                moved = moved.copy(stepAppliedSteps = fresh.steps.coerceAtLeast(game.stepAppliedSteps))
                if (justClosed) {
                    val endLabel = when (fresh.status) {
                        "completed" -> "arrêt automatique au seuil"
                        "error" -> "interruption : ${fresh.error}"
                        else -> "arrêt manuel"
                    }
                    moved = moved.copy(stepLastClosedId = fresh.id,
                        log = (listOf("👣 Podomètre : ${fresh.steps} pas (${fresh.steps} m) · $endLabel.") + moved.log).take(100))
                }
                update(moved)
            }
            delay(750L)
        }
    }
    fun beginStepSession() {
        val game = state
        val old = StepSessionStore.read(context)
        if (old.active || (old.id == game.stepSessionId && old.steps > game.stepAppliedSteps)) return
        val boundaryKm = nextStepBoundary(game) ?: return
        val cap = stepsUntilBoundary(boundaryKm)
        if (cap <= 0 || game.character.profileType == "novice") return
        val stageEnd = if (validGuidedRoute(game)) nextRouteEnd(game) else game.destinationKm
        val spacing = if ("Le Troll Conteur" in availableCompanions(game)) 3.0 else 6.0
        val count = kotlin.math.floor((game.totalTravelKm + 0.00000001) / spacing).toInt()
        val nextEncounter = if (game.retreatActive || game.chevalierProtectionActive) Double.POSITIVE_INFINITY
            else ((count + 1) * spacing - game.totalTravelKm).coerceAtLeast(0.0)
        val stopKind = stepStopKind(boundaryKm, nextEncounter,
            validGuidedRoute(game) && game.routeLegIndex < game.routeLegKm.lastIndex)
        val stopPlace = if (stopKind == "waypoint") game.routeStops[game.routeLegIndex + 1]
            else game.destination
        try {
            val session = StepSessionStore.begin(context, cap, stopKind, stopPlace)
            // Sauvegarde du curseur d'abord, puis lancement du capteur.
            update(game.copy(stepSessionId = session.id, stepAppliedSteps = 0,
                log = (listOf("👣 Podomètre lancé : plafond de $cap pas avant la prochaine rencontre ou étape.") + game.log).take(100)))
            stepSnapshot = session
            StepTrackingService.dispatch(context, StepTrackingService.ACTION_START)
            stepNotice = "Session lancée. Attends la première mesure du capteur avant de marcher."
        } catch (ex: Exception) {
            StepSessionStore.status(context, "error", error = "Démarrage impossible : ${ex.javaClass.simpleName}")
            stepSnapshot = StepSessionStore.read(context)
        }
    }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        // Même si les notifications sont refusées, Android autorise le suivi via le service visible dans son gestionnaire de tâches.
        beginStepSession()
    }
    val activityPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            if (Build.VERSION.SDK_INT >= 33 &&
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            else beginStepSession()
        } else stepNotice = "Autorisation d'activité physique refusée : le mode manuel reste disponible."
    }
    fun requestStepStart() {
        if (!StepSessionStore.hasSensor(context)) {
            stepNotice = "Aucun capteur de pas compatible sur ce téléphone. Utilise les kilomètres manuels."
        } else if (!StepSessionStore.hasPermission(context) && Build.VERSION.SDK_INT >= 29) {
            activityPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else beginStepSession()
    }
    val stepBusy = stepSnapshot.active || (stepSnapshot.id == state.stepSessionId && stepSnapshot.steps > state.stepAppliedSteps)
    var tab by remember { mutableStateOf("home") }
    LaunchedEffect(adventureNavigationToken) {
        if (adventureNavigationToken > 0) tab = "adventure"
    }
    var resetDialog by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFFE5B66C), onPrimary = Color(0xFF211609),
        secondary = Color(0xFF80B9BB), surface = Color(0xFF191B25),
        background = Color(0xFF11141D), surfaceVariant = Color(0xFF292A36)
    )) {
        Scaffold(bottomBar = { NavigationBar {
            listOf("home" to "Accueil", "adventure" to "Aventure", "character" to "Personnage", "inventory" to "Sac", "journal" to "Journal", "application" to "Application").forEach { (id, label) ->
                NavigationBarItem(selected = tab == id || (id == "adventure" && tab in listOf("chapters", "companions")), onClick = { tab = id }, icon = { Text(if (id == "home") "⌂" else if (id == "adventure") "⚔" else if (id == "character") "♙" else if (id == "inventory") "◆" else if (id == "journal") "▤" else "⚙") }, label = { Text(label) })
            }
        }}) { pad ->
            Box(Modifier.padding(pad).fillMaxSize()) {
                when(tab) {
                    "home" -> Home(state) { tab = "adventure" }
                    "chapters" -> CampaignChapters(state, onBack = { tab = "adventure" },
                        onCompletionChange = { title, done -> update(setChapterCompleted(state, title, done)) },
                        onAccessChange = { title, acquired -> update(confirmChapterAccess(state, title, acquired)) },
                        onClaimLoot = { title -> update(claimChapterLoot(state, title)) })
                    "companions" -> CompanionsScreen(state, onBack = { tab = "adventure" },
                        onCompanionChange = { name, acquired -> update(setCompanionPresence(state, name, acquired)) })
                    "adventure" -> Adventure(state,
                        onOpenChapters = { tab = "chapters" },
                        onOpenCompanions = { tab = "companions" },
                        onTravel = { km -> if (!stepBusy && !state.mageResting) update(travel(state, km)) },
                        stepSnapshot = stepSnapshot,
                        stepNotice = stepNotice,
                        stepBusy = stepBusy,
                        stepSensorAvailable = StepSessionStore.hasSensor(context),
                        onStartSteps = { if (!state.mageResting) requestStepStart() },
                        onPauseSteps = { StepTrackingService.dispatch(context, StepTrackingService.ACTION_PAUSE) },
                        onResumeSteps = { StepTrackingService.dispatch(context, StepTrackingService.ACTION_RESUME) },
                        onStopSteps = { StepTrackingService.dispatch(context, StepTrackingService.ACTION_STOP) },
                        onWarrior = { update(activateWarrior(state)) },
                        onResolve = { damage, heroic, crown -> if (!state.mageResting) update(resolve(state, enrichedAttackDamage(state, damage, heroic, crown), attack = true)) },
                        onMageSpell = { spell, target ->
                            if (spell == "rest" && !state.mageResting &&
                                StepSessionStore.read(context).status in listOf("starting", "running")) {
                                StepTrackingService.dispatch(context, StepTrackingService.ACTION_PAUSE)
                            }
                            val updated = castMageSpell(state, spell, target)
                            if (updated != state) update(updated)
                        },
                        onWeaponAttack = { name, count, pattesUsed, target, supportConfirmed, armorConfirmed, heroic, crown ->
                            val weapon = weapons.firstOrNull { it.name == name }
                            if (!state.mageResting && weapon != null && name in state.inventory && count > 0)
                                {
                                    val support = supports.firstOrNull { it.name == state.equippedSupport && it.name in state.inventory }
                                    val armor = armors.firstOrNull { it.name == state.equippedArmor && it.name in state.inventory }
                                    val extraArmor = ArmorRules.bonusForUnits(armor?.bonus ?: 0, count, armorConfirmed)
                                    val damage = enrichedAttackDamage(state, attackDamage(weapon, count, state.inventory, pattesUsed, state.meditationActive, state.vigorActive,
                                        ((if (supportConfirmed) support?.bonus ?: 0 else 0).toLong() + extraArmor.toLong()).coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                                        state.cardioScrollActive, state.forceScrollActive, state.congestionScrollActive), heroic, crown)
                                    val e = state.lastEncounter
                                    if (weapon.ranged && e != null) {
                                        val hp = encounterHp(e)
                                        if (target != null && target in hp.indices && hp[target] > 0) {
                                            val after = resolveTarget(state, damage, target, attack = true)
                                            val actual = hp[target] - (after.lastEncounter?.let { encounterHp(it).getOrNull(target) } ?: 0)
                                            update(after.copy(log = (listOf("${weapon.name} : cible ${e.name} n°${target + 1}, $actual PV infligés (bonus acido-basique éventuel inclus, sans transfert).") + after.log).take(100)))
                                        }
                                    } else if (!weapon.ranged) update(resolve(state, damage, attack = true))
                                }
                        },
                        onSkip = { if (!state.mageResting) update(skipEncounter(state)) },
                        onChevalierEscort = { if (!state.mageResting) update(acceptChevalierEscort(state)) },
                        onAcceptShaker = { name -> if (!state.mageResting) update(acceptBulkerShaker(state, name)) },
                        onPayGuards = { if (!state.mageResting) update(payFitlandGuards(state)) },
                        onFlee = { towardDestination -> if (!state.mageResting) update(fleeEncounter(state, towardDestination)) },
                        onHunt = { update(useHunterAbility(state)) },
                        onBulker = { update(useBulkerCompanion(state)) },
                        onOracleCombat = { update(activateOracleCombat(state)) },
                        onPaladin = { update(usePaladinQuantity(state)) },
                        onFairyTeleport = { target -> update(useFairyTeleport(state, target)) },
                        onMystic = { kind, dieIndex, delta -> update(useMysticDie(state, kind, dieIndex, delta)) },
                        onArmorReroll = { update(startArmorReroll(state)) },
                        onArmorChoose = { chooseSecond -> update(chooseArmorReroll(state, chooseSecond)) },
                        onCavalier = { place -> update(useCavalierAbility(state, place)) },
                        onMeditate = {
                            val e = state.lastEncounter
                            if (e != null && !e.safe && "Manuel de méditation" in state.inventory && !state.meditationActive)
                                update(state.copy(meditationActive = true, log = (listOf("Méditation effectuée : ${60 * e.quantity} secondes pour ${e.quantity} adversaire(s).") + state.log).take(100)))
                        },
                        onUsePersuasion = {
                            val e = state.lastEncounter
                            if (e != null && "Shaker persuasion" in state.inventory)
                                update(state.copy(lastEncounter = null, meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
                                    inventory = removeOne(state.inventory, "Shaker persuasion"),
                                    log = (listOf("Shaker persuasion consommé : combat contre ${e.name} ignoré, sans récompense.") + state.log).take(100)))
                        },
                        onUseConsumable = { name, mode ->
                            val e = state.lastEncounter
                            if (!state.mageResting && e != null && !e.safe && name in state.inventory) {
                                when (name) {
                                    "Shaker poison" -> {
                                        val current = encounterHp(e)
                                        val target = mode.toIntOrNull()?.coerceIn(0, current.lastIndex) ?: current.indexOfFirst { it > 0 }.coerceAtLeast(0)
                                        val poisonDamage = minOf(700, current[target])
                                        val after = resolveTarget(state, poisonDamage, target)
                                        update(after.copy(inventory = removeOne(after.inventory, name),
                                            log = (listOf("Shaker poison : $poisonDamage dégâts à distance sur ${e.name} n°${target + 1} (maximum 700, sans transfert aux autres ennemis).") + after.log).take(100)))
                                    }
                                    "Shaker sombre" -> {
                                        // Livre p. 173 : 700 dégâts à distance, cible un groupe.
                                        // Convention explicite : 700 sur chaque ennemi vivant de la rencontre.
                                        val after = useDarkShaker(state)
                                        if (after != state) update(after)
                                    }
                                    "Shaker vigueur" -> if (!state.vigorActive)
                                        update(state.copy(vigorActive = true, inventory = removeOne(state.inventory, name),
                                            log = (listOf("Shaker vigueur : dégâts ×2 jusqu’à la fin du combat.") + state.log).take(100)))
                                    "Shaker illusion" -> if (!state.illusionActive) {
                                        // Règle maison demandée : 1d6 adversaires quittent le combat, minimum 1 restant.
                                        val die = Random.nextInt(1, 7)
                                        val removed = minOf(die, e.quantity - 1)
                                        val remainingEnemies = e.quantity - removed
                                        val newEnemyHp = encounterHp(e).take(remainingEnemies)
                                        val newHp = newEnemyHp.sum()
                                        val newReward = if (e.quantity > 0) (e.reward.toLong() * remainingEnemies / e.quantity).toInt() else 0
                                        val updated = e.copy(quantity = remainingEnemies, remainingHp = newHp,
                                            reward = newReward, initialQuantity = e.initialQuantity, illusionDie = die, enemyHp = newEnemyHp)
                                        update(state.copy(lastEncounter = updated, illusionActive = true,
                                            inventory = removeOne(state.inventory, name),
                                            log = (listOf("Shaker illusion (règle maison) : 1d6 = $die ; $removed adversaire(s) écarté(s), $remainingEnemies restant(s). Butin ajusté.") + state.log).take(100)))
                                    }
                                    "Shaker quantique" -> if (state.quantumMode.isBlank() && mode in listOf("Accélérer", "Ralentir"))
                                        update(state.copy(quantumMode = mode, inventory = removeOne(state.inventory, name),
                                            log = (listOf("Shaker quantique : ${mode.lowercase()} le temps, facteur 2, pendant ce combat (durées recalculées dans les attaques).") + state.log).take(100)))
                                    "Parchemin comestible de cardio" -> if (!state.cardioScrollActive)
                                        update(state.copy(cardioScrollActive = true, inventory = removeOne(state.inventory, name),
                                            log = (listOf("Parchemin de cardio (p. 174) : base des attaques à durée ×2 pendant ce combat.") + state.log).take(100)))
                                    "Parchemin comestible de force" -> if (!state.forceScrollActive)
                                        update(state.copy(forceScrollActive = true, inventory = removeOne(state.inventory, name),
                                            log = (listOf("Parchemin de force (p. 174) : base des attaques par exécution ×2 pendant ce combat.") + state.log).take(100)))
                                    "Parchemin comestible de congestion" -> if (!state.congestionScrollActive)
                                        update(state.copy(congestionScrollActive = true, inventory = removeOne(state.inventory, name),
                                            log = (listOf("Parchemin de congestion (p. 174) : bonus des améliorations ×2 par attaque pendant ce combat (hors soutien).") + state.log).take(100)))
                                    "Parchemin comestible de flexibilité" -> if (!e.flexibilityScrollUsed && e.contextDie in 1..6 && encounterHp(e).all { it == e.hp }) {
                                        val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
                                        val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
                                        val index = ScrollCombatRules.flexibilityContext(e.contextDie, e.contextBonus) - 1
                                        val edited = e.copy(contextBonus = e.contextBonus + 5, context = contexts[index], exercise = effects[index],
                                            contextWeapon = listOf(e.contextWeapon, "Parchemin flexibilité +5").filter { it.isNotBlank() }.joinToString(" + "),
                                            flexibilityScrollUsed = true)
                                        update(state.copy(lastEncounter = edited, inventory = removeOne(state.inventory, name),
                                            log = (listOf("Parchemin de flexibilité (p. 174) consommé : contexte +5, plafonné à 6. À utiliser avant de résoudre les exercices immédiats du contexte.") + state.log).take(100)))
                                    }
                                }
                            }
                        },
                        onPlan = { destination, km, scout, bardAudacious, followMap, bardMusic, oraclePrayer, paladinPrayer ->
                            if (!state.mageResting) update(planJourney(state, destination, km, scout, bardAudacious, followMap, bardMusic, oraclePrayer, paladinPrayer))
                        },
                        onCheckpoint = { if (!state.mageResting) update(confirmRouteCheckpoint(state)) },
                        onArrive = {
                            if (!state.mageResting && state.lastEncounter == null && state.destinationKm > 0.0 && state.distance + 0.00001 >= state.destinationKm) {
                                update(state.copy(character = state.character.copy(location = state.destination),
                                    visitedPlaces = (state.visitedPlaces + state.character.location + state.destination).distinct(),
                                    destination = "", destinationKm = 0.0, distance = 0.0, bardJourney = false, retreatActive = false, retreatOrigin = "",
                                    oracleJourneyActive = false, oracleCombatActive = false, chevalierProtectionActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
                                    paladinJourneyArmed = false, paladinJourneyUsed = false, companionBardJourney = false,
                                    routeStops = emptyList(), routeLegKm = emptyList(), routeLegIndex = 0,
                                    log = (listOf("Arrivée à ${state.destination}. Chapitre à valider selon le livre.") + state.log).take(100)))
                            }
                        })
                    "character" -> CharacterScreen(state.character, onUpdate = { candidate ->
                        val existing = state.character.profileType
                        if (existing == "prototype" || existing == candidate.profileType) {
                            val firstRegistration = existing == "prototype" && candidate.profileType != "prototype"
                            val startingPlace = if (firstRegistration) when(candidate.className) {
                                "Éclaireur" -> "Sud du Fitland"
                                "Cavalier" -> "Plaine du Fitland"
                                "Moine" -> "Tombe d’Arno"
                                "Chasseur" -> "Dévers du Fitland"
                                "Guerrier" -> "Temple de Tasmina"
                                "Mage" -> "Tique-Couenne"
                                "Barde" -> "Fontaria"
                                "Acrobate" -> "Fei Tôro Choh"
                                else -> "Fontaria"
                            } else state.character.location
                            update(state.copy(character = candidate.copy(location = startingPlace),
                                destination = if (firstRegistration) "" else state.destination,
                                destinationKm = if (firstRegistration) 0.0 else state.destinationKm,
                                distance = if (firstRegistration) 0.0 else state.distance))
                        }
                    }, onReset = { resetDialog = true })
                    "inventory" -> Inventory(state, onInvestMageJewel = {
                        if (state.character.po >= 200 && state.mageJewelSpent <= Int.MAX_VALUE - 200) {
                            val first = "Bijou de mage" !in state.inventory
                            update(state.copy(character = state.character.copy(po = state.character.po - 200),
                                mageJewelSpent = state.mageJewelSpent + 200,
                                inventory = if (first) state.inventory + "Bijou de mage" else state.inventory,
                                log = (listOf("💎 Bijou de mage (p. 164) : 200 PO investis, " +
                                    "bonus de contexte +${MageSpellRules.jewelBonus(state.mageJewelSpent + 200)} pour les futures rencontres.") + state.log).take(100)))
                        }
                    }, onArmorBuy = { armor ->
                        if (state.character.po >= armor.price && armor.name !in state.inventory)
                            update(state.copy(character = state.character.copy(po = state.character.po - armor.price),
                                inventory = state.inventory + armor.name,
                                log = (listOf("Achat : ${armor.name} (${armor.price} PO).") + state.log).take(100)))
                    }, onEquipArmor = { name ->
                        if (name.isEmpty() || (name in state.inventory && armors.any { it.name == name })) update(state.copy(equippedArmor = name))
                    }, onTestGold = { amount ->
                        if (amount in 1..10000 && state.character.po <= Int.MAX_VALUE - amount)
                            update(state.copy(character = state.character.copy(po = state.character.po + amount),
                                log = (listOf("MODE TEST : +$amount PO ajoutés (hors règles officielles).") + state.log).take(100)))
                    }, onConsumableBuy = { name ->
                        if (name in listOf("Shaker persuasion", "Shaker poison", "Shaker vigueur", "Shaker quantique", "Shaker illusion",
                            "Parchemin comestible de cardio", "Parchemin comestible de congestion",
                            "Parchemin comestible de flexibilité", "Parchemin comestible de force") && state.character.po >= 100)
                            update(state.copy(character = state.character.copy(po = state.character.po - 100),
                                inventory = state.inventory + name,
                                log = (listOf("Achat : $name (100 PO)") + state.log).take(100)))
                    }, onSupportBuy = { support ->
                        if (state.character.po >= support.price && support.name !in state.inventory)
                            update(state.copy(character = state.character.copy(po = state.character.po - support.price),
                                inventory = state.inventory + support.name, log = (listOf("Achat : ${support.name} (${support.price} PO)") + state.log).take(100)))
                    }, onEquipSupport = { name ->
                        if (name.isEmpty() || (name in state.inventory && supports.any { it.name == name })) update(state.copy(equippedSupport = name))
                    }, onUpgradeBuy = { upgrade ->
                        if (state.character.po >= upgrade.price && upgrade.name !in state.inventory)
                            update(state.copy(character = state.character.copy(po = state.character.po - upgrade.price),
                                inventory = state.inventory + upgrade.name,
                                log = (listOf("Achat : ${upgrade.name} (${upgrade.price} PO)") + state.log).take(100)))
                    }, onEquip = { name ->
                        if (name.isEmpty() || name in state.inventory) update(state.copy(equippedWeapon = name))
                    }, onBuy = { weapon ->
                        if (weapon.price > 0 && state.character.po >= weapon.price)
                            update(state.copy(character = state.character.copy(po = state.character.po - weapon.price),
                                inventory = state.inventory + weapon.name,
                                log = (listOf("Achat : ${weapon.name} (${weapon.price} PO)") + state.log).take(100)))
                    })
                    "journal" -> Journal(state)
                    "application" -> ApplicationInfo(state, onResetDailyTests = {
                        val current = dailyState(state)
                        update(current.copy(scoutUses = 0, bardUses = 0, warriorUsed = false, cavalierUsed = false,
                            log = (listOf("MODE TEST : compteurs quotidiens Éclaireur, Barde, Guerrier et Cavalier remis à zéro (sans modifier l’aventure).") + current.log).take(100)))
                    })
                    else -> Home(state) { tab = "adventure" }
                }
            }
        }
        if (resetDialog) {
            AlertDialog(
                onDismissRequest = { resetDialog = false },
                title = { Text("Tout recommencer ?") },
                text = { Text("Cette action supprime définitivement le personnage, les tests, les kilomètres, l'inventaire et le journal. Elle ne peut pas être annulée.") },
                confirmButton = { TextButton(onClick = {
                    context.stopService(android.content.Intent(context, StepTrackingService::class.java))
                    StepSessionStore.clear(context)
                    stepSnapshot = StepSnapshot()
                    stepNotice = ""
                    context.getSharedPreferences("dof_save", Context.MODE_PRIVATE).edit().clear().commit()
                    state = AppState()
                    tab = "home"
                    resetDialog = false
                }) { Text("EFFACER TOUT") } },
                dismissButton = { TextButton(onClick = { resetDialog = false }) { Text("Annuler") } }
            )
        }
    }
}

@Composable private fun Home(state: AppState, start: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("⚔ DUNGEON OF FITNESS", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineMedium) }
        item { ClassBanner(state.character) }
        item { Text("Exploration, combats et suivi de campagne. Le livre reste nécessaire pour les chapitres et leurs épreuves.", style = MaterialTheme.typography.bodyLarge) }
        item { Card { Column(Modifier.padding(18.dp)) {
            Text(state.character.name, style = MaterialTheme.typography.titleLarge)
            Text("Niveau ${state.character.level} · ${state.character.className} · Disciple de ${state.character.divinity}")
            Spacer(Modifier.height(10.dp)); Text("📍 ${state.character.location}  →  ${state.destination}")
            Text(if (state.destinationKm > 0.0) "Distance : ${"%.1f".format(state.distance)} / ${state.destinationKm} ${if (state.character.profileType == "novice") "min" else "km"}" else "Aucun trajet en cours")
            if (validGuidedRoute(state)) {
                Text("🧭 Étape ${state.routeLegIndex + 1}/${state.routeLegKm.size} : vers ${state.routeStops[state.routeLegIndex + 1]}",
                    color = Color(0xFFEBC582), style = MaterialTheme.typography.bodySmall)
            }
        } } }
        item { Button(onClick = start, modifier = Modifier.fillMaxWidth()) { Text("⚔ COMMENCER L'AVENTURE") } }
        item { Text("Dernier événement", style = MaterialTheme.typography.titleMedium) }
        item { Text(state.log.firstOrNull() ?: "Aucun événement") }
    }
}

private val fitlandPlaces = listOf(
    "Fontaria", "Temple de Tasmina", "Dévers du Fitland", "Plaine du Fitland",
    "Sud du Fitland", "Tique-Couenne", "Tombe d’Arno", "Fei Tôro Choh",
    "Entrepôts Shaker+", "Falaises Olympiques", "Ruines du Golem",
    "Forêt de Brise Mollets", "Omega Beach", "Mont Stepper",
    "Nosferathon", "Garde Froide", "Litière Ville", "Khôlkozia",
    "Dunes des sacs crevés", "Erythropoiéta", "Oasis Interdite",
    "Temple de la Prophétie", "Rives de la Mer de Nuages",
    "Caverne des Nuages sans fin", "Rocher de la Croupe Draconique",
    "Île des poids guidés", "Antre de Mollefesse"
)

// Carte officielle p. 51 : uniquement les chemins EN POINTILLÉS et distances inscrites.
// Liens bidirectionnels pour les déplacements, sans raccourcis implicites.
// Lieux avec cadenas : accès à valider dans le livre (non déverrouillés automatiquement).
private data class MapLink(val from: String, val to: String, val km: Int)
private val officialMapLinks = listOf(
    MapLink("Fontaria", "Temple de Tasmina", 10),
    MapLink("Temple de Tasmina", "Entrepôts Shaker+", 15),
    MapLink("Entrepôts Shaker+", "Antre de Mollefesse", 20),
    MapLink("Entrepôts Shaker+", "Dévers du Fitland", 10),
    MapLink("Falaises Olympiques", "Dévers du Fitland", 10),
    MapLink("Dévers du Fitland", "Ruines du Golem", 5),
    MapLink("Dévers du Fitland", "Plaine du Fitland", 10),
    MapLink("Temple de Tasmina", "Plaine du Fitland", 10),
    MapLink("Temple de Tasmina", "Omega Beach", 10),
    MapLink("Temple de Tasmina", "Mont Stepper", 20),
    MapLink("Plaine du Fitland", "Omega Beach", 10),
    MapLink("Plaine du Fitland", "Sud du Fitland", 10),
    MapLink("Omega Beach", "Sud du Fitland", 5),
    MapLink("Sud du Fitland", "Forêt de Brise Mollets", 10),
    MapLink("Forêt de Brise Mollets", "Dunes des sacs crevés", 5),
    MapLink("Dunes des sacs crevés", "Erythropoiéta", 10),
    MapLink("Dunes des sacs crevés", "Temple de la Prophétie", 5),
    MapLink("Dunes des sacs crevés", "Tique-Couenne", 10),
    MapLink("Tique-Couenne", "Oasis Interdite", 10),
    MapLink("Tique-Couenne", "Rocher de la Croupe Draconique", 10),
    MapLink("Rocher de la Croupe Draconique", "Rives de la Mer de Nuages", 10),
    MapLink("Rocher de la Croupe Draconique", "Caverne des Nuages sans fin", 10),
    MapLink("Rocher de la Croupe Draconique", "Khôlkozia", 30),
    MapLink("Khôlkozia", "Fei Tôro Choh", 20),
    MapLink("Mont Stepper", "Tombe d’Arno", 5),
    MapLink("Mont Stepper", "Nosferathon", 5),
    MapLink("Nosferathon", "Litière Ville", 10),
    MapLink("Litière Ville", "Garde Froide", 20)
)
private val restrictedMapPlaces = setOf(
    "Île des poids guidés", "Antre de Mollefesse", "Falaises Olympiques",
    "Ruines du Golem", "Temple de la Prophétie", "Caverne des Nuages sans fin"
)

// Chapitres principaux de la campagne, sommaire du livre p. 218-219.
// Les contenus narratifs et les exercices restent dans le livre papier : on enregistre la progression.
private data class StoryChapter(
    val title: String, val page: Int, val restricted: Boolean = false,
    val place: String? = title, val grants: List<String> = emptyList(),
    val companions: List<String> = emptyList(), val loot: List<String> = emptyList()
)
private val storyChapters = listOf(
    StoryChapter("Fontaria", 68),
    StoryChapter("Entrepôts Shaker+©", 70, place = "Entrepôts Shaker+"),
    StoryChapter("Tique-Couenne", 72, companions = listOf("Oracle de Tasmina")),
    StoryChapter("Nosferathon", 74, grants = listOf("Salle secrète de l’Oasis"), companions = listOf("Igor")),
    StoryChapter("Omega Beach", 76, companions = listOf("Bulker Fruité")),
    StoryChapter("Garde Froide", 78),
    StoryChapter("Litière Ville", 80),
    StoryChapter("Mont Stepper", 82),
    StoryChapter("Tombe d’Arno", 84, grants = listOf("Île des poids guidés")),
    StoryChapter("Temple de Tasmina", 86),
    StoryChapter("Plaine du Fitland", 88),
    StoryChapter("Sud du Fitland", 90),
    StoryChapter("Forêt de Brise Mollets", 92, companions = listOf("La Fée")),
    StoryChapter("Dévers du Fitland", 94),
    StoryChapter("Dunes des sacs crevés", 96),
    StoryChapter("Erythropoiéta", 98),
    StoryChapter("Oasis Interdite", 100),
    StoryChapter("Rocher de la Croupe Draconique", 102),
    StoryChapter("Khôlkozia", 104),
    StoryChapter("Fei Tôro Choh", 106),
    StoryChapter("Rives de la Mer de Nuages", 108),
    // Ces trois scènes sont situées à Erythropoiéta (livre p.110-114).
    StoryChapter("L’audience du Roi", 110, place = "Erythropoiéta", grants = listOf("Temple de la prophétie")),
    StoryChapter("L’arène", 112, place = "Erythropoiéta", grants = listOf("Ruines du Golem"), companions = listOf("Barde")),
    StoryChapter("Le marché libre", 114, place = "Erythropoiéta"),
    StoryChapter("Île des poids guidés", 116, restricted = true, grants = listOf("Caverne des nuages sans fin"), companions = listOf("Le Gnome Cartographe")),
    StoryChapter("Antre de Mollefesse", 118, restricted = true, companions = listOf("Le Troll Conteur")),
    StoryChapter("Falaises Olympiques", 120, restricted = true, grants = listOf("Antre de Mollefesse")),
    StoryChapter("Temple de la prophétie", 122, restricted = true, place = "Temple de la Prophétie", grants = listOf("Exploration du Temple de Tasmina"), loot = listOf("Shaker vigueur", "Shaker quantique", "Shaker sombre")),
    StoryChapter("Caverne des nuages sans fin", 124, restricted = true, place = "Caverne des Nuages sans fin"),
    StoryChapter("Exploration du Temple de Tasmina", 126, restricted = true, place = "Temple de Tasmina", loot = listOf("Epée de Tasmina")),
    StoryChapter("Ruines du Golem", 128, restricted = true, grants = listOf("Falaises Olympiques"), companions = listOf("Le Paladin et le Quêteur")),
    StoryChapter("Salle secrète de l’Oasis", 130, restricted = true, place = "Oasis Interdite"),
    StoryChapter("La Communauté des Abdos", 132, restricted = true, place = null),
    StoryChapter("L’audience du Khôl", 134, restricted = true, place = "Khôlkozia"),
    StoryChapter("Les ombres de Nosferathon", 136, restricted = true, place = "Nosferathon"),
    StoryChapter("Au-delà de la fin", 138, restricted = true, place = null)
)
// Compagnons de la campagne : noms et pouvoirs issus des chapitres p. 72-129.
// Certaines capacités demandent une action hors téléphone ; elles sont documentées mais pas simulées.
private data class StoryCompanion(val name: String, val source: String, val ability: String, val automatic: Boolean)
private val storyCompanions = listOf(
    StoryCompanion("Oracle de Tasmina", "Tique-Couenne · p. 73", "Après une Prière à votre divinité : dégâts ×2 pour un affrontement, ou quitter un lieu pour bénéficier de l'effet pendant un déplacement.", true),
    StoryCompanion("Igor", "Nosferathon · p. 75", "Donne accès à la Salle secrète de l’Oasis ; aucun pouvoir de combat n'est indiqué.", true),
    StoryCompanion("Bulker Fruité", "Omega Beach · p. 76", "Neutralise un adversaire en discutant avec lui pendant un voyage entre deux zones.", true),
    StoryCompanion("La Fée", "Forêt de Brise Mollets · p. 93", "Téléporte une fois dans toute la campagne vers une zone accessible de votre choix.", true),
    StoryCompanion("Barde", "L’arène · p. 113", "Divise par deux une distance entre zones lorsque vous écoutez de la musique ; non cumulée avec les raccourcis de classe (convention explicite).", true),
    StoryCompanion("Le Gnome Cartographe", "Île des poids guidés · p. 117", "Donne accès à la Caverne des nuages sans fin.", true),
    StoryCompanion("Le Troll Conteur", "Antre de Mollefesse · p. 119", "Multiplie par deux la fréquence des rencontres (un jet tous les 3 km plutôt que 6).", true),
    StoryCompanion("Le Paladin et le Quêteur", "Ruines du Golem · p. 129", "Après une Prière avant de quitter une zone, retirez 2 au dé de quantité d'une rencontre choisie pendant le prochain voyage.", true)
)
private fun availableCompanions(s: AppState): Set<String> =
    (s.manualCompanions + storyChapters.filter { it.title in s.completedChapters }.flatMap { it.companions })
        .filter { it !in s.dismissedCompanions }.toSet()

private fun setCompanionPresence(s: AppState, name: String, acquired: Boolean): AppState {
    if (storyCompanions.none { it.name == name }) return s
    val manual = if (acquired) (s.manualCompanions + name).distinct() else s.manualCompanions - name
    val dismissed = if (acquired) s.dismissedCompanions - name else (s.dismissedCompanions + name).distinct()
    return s.copy(manualCompanions = manual, dismissedCompanions = dismissed,
        oracleJourneyActive = if (!acquired && name == "Oracle de Tasmina") false else s.oracleJourneyActive,
        oracleCombatActive = if (!acquired && name == "Oracle de Tasmina") false else s.oracleCombatActive,
        paladinJourneyArmed = if (!acquired && name == "Le Paladin et le Quêteur") false else s.paladinJourneyArmed,
        companionBardJourney = if (!acquired && name == "Barde") false else s.companionBardJourney,
        log = (listOf("Compagnon $name : ${if (acquired) "présence confirmée d'après le livre" else "retiré de l'équipe"} (suivi manuel).") + s.log).take(100))
}

// Une seule attribution d'objets par chapitre, même après annulation et revalidation.
private fun claimChapterLoot(s: AppState, title: String): AppState {
    val chapter = storyChapters.firstOrNull { it.title == title } ?: return s
    if (title !in s.completedChapters || title in s.claimedChapterLoot || chapter.loot.isEmpty() ||
        s.destinationKm > 0.0 || s.lastEncounter != null) return s
    return s.copy(inventory = s.inventory + chapter.loot,
        claimedChapterLoot = (s.claimedChapterLoot + title).distinct(),
        log = (listOf("Récompense p. ${chapter.page} récupérée : ${chapter.loot.joinToString()}. Une seule fois ; les effets non implémentés restent manuels.") + s.log).take(100))
}

private fun useBulkerCompanion(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if ("Bulker Fruité" !in availableCompanions(s) || s.destinationKm <= 0.0 ||
        e.safe || e.bulkerUsed || e.quantity < 1 || encounterHp(e).any { it != e.hp }) return s
    val hp = encounterHp(e).dropLast(1)
    val remaining = hp.size
    if (remaining == 0) return s.copy(lastEncounter = null, meditationActive = false, vigorActive = false,
        quantumMode = "", illusionActive = false, warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
        log = (listOf("Bulker Fruité a neutralisé l'unique adversaire en discutant avec lui : rencontre résolue sans combat ni butin (p. 76).") + s.log).take(100))
    val reward = (e.reward.toLong() * remaining / e.quantity).toInt()
    return s.copy(lastEncounter = e.copy(quantity = remaining, remainingHp = hp.sum(), enemyHp = hp,
        reward = reward, bulkerUsed = true), log =
        (listOf("Bulker Fruité : 1 adversaire neutralisé par discussion ; $remaining restant(s), butin ajusté à $reward PO (p. 76).") + s.log).take(100))
}

private fun fairyDestinations(s: AppState): List<String> {
    val access = availableChapterAccess(s)
    return fitlandPlaces.filter { place -> place != s.character.location &&
        (place !in restrictedMapPlaces || storyChapters.any { it.place == place && it.title in access }) }
}
private fun useFairyTeleport(s: AppState, target: String): AppState {
    if ("La Fée" !in availableCompanions(s) || s.fairyTeleportUsed || s.destinationKm > 0.0 ||
        s.lastEncounter != null || target !in fairyDestinations(s)) return s
    return s.copy(character = s.character.copy(location = target), fairyTeleportUsed = true, chevalierProtectionActive = false,
        visitedPlaces = (s.visitedPlaces + s.character.location + target).distinct(),
        routeStops = emptyList(), routeLegKm = emptyList(), routeLegIndex = 0,
        log = (listOf("La Fée : téléportation UNIQUE de la campagne depuis ${s.character.location} vers $target (p. 93). Aucun km, aucune rencontre, chapitre non validé.") + s.log).take(100))
}

private fun availableChapterAccess(s: AppState): Set<String> =
    (s.manualChapterAccess + storyChapters.filter { it.title in s.completedChapters }.flatMap { it.grants }).toSet()

private fun canFinishChapter(s: AppState, chapter: StoryChapter): Boolean =
    (chapter.place == null || s.character.location == chapter.place) &&
    (s.destinationKm <= 0.0 && s.lastEncounter == null) &&
    (!chapter.restricted || chapter.title in availableChapterAccess(s))

private fun setChapterCompleted(s: AppState, title: String, completed: Boolean): AppState {
    val chapter = storyChapters.firstOrNull { it.title == title } ?: return s
    if (completed && !canFinishChapter(s, chapter)) return s
    val updated = if (completed) (s.completedChapters + title).distinct() else s.completedChapters - title
    return s.copy(completedChapters = updated, log =
        (listOf("Chapitre « $title » (p. ${chapter.page}) ${if (completed) "confirmé terminé" else "validation annulée"} par le joueur. Les compagnons et accès explicitement accordés sont recalculés ; vérifiez le livre pour les autres conséquences.") + s.log).take(100))
}

private fun confirmChapterAccess(s: AppState, title: String, acquired: Boolean): AppState {
    val chapter = storyChapters.firstOrNull { it.title == title && it.restricted } ?: return s
    val newManual = if (acquired) (s.manualChapterAccess + title).distinct() else s.manualChapterAccess - title
    return s.copy(manualChapterAccess = newManual,
        log = (listOf("Accès « ${chapter.title} » ${if (acquired) "confirmé obtenu dans le livre" else "retiré du carnet"} (suivi manuel).") + s.log).take(100))
}

@Composable private fun CompanionsScreen(
    state: AppState, onBack: () -> Unit, onCompanionChange: (String, Boolean) -> Unit
) {
    val owned = availableCompanions(state)
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("🤝 Compagnons d'aventure", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFEBC582)) }
        item { OutlinedButton(onClick = onBack) { Text("← Retour à l'aventure") } }
        item { Text("${owned.size} / ${storyCompanions.size} compagnons recrutés · Livre p. 47 et chapitres p. 73-129") }
        item { Text("Le carnet déduit les compagnons des chapitres terminés. Utilisez la confirmation manuelle seulement si vous les avez déjà recrutés dans une sauvegarde ancienne, ou si le récit vous impose de modifier votre groupe.", style = MaterialTheme.typography.bodySmall) }
        items(storyCompanions, key = { it.name }) { companion ->
            val present = companion.name in owned
            val chapterEarned = storyChapters.any { it.title in state.completedChapters && companion.name in it.companions }
            Card(border = BorderStroke(1.dp, if (present) Color(0xFF4A9C73) else Color(0xFF575460))) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${if (present) "✅" else "○"} ${companion.name}", style = MaterialTheme.typography.titleMedium)
                    Text(companion.source, style = MaterialTheme.typography.labelMedium, color = Color(0xFFEBC582))
                    Text(companion.ability)
                    Text(if (companion.automatic) "Pouvoir ou effet suivi dans la version actuelle." else "Effet à appliquer manuellement selon le livre.", style = MaterialTheme.typography.bodySmall)
                    if (present) {
                        OutlinedButton(onClick = { onCompanionChange(companion.name, false) }) { Text("Retirer du groupe") }
                    } else {
                        OutlinedButton(onClick = { onCompanionChange(companion.name, true) }) { Text("Confirmer son recrutement dans le livre") }
                    }
                    if (chapterEarned) Text("Chapitre d'obtention validé. Un retrait manuel prévaut jusqu'à votre nouvelle confirmation.", style = MaterialTheme.typography.bodySmall)
                    if (companion.name == "La Fée" && present) Text(if (state.fairyTeleportUsed) "✨ Téléportation unique déjà consommée." else "✨ Téléportation unique disponible depuis Aventure.")
                }
            }
        }
        item { Text("✨ Oracle : Prière avant départ = dégâts ×2 pendant le voyage ; Prière avant affrontement = dégâts ×2 pour le combat. 🎵 Barde compagnon : musique confirmée avant départ, distance ÷2. 🛡️ Paladin/Quêteur : Prière avant départ, dé de quantité −2 pour une rencontre choisie lors de ce voyage. Aucun cumul de deux raccourcis.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable private fun CampaignChapters(
    state: AppState, onBack: () -> Unit,
    onCompletionChange: (String, Boolean) -> Unit,
    onAccessChange: (String, Boolean) -> Unit,
    onClaimLoot: (String) -> Unit
) {
    var category by remember { mutableStateOf("Sur place") }
    val access = availableChapterAccess(state)
    val current = state.character.location
    val completedCount = storyChapters.count { it.title in state.completedChapters }
    val visited = (state.visitedPlaces + current).distinct()
    val visible = storyChapters.filter { chapter -> when (category) {
        "Sur place" -> chapter.place == current
        "Accès restreint" -> chapter.restricted
        "Terminés" -> chapter.title in state.completedChapters
        else -> true
    } }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("📖 Carnet de campagne", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineSmall) }
        item { OutlinedButton(onClick = onBack) { Text("← Retour à l'aventure") } }
        item { Text("📍 $current · $completedCount / ${storyChapters.size} chapitres validés · ${visited.size} lieu(x) visité(s)") }
        item { LinearProgressIndicator(progress = { completedCount.toFloat() / storyChapters.size }, modifier = Modifier.fillMaxWidth()) }
        item { Text("Index officiel des chapitres (sommaire p. 218-219). Les pages indiquent où lire et effectuer les épreuves : l'application ne remplace pas le livre.", style = MaterialTheme.typography.bodySmall) }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("Sur place", "Tous", "Accès restreint", "Terminés").forEach { mode ->
                    FilterChip(selected = category == mode, onClick = { category = mode }, label = { Text(mode) })
                }
            }
        }
        if (visible.isEmpty()) item { Text(if (category == "Sur place") "Aucun chapitre principal associé à cette position. Consultez le livre pour les scènes spéciales." else "Aucun chapitre pour ce filtre.") }
        items(visible, key = { it.page }) { chapter ->
            val done = chapter.title in state.completedChapters
            val unlocked = !chapter.restricted || chapter.title in access
            val here = chapter.place == current
            val canFinish = canFinishChapter(state, chapter)
            Card(border = BorderStroke(1.dp, if (done) Color(0xFF4A9C73) else if (unlocked) Color(0xFF97784A) else Color(0xFF575460))) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("${if (done) "✅" else if (chapter.restricted && !unlocked) "🔒" else "📜"} ${chapter.title}", style = MaterialTheme.typography.titleMedium)
                    Text("Livre p. ${chapter.page} · ${if (chapter.restricted) "Accès restreint" else "Accès libre"}", style = MaterialTheme.typography.bodySmall)
                    Text(if (chapter.place == null) "Scène narrative : vérifiez la situation dans le livre avant validation."
                         else if (here) "📍 Vous êtes dans la zone de ce chapitre."
                         else "📍 Zone : ${chapter.place} ${if (chapter.place in visited) "· déjà visitée" else "· à rejoindre"}",
                         style = MaterialTheme.typography.bodySmall)
                    if (chapter.restricted && !unlocked) {
                        Text("Accès non encore enregistré. Vérifiez dans le livre que votre personnage l'a obtenu avant de cocher.", style = MaterialTheme.typography.bodySmall)
                    }
                    if (chapter.restricted) {
                        val manual = chapter.title in state.manualChapterAccess
                        if (manual) {
                            TextButton(onClick = { onAccessChange(chapter.title, false) }) { Text("Retirer l'accès confirmé manuellement") }
                        } else if (!unlocked) {
                            OutlinedButton(onClick = { onAccessChange(chapter.title, true) }) { Text("J'ai obtenu cet accès dans le livre") }
                        } else {
                            Text("🔑 Accès obtenu grâce à un chapitre déjà terminé.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF89C9A1))
                        }
                    }
                    if (chapter.grants.isNotEmpty()) Text("🔑 Accès obtenu après validation : ${chapter.grants.joinToString()} (livre p. ${chapter.page}).", style = MaterialTheme.typography.bodySmall)
                    if (chapter.companions.isNotEmpty()) Text("🤝 Compagnon(s) obtenu(s) : ${chapter.companions.joinToString()} (effets détaillés dans le carnet des compagnons).", style = MaterialTheme.typography.bodySmall)
                    if (chapter.loot.isNotEmpty()) {
                        Text("🎒 Objets indiqués par le livre : ${chapter.loot.joinToString()}.", style = MaterialTheme.typography.bodySmall)
                        if (done && chapter.title !in state.claimedChapterLoot) {
                            OutlinedButton(onClick = { onClaimLoot(chapter.title) }) { Text("Récupérer ces objets (une fois)") }
                        } else if (chapter.title in state.claimedChapterLoot) {
                            Text("✅ Objets récupérés dans le sac ; pas de double attribution après revalidation.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (done) {
                        OutlinedButton(onClick = { onCompletionChange(chapter.title, false) }) { Text("Annuler la validation") }
                    } else {
                        Button(onClick = { onCompletionChange(chapter.title, true) }, enabled = canFinish,
                            modifier = Modifier.fillMaxWidth()) { Text("J'ai terminé les épreuves du chapitre") }
                        if (!canFinish) Text("Validation indisponible : ${when {
                            state.destinationKm > 0.0 -> "terminez le trajet en cours"
                            state.lastEncounter != null -> "résolvez la rencontre"
                            chapter.restricted && !unlocked -> "obtenez l'accès dans le livre"
                            !here && chapter.place != null -> "rendez-vous à ${chapter.place}"
                            else -> "conditions à vérifier dans le livre"
                        }}.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item { Text("Important : un passage pendant un trajet ne valide aucun chapitre. Les accès et compagnons explicitement indiqués sont suivis automatiquement à la confirmation des chapitres. Certains objets peuvent être récupérés une seule fois. Les épreuves et autres effets restent à vérifier dans le livre. Les épreuves sont à réaliser selon le livre.", style = MaterialTheme.typography.bodySmall) }
    }
}

private data class MapRoute(val places: List<String>, val km: Int)
private fun mapNeighbors(place: String): List<Pair<String, Int>> = officialMapLinks.mapNotNull { link ->
    when (place) {
        link.from -> link.to to link.km
        link.to -> link.from to link.km
        else -> null
    }
}.sortedBy { it.first }

// Dijkstra : renvoie le plus court trajet uniquement parmi les tronçons transcrits.
// Les chapitres à cadenas ne servent pas de raccourcis de transit.
private fun mapRoute(origin: String, target: String): MapRoute? {
    if (origin == target) return MapRoute(listOf(origin), 0)
    if (origin !in fitlandPlaces || target !in fitlandPlaces) return null
    val distances = mutableMapOf(origin to 0)
    val previous = mutableMapOf<String, String>()
    val visited = mutableSetOf<String>()
    while (true) {
        val next = distances.filterKeys { it !in visited }.minByOrNull { it.value }?.key ?: break
        if (next == target) break
        visited.add(next)
        if (next in restrictedMapPlaces && next != origin) continue
        for ((neighbor, km) in mapNeighbors(next)) {
            if (neighbor in visited) continue
            val candidate = distances.getValue(next) + km
            if (candidate < (distances[neighbor] ?: Int.MAX_VALUE)) {
                distances[neighbor] = candidate
                previous[neighbor] = next
            }
        }
    }
    val result = distances[target] ?: return null
    val path = mutableListOf(target)
    while (path.last() != origin) {
        path.add(previous[path.last()] ?: return null)
    }
    return MapRoute(path.asReversed(), result)
}

// Chaque étape dispose d'une distance EFFECTIVE (déjà réduite pour Éclaireur/Barde).
// Un itinéraire provenant de la v1.22 reste un trajet manuel si ces champs sont absents.
private fun validGuidedRoute(s: AppState): Boolean =
    !s.retreatActive && s.destinationKm > 0.0 && s.routeStops.size >= 2 &&
        s.routeLegKm.size == s.routeStops.size - 1 &&
        s.routeLegIndex in s.routeLegKm.indices &&
        s.routeStops.last() == s.destination &&
        s.routeStops[s.routeLegIndex] == s.character.location &&
        s.routeLegKm.all { it > 0.0 && it.isFinite() } &&
        kotlin.math.abs(s.routeLegKm.sum() - s.destinationKm) < 0.001

private fun routeLegStart(s: AppState): Double = s.routeLegKm.take(s.routeLegIndex).sum()
private fun nextRouteEnd(s: AppState): Double = s.routeLegKm.take(s.routeLegIndex + 1).sum()
private fun hasPendingWaypoint(s: AppState): Boolean = validGuidedRoute(s) &&
    s.routeLegIndex < s.routeLegKm.lastIndex && s.distance + 0.00001 >= nextRouteEnd(s)

private fun confirmRouteCheckpoint(s: AppState): AppState {
    if (s.lastEncounter != null || !hasPendingWaypoint(s)) return s
    val reached = s.routeStops[s.routeLegIndex + 1]
    return s.copy(character = s.character.copy(location = reached), routeLegIndex = s.routeLegIndex + 1,
        visitedPlaces = (s.visitedPlaces + s.character.location + reached).distinct(),
        log = (listOf("Passage à $reached (${s.routeLegIndex + 1}/${s.routeLegKm.size}) ; chapitre NON validé, trajet poursuivi vers ${s.destination}.") + s.log).take(100))
}

@Composable private fun OfficialMapViewer(onDismiss: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color(0xFF97784A))) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🗺️ Milieu du Disque-Fonte · p. 51", style = MaterialTheme.typography.titleMedium)
                Text("Pincez pour agrandir, glissez pour vous déplacer. Les cadenas signalent un accès restreint.", style = MaterialTheme.typography.bodySmall)
                Box(Modifier.fillMaxWidth().height(380.dp).clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFECEAE6))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 5f)
                            scale = newScale
                            if (newScale <= 1.01f) { offsetX = 0f; offsetY = 0f }
                            else {
                                val bound = (newScale - 1f) * size.width.coerceAtLeast(size.height) * 0.5f
                                offsetX = (offsetX + pan.x).coerceIn(-bound, bound)
                                offsetY = (offsetY + pan.y).coerceIn(-bound, bound)
                            }
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.carte_disque_fonte),
                        contentDescription = "Carte officielle du Disque-Fonte, livre page 51",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().graphicsLayer {
                            scaleX = scale; scaleY = scale
                            translationX = offsetX; translationY = offsetY
                        }
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { scale = 1f; offsetX = 0f; offsetY = 0f }) { Text("Réinitialiser le zoom") }
                    Button(onClick = onDismiss) { Text("Fermer") }
                }
            }
        }
    }
}

@Composable private fun Adventure(
    state: AppState,
    onOpenChapters: () -> Unit,
    onOpenCompanions: () -> Unit,
    onTravel: (Double) -> Unit,
    stepSnapshot: StepSnapshot,
    stepNotice: String,
    stepBusy: Boolean,
    stepSensorAvailable: Boolean,
    onStartSteps: () -> Unit,
    onPauseSteps: () -> Unit,
    onResumeSteps: () -> Unit,
    onStopSteps: () -> Unit,
    onWarrior: () -> Unit,
    onResolve: (Int, Boolean, Boolean) -> Unit,
    onMageSpell: (String, Int) -> Unit,
    onWeaponAttack: (String, Int, Boolean, Int?, Boolean, Boolean, Boolean, Boolean) -> Unit,
    onSkip: () -> Unit,
    onChevalierEscort: () -> Unit,
    onAcceptShaker: (String) -> Unit,
    onPayGuards: () -> Unit,
    onFlee: (Boolean) -> Unit,
    onHunt: () -> Unit,
    onBulker: () -> Unit,
    onOracleCombat: () -> Unit,
    onPaladin: () -> Unit,
    onFairyTeleport: (String) -> Unit,
    onMystic: (String, Int, Int) -> Unit,
    onArmorReroll: () -> Unit,
    onArmorChoose: (Boolean) -> Unit,
    onCavalier: (String) -> Unit,
    onMeditate: () -> Unit,
    onUsePersuasion: () -> Unit,
    onUseConsumable: (String, String) -> Unit,
    onPlan: (String, Double, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) -> Unit,
    onCheckpoint: () -> Unit,
    onArrive: () -> Unit
) {
    var selectedPlace by remember(state.character.location) {
        mutableStateOf(fitlandPlaces.firstOrNull { it != state.character.location } ?: "Fontaria")
    }
    var expanded by remember { mutableStateOf(false) }
    var distanceInput by remember(state.character.location) { mutableStateOf("") }
    var followMap by remember(state.character.location) { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }
    var teleportExpanded by remember { mutableStateOf(false) }
    var fairyExpanded by remember { mutableStateOf(false) }
    var fairyTarget by remember { mutableStateOf("") }
    var fairyConfirm by remember { mutableStateOf(false) }
    var darkShakerConfirm by remember(state.lastEncounter?.roll, state.lastEncounter?.contextDie) { mutableStateOf(false) }
    var teleportTarget by remember(state.character.location) { mutableStateOf(mapNeighbors(state.character.location).firstOrNull()?.first.orEmpty()) }
    var scoutChosen by remember { mutableStateOf(false) }
    var bardAudacious by remember { mutableStateOf(false) }
    var companionMusic by remember(state.character.location) { mutableStateOf(false) }
    var oraclePrayerJourney by remember(state.character.location) { mutableStateOf(false) }
    var paladinPrayerJourney by remember(state.character.location) { mutableStateOf(false) }
    var oracleCombatPrayer by remember(state.lastEncounter?.roll, state.lastEncounter?.contextDie) { mutableStateOf(false) }
    var mysticKind by remember(state.lastEncounter?.roll, state.lastEncounter?.name) { mutableStateOf("Contexte") }
    var selectedTypeDie by remember(state.lastEncounter?.roll, state.lastEncounter?.name) { mutableIntStateOf(0) }
    var monkPrayerConfirmed by remember(state.lastEncounter?.roll, state.lastEncounter?.contextDie) { mutableStateOf(false) }
    var damageInput by remember { mutableStateOf("") }
    var mageExerciseDone by remember(state.lastEncounter?.roll, state.lastEncounter?.contextDie) { mutableStateOf(false) }
    var mageTarget by remember(state.lastEncounter?.roll, state.lastEncounter?.name) { mutableIntStateOf(0) }
    var selectedWeapon by remember { mutableStateOf("") }
    var rangedTarget by remember { mutableIntStateOf(-1) }
    var attackCount by remember { mutableStateOf("1") }
    var pattesUsed by remember { mutableStateOf(false) }
    var supportConfirmed by remember { mutableStateOf(false) }
    var armorConfirmed by remember { mutableStateOf(false) }
    var heroicAllyConfirmed by remember(state.encounters) { mutableStateOf(false) }
    var crownRealPlayerConfirmed by remember(state.encounters) { mutableStateOf(false) }
    var guardPaymentConfirm by remember(state.encounters) { mutableStateOf(false) }
    var bulkerChosenShaker by remember(state.encounters) { mutableStateOf("Shaker vigueur") }
    if (state.lastEncounter?.armorSecondDie in 1..6) {
        val e = state.lastEncounter!!
        val first = ArmorRules.effectiveContext(e.contextDie, e.contextBonus)
        val second = ArmorRules.effectiveContext(e.armorSecondDie, e.contextBonus)
        AlertDialog(onDismissRequest = {},
            title = { Text("🛡️ Armure respirante — choisir le contexte") },
            text = { Text("Premier jet : ${e.contextDie} + ${e.contextBonus} → $first.\n" +
                "Second jet : ${e.armorSecondDie} + ${e.contextBonus} → $second.\n" +
                "Le livre p. 164 autorise à conserver le résultat que tu préfères. Le choix est définitif.") },
            confirmButton = { TextButton(onClick = { onArmorChoose(true) }) { Text("Garder le deuxième ($second)") } },
            dismissButton = { TextButton(onClick = { onArmorChoose(false) }) { Text("Garder le premier ($first)") } })
    }
    val novice = state.character.profileType == "novice"
    val unit = if (novice) "min" else "km"
    val pending = state.lastEncounter != null
    val mapTrip = mapRoute(state.character.location, selectedPlace)
    val mapAdjacent = mapNeighbors(state.character.location)
    if (showMap) OfficialMapViewer { showMap = false }
    if (fairyConfirm) {
        AlertDialog(onDismissRequest = { fairyConfirm = false },
            title = { Text("✨ Dernière vérification — la Fée") },
            text = { Text("Téléportation vers $fairyTarget : une seule fois pendant TOUTE la campagne. Cette action est irréversible et ne valide aucun chapitre. Confirmer ?") },
            confirmButton = { TextButton(onClick = { onFairyTeleport(fairyTarget); fairyConfirm = false }) { Text("Téléporter une fois") } },
            dismissButton = { TextButton(onClick = { fairyConfirm = false }) { Text("Annuler") } })
    }
    if (guardPaymentConfirm && state.lastEncounter?.name == "Gardes du Fitland") {
        AlertDialog(onDismissRequest = { guardPaymentConfirm = false },
            title = { Text("🛡️ Payer les Gardes du Fitland ?") },
            text = { Text("Le livre p. 43 exige la totalité des PO pour éviter le combat. Tu vas perdre ${state.character.po} PO sans recevoir de butin. Ce choix est définitif. Confirmer ?") },
            confirmButton = { TextButton(onClick = { guardPaymentConfirm = false; onPayGuards() }) { Text("Payer tous mes PO") } },
            dismissButton = { TextButton(onClick = { guardPaymentConfirm = false }) { Text("Annuler") } })
    }
    if (darkShakerConfirm && state.lastEncounter != null && !state.lastEncounter.safe && "Shaker sombre" in state.inventory) {
        val targets = encounterHp(state.lastEncounter)
        val effect = groupSplashDamage(targets, 700)
        AlertDialog(onDismissRequest = { darkShakerConfirm = false },
            title = { Text("🧪 Utiliser le Shaker sombre ?") },
            text = { Text("Objet unique du livre (p. 173), non achetable. " +
                "Cette adaptation inflige 700 dégâts à distance à CHACUN des ${targets.count { it > 0 }} adversaires encore debout " +
                "(${effect.damageDealt} PV effectifs au total). Le shaker sera définitivement consommé. Confirmer ?") },
            confirmButton = { TextButton(onClick = { darkShakerConfirm = false; onUseConsumable("Shaker sombre", "") }) { Text("Consommer") } },
            dismissButton = { TextButton(onClick = { darkShakerConfirm = false }) { Text("Annuler") } })
    }
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("🗺️ Expédition", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineSmall) }
        item { OutlinedButton(onClick = { showMap = true }, modifier = Modifier.fillMaxWidth()) { Text("🗺️ Ouvrir la carte officielle (zoom)") } }
        item { Button(onClick = onOpenChapters, modifier = Modifier.fillMaxWidth()) { Text("📖 Chapitres, accès et progression") } }
        item { OutlinedButton(onClick = onOpenCompanions, modifier = Modifier.fillMaxWidth()) { Text("🤝 Compagnons (${availableCompanions(state).size})") } }
        item { Text("Position actuelle : ${state.character.location}") }
        item { Text("Classe : ${state.character.className} · Niveau ${state.character.level}") }
        if ("Le Troll Conteur" in availableCompanions(state)) item {
            Text("📜 Le Troll Conteur voyage avec vous : jet de rencontre tous les 3 km cumulés (livre p. 119).", color = Color(0xFFEBC582))
        }
        if (state.character.specialAbility.isNotBlank()) item { Text("Mouvement personnel : ${state.character.specialAbility}", style = MaterialTheme.typography.bodySmall) }
        if (state.character.profileType == "advanced" && state.character.className == "Acrobate") item { Text("Acrobate (livre p. 41) : +2 au jet de contexte ; en cas de fuite, retour au départ OU poursuite vers la destination.") }
        if (state.character.profileType == "advanced" && state.character.className == "Chasseur") item { Text("Chasseur (livre p. 41) : peut réduire de 1 le dé de quantité d’une rencontre choisie pendant un déplacement.") }
        if (state.character.profileType == "advanced" && state.character.className == "Éclaireur") item { Text("Éclaireur : distance divisée par 2 pour un déplacement, deux fois par jour. Utilisations restantes : ${2 - dailyState(state).scoutUses}.") }
        if (state.character.profileType == "advanced" && state.character.className == "Mage") item { Text("Mage (p. 41–42) : modifier de ±1 un dé de type, de quantité ou de contexte avant le combat.") }
        if (state.character.profileType == "advanced" && state.character.className == "Moine") item { Text("Moine (p. 41–42) : modifier de ±1 ou ±2 un dé de type, de quantité ou de contexte contre une Prière.") }
        if (state.character.profileType == "advanced" && state.character.className == "Cavalier") item { Text("Cavalier (livre p. 41) : téléportation automatique vers une zone reliée directement par un chemin en pointillés sur la carte p. 51 (une fois par jour).") }
        if (state.character.profileType == "advanced" && state.character.className == "Barde") item { Text("Barde (règle maison solo) : distance ÷2 deux fois par jour. Mode prudent : quantité et butin normaux ; mode audacieux : quantité ×2 et butin total ×2,5. Utilisations restantes : ${2 - dailyState(state).bardUses}.") }
        if (state.character.profileType == "advanced" && state.character.className == "Guerrier") item { Text("Guerrier : double les dégâts d’une rencontre contre une Prière, une fois par jour. ${if (dailyState(state).warriorUsed) "Déjà utilisée aujourd’hui." else "Disponible."}") }
        if (state.retreatActive) item { Text("RETOUR DE FUITE : rejoignez ${state.retreatOrigin}. Aucune nouvelle rencontre pendant ce trajet.", color = MaterialTheme.colorScheme.error) }
        item { Text("Selon le livre (p. 41), les trajets sont réalisés réellement à pied, à vélo ou par un autre moyen non motorisé. Pour un novice : 1 km de carte = 1 minute d'endurance.") }
        if (state.destinationKm <= 0.0) {
            if ("La Fée" in availableCompanions(state)) item {
                Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("✨ La Fée — téléportation de campagne", style = MaterialTheme.typography.titleMedium)
                        Text(if (state.fairyTeleportUsed) "Déjà utilisée : une seule fois pendant toute l'aventure (livre p. 93)."
                             else "Choisissez une zone accessible, sans distance ni rencontre. Cette capacité est consommée définitivement.")
                        if (!state.fairyTeleportUsed && !pending) {
                            val destinations = fairyDestinations(state)
                            Box {
                                OutlinedButton(onClick = { fairyExpanded = true }) { Text("Destination : ${fairyTarget.ifBlank { "Choisir" }} ▾") }
                                DropdownMenu(expanded = fairyExpanded, onDismissRequest = { fairyExpanded = false }) {
                                    destinations.forEach { place ->
                                        DropdownMenuItem(text = { Text(place) }, onClick = { fairyTarget = place; fairyExpanded = false })
                                    }
                                }
                            }
                            Button(onClick = { fairyConfirm = true }, enabled = fairyTarget in destinations,
                                modifier = Modifier.fillMaxWidth()) { Text("✨ Utiliser la Fée (confirmation)") }
                        }
                    }
                }
            }
            item { Text("Préparer un trajet", style = MaterialTheme.typography.titleMedium) }
            item {
                Box {
                    OutlinedButton(onClick = { expanded = true }) { Text("Destination : $selectedPlace ▾") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        fitlandPlaces.filter { it != state.character.location }.forEach { place ->
                            DropdownMenuItem(text = { Text(place) }, onClick = {
                                selectedPlace = place
                                val found = mapRoute(state.character.location, place)
                                distanceInput = found?.km?.toString().orEmpty()
                                followMap = found != null && found.km > 0
                                expanded = false
                            })
                        }
                    }
                }
            }
            item {
                Card(border = BorderStroke(1.dp, Color(0xFF6C705A))) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("📍 Itinéraire sur la carte officielle", style = MaterialTheme.typography.titleMedium)
                        if (mapTrip != null && mapTrip.km > 0) {
                            Text("${mapTrip.km} km · ${mapTrip.places.size - 1} tronçon(s)",
                                color = Color(0xFFEBC582), style = MaterialTheme.typography.titleMedium)
                            Text(mapTrip.places.joinToString(" → "))
                            if (mapTrip.places.any { it in restrictedMapPlaces })
                                Text("⚠️ Accès restreint sur cet itinéraire : vérifiez les conditions du chapitre dans le livre.",
                                    color = Color(0xFFF0B44E), style = MaterialTheme.typography.bodySmall)
                            OutlinedButton(onClick = {
                                distanceInput = mapTrip.km.toString()
                                followMap = true
                            }) {
                                Text("Utiliser ${mapTrip.km} km et suivre les étapes")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = followMap && distanceInput.replace(',', '.').toDoubleOrNull() == mapTrip.km.toDouble(),
                                    onCheckedChange = { followMap = it },
                                    enabled = distanceInput.replace(',', '.').toDoubleOrNull() == mapTrip.km.toDouble())
                                Text("Suivre chaque étape du trajet sur la carte", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Chaque passage intermédiaire sera confirmé sans valider le chapitre correspondant. Rencontres tous les 6 km cumulés (ou 3 km avec le Troll Conteur).",
                                style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Aucun trajet relié par les chemins transcrits depuis cette position. Vérifiez le livre p. 51 et renseignez la distance manuellement.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Calcul du plus court itinéraire parmi les liaisons relevées. Les cadenas restent soumis aux conditions des chapitres ; traverser une zone ne valide pas automatiquement son chapitre.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { OutlinedTextField(value = distanceInput, onValueChange = { distanceInput = it; followMap = false },
                label = { Text("Distance du trajet (km, modifiable)") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()) }
            item { Text("En profil novice, 1 km de carte représente 1 minute d'endurance. La saisie manuelle reste disponible.", style = MaterialTheme.typography.bodySmall) }
            if (state.character.profileType == "advanced" && state.character.className == "Cavalier") {
                item {
                    val used = dailyState(state).cavalierUsed
                    val available = !used && !pending && mapAdjacent.isNotEmpty()
                    Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🐎 Téléportation du Cavalier", style = MaterialTheme.typography.titleMedium)
                            Text(if (used) "Pouvoir déjà utilisé aujourd’hui."
                                else "Une fois par jour : une liaison directe en pointillés sur la carte p. 51, sans km ni rencontre.")
                            if (mapAdjacent.isEmpty()) {
                                Text("Aucune liaison directe renseignée pour ${state.character.location} ; consultez le livre. Téléportation non disponible ici.",
                                    color = Color(0xFFF0B44E))
                            } else {
                                Box {
                                    OutlinedButton(onClick = { teleportExpanded = true }, enabled = available) {
                                        Text("Zone adjacente : ${if (mapAdjacent.any { it.first == teleportTarget }) teleportTarget else mapAdjacent.first().first} ▾")
                                    }
                                    DropdownMenu(expanded = teleportExpanded, onDismissRequest = { teleportExpanded = false }) {
                                        mapAdjacent.forEach { (neighbor, length) ->
                                            DropdownMenuItem(text = { Text("$neighbor · $length km") }, onClick = {
                                                teleportTarget = neighbor
                                                teleportExpanded = false
                                            })
                                        }
                                    }
                                }
                                Text("Adjacence vérifiée automatiquement sur le graphe de la carte officielle.",
                                    style = MaterialTheme.typography.bodySmall)
                                val target = teleportTarget.takeIf { place -> mapAdjacent.any { it.first == place } }
                                    ?: mapAdjacent.first().first
                                Button(onClick = { onCavalier(target) }, enabled = available,
                                    modifier = Modifier.fillMaxWidth()) { Text("Téléporter à $target (1/jour)") }
                            }
                        }
                    }
                }
            }
            if (state.character.profileType == "advanced" && state.character.className in listOf("Éclaireur", "Barde")) item {
                val bard = state.character.className == "Barde"
                val available = if (bard) dailyState(state).bardUses < 2 else dailyState(state).scoutUses < 2
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = scoutChosen, enabled = available, onCheckedChange = { scoutChosen = it })
                    Text(if (bard) "Barde (règle maison) : demi-distance, 2 fois/jour (${2 - dailyState(state).bardUses} restantes)" else "Éclaireur : utiliser 1 charge quotidienne pour réduire ce trajet de moitié")
                }
                if (bard && scoutChosen && available) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = bardAudacious, onCheckedChange = { bardAudacious = it })
                        Text("Mode audacieux : ennemis ×2, PO ×2,5 (règle maison)")
                    }
                    Text(if (bardAudacious) "Voyage audacieux : récompense = 125 % du butin des ennemis doublés (arrondi à l'entier inférieur)." else "Voyage prudent : ennemis et récompenses normaux.")
                }
                if (scoutChosen && available) Text("Distance à parcourir : ${"%.2f".format((distanceInput.replace(',','.').toDoubleOrNull() ?: 0.0) / 2)} km ${if (bard) "(règle maison)" else "(livre p. 41)"}")
            }
            if ("Barde" in availableCompanions(state)) item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = companionMusic, enabled = !scoutChosen, onCheckedChange = { companionMusic = it })
                    Text("🎵 Barde compagnon (p. 113) : j'écouterai de la musique pendant ce trajet (distance ÷2).")
                }
                if (scoutChosen && companionMusic) Text("Deux raccourcis ne se cumulent pas : le pouvoir de classe prévaut.", style = MaterialTheme.typography.bodySmall)
                if (companionMusic && !scoutChosen) Text("Distance réelle : ${"%.2f".format((distanceInput.replace(',','.').toDoubleOrNull() ?: 0.0) / 2)} $unit")
            }
            if ("Oracle de Tasmina" in availableCompanions(state)) item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = oraclePrayerJourney, onCheckedChange = { oraclePrayerJourney = it })
                    Text("✨ Oracle (p. 73) : j'ai fait la Prière à ma divinité avant de partir ; dégâts ×2 pendant le déplacement.")
                }
            }
            if ("Le Paladin et le Quêteur" in availableCompanions(state)) item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = paladinPrayerJourney, onCheckedChange = { paladinPrayerJourney = it })
                    Text("🛡️ Paladin et Quêteur (p. 129) : j'ai fait une Prière avant de partir ; dé de quantité −2 lors d'une rencontre choisie de ce voyage.")
                }
            }
            item { Button(enabled = (distanceInput.replace(',','.').toDoubleOrNull()?.let { it.isFinite() && it > 0.0 } == true) &&
                selectedPlace != state.character.location && !pending && !state.mageResting,
                onClick = {
                    onPlan(selectedPlace, distanceInput.replace(',','.').toDouble(), scoutChosen, scoutChosen && bardAudacious, followMap,
                        companionMusic, oraclePrayerJourney, paladinPrayerJourney)
                    scoutChosen = false; bardAudacious = false; followMap = false
                    companionMusic = false; oraclePrayerJourney = false; paladinPrayerJourney = false
                },
                modifier = Modifier.fillMaxWidth()) { Text("Démarrer le trajet") } }
        } else {
            if (state.companionBardJourney) item { Text("🎵 Barde compagnon : musique confirmée, trajet à demi-distance.", color = Color(0xFFEBC582)) }
            if (state.oracleJourneyActive) item { Text("✨ Oracle de Tasmina : dégâts ×2 pendant ce déplacement.", color = Color(0xFFEBC582)) }
            if (state.paladinJourneyArmed) item { Text(if (state.paladinJourneyUsed) "🛡️ Paladin/Quêteur : pouvoir de ce voyage déjà utilisé." else "🛡️ Paladin/Quêteur : dé de quantité −2 encore disponible.", color = Color(0xFFEBC582)) }
            item { Text("Destination : ${state.destination} · ${"%.1f".format(state.destinationKm)} $unit") }
            item { LinearProgressIndicator(progress = { (state.distance / state.destinationKm).coerceIn(0.0,1.0).toFloat() }, modifier = Modifier.fillMaxWidth()) }
            item { Text("${"%.3f".format(state.distance)} / ${"%.2f".format(state.destinationKm)} $unit") }
            item {
                StepTrackerPanel(state, stepSnapshot, stepNotice, stepBusy, stepSensorAvailable,
                    onStartSteps, onPauseSteps, onResumeSteps, onStopSteps)
            }
            val guided = validGuidedRoute(state)
            if (guided) {
                val end = nextRouteEnd(state)
                val atWaypoint = hasPendingWaypoint(state)
                item {
                    Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text("🧭 Étapes de l'itinéraire", style = MaterialTheme.typography.titleMedium)
                            Text("Étape ${state.routeLegIndex + 1}/${state.routeLegKm.size} : ${state.routeStops[state.routeLegIndex]} → ${state.routeStops[state.routeLegIndex + 1]}")
                            Text("Jusqu'au prochain arrêt : ${"%.1f".format((end - state.distance).coerceAtLeast(0.0))} $unit")
                            state.routeLegKm.forEachIndexed { i, d ->
                                val marker = when {
                                    i < state.routeLegIndex -> "✅"
                                    i == state.routeLegIndex -> "📍"
                                    else -> "○"
                                }
                                Text("$marker ${state.routeStops[i]} → ${state.routeStops[i + 1]} · ${"%.1f".format(d)} $unit",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Confirmer une étape change la position sur la carte, sans jouer ni valider son chapitre.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (atWaypoint) item {
                    Button(onClick = onCheckpoint, enabled = !pending,
                        modifier = Modifier.fillMaxWidth()) {
                        Text("Confirmer le passage à ${state.routeStops[state.routeLegIndex + 1]}")
                    }
                }
            }
            val canContinue = !pending && !stepBusy && !state.mageResting && state.distance + 0.00001 < state.destinationKm && !hasPendingWaypoint(state)
            if (novice) {
                item { Button(enabled = canContinue,
                    onClick = { onTravel(1.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 1 minute d'endurance") } }
                item { OutlinedButton(enabled = canContinue,
                    onClick = { onTravel(5.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 5 minutes d'endurance") } }
            } else {
                item { Button(enabled = canContinue,
                    onClick = { onTravel(1.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 1 km parcouru") } }
                item { OutlinedButton(enabled = canContinue,
                    onClick = { onTravel(0.1) }, modifier = Modifier.fillMaxWidth()) { Text("+ 100 m") } }
            }
            if (state.distance + 0.00001 >= state.destinationKm && !pending && !state.mageResting && !hasPendingWaypoint(state)) {
                item { Button(onClick = onArrive, modifier = Modifier.fillMaxWidth()) { Text("Valider l'arrivée à ${state.destination}") } }
            }
        }
        if (state.chevalierProtectionActive && state.destinationKm > 0.0) item {
            Text("🛡️ Escorte templiométrique ACTIVE : pas de rencontres jusqu'à ${state.destination} (p. 43). Le podomètre continue jusqu'aux étapes/à l'arrivée.", color = Color(0xFFEBC582))
        }
        item { Text("Progression totale : ${"%.3f".format(state.totalTravelKm)} $unit · Rencontres déclenchées : ${state.encounters}") }
        item { Text("Une rencontre est déclenchée tous les ${if ("Le Troll Conteur" in availableCompanions(state)) 3 else 6} ${unit} cumulés${if ("Le Troll Conteur" in availableCompanions(state)) " (Troll Conteur)" else ""}${if (state.chevalierProtectionActive) " sauf sous escorte" else ""}. Les adversaires et contextes proviennent de la table officielle (p. 42-43). Certains effets spéciaux restent manuels.", style = MaterialTheme.typography.bodySmall) }
        if (state.mageResting && (state.character.className != "Mage" || state.character.profileType != "advanced")) item {
            Button(onClick = { onMageSpell("resume", -1) }, modifier = Modifier.fillMaxWidth()) {
                Text("⏯ Reprendre la partie mise en pause par le sort")
            }
        }
        if (state.character.className == "Mage" && state.character.profileType == "advanced") {
            item { Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("🔮 Magie conventionnelle — Second souffle quantique (p. 176)", style = MaterialTheme.typography.titleMedium)
                    if (MageSpellRules.mayCast("rest", state.character.className, state.character.agility)) {
                        Text("Agilité 2 requise. Dessinez SECONDSOUFFLE avec votre corps. Ce sort permet de suspendre votre partie et de la reprendre plus tard, sans autre bonus.")
                        if (state.mageResting) {
                            Text("⏸ Pause magique enregistrée. Votre trajet et votre combat sont conservés.")
                            Button(onClick = { onMageSpell("resume", -1) }, modifier = Modifier.fillMaxWidth()) { Text("Reprendre la partie") }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = mageExerciseDone, onCheckedChange = { mageExerciseDone = it })
                                Text("J'ai écrit SECONDSOUFFLE avec mon corps")
                            }
                            OutlinedButton(enabled = mageExerciseDone, onClick = {
                                onMageSpell("rest", -1); mageExerciseDone = false
                            }, modifier = Modifier.fillMaxWidth()) { Text("Suspendre la partie (sauvegarde et pause du podomètre)") }
                        }
                    } else Text("Nécessite Agilité 2.")
                }
            } }
        }
        if (state.mageResting) item {
            Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
                Column(Modifier.padding(12.dp)) {
                    Text("⏸ Partie suspendue par Second souffle quantique", style = MaterialTheme.typography.titleMedium)
                    Text("Combat et déplacements conservés. Reprends la partie dans la carte du sort ci-dessus avant de poursuivre.")
                }
            }
        } else state.lastEncounter?.let { e ->
            item { HorizontalDivider() }
            item { Text("⚔️ RENCONTRE — TABLE DU LIVRE", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineSmall) }
            item { Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${e.name} ×${e.quantity} (jet : ${e.roll})", style = MaterialTheme.typography.titleLarge)
                HealthGauge("❤️ Santé totale des ennemis", encounterHp(e).sum(), e.hp * e.quantity)
                Text("🪙 Butin potentiel : ${e.reward} PO")
                if (state.chevalierProtectionActive) Text("🛡️ Escorte templiométrique active : aucune autre rencontre jusqu’à la destination.", color = Color(0xFFEBC582))
                if (e.name == "Chevalier Templiométrique" && e.safe) {
                    Text("Chevalier templiométrique (p. 43) : accepte son escorte pour éviter les rencontres restantes jusqu’à la destination de ce voyage. Aucun PO gagné.")
                    Button(onClick = onChevalierEscort, modifier = Modifier.fillMaxWidth()) { Text("Accepter l’escorte — protéger le reste du trajet") }
                }
                if (e.name == "Bulker Fruité" && e.safe) {
                    Text("Bulker Fruité (p. 43) : reçois un shaker de ton choix, une seule fois pour cette rencontre, sans PO.")
                    val shakerOptions = listOf("Shaker persuasion", "Shaker poison", "Shaker vigueur", "Shaker quantique", "Shaker illusion", "Shaker sombre")
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        shakerOptions.forEach { shaker -> OutlinedButton(onClick = { bulkerChosenShaker = shaker }) {
                            Text(if (bulkerChosenShaker == shaker) "● $shaker" else shaker)
                        } }
                    }
                    Button(onClick = { onAcceptShaker(bulkerChosenShaker) }, modifier = Modifier.fillMaxWidth()) { Text("Recevoir : $bulkerChosenShaker") }
                }
                if (e.name == "Gardes du Fitland" && !e.safe && encounterHp(e).all { it == e.hp }) {
                    Text("Gardes du Fitland (p. 43) : perdre tous tes PO OU les affronter. Tu peux payer avant ta première attaque.")
                    OutlinedButton(onClick = { guardPaymentConfirm = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Éviter le combat : payer mes ${state.character.po} PO")
                    }
                }
                if ("Le Troll Conteur" in availableCompanions(state)) Text("📜 Troll Conteur : fréquence des rencontres ×2, jet tous les 3 km.")
                if (state.bardJourney) {
                    if (e.effect.contains("quantité fixe", ignoreCase = true))
                        Text("Barde audacieux : rencontre à quantité fixe (livre), ni doublement ni bonus de 25 %.", style = MaterialTheme.typography.bodySmall)
                    else
                        Text("Barde audacieux (règle maison) : quantité ×2 et butin +25 % sur la quantité doublée (×2,5 par rapport au butin de base).", style = MaterialTheme.typography.bodySmall)
                }
                Text("Adversaires : ${encounterHp(e).count { it > 0 }} encore debout sur ${e.quantity}")
                encounterHp(e).forEachIndexed { index, hpLeft ->
                    HealthGauge("${if (hpLeft == 0) "💀" else "👹"} ${e.name} n°${index + 1}", hpLeft, e.hp)
                }
                if (state.character.className == "Mage" && state.character.profileType == "advanced" && !e.safe && !state.mageResting) {
                    HorizontalDivider()
                    Text("🔮 SORTS DU MAGE — p. 176", style = MaterialTheme.typography.titleMedium, color = Color(0xFFEBC582))
                    Text("Dessine les lettres du sort avec ton corps avant chaque lancement. Les sorts ne nécessitent pas d'achat.", style = MaterialTheme.typography.bodySmall)
                    val livingTargets = encounterHp(e).indices.filter { encounterHp(e)[it] > 0 }
                    if (livingTargets.isNotEmpty()) {
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            livingTargets.forEach { idx ->
                                OutlinedButton(onClick = { mageTarget = idx }) {
                                    Text("${if (mageTarget == idx) "●" else "○"} ${e.name} n°${idx+1}")
                                }
                            }
                        }
                        Text("Cible choisie : ${e.name} n°${mageTarget + 1}")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = mageExerciseDone, onCheckedChange = { mageExerciseDone = it })
                        Text("J'ai écrit le mot du sort choisi avec mon corps")
                    }
                    if (MageSpellRules.mayCast("force", state.character.className, state.character.agility)) {
                        OutlinedButton(enabled = mageExerciseDone && mageTarget in livingTargets,
                            onClick = { onMageSpell("force", mageTarget); mageExerciseDone = false },
                            modifier = Modifier.fillMaxWidth()) {
                            Text("FORCE — Boule de force : 50 PV à distance sur la cible")
                        }
                        val cardioPossible = e.contextDie in 1..6 && e.armorSecondDie == 0 &&
                            encounterHp(e).all { it == e.hp } && !e.flexibilityScrollUsed
                        OutlinedButton(enabled = mageExerciseDone && cardioPossible,
                            onClick = { onMageSpell("cardio", -1); mageExerciseDone = false },
                            modifier = Modifier.fillMaxWidth()) {
                            Text("CARDIO — Onde de cardio : contexte +1 (avant attaque)")
                        }
                        if (e.mageCardioCasts > 0) Text("Onde de cardio utilisée ${e.mageCardioCasts} fois pendant cette rencontre.", style = MaterialTheme.typography.bodySmall)
                        if (!cardioPossible) Text("Onde de cardio : utilisable avant toute attaque, hors relance d'armure en attente.", style = MaterialTheme.typography.bodySmall)
                    } else Text("Boule de force et Onde de cardio : Agilité 5 requise.")
                    if (MageSpellRules.mayCast("acid", state.character.className, state.character.agility)) {
                        if (e.acidTargets.isNotEmpty()) Text("Cibles acido-basiques : ${e.acidTargets.joinToString { "n°${it + 1}" }} (+50 PV sur chaque attaque qui les atteint).")
                        OutlinedButton(enabled = mageExerciseDone && mageTarget in livingTargets && mageTarget !in e.acidTargets,
                            onClick = { onMageSpell("acid", mageTarget); mageExerciseDone = false },
                            modifier = Modifier.fillMaxWidth()) {
                            Text("ACIDOBASIQUE — Marquer la cible (+50 PV/attaque reçue)")
                        }
                    } else Text("Flèche acido-basique : Agilité 7 requise.")
                }
                if (e.contextDie > 0) {
                    Text("Dé de contexte : ${e.contextDie} + ${e.contextBonus} = ${(e.contextDie + e.contextBonus).coerceAtMost(6)}")
                    if (e.contextWeapon.isNotBlank()) Text("Équipement actif au tirage : ${e.contextWeapon}")
                    if (e.effect.contains("Contexte +2")) Text("Effet Trollympien : +2 au contexte")
                } else Text("Rencontre enregistrée avant v1.3 : jet de contexte initial indisponible.")
                Text("Contexte : ${e.context}")
                Text("Conséquence : ${if (e.context == "Vous êtes piégé !") "${10 * e.quantity} burpees (10 × ${e.quantity} adversaires)" else e.exercise}")
                if (e.context == "Une embuscade !") Text("Attention : la durée d’utilisation des objets est doublée. Pour les armes à durée, chaque tranche doit durer deux fois plus longtemps ; les dégâts par tranche restent identiques.")
                if (e.effect.isNotBlank()) Text("Effet immédiat : ${e.effect}")
                if (e.mysticDescription.isNotBlank()) Text("Pouvoir de classe : ${e.mysticDescription}")
                if (e.contextArmor == "Armure respirante" && "Armure respirante" in state.inventory &&
                    !e.armorRerollUsed && e.contextDie in 1..6 && !e.mysticUsed && !e.flexibilityScrollUsed &&
                    !e.hunterUsed && !e.bulkerUsed && encounterHp(e).all { it == e.hp }) {
                    Text("🛡️ Armure respirante (p. 164) : en portant un sac à dos vide, relance le dé de contexte et choisis l'un des deux résultats.")
                    OutlinedButton(onClick = onArmorReroll, modifier = Modifier.fillMaxWidth()) {
                        Text("Relancer le dé de contexte — Armure respirante")
                    }
                }
                if (e.armorRerollUsed) Text("🛡️ Relance de l'Armure respirante déjà utilisée pour cette rencontre.")
                if (state.character.profileType == "advanced" && state.character.className in listOf("Mage", "Moine") && !e.mysticUsed && encounterHp(e).all { it == e.hp } && !e.hunterUsed && !e.bulkerUsed && !state.illusionActive) {
                    Text("🔮 Manipulation d’un dé de rencontre (livre p. 41–42)", style = MaterialTheme.typography.titleMedium)
                    Text(if (state.character.className == "Moine") "Moine : une Prière pour modifier un dé de ±1 ou ±2." else "Mage : modifier gratuitement un seul dé de ±1.")
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf("Type", "Quantité", "Contexte").forEach { kind ->
                            OutlinedButton(onClick = { mysticKind = kind }, enabled = when (kind) {
                                "Type" -> e.typeDice.size == 7
                                "Quantité" -> e.quantityDie in 1..6 && !e.effect.contains("quantité fixe", ignoreCase = true)
                                else -> e.contextDie in 1..6
                            }) { Text(if (mysticKind == kind) "● $kind" else kind) }
                        }
                    }
                    if (e.typeDice.size == 7) Text("Type : ${e.typeDice.joinToString(" + ")} = ${e.roll}")
                    else Text("Dés de type non enregistrés pour cette ancienne rencontre.", style = MaterialTheme.typography.bodySmall)
                    if (e.quantityDie > 0) Text("Quantité : dé ${e.quantityDie}${if (e.roll == 7) " × 10" else ""}${if (state.bardJourney) " × 2 (Barde)" else ""}")
                    else Text("Dé de quantité absent (rencontre fixe ou ancienne sauvegarde).", style = MaterialTheme.typography.bodySmall)
                    val targetDie = when (mysticKind) {
                        "Type" -> e.typeDice.getOrNull(selectedTypeDie) ?: 0
                        "Quantité" -> e.quantityDie
                        else -> e.contextDie
                    }
                    if (mysticKind == "Type" && e.typeDice.size == 7) {
                        Text("Choisir le dé de type à modifier :")
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            e.typeDice.forEachIndexed { index, die ->
                                OutlinedButton(onClick = { selectedTypeDie = index }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text("${if (selectedTypeDie == index) "●" else "○"} $die")
                                }
                            }
                        }
                    }
                    if (targetDie in 1..6) Text("Dé sélectionné : $targetDie. Le résultat doit rester compris entre 1 et 6.")
                    if (state.character.className == "Moine") Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = monkPrayerConfirmed, onCheckedChange = { monkPrayerConfirmed = it })
                        Text("J’ai effectué une Prière")
                    }
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val deltas = if (state.character.className == "Moine") listOf(-2, -1, 1, 2) else listOf(-1, 1)
                        deltas.forEach { d -> OutlinedButton(onClick = { onMystic(mysticKind, selectedTypeDie, d); monkPrayerConfirmed = false },
                            enabled = (targetDie + d) in 1..6 && (state.character.className != "Moine" || monkPrayerConfirmed)) {
                            Text(if (d > 0) "+$d" else "$d")
                        } }
                    }
                    Text("Une modification par rencontre (convention d’interface). Les rencontres hors Fitland restent ignorées comme dans la génération actuelle.", style = MaterialTheme.typography.bodySmall)
                }
                if (state.paladinJourneyArmed && !e.safe) {
                    if (state.paladinJourneyUsed) Text("🛡️ Paladin/Quêteur : pouvoir de quantité déjà utilisé ce voyage.")
                    else if (e.quantityDie in 1..6 && !e.effect.contains("quantité fixe", ignoreCase = true) &&
                        encounterHp(e).all { it == e.hp } && !e.mysticUsed && !e.hunterUsed && !e.bulkerUsed && !state.illusionActive) {
                        Text("🛡️ Paladin/Quêteur (p. 129) : votre Prière avant départ permet de diminuer de 2 le dé de quantité de CETTE rencontre. ${e.quantityDie} → ${(e.quantityDie - 2).coerceAtLeast(1)}. Un seul usage durant ce voyage.")
                        if (e.quantityDie <= 2) Text("Convention : minimum 1 sur le dé (le livre ne précise pas le cas 0 ou négatif).", style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(onClick = onPaladin, modifier = Modifier.fillMaxWidth()) { Text("Utiliser Paladin/Quêteur : quantité −2") }
                    } else Text("Paladin/Quêteur : indisponible pour une rencontre fixe ou après modification/attaque.", style = MaterialTheme.typography.bodySmall)
                }
                if ("Oracle de Tasmina" in availableCompanions(state) && !e.safe) {
                    if (state.oracleJourneyActive) Text("✨ Oracle de Tasmina (p. 73) : Prière avant départ, dégâts des attaques ×2 déjà actifs.")
                    else if (state.oracleCombatActive) Text("✨ Oracle de Tasmina : dégâts ×2 actifs pour cet affrontement.")
                    else if (encounterHp(e).all { it == e.hp }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = oracleCombatPrayer, onCheckedChange = { oracleCombatPrayer = it })
                            Text("J'ai effectué une Prière à ma divinité avant cet affrontement (p. 73).")
                        }
                        OutlinedButton(enabled = oracleCombatPrayer, onClick = { onOracleCombat(); oracleCombatPrayer = false },
                            modifier = Modifier.fillMaxWidth()) { Text("✨ Activer l'Oracle : dégâts ×2 pour ce combat") }
                    } else Text("Oracle : la Prière doit être confirmée avant la première attaque.", style = MaterialTheme.typography.bodySmall)
                }
                if (state.character.profileType == "advanced" && state.character.className == "Chasseur" && !e.safe) {
                    val unhurt = encounterHp(e).all { it == e.hp }
                    if (!e.hunterUsed && unhurt && e.quantity > (if (e.roll == 7) 10 else 1) && !e.effect.contains("quantité fixe", ignoreCase = true)) {
                        Text("Capacité Chasseur : réduire de 1 le résultat du dé de quantité (avant toute attaque).")
                        OutlinedButton(onClick = onHunt, modifier = Modifier.fillMaxWidth()) { Text("Chasseur — écarter 1 adversaire") }
                    } else if (e.hunterUsed) Text("Capacité Chasseur déjà utilisée sur cette rencontre.")
                    else if (e.quantity <= 1) Text("Chasseur : quantité minimale de 1 retenue pour l’application.")
                }
                if ("Bulker Fruité" in availableCompanions(state) && !e.safe && state.destinationKm > 0.0) {
                    if (!e.bulkerUsed && encounterHp(e).all { it == e.hp }) {
                        Text("🤝 Bulker Fruité (p. 76) : neutralise 1 adversaire par discussion. Le butin de cet adversaire est retiré.")
                        OutlinedButton(onClick = onBulker, modifier = Modifier.fillMaxWidth()) {
                            Text("Confirmer la discussion de Bulker (−1 ennemi)")
                        }
                    } else if (e.bulkerUsed) Text("Bulker a déjà neutralisé un adversaire lors de cette rencontre.")
                    else Text("Bulker ne peut intervenir qu'avant le début des attaques.")
                }
                if (!e.safe) {
                    if ("Manuel de méditation" in state.inventory) {
                        if (state.meditationActive) Text("Méditation active : +100 PV sur chacune de vos attaques jusqu’à la fin de ce combat.")
                        else Button(onClick = onMeditate, modifier = Modifier.fillMaxWidth()) {
                            Text("J’ai médité ${60 * e.quantity} secondes (${e.quantity} × 60 sec.) — activer +100 PV/attaque")
                        }
                    }
                    if ("Shaker persuasion" in state.inventory) {
                        Text("Shaker persuasion : consommable officiel, permet d’ignorer le combat quel que soit le nombre d’adversaires.")
                        Button(onClick = onUsePersuasion, modifier = Modifier.fillMaxWidth()) { Text("Consommer un Shaker persuasion — ignorer le combat") }
                    }
                    if (state.illusionActive) Text("Shaker illusion (règle maison) : dé ${e.illusionDie}, ${e.initialQuantity - e.quantity} adversaire(s) écarté(s), ${e.quantity} restant(s). Butin recalculé.")
                    if ("Shaker illusion" in state.inventory && !state.illusionActive) {
                        Button(onClick = { onUseConsumable("Shaker illusion", "") }, modifier = Modifier.fillMaxWidth()) { Text("Shaker illusion : écarter 1d6 ennemis (minimum 1 restant)") }
                    }
                    if (state.vigorActive) Text("Shaker vigueur actif : dégâts des attaques ×2 pour ce combat.")
                    if (state.quantumMode.isNotBlank()) Text("Shaker quantique actif : ${state.quantumMode.lowercase()} le temps (facteur 2). Les durées des attaques sont recalculées automatiquement ci-dessous ; dégâts inchangés.")
                    if ("Shaker poison" in state.inventory) {
                        Text("Shaker poison : 700 dégâts à distance sur une seule cible ; consommé après usage.")
                        val living = encounterHp(e).indices.filter { encounterHp(e)[it] > 0 }
                        living.forEach { index ->
                            OutlinedButton(onClick = { onUseConsumable("Shaker poison", index.toString()) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Poison → ${e.name} n°${index + 1} (${encounterHp(e)[index]} PV)")
                            }
                        }
                    }
                    if ("Shaker sombre" in state.inventory) {
                        val group = groupSplashDamage(encounterHp(e), 700)
                        Text("🧪 Shaker sombre (livre p. 173) : 700 dégâts à distance sur un groupe. " +
                            "Convention de l'application : 700 dégâts sur chaque ennemi encore debout, " +
                            "sans multiplication par le Guerrier, l’Oracle ni le Shaker vigueur. " +
                            "Total effectif maintenant : ${group.damageDealt} PV.")
                        OutlinedButton(onClick = { darkShakerConfirm = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Utiliser le Shaker sombre — confirmer")
                        }
                    }
                    if ("Shaker vigueur" in state.inventory && !state.vigorActive) {
                        Button(onClick = { onUseConsumable("Shaker vigueur", "") }, modifier = Modifier.fillMaxWidth()) { Text("Boire un Shaker vigueur — dégâts ×2") }
                    }
                    if (state.cardioScrollActive || state.forceScrollActive || state.congestionScrollActive) {
                        Text("📜 Parchemins actifs : " + listOfNotNull(
                            if (state.cardioScrollActive) "Cardio (attaque à durée ×2)" else null,
                            if (state.forceScrollActive) "Force (attaque par exécution ×2)" else null,
                            if (state.congestionScrollActive) "Congestion (améliorations ×2)" else null
                        ).joinToString(" · "), style = MaterialTheme.typography.bodySmall)
                    }
                    listOf(
                        "Parchemin comestible de cardio" to "📜 Cardio — attaques à durée ×2",
                        "Parchemin comestible de force" to "📜 Force — attaques par exécution ×2",
                        "Parchemin comestible de congestion" to "📜 Congestion — améliorations ×2"
                    ).forEach { (name, label) ->
                        val activated = when (name) {
                            "Parchemin comestible de cardio" -> state.cardioScrollActive
                            "Parchemin comestible de force" -> state.forceScrollActive
                            else -> state.congestionScrollActive
                        }
                        if (name in state.inventory && !activated) {
                            OutlinedButton(onClick = { onUseConsumable(name, "") }, modifier = Modifier.fillMaxWidth()) {
                                Text("Consommer $label (100 PO)")
                            }
                        }
                    }
                    if ("Parchemin comestible de flexibilité" in state.inventory && !e.flexibilityScrollUsed &&
                        e.contextDie in 1..6 && encounterHp(e).all { it == e.hp }) {
                        Text("📜 Flexibilité (p. 174) : +5 au dé de contexte, plafonné à 6. À activer avant d'accomplir les éventuels exercices immédiats de la rencontre.")
                        OutlinedButton(onClick = { onUseConsumable("Parchemin comestible de flexibilité", "") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Consommer 📜 Flexibilité — contexte +5")
                        }
                    }
                    if (e.flexibilityScrollUsed) Text("📜 Parchemin de flexibilité déjà utilisé pour cette rencontre.")

                    if ("Shaker quantique" in state.inventory && state.quantumMode.isBlank()) {
                        Text("Shaker quantique : accélère ou ralentit le temps (facteur 2) durant le combat. Les durées des armes sont adaptées automatiquement.")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onUseConsumable("Shaker quantique", "Accélérer") }) { Text("Accélérer") }
                            Button(onClick = { onUseConsumable("Shaker quantique", "Ralentir") }) { Text("Ralentir") }
                        }
                    }
                    if (state.character.profileType == "advanced" && state.character.className == "Guerrier") {
                        if (state.warriorActive) Text("Guerrier : Prière accomplie, dégâts des attaques avec arme ×2 jusqu’à la fin de cette rencontre.")
                        else if (!dailyState(state).warriorUsed) Button(onClick = onWarrior, modifier = Modifier.fillMaxWidth()) { Text("Guerrier — confirmer 1 Prière effectuée, activer dégâts ×2") }
                        else Text("Guerrier : pouvoir quotidien déjà utilisé.")
                    }
                    val hasHeroic = "Récits héroïques" in state.inventory
                    val hasCrown = "Réplique de couronne" in state.inventory
                    if (hasHeroic || hasCrown) {
                        HorizontalDivider()
                        Text("📜 Améliorations de combat en groupe (livre p. 171)", style = MaterialTheme.typography.titleMedium)
                        if (hasHeroic) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = heroicAllyConfirmed, onCheckedChange = { heroicAllyConfirmed = it })
                                Text("Récits héroïques : nous sommes exactement deux combattants alliés contre un seul ennemi encore debout (×2)")
                            }
                            if (encounterHp(e).count { it > 0 } != 1) Text("Récits héroïques : bonus indisponible tant que plusieurs adversaires restent debout.")
                        }
                        if (hasCrown) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = crownRealPlayerConfirmed, onCheckedChange = { crownRealPlayerConfirmed = it })
                                Text("Réplique de couronne : je combats réellement aux côtés d’un autre joueur (×2)")
                            }
                        }
                        Text("Les présences sont déclarées à chaque attaque, elles ne sont pas déduites automatiquement des compagnons. Cumul des deux objets : multiplicateurs successifs (convention d’adaptation).", style = MaterialTheme.typography.bodySmall)
                    }
                    val owned = weapons.filter { it.name in state.inventory }
                    if (owned.isNotEmpty()) {
                        Text("Attaquer avec l'équipement", style = MaterialTheme.typography.titleMedium)
                        owned.forEach { w ->
                            OutlinedButton(onClick = { selectedWeapon = w.name; rangedTarget = -1 }, modifier = Modifier.fillMaxWidth()) {
                                Text("${if (selectedWeapon == w.name) "●" else "○"} ${itemSymbol(w.name)} ${w.name} — ${w.damage} PV / ${w.unit}")
                            }
                        }
                        val w = owned.firstOrNull { it.name == selectedWeapon }
                        if (w != null) {
                            Text("Exercice : ${w.exercise}")
                            if (w.ranged) {
                                Text("Attaque à distance : choisis une cible. Les dégâts excédentaires ne touchent pas les autres ennemis (règle maison de ciblage).")
                                val living = encounterHp(e).indices.filter { encounterHp(e)[it] > 0 }
                                living.forEach { index ->
                                    OutlinedButton(onClick = { rangedTarget = index }, modifier = Modifier.fillMaxWidth()) {
                                        Text("${if (rangedTarget == index) "●" else "○"} ${e.name} n°${index + 1} — ${encounterHp(e)[index]} PV")
                                    }
                                }
                                if (rangedTarget !in living) Text("Sélectionne un adversaire encore debout pour lancer l'attaque.")
                            }
                            if (w.contextBonus > 0) Text("Effet : +${w.contextBonus} au contexte, si équipée avant le tirage")
                            val baseSeconds = if (w.unit.contains("sec.")) w.unit.substringBefore(' ').toIntOrNull() else null
                            if (baseSeconds != null) {
                                val ambushFactor = if (e.context == "Une embuscade !") 2 else 1
                                val adjustedSeconds = when (state.quantumMode) {
                                    "Accélérer" -> (baseSeconds * ambushFactor + 1) / 2
                                    "Ralentir" -> baseSeconds * ambushFactor * 2
                                    else -> baseSeconds * ambushFactor
                                }
                                Text("Durée réelle à effectuer : $adjustedSeconds sec. par série (au lieu de $baseSeconds sec.).")
                                if (state.quantumMode.isNotBlank()) Text("Shaker quantique : ${state.quantumMode.lowercase()} ×2 — conversion automatique des durées. Convention d'application : le livre ne précise pas le sens du facteur 2 pour les exercices.", style = MaterialTheme.typography.bodySmall)
                            } else if (state.quantumMode.isNotBlank()) {
                                Text("Cette arme fonctionne par exécution : le Shaker quantique ne change pas le nombre de répétitions. Aucun effet chiffré prévu par le livre pour cette arme.", style = MaterialTheme.typography.bodySmall)
                            }
                            OutlinedTextField(value = attackCount, onValueChange = { attackCount = it.filter(Char::isDigit) },
                                label = { Text("Nombre de séries / exécutions de ${w.unit}") },
                                modifier = Modifier.fillMaxWidth(), singleLine = true)
                            if ("Manuel de bottes de Maître" in state.inventory) Text("Manuel de bottes de Maître : +100 PV par attaque")
                            if (state.congestionScrollActive) Text("Parchemin de congestion : les bonus des améliorations (manuels, Pattes d’ours) sont doublés ; pas les soutiens.")
                            if (state.cardioScrollActive && w.unit.contains("sec.")) Text("Parchemin de cardio : dégâts de base ×2 pour cette arme à durée.")
                            if (state.forceScrollActive && !w.unit.contains("sec.")) Text("Parchemin de force : dégâts de base ×2 pour cette arme par exécution.")
                            if ("Manuel de combat à mains nues" in state.inventory && w.name.startsWith("Gants")) Text("Manuel de combat à mains nues : +50 PV par attaque")
                            if (state.meditationActive) Text("Manuel de méditation : +100 PV par attaque (méditation déjà confirmée)")
                            if ("Pattes d’ours" in state.inventory) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = pattesUsed, onCheckedChange = { pattesUsed = it })
                                    Text("J'ai compté à voix haute (+50 PV pour cette attaque)")
                                }
                            }
                            val activeSupport = supports.firstOrNull { it.name == state.equippedSupport && it.name in state.inventory }
                            if (activeSupport != null) {
                                Text("Soutien : ${activeSupport.name} — ${activeSupport.exercise}")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = supportConfirmed, onCheckedChange = { supportConfirmed = it })
                                    Text("Exercice de soutien réalisé (+${activeSupport.bonus} PV sur cette attaque)")
                                }
                            }
                            val activeArmor = armors.firstOrNull { it.name == state.equippedArmor && it.name in state.inventory }
                            if (activeArmor != null && activeArmor.bonus > 0) {
                                Text("Armure : ${activeArmor.name} — ${activeArmor.exercise}")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = armorConfirmed, onCheckedChange = { armorConfirmed = it })
                                    Text("Je porte effectivement ce lest (+${activeArmor.bonus} PV par exécution/série validée)")
                                }
                            }
                            val n = (attackCount.toIntOrNull() ?: 0).coerceIn(0, 1000)
                            val damagePreview = if (n > 0) enrichedAttackDamage(state, attackDamage(w, n, state.inventory, pattesUsed, state.meditationActive, state.vigorActive, ((if (supportConfirmed) activeSupport?.bonus ?: 0 else 0).toLong() + ArmorRules.bonusForUnits(activeArmor?.bonus ?: 0, n, armorConfirmed).toLong()).coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                                state.cardioScrollActive, state.forceScrollActive, state.congestionScrollActive), heroicAllyConfirmed, crownRealPlayerConfirmed) else 0
                            if (baseSeconds != null && n > 0) {
                                val ambushFactor = if (e.context == "Une embuscade !") 2 else 1
                                val effective = when (state.quantumMode) {
                                    "Accélérer" -> (baseSeconds * ambushFactor + 1) / 2
                                    "Ralentir" -> baseSeconds * ambushFactor * 2
                                    else -> baseSeconds * ambushFactor
                                }
                                Text("Temps total à réaliser : ${effective.toLong() * n} secondes pour $n série(s).")
                            }
                            if (w.ranged && rangedTarget in encounterHp(e).indices && encounterHp(e)[rangedTarget] > 0 && n > 0)
                                Text("Dégâts effectifs sur cette cible : ${minOf(damagePreview, encounterHp(e)[rangedTarget])} PV ; surplus perdu.")
                            Button(enabled = n in 1..1000 && (!w.ranged || (rangedTarget in encounterHp(e).indices && encounterHp(e)[rangedTarget] > 0)),
                                onClick = { onWeaponAttack(w.name, n, pattesUsed, if (w.ranged) rangedTarget else null, supportConfirmed, armorConfirmed, heroicAllyConfirmed, crownRealPlayerConfirmed); attackCount = "1"; pattesUsed = false; supportConfirmed = false; armorConfirmed = false; heroicAllyConfirmed = false; crownRealPlayerConfirmed = false },
                                modifier = Modifier.fillMaxWidth()) { Text("Exercice réalisé — infliger $damagePreview PV") }
                        }
                    } else Text("Aucune arme achetée : consulte le Sac pour t'équiper.")
                    Text("Saisie manuelle de secours : entre le résultat final de tes exercices, y compris les éventuels bonus des parchemins ; l'application ne les réapplique pas à cette saisie.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = damageInput, onValueChange = { damageInput = it.filter(Char::isDigit) },
                        label = { Text("Dégâts infligés avec les objets du livre (PV)") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true)
                    val rawDamage = damageInput.toIntOrNull() ?: 0
                    if (state.oracleCombatActive || state.oracleJourneyActive || state.warriorActive || "Récits héroïques" in state.inventory || "Réplique de couronne" in state.inventory)
                        Text("Les multiplicateurs actifs de l’Oracle, du Guerrier, des Récits héroïques et de la Réplique de couronne sont appliqués aux dégâts bruts. Les conditions de groupe doivent être confirmées ci-dessus.", style = MaterialTheme.typography.bodySmall)
                    Button(enabled = rawDamage > 0,
                        onClick = { onResolve(rawDamage, heroicAllyConfirmed, crownRealPlayerConfirmed); damageInput = ""; heroicAllyConfirmed = false; crownRealPlayerConfirmed = false },
                        modifier = Modifier.fillMaxWidth()) { Text("Enregistrer ${enrichedAttackDamage(state, rawDamage, heroicAllyConfirmed, crownRealPlayerConfirmed)} dégâts") }
                }
                if (e.safe || e.context == "Ils n'ont rien vu.") {
                    OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                        Text(if (e.safe) "Terminer la rencontre pacifique" else "Éviter la rencontre — sans fuite")
                    }
                }
                if (!e.safe) {
                    var fleeExercisesConfirmed by remember(e.name, e.roll, e.contextDie) { mutableStateOf(false) }
                    val mandatory = when (e.context) {
                        "Vous êtes piégé !" -> "${10 * e.quantity} burpees"
                        "Ils arrivent !" -> "50 jumping jacks"
                        "Ils ne vous échapperont pas." -> "20 jumping jacks"
                        else -> "Aucun exercice de contexte supplémentaire"
                    }
                    Text("Fuite : $mandatory. Retour obligatoire vers le point de départ du trajet, sans nouvelle rencontre ni butin.")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = fleeExercisesConfirmed, onCheckedChange = { fleeExercisesConfirmed = it })
                        Text("J'ai effectué les exercices de contexte requis")
                    }
                    OutlinedButton(enabled = fleeExercisesConfirmed && !state.retreatActive, onClick = { onFlee(false) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Fuir — revenir au départ")
                    }
                    if (state.character.profileType == "advanced" && state.character.className == "Acrobate" && state.destinationKm > 0.0) {
                        OutlinedButton(enabled = fleeExercisesConfirmed && !state.retreatActive, onClick = { onFlee(true) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Acrobate — fuir vers la destination")
                        }
                    }
                }
                Text("Les effets immédiats des monstres restent à appliquer manuellement. La fuite retourne au départ du trajet en cours ; capacité spéciale de l'Acrobate intégrée.", style = MaterialTheme.typography.bodySmall)
            } } }
        }
        item { Text("Journal", style = MaterialTheme.typography.titleMedium) }
        items(state.log.take(8)) { Text("• $it") }
    }
}

// Barèmes du profil novice : La Prière de Tasmina, livre p. 23.
private fun rank(value: Int, thresholds: List<Int>): Int =
    (thresholds.indexOfLast { value >= it } + 1).coerceAtLeast(1)

private fun noviceProfile(c: Character, pushups: Int, jacks: Int, balance: Int, capable: Boolean): Character {
    val vigour = rank(pushups, listOf(1, 3, 5, 7, 10, 15, 20, 25, 30, 35))
    val resistance = rank(jacks, listOf(0, 50, 60, 70, 80, 90, 100, 120, 140, 180))
    // Échec = rang 1 ; posture tenue = rang 2 ; 2,3,4,5,6,7,10,15 RSRA = rangs 3..10.
    val adaptation = if (!capable) 1 else rank(balance, listOf(0, 0, 2, 3, 4, 5, 6, 7, 10, 15)).coerceAtLeast(2)
    val level = (vigour + resistance + adaptation) / 3
    val className = when (level) { in 1..3 -> "Recrue"; in 4..6 -> "Stagiaire"; else -> "Cadet" }
    return c.copy(profileType = "novice", pushups30 = pushups, jumpingJacks180 = jacks,
        balanceReps = balance, balanceCapable = capable, vigour = vigour,
        resistance = resistance, adaptation = adaptation, level = level,
        className = className, divinity = "Tasmina")
}

// Barèmes du profil avancé : Prières de Proteidon, Endurox, Dumbella et Athleena.
private fun advancedRank(value: Double, thresholds: List<Double>): Int =
    if (value <= 0) 0 else (thresholds.indexOfLast { value >= it } + 1).coerceAtLeast(0)

private fun advancedProfile(c: Character, strength: String, hours: String, distance: String,
    moves: String, precision: String, seconds: String, difficulty: String, bonus: Int,
    activity: String, special: String): Character {
    val force = strength.toDoubleOrNull()?.let { advancedRank(it, listOf(65.0,80.0,100.0,120.0,140.0,160.0,180.0,200.0,220.0,240.0)) } ?: 0
    val enduranceByHours = hours.toDoubleOrNull()?.let { advancedRank(it, listOf(0.5,0.75,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0)) } ?: 0
    val enduranceByDistance = distance.toDoubleOrNull()?.let { advancedRank(it, listOf(0.01,2.0,2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8)) } ?: 0
    val endurance = maxOf(enduranceByHours, enduranceByDistance)
    val agility = moves.toDoubleOrNull()?.let { advancedRank(it, listOf(2.0,3.0,6.0,10.0,12.0,15.0,18.0,21.0,25.0,30.0)) } ?: 0
    val dexterity = maxOf(
        precision.toDoubleOrNull()?.let { advancedRank(it, listOf(5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0)) } ?: 0,
        seconds.toDoubleOrNull()?.let { advancedRank(it, listOf(5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0)) } ?: 0
    )
    // Barème de résilience mentale, p. 33 : moyenne de difficulté (1-10), 1..2→1, 3..4→2, etc.
    val avg = difficulty.toDoubleOrNull()?.toInt() ?: 0
    val mentalBase = when (avg) { 1,2 -> 1; 3,4 -> 2; 5,6 -> 3; 7 -> 4; 8 -> 5; 9 -> 6; 10 -> 7; else -> 0 }
    val mental = (mentalBase + bonus).coerceIn(0,10)
    val scores = listOf(force, endurance, agility, dexterity, mental)
    val names = listOf("Proteidon", "Endurox", "Dumbella", "Athleena", "Arno")
    val max = scores.maxOrNull() ?: 0
    val divinity = if (max == 0) "Non déterminée" else names.filterIndexed { i, _ -> scores[i] == max }.joinToString(" et ")
    // Le texte du livre p. 35 précise une somme divisée par dix (et non par cinq).
    val level = scores.sum() / 10
    val classes = mapOf("Course / randonnée / natation" to "Éclaireur", "Cyclisme / skate" to "Cavalier",
        "Musculation / force" to "Moine", "Sports de balle" to "Chasseur", "Sports de combat" to "Guerrier",
        "Yoga / Pilates" to "Mage", "Danse" to "Barde", "Escalade / gymnastique" to "Acrobate")
    val nextClass = classes[activity] ?: "Non déterminée"
    return c.copy(profileType = "advanced", force = force, endurance = endurance, agility = agility,
        dexterity = dexterity, mental = mental, divinity = divinity, level = level,
        className = nextClass, primaryActivity = activity,
        specialAbility = special,
        unlockedClasses = (c.unlockedClasses + listOfNotNull(classes[activity])).distinct(),
        completedAchievements = c.completedAchievements, strengthPercent = strength, enduranceHours = hours,
        enduranceDistance = distance, agilityMoves = moves, dexterityPercent = precision,
        dexteritySeconds = seconds, mentalDifficulty = difficulty, mentalBonus = bonus)
}

@Composable private fun CharacterScreen(c: Character, onUpdate: (Character) -> Unit, onReset: () -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var name by remember(c.name) { mutableStateOf(c.name) }
    var mode by remember(c.profileType) { mutableStateOf(if (c.profileType == "advanced") "advanced" else "novice") }
    var pushups by remember(c.pushups30) { mutableStateOf(c.pushups30.toString()) }
    var jacks by remember(c.jumpingJacks180) { mutableStateOf(c.jumpingJacks180.toString()) }
    var balance by remember(c.balanceReps) { mutableStateOf(c.balanceReps.toString()) }
    var capable by remember(c.balanceCapable) { mutableStateOf(c.balanceCapable) }
    var strength by remember(c.strengthPercent) { mutableStateOf(c.strengthPercent) }
    var hours by remember(c.enduranceHours) { mutableStateOf(c.enduranceHours) }
    var distance by remember(c.enduranceDistance) { mutableStateOf(c.enduranceDistance) }
    var moves by remember(c.agilityMoves) { mutableStateOf(c.agilityMoves) }
    var precision by remember(c.dexterityPercent) { mutableStateOf(c.dexterityPercent) }
    var seconds by remember(c.dexteritySeconds) { mutableStateOf(c.dexteritySeconds) }
    var difficulty by remember(c.mentalDifficulty) { mutableStateOf(c.mentalDifficulty) }
    var bonus by remember(c.mentalBonus) { mutableIntStateOf(c.mentalBonus) }
    val activities = listOf("Course / randonnée / natation", "Cyclisme / skate", "Musculation / force", "Sports de balle", "Sports de combat", "Yoga / Pilates", "Danse", "Escalade / gymnastique")
    var activity by remember(c.primaryActivity) { mutableStateOf(c.primaryActivity.ifBlank { activities[0] }) }
    var special by remember(c.specialAbility) { mutableStateOf(c.specialAbility) }
    val profileLocked = c.profileType == "novice" || c.profileType == "advanced"
    val noviceValid = listOf(pushups, jacks, balance).all { it.toIntOrNull() != null && it.toInt() in 0..10000 }
    fun validNumber(v: String) = v.isBlank() || (v.replace(',', '.').toDoubleOrNull()?.let { it >= 0 && it <= 100000 } == true)
    val advancedValid = listOf(strength, hours, distance, moves, precision, seconds).all(::validNumber) &&
        (difficulty.isBlank() || difficulty.toIntOrNull() in 1..10)
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Fiche de personnage", style = MaterialTheme.typography.headlineSmall) }
        item { ClassBanner(c) }
        item { Text("${c.name} · ${c.className} · ${c.divinity} · Niveau ${c.level}") }
        item { OutlinedButton(onClick = { editing = !editing }) { Text(if (editing) "Fermer" else "Modifier le nom") } }
        if (editing) {
            item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom du personnage") }, singleLine = true) }
            item { Button(onClick = { if (name.isNotBlank()) { onUpdate(c.copy(name = name.trim())); editing = false } }) { Text("Enregistrer") } }
        }
        item { HorizontalDivider() }
        if (profileLocked) {
            item { Text("Caractéristiques · jauges sur 10", style = MaterialTheme.typography.titleMedium) }
            if (c.profileType == "novice") {
                item { StatRow("Vigueur", c.vigour) }; item { StatRow("Résistance", c.resistance) }; item { StatRow("Adaptation", c.adaptation) }
            } else {
                item { StatRow("Force", c.force) }; item { StatRow("Endurance", c.endurance) }
                item { StatRow("Agilité", c.agility) }; item { StatRow("Dextérité", c.dexterity) }; item { StatRow("Mental", c.mental) }
                item { Text("Discipline : ${c.primaryActivity.ifBlank { "Non renseignée" }}") }
                item { Text("Capacité : ${c.specialAbility.ifBlank { "Non renseignée" }}") }
            }
        }
        if (c.profileType == "advanced") {
            item { HorizontalDivider() }
            item { Text("Classes et accomplissements", style = MaterialTheme.typography.titleMedium) }
            item { Text("La classe actuelle affiche tous ses accomplissements. Les anciennes classes n’affichent que ceux déjà validés. Les validations sont conservées lors des changements de classe.", style = MaterialTheme.typography.bodySmall) }
            val activeClass = c.className
            if (activeClass in classAchievements) {
                item { Text("Classe actuelle : $activeClass", style = MaterialTheme.typography.titleSmall) }
                classAchievements[activeClass].orEmpty().forEach { achievement ->
                    val key = "$activeClass::$achievement"
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = key in c.completedAchievements, onCheckedChange = { checked ->
                                val updated = if (checked) (c.completedAchievements + key).distinct() else c.completedAchievements - key
                                onUpdate(c.copy(completedAchievements = updated))
                            })
                            Text(achievement)
                        }
                    }
                }
            }
            val pastClasses = c.completedAchievements.mapNotNull { key ->
                val className = key.substringBefore("::", "")
                className.takeIf { it != activeClass && it in classAchievements }
            }.distinct()
            if (pastClasses.isNotEmpty()) {
                item { Text("Accomplissements acquis dans d’autres classes", style = MaterialTheme.typography.titleMedium) }
                pastClasses.forEach { className ->
                    item { Text(className, style = MaterialTheme.typography.titleSmall) }
                    classAchievements[className].orEmpty().forEach { achievement ->
                        val key = "$className::$achievement"
                        if (key in c.completedAchievements) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = true, onCheckedChange = { checked ->
                                        if (!checked) onUpdate(c.copy(completedAchievements = c.completedAchievements - key))
                                    })
                                    Text(achievement)
                                }
                            }
                        }
                    }
                }
            }
        }
        item { HorizontalDivider() }
        item { Text("Modifier le profil", style = MaterialTheme.typography.titleMedium) }
        item { Text("Type de profil", style = MaterialTheme.typography.titleMedium) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = mode == "novice", enabled = !profileLocked, onClick = { mode = "novice" }, label = { Text("Novice") })
            FilterChip(selected = mode == "advanced", enabled = !profileLocked, onClick = { mode = "advanced" }, label = { Text("Avancé") })
        } }
        if (profileLocked) {
            item { Text("Profil ${if (c.profileType == "novice") "novice" else "avancé"} confirmé. Le type de profil est verrouillé. Tu peux actualiser les résultats des épreuves, mais pour changer de type, utilise « Réinitialiser toute l’aventure » (efface toutes les données).", style = MaterialTheme.typography.bodyMedium) }
        }
        if (mode == "novice") {
            item { Text("Prière de Tasmina", style = MaterialTheme.typography.titleLarge) }
            item { Text("Entre les résultats des trois épreuves. Le niveau est calculé automatiquement.") }
            item { OutlinedTextField(value = pushups, onValueChange = { if (it.length <= 5 && it.all(Char::isDigit)) pushups = it }, label = { Text("Pompes en 30 secondes") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = jacks, onValueChange = { if (it.length <= 5 && it.all(Char::isDigit)) jacks = it }, label = { Text("Jumping jacks en 3 minutes") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = capable, onCheckedChange = { capable = it }); Text("Posture d'équilibre tenue") } }
            if (capable) item { OutlinedTextField(value = balance, onValueChange = { if (it.length <= 5 && it.all(Char::isDigit)) balance = it }, label = { Text("Répétitions sans reprendre appui") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { Button(enabled = noviceValid && (!profileLocked || c.profileType == "novice"), onClick = { onUpdate(noviceProfile(c, pushups.toInt(), jacks.toInt(), balance.toInt(), capable)) }, modifier = Modifier.fillMaxWidth()) { Text("Enregistrer le profil novice") } }
        } else {
            item { Text("Profil avancé · Les quatre Prières", style = MaterialTheme.typography.titleLarge) }
            item { Text("Les tests sont facultatifs : toute caractéristique non évaluée vaut 0. Les valeurs saisies sont conservées.") }
            item { Text("Proteidon · Force", style = MaterialTheme.typography.titleMedium) }
            item { OutlinedTextField(value = strength, onValueChange = { strength = it }, label = { Text("Charge déplacée (% du poids de corps)") }, supportingText = { Text("Ex. 140 pour 140 %. Pompe = 65 %, traction = 90 % du poids de corps.") }, modifier = Modifier.fillMaxWidth()) }
            item { Text("Endurox · Endurance", style = MaterialTheme.typography.titleMedium) }
            item { OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Durée d'effort en heures (ex. 1.5)") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = distance, onValueChange = { distance = it }, label = { Text("OU kilomètres à pied en 12 minutes") }, modifier = Modifier.fillMaxWidth()) }
            item { Text("Dumbella · Agilité", style = MaterialTheme.typography.titleMedium) }
            item { OutlinedTextField(value = moves, onValueChange = { moves = it }, label = { Text("Nombre de mouvements maîtrisés") }, modifier = Modifier.fillMaxWidth()) }
            item { Text("Athleena · Dextérité", style = MaterialTheme.typography.titleMedium) }
            item { OutlinedTextField(value = precision, onValueChange = { precision = it }, label = { Text("Réussite des lancers (%) · au moins 20") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = seconds, onValueChange = { seconds = it }, label = { Text("OU équilibre yeux fermés (secondes)") }, modifier = Modifier.fillMaxWidth()) }
            item { Text("Arno · Mental", style = MaterialTheme.typography.titleMedium) }
            item { OutlinedTextField(value = difficulty, onValueChange = { difficulty = it }, label = { Text("Moyenne des difficultés des 4 Prières (1 à 10)") }, supportingText = { Text("Arrondir la moyenne à l'entier inférieur. 1–2 = Mental 1, 3–4 = 2, etc.") }, modifier = Modifier.fillMaxWidth()) }
            item { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { bonus = (bonus - 1).coerceAtLeast(0) }) { Text("−") }
                Text("Progressions mentales : +$bonus")
                OutlinedButton(onClick = { bonus = (bonus + 1).coerceAtMost(10) }) { Text("+") }
            } }
            item { Text("Classe et compétence", style = MaterialTheme.typography.titleMedium) }
            item { Text("Choisis la discipline ayant donné ton meilleur score. En cas d'égalité, choisis l'une des disciplines à égalité.") }
            activities.forEach { a -> item { Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = activity == a, onClick = { activity = a }); Text(a)
            } } }
            item { OutlinedTextField(value = special, onValueChange = { special = it }, label = { Text("Capacité spéciale / exercice marquant") }, modifier = Modifier.fillMaxWidth()) }
            item { Button(enabled = advancedValid && (!profileLocked || c.profileType == "advanced"), onClick = {
                onUpdate(advancedProfile(c, strength.replace(',','.'), hours.replace(',','.'), distance.replace(',','.'), moves.replace(',','.'), precision.replace(',','.'), seconds.replace(',','.'), difficulty, bonus, activity, special))
            }, modifier = Modifier.fillMaxWidth()) { Text("Enregistrer le profil avancé") } }
        }
        item { HorizontalDivider() }
        item { Text("Gestion de la sauvegarde", style = MaterialTheme.typography.titleMedium) }
        item { OutlinedButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) { Text("Réinitialiser toute l'aventure") } }
        item { Text("Une confirmation sera demandée avant d'effacer les données.", style = MaterialTheme.typography.bodySmall) }
    }
}
@Composable private fun StatRow(name: String, value: Int) {
    val statColor = when (name) {
        "Force", "Vigueur" -> Color(0xFFE28669)
        "Endurance", "Résistance" -> Color(0xFF6FC4A8)
        "Agilité", "Adaptation" -> Color(0xFFE8C46F)
        "Dextérité" -> Color(0xFF84B4E6)
        else -> Color(0xFFB69AE5)
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name)
            Text("$value / 10", color = statColor, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(progress = { (value.toFloat() / 10f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp)),
            color = statColor, trackColor = Color(0xFF302B35))
    }
}

@Composable private fun ApplicationInfo(state: AppState, onResetDailyTests: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var notificationTestFeedback by remember { mutableStateOf("") }
    var encounterPreview by remember { mutableStateOf<Encounter?>(null) }
    var encounterPreviewInfo by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Application", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Dungeon of Fitness · Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.titleLarge) }
        item { Text("Mode test — capacités quotidiennes", style = MaterialTheme.typography.titleMedium) }
        item { Text("Éclaireur : ${dailyState(state).scoutUses}/2 · Barde : ${dailyState(state).bardUses}/2 · Guerrier : ${if (dailyState(state).warriorUsed) "utilisé" else "disponible"} · Cavalier : ${if (dailyState(state).cavalierUsed) "utilisé" else "disponible"}.") }
        item { OutlinedButton(onClick = onResetDailyTests, modifier = Modifier.fillMaxWidth()) { Text("MODE TEST : remettre les utilisations du jour à zéro") } }
        item { Text("Ne supprime ni le personnage, ni les PO, ni l’inventaire, ni les kilomètres, ni le trajet ou le combat en cours. Le bonus Guerrier déjà activé pendant le combat reste actif.", style = MaterialTheme.typography.bodySmall) }
        item { Text("Mode test — notifications", style = MaterialTheme.typography.titleMedium) }
        item { Text("Trois aperçus : les mêmes messages que les événements réels, " +
            "sans modifier le trajet, le capteur, les kilomètres ou les combats.", style = MaterialTheme.typography.bodySmall) }
        listOf("encounter" to "⚔️ Rencontre", "waypoint" to "🧭 Étape", "arrival" to "🏁 Arrivée").forEach { (kind, label) ->
            item { OutlinedButton(onClick = {
                notificationTestFeedback = if (StepTrackingService.sendTestNotification(context, kind))
                    "Notification « $label » envoyée. Ouvre-la pour accéder à Aventure."
                else "Notification bloquée par Android : vérifie l'autorisation et le canal Événements d’aventure."
            }, modifier = Modifier.fillMaxWidth()) { Text("MODE TEST : $label") } }
        }
        if (notificationTestFeedback.isNotBlank()) item { Text(notificationTestFeedback, style = MaterialTheme.typography.bodySmall) }
        item { Text("Ce test n'utilise pas le capteur et ne modifie ni les pas ni le trajet en cours.", style = MaterialTheme.typography.bodySmall) }
        item { HorizontalDivider() }
        item { Text("Mode test — aperçu d'une rencontre", style = MaterialTheme.typography.titleMedium) }
        item { Text("Tirer une rencontre fictive (7D6, quantité, contexte), sans déplacement, combat, récompense ou sauvegarde. " +
            "Cette simulation ne valide PAS l'arrêt réel au seuil des 6 km : celui-ci doit toujours être testé dehors.",
            style = MaterialTheme.typography.bodySmall) }
        item { OutlinedButton(onClick = {
            encounterPreview = generateEncounter(state.equippedWeapon, state.equippedSupport,
                state.character, state.bardJourney, state.inventory, state.equippedArmor, state.mageJewelSpent)
            encounterPreviewInfo = if (encounterPreview == null)
                "Jet ignoré : adversaire indisponible dans la zone Fitland, conformément au filtre actuel. Relance le test."
            else "Résultat de test (aucune modification du jeu)."
        }, modifier = Modifier.fillMaxWidth()) { Text("MODE TEST : tirer une rencontre fictive") } }
        if (encounterPreviewInfo.isNotBlank()) item { Text(encounterPreviewInfo, style = MaterialTheme.typography.bodySmall) }
        encounterPreview?.let { e ->
            item { Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("⚔️ ${e.name} ×${e.quantity} · jet ${e.roll}", style = MaterialTheme.typography.titleMedium)
                Text("7D6 : ${e.typeDice.joinToString(" + ")} · quantité : ${if (e.quantityDie == 0) "fixe" else e.quantityDie} · contexte : ${e.contextDie}")
                Text("Contexte : ${e.context} · ${e.exercise}")
                Text("PV totaux : ${e.remainingHp} · butin théorique : ${e.reward} PO")
                val effect = groupSplashDamage(encounterHp(e), 700)
                Text("Simulation Shaker sombre : ${effect.damageDealt} dégâts, ${effect.remainingHp.sum()} PV restants (aucun objet consommé).")
            } } }
        }
        item { Text("Fonctionnalités intégrées", style = MaterialTheme.typography.titleMedium) }
        listOf(
            "Navigation entre les écrans",
            "Planification manuelle d'un trajet sur la carte du livre",
            "Suivi des distances cumulées et validation d'arrivée",
            "Podomètre optionnel (1 pas = 1 m), sessions pause/reprise, seuil de rencontre et suivi écran verrouillé",
            "Alertes distinctes de rencontre, étape et arrivée, avec ouverture directe d'Aventure et tests indépendants",
            "Shaker sombre : effet de groupe à 700 PV par ennemi (convention annoncée)",
            "Rencontres : table officielle Fitland (7D6), quantité, contexte et PV",
            "Arme active et bonus de contexte des gants de vétéran",
            "Historique des dés (7D6, quantité et contexte), et manipulation Mage/Moine",
            "Téléportation Cavalier (adjacence confirmée sur la carte du livre)",
            "Jauges des caractéristiques du personnage (affichage uniquement)",
            "Suivi manuel des dégâts et butin à la victoire",
            "Ciblage individuel pour les armes à distance (règle maison)",
            "Mode novice : distance de carte convertie en minutes",
            "Position initiale selon la classe à la création du personnage",
            "Sauvegarde locale automatique",
            "Profils novice et avancé et verrouillage du type",
            "Calcul des caractéristiques et du niveau",
            "Réinitialisation complète avec confirmation",
            "Classes et historique des classes débloquées",
            "Accomplissements conservés lors du changement de classe",
            "Accomplissements non validés des anciennes classes masqués",
            "Carnet des 36 chapitres officiels : pages, accès, validation manuelle et suivi des lieux visités",
            "Fiche : statistiques et accomplissements en haut"
        ).forEach { feature -> item { Text("✓ $feature") } }
        item { HorizontalDivider() }
        item { Text("Carnet des lieux de Fitland", style = MaterialTheme.typography.titleMedium) }
        item { Text("La carte zoomable et le calcul d’itinéraires sont dans Aventure ; le carnet des 36 chapitres se trouve dans « Chapitres, accès et progression ».") }
        listOf("Fontaria", "Entrepôts Shaker+", "Tique-Couenne", "Nosferathon", "Omega Beach", "Garde Froide", "Litière Ville", "Mont Stepper").forEach { place ->
            item { Text("• $place") }
        }
        item { HorizontalDivider() }
        item { Text("En développement", style = MaterialTheme.typography.titleMedium) }
        listOf(
            "Conditions officielles de validation des accomplissements",
            "Combats automatisés avec dégâts calculés depuis l'équipement",
            "Objets, quêtes et entraînements complets"
        ).forEach { feature -> item { Text("○ $feature") } }
        item { Text("Rencontres : table officielle p. 43 et contextes p. 42. Effets spéciaux et équipement à appliquer manuellement.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable private fun Inventory(state: AppState, onInvestMageJewel: () -> Unit, onEquip: (String) -> Unit, onBuy: (Weapon) -> Unit, onUpgradeBuy: (CombatUpgrade) -> Unit, onSupportBuy: (Support) -> Unit, onEquipSupport: (String) -> Unit, onArmorBuy: (Armor) -> Unit, onEquipArmor: (String) -> Unit, onConsumableBuy: (String) -> Unit, onTestGold: (Int) -> Unit) {
    var bagTab by remember { mutableStateOf("Armes") }
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("🎒 Sac et armurerie", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineSmall) }
        item { Text("🪙 Bourse : ${state.character.po} PO", style = MaterialTheme.typography.titleMedium) }
        item { Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Laboratoire de test — PO fictifs", style = MaterialTheme.typography.titleMedium)
            Text("Hors règles du livre : ces boutons ajoutent des PO à la sauvegarde actuelle pour tester les achats. Désactiver leur usage lors d'une vraie partie.", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onTestGold(100) }) { Text("+100 PO") }
                OutlinedButton(onClick = { onTestGold(1000) }) { Text("+1 000 PO") }
            }
        } } }
        item { androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            listOf("Armes", "Armures", "Soutien", "Améliorations", "Consommables").forEach { category ->
                androidx.compose.material3.TextButton(onClick = { bagTab = category }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp)) {
                    Text(if (bagTab == category) "●$category" else category, style = MaterialTheme.typography.labelSmall)
                }
            }
        } }
        item { Text("Équipement possédé dans cette catégorie", style = MaterialTheme.typography.titleMedium) }
        if (bagTab == "Consommables" && "Shaker sombre" in state.inventory) item {
            Text("🧪 Shaker sombre (p. 173) : 700 dégâts à distance contre un groupe. " +
                "Utilisable pendant un combat, avec confirmation ; objet rare non achetable. " +
                "Convention : 700 PV sur chaque ennemi vivant.",
                style = MaterialTheme.typography.bodySmall, color = Color(0xFFEBC582))
        }
        val owned = state.inventory.filter { name -> when(bagTab) {
            "Armes" -> weapons.any { it.name == name }
            "Armures" -> armors.any { it.name == name }
            "Soutien" -> supports.any { it.name == name }
            "Améliorations" -> combatUpgrades.any { it.name == name } || name == "Bijou de mage"
            else -> name.startsWith("Shaker ") || name.startsWith("Parchemin comestible")
        } }
        if (owned.isEmpty()) item { Text("Aucun objet dans cette catégorie.") }
        items(owned.groupingBy { it }.eachCount().toList().sortedBy { it.first }) { (name, quantity) ->
            Text("${itemSymbol(name)}  ${if (name == "Bijou de mage") MageSpellRules.jewelLabel(state.mageJewelSpent) else name}${if (quantity > 1) " ×$quantity" else ""}")
        }
        if (bagTab == "Armes") {
        item { Text("Arme active pour les prochains jets de rencontre", style = MaterialTheme.typography.titleMedium) }
        item { Text(if (state.equippedWeapon.isBlank()) "Aucune arme active" else "Active : ${state.equippedWeapon}") }
        item { OutlinedButton(onClick = { onEquip("") }) { Text("Aucune arme active") } }
        items(weapons.filter { it.name in state.inventory }) { w ->
            OutlinedButton(onClick = { onEquip(w.name) }, modifier = Modifier.fillMaxWidth()) {
                Text("${if (state.equippedWeapon == w.name) "●" else "○"} ${itemSymbol(w.name)} ${w.name}${if (w.contextBonus != 0) " · Contexte +${w.contextBonus}" else ""}")
            }
        }
        item { Text("L'arme doit être sélectionnée AVANT le tirage : changer d'arme ne modifie pas rétroactivement une rencontre en cours.", style = MaterialTheme.typography.bodySmall) }
        }
        if (bagTab == "Armures") {
        item { Text("🛡️ Armures — livre p. 164", style = MaterialTheme.typography.titleMedium) }
        item { Text("L'armure est portée dans un emplacement distinct de l'arme et du soutien (convention d'équipement). Ses dégâts sont ajoutés par exécution/série confirmée. Confirme que tu portes réellement le lest. Le bonus de contexte s'applique uniquement si elle est équipée avant le jet. La relance de l'Armure respirante est proposée avant le combat.", style = MaterialTheme.typography.bodySmall) }
        item { Text("Armure équipée : ${state.equippedArmor.ifBlank { "aucune" }}") }
        item { OutlinedButton(onClick = { onEquipArmor("") }) { Text("Retirer l'armure") } }
        items(armors.filter { it.name in state.inventory }) { a ->
            OutlinedButton(onClick = { onEquipArmor(a.name) }, modifier = Modifier.fillMaxWidth()) {
                Text("${if (state.equippedArmor == a.name) "●" else "○"} ${a.name}")
            }
        }
        items(armors) { a ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${itemSymbol(a.name)} ${a.name} — ${a.price} PO", style = MaterialTheme.typography.titleMedium)
                Text(if (a.reroll) "Relancer une fois le dé de contexte, puis choisir le résultat (p. 164)." else "+${a.bonus} PV par exécution/série confirmée · contexte +${a.context}")
                Text(a.exercise)
                Button(enabled = state.character.po >= a.price && a.name !in state.inventory,
                    onClick = { onArmorBuy(a) }) { Text(if (a.name in state.inventory) "Déjà possédée" else "Acheter (${a.price} PO)") }
            } }
        }
        }
        if (bagTab == "Soutien") {
        item { Text("Soutien actif (avant le jet de contexte)", style = MaterialTheme.typography.titleMedium) }
        item { Text("${if (state.equippedSupport.isBlank()) "Aucun" else state.equippedSupport} — un seul soutien actif à la fois. Pour obtenir ses dégâts, réaliser et confirmer son exercice à chaque attaque.", style = MaterialTheme.typography.bodySmall) }
        item { OutlinedButton(onClick = { onEquipSupport("") }) { Text("Retirer le soutien") } }
        items(supports.filter { it.name in state.inventory }) { u ->
            OutlinedButton(onClick = { onEquipSupport(u.name) }, modifier = Modifier.fillMaxWidth()) {
                Text("${if (state.equippedSupport == u.name) "●" else "○"} ${itemSymbol(u.name)} ${u.name} (+${u.bonus} PV${if (u.context > 0) ", contexte +${u.context}" else ""})")
            }
        }
        item { HorizontalDivider() }
        item { Text("Boucliers et équipements de soutien", style = MaterialTheme.typography.titleMedium) }
        item { Text("Bonus de dégâts uniquement après l'exercice propre à l'objet ; bonus de contexte uniquement si le soutien est sélectionné avant la rencontre. Convention d'interface : un seul soutien actif.", style = MaterialTheme.typography.bodySmall) }
        items(supports) { u ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${itemSymbol(u.name)} ${u.name} — ${u.price} PO", style = MaterialTheme.typography.titleMedium)
                Text("+${u.bonus} PV par exécution${if (u.context > 0) " · contexte +${u.context}" else ""}")
                Text(u.exercise)
                Button(enabled = state.character.po >= u.price && u.name !in state.inventory, onClick = { onSupportBuy(u) }) {
                    Text(if (u.name in state.inventory) "Déjà possédé" else "Acheter (${u.price} PO)")
                }
            } }
        }
        }
        if (bagTab == "Améliorations") {
        item { Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("💎 ${if ("Bijou de mage" in state.inventory) MageSpellRules.jewelLabel(state.mageJewelSpent) else "Bijou de mage"} — livre p. 164", style = MaterialTheme.typography.titleMedium)
                Text("+1 au contexte pour chaque tranche de 200 PO investis dans le bijou. Achat initial 200 PO ; les investissements suivants augmentent sa valeur. Pas de bonus rétroactif pendant une rencontre.")
                Text("Somme investie : ${state.mageJewelSpent} PO · Contexte +${if ("Bijou de mage" in state.inventory) MageSpellRules.jewelBonus(state.mageJewelSpent) else 0}")
                Button(enabled = state.character.po >= 200 && state.mageJewelSpent <= Int.MAX_VALUE - 200,
                    onClick = onInvestMageJewel) { Text(if ("Bijou de mage" in state.inventory) "Investir 200 PO de plus" else "Acheter le bijou (200 PO)") }
            }
        } }
        item { Text("Améliorations de combat", style = MaterialTheme.typography.titleMedium) }
        item { Text("Bonus fixes appliqués une fois par attaque, et non à chaque répétition. Les Pattes d’ours nécessitent de confirmer le comptage oral.", style = MaterialTheme.typography.bodySmall) }
        items(combatUpgrades) { u ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${itemSymbol(u.name)} ${u.name} — ${u.price} PO", style = MaterialTheme.typography.titleMedium)
                Text("${when (u.name) { "Manuel d’agilité" -> "Contexte +2"; "Récits héroïques", "Réplique de couronne" -> "Dégâts ×2 si condition remplie"; else -> "+${u.bonus} PV" }} · ${u.description}")
                Button(enabled = state.character.po >= u.price && u.name !in state.inventory,
                    onClick = { onUpgradeBuy(u) }) { Text(if (u.name in state.inventory) "Déjà possédé" else "Acheter (${u.price} PO)") }
            } }
        }
        }
        if (bagTab == "Consommables") {
        item { HorizontalDivider() }
        item { Text("Consommables", style = MaterialTheme.typography.titleMedium) }
        val consumables = listOf(
            "Shaker persuasion" to "Ignore un combat, sans récompense",
            "Shaker poison" to "700 PV à distance sur une cible",
            "Shaker vigueur" to "Dégâts ×2 pendant un combat",
            "Shaker quantique" to "Accélère ou ralentit les durées d’exercice (facteur 2) pendant un combat",
            "Shaker illusion" to "Règle maison : 1d6 ennemis écartés, minimum 1 restant ; butin réduit",
            "Parchemin comestible de cardio" to "Livre p. 174 : dégâts de base ×2 sur les attaques à durée pendant un combat",
            "Parchemin comestible de congestion" to "Livre p. 174 : bonus des améliorations ×2 pendant un combat (hors soutien)",
            "Parchemin comestible de flexibilité" to "Livre p. 174 : +5 au jet de contexte d'une rencontre (avant exercices)",
            "Parchemin comestible de force" to "Livre p. 174 : dégâts de base ×2 sur les attaques par exécution pendant un combat"
        )
        items(consumables) { (name, effect) ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${itemSymbol(name)} $name — 100 PO", style = MaterialTheme.typography.titleMedium)
                Text(effect)
                Text("En stock : ${state.inventory.count { it == name }}")
                Button(enabled = state.character.po >= 100, onClick = { onConsumableBuy(name) }) { Text("Acheter (100 PO)") }
            } }
        }
        }
        if (bagTab == "Armes") {
        item { Text("Catalogue — armes et exercices du livre", style = MaterialTheme.typography.titleMedium) }
        item { Text("${weapons.size} armes et exercices, dont une épée rare à obtenir pendant la campagne (p. 163–172).", style = MaterialTheme.typography.bodySmall) }
        item { Text("Achat irréversible : le livre interdit la revente. Les règles de dotation initiale (10 PO par niveau à dépenser intégralement) ne sont pas encore automatisées.", style = MaterialTheme.typography.bodySmall) }
        items(weapons) { w ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${itemSymbol(w.name)} ${w.name} — ${if (w.price > 0) "${w.price} PO" else "Récompense de campagne"}", style = MaterialTheme.typography.titleMedium)
                Text("${w.damage} PV / ${w.unit}")
                Text(w.exercise)
                if (w.ranged) Text("Effet officiel : dégâts à distance")
                if (w.contextBonus > 0) Text("Effet officiel : +${w.contextBonus} au jet de contexte (si arme active lors du tirage)")
                if (w.price == 0) Text("Objet non vendu : à récupérer après l'Exploration du Temple de Tasmina (p. 126–127).", style = MaterialTheme.typography.bodySmall)
                else Button(enabled = state.character.po >= w.price && w.name !in state.inventory,
                    onClick = { onBuy(w) }) { Text(if (w.name in state.inventory) "Déjà possédé" else "Acheter (${w.price} PO)") }
            } }
        }
        }
    }
}

@Composable private fun Journal(state: AppState) {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Journal d'aventure", style = MaterialTheme.typography.headlineSmall) }
        if (validGuidedRoute(state)) item {
            Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🧭 Voyage en cours", style = MaterialTheme.typography.titleMedium)
                    Text("De ${state.routeStops.first()} vers ${state.destination}")
                    Text("Étape ${state.routeLegIndex + 1}/${state.routeLegKm.size} · ${"%.1f".format(state.distance)} / ${"%.1f".format(state.destinationKm)} ${if (state.character.profileType == "novice") "min" else "km"}")
                    Text("Prochain passage : ${state.routeStops[state.routeLegIndex + 1]}")
                }
            }
        }
        items(state.log) { Text(it) }
        item { Text("🤝 Compagnons présents : ${availableCompanions(state).joinToString().ifEmpty { "aucun" }}") }
        if (state.oracleJourneyActive || state.oracleCombatActive || state.paladinJourneyArmed || state.companionBardJourney) item {
            Text("Effets de compagnons : ${listOfNotNull(if (state.oracleJourneyActive) "Oracle voyage ×2" else if (state.oracleCombatActive) "Oracle combat ×2" else null, if (state.paladinJourneyArmed) "Paladin/Quêteur ${if (state.paladinJourneyUsed) "consommé" else "disponible"}" else null, if (state.companionBardJourney) "Barde musique ÷2" else null).joinToString(" · ")}")
        }
        item { Text("Quêtes") }
        items(state.quests) { Text("□ $it") }
    }
}

/** Distance maximale créditable avant le premier blocage : combat, arrivée ou étape. */
private fun nextStepBoundary(s: AppState): Double? {
    if (s.lastEncounter != null || s.destinationKm <= 0.0 || hasPendingWaypoint(s) ||
        s.distance + 0.00000001 >= s.destinationKm) return null
    val end = if (validGuidedRoute(s)) nextRouteEnd(s) else s.destinationKm
    val spacing = if ("Le Troll Conteur" in availableCompanions(s)) 3.0 else 6.0
    val checks = kotlin.math.floor((s.totalTravelKm + 0.00000001) / spacing).toInt()
    val beforeEncounter = if (s.retreatActive || s.chevalierProtectionActive) Double.POSITIVE_INFINITY
        else ((checks + 1) * spacing - s.totalTravelKm).coerceAtLeast(0.0)
    val remaining = minOf(end - s.distance, beforeEncounter)
    return remaining.takeIf { it.isFinite() && it > 0.00000001 }
}

@Composable private fun StepTrackerPanel(
    state: AppState, session: StepSnapshot, notice: String, busy: Boolean, hasSensor: Boolean,
    start: () -> Unit, pause: () -> Unit, resume: () -> Unit, stop: () -> Unit
) {
    Card(border = BorderStroke(1.dp, Color(0xFF97784A))) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("👣 Podomètre — Suivi des pas", style = MaterialTheme.typography.titleMedium)
            Text("1 pas détecté = 1 mètre de progression (convention de jeu, pas une mesure GPS).",
                style = MaterialTheme.typography.bodySmall)
            if (state.character.profileType == "novice") {
                Text("Mode novice : les trajets sont exprimés en minutes ; utilisez les boutons manuels.",
                    color = Color(0xFFEBC582), style = MaterialTheme.typography.bodySmall)
            } else if (!hasSensor) {
                Text("Aucun capteur de pas compatible : le mode manuel reste disponible.",
                    color = MaterialTheme.colorScheme.error)
            } else {
                val current = session.id == state.stepSessionId
                val count = if (current) session.steps else 0
                val cap = if (current) session.cap else 0
                val status = if (current) session.status else "idle"
                val statusText = when (status) {
                    "starting" -> "Démarrage du capteur…"
                    "running" -> if (session.ready) "En marche — écran verrouillable" else "En attente de la première mesure du capteur…"
                    "paused" -> "En pause — aucun nouveau pas compté"
                    "completed" -> stepEventMessage(session.stopKind, session.stopPlace).first + " Ouvre Aventure."
                    "stopped" -> "Session arrêtée"
                    "error" -> "Capteur indisponible"
                    else -> "Prêt à commencer"
                }
                Text("$statusText · $count pas (${count} m)", style = MaterialTheme.typography.bodyMedium)
                if (cap > 0) {
                    LinearProgressIndicator(progress = { (count.toFloat() / cap).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth())
                    Text("Limite de cette session : $cap pas, jusqu'au prochain arrêt du voyage.",
                        style = MaterialTheme.typography.bodySmall)
                }
                if (status == "error" && current && session.error.isNotBlank())
                    Text(session.error, color = MaterialTheme.colorScheme.error)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!busy && !state.mageResting && nextStepBoundary(state) != null) {
                        Button(onClick = start) { Text("▶ Démarrer") }
                    }
                    if (status == "starting" || status == "running") {
                        OutlinedButton(onClick = pause) { Text("⏸ Pause") }
                    }
                    if (status == "paused") {
                        Button(onClick = resume) { Text("▶ Reprendre") }
                    }
                    if (status in listOf("starting", "running", "paused")) {
                        OutlinedButton(onClick = stop) { Text("■ Arrêter") }
                    }
                }
                if (busy) Text("Ajout manuel de kilomètres désactivé pendant cette session (anti-doublon).",
                    style = MaterialTheme.typography.bodySmall)
                if (notice.isNotBlank()) Text(notice, style = MaterialTheme.typography.bodySmall)
                val ctx = androidx.compose.ui.platform.LocalContext.current
                val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                val alertsAllowed = nm.areNotificationsEnabled() &&
                    (nm.getNotificationChannel(StepTrackingService.EVENT_CHANNEL_ID)?.importance
                        ?: android.app.NotificationManager.IMPORTANCE_HIGH) > android.app.NotificationManager.IMPORTANCE_NONE
                if (!alertsAllowed) Text("⚠️ Les alertes sont désactivées dans Android. Le podomètre fonctionne, mais tu ne recevras pas la notification de rencontre ou d'arrivée. Active les notifications de Dungeon of Fitness dans les paramètres du téléphone.",
                    color = Color(0xFFEBC582), style = MaterialTheme.typography.bodySmall)
                Text("Une notification d'aventure est envoyée à chaque seuil de rencontre, étape ou arrivée, si les notifications sont autorisées. Le suivi s'arrête au seuil : les pas supplémentaires ne sont pas crédités. Les restrictions d'énergie peuvent affecter le comptage écran éteint.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun travel(s: AppState, km: Double, viaSteps: Boolean = false): AppState {
    if (s.lastEncounter != null || s.destinationKm <= 0.0 || !km.isFinite() || km <= 0.0 || hasPendingWaypoint(s)) return s
    val old = s.distance
    val stageEnd = if (validGuidedRoute(s)) nextRouteEnd(s) else s.destinationKm
    val encounterSpacing = if ("Le Troll Conteur" in availableCompanions(s)) 3.0 else 6.0
    val oldChecks = kotlin.math.floor((s.totalTravelKm + 0.00000001) / encounterSpacing).toInt()
    val beforeEncounter = if (s.retreatActive || s.chevalierProtectionActive) Double.POSITIVE_INFINITY
        else ((oldChecks + 1) * encounterSpacing - s.totalTravelKm).coerceAtLeast(0.0)
    // Une action ne peut ni sauter une étape ni franchir plusieurs seuils de rencontre.
    val increment = minOf(km, (stageEnd - old).coerceAtLeast(0.0), beforeEncounter)
    if (increment <= 0.00000001) return s
    val newDistance = (old + increment).coerceAtMost(s.destinationKm)
    val newTotal = s.totalTravelKm + if (s.retreatActive) 0.0 else increment
    val newChecks = kotlin.math.floor((newTotal + 0.00000001) / encounterSpacing).toInt()
    val encounterTriggered = !s.retreatActive && !s.chevalierProtectionActive && newChecks > oldChecks
    val encounter = if (encounterTriggered) generateEncounter(s.equippedWeapon, s.equippedSupport, s.character, s.bardJourney, s.inventory, s.equippedArmor, s.mageJewelSpent) else s.lastEncounter
    val logs = if (encounter != null && encounterTriggered) listOf(
        "Rencontre : ${encounter.name} ×${encounter.quantity}" + if (s.bardJourney) {
            if (encounter.effect.contains("quantité fixe", ignoreCase = true)) " — Barde audacieux : quantité fixe, pas de bonus."
            else " — Barde audacieux : quantité doublée et butin ×2,5."
        } else ""
    ) + s.log else if (viaSteps && !encounterTriggered) s.log else if (viaSteps) listOf("Podomètre : seuil de rencontre atteint (jet non applicable à cette zone).") + s.log else listOf("+${"%.2f".format(increment)} ${if (s.character.profileType == "novice") "minute(s)" else "km"} parcouru(s)" +
        if (encounterTriggered) " ; jet de rencontre ignoré (type indisponible)." else "") + s.log
    return s.copy(distance = newDistance, totalTravelKm = newTotal, lastEncounter = encounter,
        meditationActive = if (encounterTriggered) false else s.meditationActive,
        warriorActive = if (encounterTriggered) false else s.warriorActive,
        vigorActive = if (encounterTriggered) false else s.vigorActive,
        quantumMode = if (encounterTriggered) "" else s.quantumMode,
        encounters = s.encounters + if (encounterTriggered) 1 else 0, log = logs.take(100))
}

private data class EncounterRule(val name: String, val reward: Int, val hp: Int, val zones: String, val effect: String)
private val encounterRules = listOf(
    EncounterRule("Liquebide", 5, 50, "Fitland, Maaskinland", "Quantité ×10"),
    EncounterRule("Chardio", 5, 50, "Toutes", ""),
    EncounterRule("Gnome Zombie", 10, 100, "Fitland, Maaskinland", ""),
    EncounterRule("Acronide", 10, 100, "Fitland, Maaskinland", ""),
    EncounterRule("Selfine", 10, 100, "Fitland, Maaskinland", "Fermez les yeux"),
    EncounterRule("Gobeleau", 10, 100, "Toutes", ""),
    EncounterRule("Fitnéant", 10, 100, "Fitland, Maaskinland", ""),
    EncounterRule("Gnome zombie", 10, 100, "Maaskinland", ""),
    EncounterRule("Trollympien", 150, 1500, "Fitland", "Contexte +2"),
    EncounterRule("Plantosaure", 15, 150, "Fitland, Maaskinland, Anabolie", ""),
    EncounterRule("Zomfit", 15, 150, "Fitland, Anabolie", ""),
    EncounterRule("Squatosaure", 20, 200, "Fitland, Pays des Khôls, Anabolie", ""),
    EncounterRule("Zumbhaka", 30, 300, "Fitland, Anabolie", "Dansez"),
    EncounterRule("Pillard Martial", 40, 400, "Toutes", ""),
    EncounterRule("Mesossé", 50, 500, "Fitland, Anabolie", ""),
    EncounterRule("Guerrier Khol", 50, 500, "Toutes", ""),
    EncounterRule("Orcfit", 50, 500, "Fitland", "Posing culturiste avant chaque attaque"),
    EncounterRule("Nosferathonien", 50, 500, "Toutes", ""),
    EncounterRule("Transpinja", 70, 700, "Toutes", ""),
    EncounterRule("Permabulker", 100, 1000, "Toutes", ""),
    EncounterRule("Chathlète", 100, 1000, "Fitland", ""),
    EncounterRule("Powerchat", 150, 1500, "Fitland", ""),
    EncounterRule("Abomination", 300, 3000, "Fitland, Maaskinland", "Quantité fixe : 1"),
    EncounterRule("Bulkœil", 300, 3000, "Toutes", "Quantité fixe : 1"),
    EncounterRule("Chose", 300, 3000, "Anabolie", "Quantité fixe : 1"),
    EncounterRule("Hydrocoolique", 900, 9000, "Falaises Olympiques, Omega Beach", "Quantité fixe : 1"),
    EncounterRule("Nécrolifter", 300, 3000, "Toutes", "Quantité fixe : 1"),
    EncounterRule("Marchand Itinérant", 0, 0, "Toutes sauf Fitland", "Inoffensif : commerce"),
    EncounterRule("Gardes du Fitland", 1000, 10000, "Fitland", "Perdez tous vos PO ou affrontez-les ; quantité fixe : 1"),
    EncounterRule("Bulker Fruité", 400, 4000, "Toutes", "Inoffensif : obtenez un shaker de votre choix"),
    EncounterRule("Chevalier Templiométrique", 300, 3000, "Toutes", "Inoffensif : aucune rencontre jusqu’à destination"),
    EncounterRule("Elfit", 100, 1000, "Toutes", "Inoffensif : échangez un objet contre un bijou de mage"),
    EncounterRule("Amazone", 100, 1000, "Toutes", "Inoffensif : échangez un objet contre un objet féérique"),
    EncounterRule("Bibilithe", 200, 2000, "Toutes", "Perdez un objet et tous vos PO ou affrontez-le"),
    EncounterRule("Clan Nazcool", 900, 9000, "Toutes", "Affrontez-les ou fin de la poursuite de l’Orienteur ; quantité fixe : 1"),
    EncounterRule("Orienteur obsédé", 0, 400, "Toutes", "Poursuite lors des rencontres suivantes ; quantité fixe : 1")
)

private fun generateEncounter(equippedWeapon: String, equippedSupport: String, character: Character, bardJourney: Boolean = false, inventory: List<String> = emptyList(), equippedArmor: String = "", mageJewelSpent: Int = 0): Encounter? {
    val typeDice = List(7) { Random.nextInt(1, 7) }
    val roll = typeDice.sum()
    val rule = encounterRules[roll - 7]
    // Campagne actuelle : Fitland. Les rencontres d'autres régions sont ignorées.
    if (!isFitlandEncounter(rule)) return null
    val fixedQuantity = rule.effect.contains("Quantité fixe", ignoreCase = true)
    val quantityDie = if (fixedQuantity) 0 else Random.nextInt(1, 7)
    val quantity = if (fixedQuantity) 1 else quantityDie * (if (roll == 7) 10 else 1) * (if (bardJourney) 2 else 1)
    // Mode audacieux : +25 % sur le butin des adversaires doublés, arrondi à l'entier inférieur.
    // Une rencontre à quantité fixe n'est pas doublée et ne bénéficie pas de ce bonus.
    val reward = (rule.reward.toLong() * quantity * (if (bardJourney && !fixedQuantity) 5L else 4L) / 4L).toInt()
    val contextDie = Random.nextInt(1, 7)
    val equipmentBonus = weapons.firstOrNull { it.name == equippedWeapon }?.contextBonus ?: 0
    val supportBonus = supports.firstOrNull { it.name == equippedSupport && it.name in inventory }?.context ?: 0
    val armorBonus = armors.firstOrNull { it.name == equippedArmor && it.name in inventory }?.context ?: 0
    val acrobatBonus = if (character.profileType == "advanced" && character.className == "Acrobate") 2 else 0
    val agilityBonus = ScrollCombatRules.agilityBonus(inventory)
    val jewelBonus = if ("Bijou de mage" in inventory) MageSpellRules.jewelBonus(mageJewelSpent) else 0
    val contextBonus = equipmentBonus + supportBonus + armorBonus + (if (roll == 15) 2 else 0) + acrobatBonus + agilityBonus + jewelBonus
    // La table p. 42 s'arrête à 6 : les dépassements sont traités comme 6.
    val contextRoll = (contextDie + contextBonus).coerceAtMost(6)
    val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
    val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
    val safe = rule.effect.startsWith("Inoffensif")
    return Encounter(rule.name, quantity, rule.hp, reward, contexts[contextRoll - 1], effects[contextRoll - 1], roll, rule.hp * quantity, rule.effect, safe, contextDie, contextBonus, listOfNotNull(if (equipmentBonus > 0) equippedWeapon else null, if (supportBonus > 0) equippedSupport else null, if (armorBonus > 0) equippedArmor else null, if (acrobatBonus > 0) "Acrobate +2" else null, if (agilityBonus > 0) "Manuel d’agilité +2" else null, if (jewelBonus > 0) "Bijou de mage +$jewelBonus" else null).joinToString(" + "), enemyHp = List(quantity) { rule.hp }, typeDice = typeDice, quantityDie = quantityDie, contextArmor = if (equippedArmor in inventory) equippedArmor else "")
}

// Les anciennes sauvegardes n'avaient qu'une réserve commune : reconstruction déterministe.
// Une relance s'effectue une seule fois avant le premier coup ; les deux résultats
// sont sauvegardés jusqu'au choix pour ne pas perdre le second jet en arrière-plan.
private fun startArmorReroll(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if (e.contextArmor != "Armure respirante" || "Armure respirante" !in s.inventory ||
        e.armorRerollUsed || e.contextDie !in 1..6 || e.mysticUsed || e.flexibilityScrollUsed ||
        e.hunterUsed || e.bulkerUsed || encounterHp(e).any { it != e.hp }) return s
    val second = Random.nextInt(1, 7)
    return s.copy(lastEncounter = e.copy(armorSecondDie = second, armorRerollUsed = true),
        log = (listOf("Armure respirante (p. 164) : deuxième jet de contexte = $second ; choix du joueur en attente.") + s.log).take(100))
}

private fun chooseArmorReroll(s: AppState, chooseSecond: Boolean): AppState {
    val e = s.lastEncounter ?: return s
    if (!e.armorRerollUsed || e.armorSecondDie !in 1..6) return s
    val die = if (chooseSecond) e.armorSecondDie else e.contextDie
    val index = ArmorRules.effectiveContext(die, e.contextBonus) - 1
    val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
    val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
    val updated = e.copy(contextDie = die, context = contexts[index], exercise = effects[index], armorSecondDie = 0)
    return s.copy(lastEncounter = updated, log = (listOf("Armure respirante : contexte ${if (chooseSecond) "relancé" else "initial"} conservé ($die + ${e.contextBonus} → ${index + 1}).") + s.log).take(100))
}

private fun encounterHp(e: Encounter): List<Int> {
    if (e.enemyHp.size == e.quantity) return e.enemyHp
    var remaining = e.remainingHp.coerceAtLeast(0)
    return List(e.quantity) {
        val hp = minOf(e.hp, remaining)
        remaining -= hp
        hp
    }
}

private fun applyDamage(s: AppState, damage: Int, target: Int?, attack: Boolean = false): AppState = s.lastEncounter?.let { e ->
    val before = encounterHp(e)
    val outcome = MageSpellRules.applyAttack(before, damage, target, if (attack) e.acidTargets else emptyList())
    if (outcome.inflicted == 0) return@let s
    val remaining = outcome.remaining.sum()
    val markNote = if (outcome.markedHitCount > 0) " · Flèche acido-basique : +50 PV sur ${outcome.markedHitCount} cible(s) touchée(s)." else ""
    if (remaining > 0) s.copy(lastEncounter = e.copy(remainingHp = remaining, enemyHp = outcome.remaining),
        log = (listOf("${e.name} : ${outcome.inflicted} dégâts, $remaining PV restants (${outcome.remaining.count { it > 0 }} debout)$markNote") + s.log).take(100))
    else s.copy(character = s.character.copy(po = s.character.po + e.reward), lastEncounter = null,
        meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
        log = (listOf("${e.name} vaincu(s), butin : ${e.reward} PO$markNote") + s.log).take(100))
} ?: s

private fun resolve(s: AppState, damage: Int, attack: Boolean = false): AppState = applyDamage(s, damage, null, attack)
private fun resolveTarget(s: AppState, damage: Int, index: Int, attack: Boolean = false): AppState = applyDamage(s, damage, index, attack)

/** Les quatre sorts conventionnels (p. 176) ne coûtent pas de PO.
 * Chaque lancer exige la confirmation du mot écrit avec le corps dans l'interface.
 */
private fun castMageSpell(s: AppState, spell: String, target: Int): AppState {
    if (spell == "resume") return if (s.mageResting) s.copy(mageResting = false,
        log = (listOf("Second souffle quantique : partie reprise au point sauvegardé.") + s.log).take(100)) else s
    if (s.mageResting || !MageSpellRules.mayCast(spell, s.character.className, s.character.agility,
            s.character.profileType == "novice", s.character.adaptation)) return s
    if (spell == "rest") return s.copy(mageResting = true,
        log = (listOf("Second souffle quantique (p. 176) : rituel SECONDSOUFFLE confirmé ; pause de la partie et du podomètre.") + s.log).take(100))
    val e = s.lastEncounter ?: return s
    if (e.safe || target < -1) return s
    return when (spell) {
        "force" -> {
            if (target !in encounterHp(e).indices || encounterHp(e)[target] <= 0) s
            else {
                val after = resolveTarget(s, warriorDamage(s, 50), target, attack = true)
                after.copy(log = (listOf("Boule de force (p. 176) : FORCE tracé ; 50 PV à distance de base sur ${e.name} n°${target + 1}.") + after.log).take(100))
            }
        }
        "acid" -> if (target !in encounterHp(e).indices || encounterHp(e)[target] <= 0 || target in e.acidTargets) s
            else s.copy(lastEncounter = e.copy(acidTargets = e.acidTargets + target),
                log = (listOf("Flèche acido-basique (p. 176) : ACIDOBASIQUE tracé, ${e.name} n°${target + 1} marqué (+50 PV par attaque qui le touche).") + s.log).take(100))
        "cardio" -> {
            if (e.contextDie !in 1..6 || e.armorSecondDie != 0 || e.flexibilityScrollUsed || encounterHp(e).any { it != e.hp }) s
            else {
                val idx = MageSpellRules.contextAfterCardio(e.contextDie, e.contextBonus) - 1
                val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
                val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
                val updated = e.copy(contextBonus = e.contextBonus + 1, context = contexts[idx], exercise = effects[idx],
                    mageCardioCasts = e.mageCardioCasts + 1, contextWeapon = listOf(e.contextWeapon, "Onde de cardio +1").filter { it.isNotBlank() }.joinToString(" + "))
                s.copy(lastEncounter = updated, log = (listOf("Onde de cardio (p. 176) : CARDIO tracé, contexte +1, nouveau résultat ${idx + 1}.") + s.log).take(100))
            }
        }
        else -> s
    }
}

/** Groupe entier : 700 PV par adversaire vivant. Convention explicitée dans l'UI. */
private fun useDarkShaker(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if (e.safe || "Shaker sombre" !in s.inventory) return s
    val result = groupSplashDamage(encounterHp(e), 700)
    if (result.damageDealt <= 0) return s
    val remaining = result.remainingHp.sum()
    val base = s.copy(inventory = removeOne(s.inventory, "Shaker sombre"))
    val note = "Shaker sombre (livre p. 173 ; convention 700 PV par ennemi) : " +
        "${result.damageDealt} dégâts à distance répartis sur ${result.hitCount} adversaire(s)."
    return if (remaining > 0) base.copy(
        lastEncounter = e.copy(remainingHp = remaining, enemyHp = result.remainingHp),
        log = (listOf(note, "${e.name} : $remaining PV restants.") + s.log).take(100))
    else base.copy(character = s.character.copy(po = s.character.po + e.reward), lastEncounter = null,
        meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false,
        warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
        log = (listOf(note, "${e.name} vaincu(s), butin : ${e.reward} PO") + s.log).take(100))
}

private fun removeOne(items: List<String>, name: String): List<String> {
    val index = items.indexOf(name)
    return if (index < 0) items else items.filterIndexed { i, _ -> i != index }
}

private fun skipEncounter(s: AppState): AppState = s.lastEncounter?.let { e ->
    if (!e.safe && e.context != "Ils n'ont rien vu.") return@let s
    s.copy(lastEncounter = null, meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false, log = (listOf("Rencontre évitée ou pacifique : ${e.name}. Aucun butin ni retour obligatoire.") + s.log).take(100))
} ?: s

/** Rencontre pacifique, p. 43 : escorte jusqu'à l'arrivée du voyage en cours.
 * La distance physique est toujours créditée, mais les jets de rencontre sont supprimés.
 */
private fun acceptChevalierEscort(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if (!e.safe || e.name != "Chevalier Templiométrique" || s.destinationKm <= 0.0) return s
    val cleared = skipEncounter(s)
    return cleared.copy(chevalierProtectionActive = true,
        log = (listOf("🛡️ Chevalier templiométrique (p. 43) : escorte acceptée, aucune rencontre jusqu'à ${s.destination}. Les kilomètres continuent de compter.") + cleared.log).take(100))
}

/** Rencontre pacifique, p. 43 : shaker au choix, attribué une seule fois. */
private fun acceptBulkerShaker(s: AppState, name: String): AppState {
    val e = s.lastEncounter ?: return s
    val eligible = listOf("Shaker persuasion", "Shaker poison", "Shaker vigueur", "Shaker quantique", "Shaker illusion", "Shaker sombre")
    if (!e.safe || e.name != "Bulker Fruité" || name !in eligible) return s
    val cleared = skipEncounter(s)
    return cleared.copy(inventory = cleared.inventory + name,
        log = (listOf("🥤 Bulker Fruité (p. 43) : $name offert une fois ; aucun PO attribué.") + cleared.log).take(100))
}

/** Gardes du Fitland, p. 43 : paiement intégral OU combat, sans butin pour le paiement. */
private fun payFitlandGuards(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if (e.safe || e.name != "Gardes du Fitland" || encounterHp(e).any { it != e.hp }) return s
    return s.copy(character = s.character.copy(po = 0), lastEncounter = null,
        meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false,
        warriorActive = false, oracleCombatActive = false,
        cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
        log = (listOf("🛡️ Gardes du Fitland (p. 43) : ${s.character.po} PO payés pour éviter le combat. Aucun butin.") + s.log).take(100))
}

private fun useHunterAbility(s: AppState): AppState = s.lastEncounter?.let { e ->
    if (s.character.profileType != "advanced" || s.character.className != "Chasseur" || e.safe || e.hunterUsed || e.quantity <= 1 || encounterHp(e).any { it != e.hp }) return@let s
    val reduction = if (e.roll == 7) 10 else 1
    if (e.quantity <= reduction || e.effect.contains("quantité fixe", ignoreCase = true)) return@let s
    val count = e.quantity - reduction
    val updated = e.copy(quantity = count, remainingHp = e.hp * count,
        reward = (e.reward.toLong() * count / e.quantity).toInt(),
        enemyHp = List(count) { e.hp }, hunterUsed = true)
    s.copy(lastEncounter = updated,
        log = (listOf("Chasseur (livre p. 41) : dé de quantité réduit de 1 (${reduction} adversaire(s) en moins) ; $count adversaire(s), récompense ajustée.") + s.log).take(100))
} ?: s

private fun fleeEncounter(s: AppState, towardDestination: Boolean): AppState = s.lastEncounter?.let { e ->
    if (e.safe || s.retreatActive) return@let s
    val acrobatForward = towardDestination && s.character.profileType == "advanced" && s.character.className == "Acrobate" && s.destinationKm > 0.0
    val currentStageStart = if (validGuidedRoute(s)) routeLegStart(s) else 0.0
    val returnDistance = if (acrobatForward) (s.destinationKm - s.distance).coerceAtLeast(0.0)
        else (s.distance - currentStageStart).coerceAtLeast(0.0)
    val start = if (acrobatForward) s.destination else s.character.location
    s.copy(lastEncounter = null, meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
        retreatActive = returnDistance > 0.0, retreatOrigin = start,
        oracleJourneyActive = false, chevalierProtectionActive = false, paladinJourneyArmed = false, paladinJourneyUsed = false,
        companionBardJourney = false,
        destination = start, destinationKm = returnDistance, distance = 0.0,
        routeStops = emptyList(), routeLegKm = emptyList(), routeLegIndex = 0,
        log = (listOf("Fuite devant ${e.name} : ${if (acrobatForward) "destination choisie (Acrobate)" else "retour"} de ${"%.1f".format(returnDistance)} unité(s) vers $start, sans rencontre et sans butin. Exercices de contexte confirmés.") + s.log).take(100))
} ?: s

// Sauvegarde locale, sans compte ni connexion. Compatible avec les données de la v0.1.
private fun saveState(context: Context, state: AppState) {
    val c = state.character
    val character = JSONObject().apply {
        put("name", c.name); put("className", c.className); put("divinity", c.divinity)
        put("level", c.level); put("force", c.force); put("endurance", c.endurance)
        put("agility", c.agility); put("dexterity", c.dexterity); put("mental", c.mental)
        put("po", c.po); put("location", c.location)
        put("profileType", c.profileType); put("pushups30", c.pushups30)
        put("jumpingJacks180", c.jumpingJacks180); put("balanceReps", c.balanceReps)
        put("balanceCapable", c.balanceCapable); put("vigour", c.vigour)
        put("resistance", c.resistance); put("adaptation", c.adaptation)
        put("strengthPercent", c.strengthPercent); put("enduranceHours", c.enduranceHours)
        put("enduranceDistance", c.enduranceDistance); put("agilityMoves", c.agilityMoves)
        put("dexterityPercent", c.dexterityPercent); put("dexteritySeconds", c.dexteritySeconds)
        put("mentalDifficulty", c.mentalDifficulty); put("mentalBonus", c.mentalBonus)
        put("primaryActivity", c.primaryActivity); put("specialAbility", c.specialAbility)
        put("unlockedClasses", JSONArray(c.unlockedClasses)); put("completedAchievements", JSONArray(c.completedAchievements))
    }
    val obj = JSONObject().apply {
        put("schema", 30); put("character", character); put("distance", state.distance)
        put("destination", state.destination); put("destinationKm", state.destinationKm)
        put("totalTravelKm", state.totalTravelKm)
        put("encounters", state.encounters); put("retreatActive", state.retreatActive); put("retreatOrigin", state.retreatOrigin)
        put("routeStops", JSONArray(state.routeStops)); put("routeLegKm", JSONArray(state.routeLegKm)); put("routeLegIndex", state.routeLegIndex)
        put("visitedPlaces", JSONArray(state.visitedPlaces)); put("completedChapters", JSONArray(state.completedChapters)); put("manualChapterAccess", JSONArray(state.manualChapterAccess)); put("manualCompanions", JSONArray(state.manualCompanions)); put("dismissedCompanions", JSONArray(state.dismissedCompanions)); put("fairyTeleportUsed", state.fairyTeleportUsed); put("claimedChapterLoot", JSONArray(state.claimedChapterLoot))
        put("oracleJourneyActive", state.oracleJourneyActive); put("oracleCombatActive", state.oracleCombatActive)
        put("paladinJourneyArmed", state.paladinJourneyArmed); put("paladinJourneyUsed", state.paladinJourneyUsed)
        put("companionBardJourney", state.companionBardJourney)
        put("stepSessionId", state.stepSessionId); put("stepAppliedSteps", state.stepAppliedSteps)
        put("stepLastClosedId", state.stepLastClosedId)
        put("log", JSONArray(state.log)); put("inventory", JSONArray(state.inventory))
        put("equippedWeapon", state.equippedWeapon); put("equippedSupport", state.equippedSupport); put("equippedArmor", state.equippedArmor); put("mageJewelSpent", state.mageJewelSpent); put("mageResting", state.mageResting); put("chevalierProtectionActive", state.chevalierProtectionActive); put("meditationActive", state.meditationActive)
        put("vigorActive", state.vigorActive); put("quantumMode", state.quantumMode); put("illusionActive", state.illusionActive)
        put("cardioScrollActive", state.cardioScrollActive); put("forceScrollActive", state.forceScrollActive)
        put("congestionScrollActive", state.congestionScrollActive)
        put("abilityDay", state.abilityDay); put("scoutUses", state.scoutUses); put("warriorUsed", state.warriorUsed); put("warriorActive", state.warriorActive); put("bardUses", state.bardUses); put("bardJourney", state.bardJourney); put("cavalierUsed", state.cavalierUsed)
        put("quests", JSONArray(state.quests))
        state.lastEncounter?.let { e -> put("lastEncounter", JSONObject().apply {
            put("name", e.name); put("quantity", e.quantity); put("hp", e.hp)
            put("reward", e.reward); put("context", e.context); put("exercise", e.exercise)
            put("roll", e.roll); put("remainingHp", e.remainingHp); put("effect", e.effect); put("safe", e.safe)
            put("contextDie", e.contextDie); put("contextBonus", e.contextBonus); put("contextWeapon", e.contextWeapon)
            put("initialQuantity", e.initialQuantity); put("illusionDie", e.illusionDie); put("hunterUsed", e.hunterUsed); put("mysticUsed", e.mysticUsed); put("mysticDescription", e.mysticDescription)
            put("enemyHp", JSONArray(encounterHp(e))); put("typeDice", JSONArray(e.typeDice)); put("quantityDie", e.quantityDie); put("bulkerUsed", e.bulkerUsed); put("flexibilityScrollUsed", e.flexibilityScrollUsed); put("armorSecondDie", e.armorSecondDie); put("armorRerollUsed", e.armorRerollUsed); put("contextArmor", e.contextArmor); put("acidTargets", JSONArray(e.acidTargets)); put("mageCardioCasts", e.mageCardioCasts)
        }) }
    }
    context.getSharedPreferences("dof_save", Context.MODE_PRIVATE).edit()
        .putString("state", obj.toString()).apply()
}

private fun readStringArray(array: JSONArray?): List<String> =
    if (array == null) emptyList() else (0 until array.length()).map { array.optString(it) }

private fun loadState(context: Context): AppState {
    val raw = context.getSharedPreferences("dof_save", Context.MODE_PRIVATE)
        .getString("state", null) ?: return AppState()
    return try {
        val obj = JSONObject(raw)
        val cj = obj.optJSONObject("character") ?: JSONObject()
        val base = Character()
        val c = base.copy(
            name = cj.optString("name", base.name),
            className = cj.optString("className", base.className),
            divinity = cj.optString("divinity", base.divinity),
            level = cj.optInt("level", base.level), force = cj.optInt("force", base.force),
            endurance = cj.optInt("endurance", base.endurance),
            agility = cj.optInt("agility", base.agility),
            dexterity = cj.optInt("dexterity", base.dexterity),
            mental = cj.optInt("mental", base.mental), po = cj.optInt("po", base.po),
            location = cj.optString("location", base.location),
            profileType = cj.optString("profileType", base.profileType),
            pushups30 = cj.optInt("pushups30", 0), jumpingJacks180 = cj.optInt("jumpingJacks180", 0),
            balanceReps = cj.optInt("balanceReps", 0), balanceCapable = cj.optBoolean("balanceCapable", false),
            vigour = cj.optInt("vigour", 0), resistance = cj.optInt("resistance", 0),
            adaptation = cj.optInt("adaptation", 0),
            strengthPercent = cj.optString("strengthPercent", ""),
            enduranceHours = cj.optString("enduranceHours", ""),
            enduranceDistance = cj.optString("enduranceDistance", ""),
            agilityMoves = cj.optString("agilityMoves", ""),
            dexterityPercent = cj.optString("dexterityPercent", ""),
            dexteritySeconds = cj.optString("dexteritySeconds", ""),
            mentalDifficulty = cj.optString("mentalDifficulty", ""),
            mentalBonus = cj.optInt("mentalBonus", 0),
            primaryActivity = cj.optString("primaryActivity", ""),
            specialAbility = cj.optString("specialAbility", ""),
            unlockedClasses = readStringArray(cj.optJSONArray("unlockedClasses")),
            completedAchievements = readStringArray(cj.optJSONArray("completedAchievements"))
        )
        fun strings(key: String, fallback: List<String>): List<String> {
            val array = obj.optJSONArray(key) ?: return fallback
            return (0 until array.length()).map { array.optString(it) }
        }
        val ej = obj.optJSONObject("lastEncounter")
        val encounter = ej?.let {
            Encounter(it.optString("name"), it.optInt("quantity"), it.optInt("hp"),
                it.optInt("reward"), it.optString("context"), it.optString("exercise"),
                it.optInt("roll", 0), it.optInt("remainingHp", it.optInt("hp") * it.optInt("quantity")),
                it.optString("effect", ""), it.optBoolean("safe", false),
                it.optInt("contextDie", 0), it.optInt("contextBonus", 0), it.optString("contextWeapon", ""),
                it.optInt("initialQuantity", it.optInt("quantity")), it.optInt("illusionDie", 0),
                it.optJSONArray("enemyHp")?.let { arr -> (0 until arr.length()).map { i -> arr.optInt(i) } } ?: emptyList(),
                it.optBoolean("hunterUsed", false), it.optBoolean("mysticUsed", false), it.optString("mysticDescription", ""),
                it.optJSONArray("typeDice")?.let { arr -> (0 until arr.length()).map { i -> arr.optInt(i) } } ?: emptyList(),
                it.optInt("quantityDie", 0), it.optBoolean("bulkerUsed", false), it.optBoolean("flexibilityScrollUsed", false),
                it.optInt("armorSecondDie", 0), it.optBoolean("armorRerollUsed", false), it.optString("contextArmor", ""),
                it.optJSONArray("acidTargets")?.let { arr -> (0 until arr.length()).map { i -> arr.optInt(i) } } ?: emptyList(),
                it.optInt("mageCardioCasts", 0))
        }
        val baseState = AppState()
        baseState.copy(character = if (c.profileType == "advanced" && c.className in classAchievements) c.copy(unlockedClasses = (c.unlockedClasses + c.className).distinct()) else c,
            distance = obj.optDouble("distance", 0.0),
            destination = obj.optString("destination", baseState.destination),
            destinationKm = obj.optDouble("destinationKm", baseState.destinationKm),
            totalTravelKm = obj.optDouble("totalTravelKm", obj.optDouble("distance", 0.0)),
            encounters = obj.optInt("encounters", 0), lastEncounter = encounter,
            retreatActive = obj.optBoolean("retreatActive", false), retreatOrigin = obj.optString("retreatOrigin", ""),
            routeStops = strings("routeStops", emptyList()),
            routeLegKm = obj.optJSONArray("routeLegKm")?.let { arr -> (0 until arr.length()).map { i -> arr.optDouble(i, 0.0) } } ?: emptyList(),
            routeLegIndex = obj.optInt("routeLegIndex", 0),
            visitedPlaces = strings("visitedPlaces", emptyList()),
            completedChapters = strings("completedChapters", emptyList()).filter { title -> storyChapters.any { it.title == title } },
            manualCompanions = strings("manualCompanions", emptyList()).filter { name -> storyCompanions.any { it.name == name } },
            dismissedCompanions = strings("dismissedCompanions", emptyList()).filter { name -> storyCompanions.any { it.name == name } },
            fairyTeleportUsed = obj.optBoolean("fairyTeleportUsed", false),
            oracleJourneyActive = obj.optBoolean("oracleJourneyActive", false),
            oracleCombatActive = obj.optBoolean("oracleCombatActive", false),
            paladinJourneyArmed = obj.optBoolean("paladinJourneyArmed", false),
            paladinJourneyUsed = obj.optBoolean("paladinJourneyUsed", false),
            companionBardJourney = obj.optBoolean("companionBardJourney", false),
            stepSessionId = obj.optString("stepSessionId", ""),
            stepAppliedSteps = obj.optInt("stepAppliedSteps", 0).coerceAtLeast(0),
            stepLastClosedId = obj.optString("stepLastClosedId", ""),
            claimedChapterLoot = strings("claimedChapterLoot", emptyList()).filter { title -> storyChapters.any { it.title == title } },
            manualChapterAccess = strings("manualChapterAccess", emptyList()).filter { title -> storyChapters.any { it.title == title && it.restricted } },
            equippedWeapon = obj.optString("equippedWeapon", ""), equippedSupport = obj.optString("equippedSupport", ""), equippedArmor = obj.optString("equippedArmor", ""), mageJewelSpent = obj.optInt("mageJewelSpent", 0).coerceAtLeast(0),
            mageResting = obj.optBoolean("mageResting", false), chevalierProtectionActive = obj.optBoolean("chevalierProtectionActive", false), meditationActive = obj.optBoolean("meditationActive", false),
            vigorActive = obj.optBoolean("vigorActive", false), quantumMode = obj.optString("quantumMode", ""), illusionActive = obj.optBoolean("illusionActive", false),
            cardioScrollActive = obj.optBoolean("cardioScrollActive", false), forceScrollActive = obj.optBoolean("forceScrollActive", false),
            congestionScrollActive = obj.optBoolean("congestionScrollActive", false),
            log = strings("log", baseState.log), inventory = strings("inventory", baseState.inventory),
            abilityDay = obj.optString("abilityDay", ""), scoutUses = obj.optInt("scoutUses", 0),
            warriorUsed = obj.optBoolean("warriorUsed", false), warriorActive = obj.optBoolean("warriorActive", false), bardUses = if (obj.has("bardUses")) obj.optInt("bardUses", 0) else if (obj.optBoolean("bardUsed", false)) 1 else 0, bardJourney = obj.optBoolean("bardJourney", false), cavalierUsed = obj.optBoolean("cavalierUsed", false),
            quests = strings("quests", baseState.quests))
    } catch (_: Exception) { AppState() }
}

// Limites quotidiennes calculées sur la date locale de l'appareil (mode hors ligne).
private fun dailyState(s: AppState): AppState {
    val today = java.time.LocalDate.now().toString()
    return if (s.abilityDay == today) s else s.copy(abilityDay = today, scoutUses = 0, warriorUsed = false, bardUses = 0, cavalierUsed = false)
}

private fun planJourney(state: AppState, destination: String, km: Double, scout: Boolean, bardAudacious: Boolean, followMap: Boolean,
    companionMusic: Boolean = false, oraclePrayer: Boolean = false, paladinPrayer: Boolean = false): AppState {
    val s = dailyState(state)
    if (!km.isFinite() || km <= 0.0 || destination == s.character.location || destination !in fitlandPlaces || s.lastEncounter != null || s.destinationKm > 0.0) return s
    val allowed = scout && s.character.profileType == "advanced" && s.character.className == "Éclaireur" && s.scoutUses < 2
    val bard = scout && s.character.profileType == "advanced" && s.character.className == "Barde" && s.bardUses < 2
    val bardCompanion = companionMusic && !allowed && !bard && "Barde" in availableCompanions(s)
    val effective = CompanionRules.journeyDistance(km, allowed || bard || bardCompanion)
    val audacious = bard && bardAudacious
    val oracle = oraclePrayer && "Oracle de Tasmina" in availableCompanions(s)
    val paladin = paladinPrayer && "Le Paladin et le Quêteur" in availableCompanions(s)
    val route = if (followMap) mapRoute(s.character.location, destination)?.takeIf { kotlin.math.abs(it.km - km) < 0.00001 } else null
    val stops = route?.places ?: emptyList()
    val legs = if (route != null) route.places.zipWithNext { a, b ->
        (mapNeighbors(a).firstOrNull { it.first == b }?.second ?: 0).toDouble() * (if (allowed || bard || bardCompanion) 0.5 else 1.0)
    } else emptyList()
    val guided = legs.isNotEmpty() && legs.all { it > 0.0 } && kotlin.math.abs(legs.sum() - effective) < 0.00001
    val note = when {
        allowed -> " (Éclaireur : demi-distance, ${s.scoutUses + 1}/2 aujourd’hui)"
        audacious -> " (Barde, règle maison : demi-distance ${s.bardUses + 1}/2 ; mode audacieux, quantité ×2, butin ×2,5)"
        bard -> " (Barde, règle maison : demi-distance ${s.bardUses + 1}/2 ; mode prudent, rencontres normales)"
        bardCompanion -> " (Barde compagnon : musique, demi-distance, p. 113)"
        else -> ""
    }
    return s.copy(destination = destination, destinationKm = effective, distance = 0.0,
        scoutUses = s.scoutUses + if (allowed) 1 else 0,
        bardUses = s.bardUses + if (bard) 1 else 0,
        bardJourney = audacious, companionBardJourney = bardCompanion,
        oracleJourneyActive = oracle, oracleCombatActive = false, chevalierProtectionActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
        paladinJourneyArmed = paladin, paladinJourneyUsed = false,
        routeStops = if (guided) stops else emptyList(),
        routeLegKm = if (guided) legs else emptyList(),
        routeLegIndex = 0,
        log = (listOf("Nouveau trajet vers $destination : ${"%.2f".format(effective)} km$note${if (guided) " · ${legs.size} étape(s) guidée(s), sans validation des chapitres." else " · suivi manuel."}${if (oracle) " · Oracle : Prière avant départ, dégâts ×2 durant le trajet." else ""}${if (paladin) " · Paladin/Quêteur : Prière avant départ, dé −2 disponible pour une rencontre." else ""}") + s.log).take(100))
}

private fun activateWarrior(state: AppState): AppState {
    val s = dailyState(state)
    if (s.character.profileType != "advanced" || s.character.className != "Guerrier" || s.lastEncounter == null || s.lastEncounter.safe || s.warriorUsed) return s
    return s.copy(warriorUsed = true, warriorActive = true,
        log = (listOf("Guerrier : Prière confirmée ; dégâts des attaques avec arme ×2 pendant cette rencontre (1 utilisation quotidienne).") + s.log).take(100))
}

private fun warriorDamage(state: AppState, damage: Int): Int {
    return CompanionRules.attackDamage(damage, state.warriorActive, state.oracleJourneyActive || state.oracleCombatActive)
}

private fun enrichedAttackDamage(state: AppState, baseDamage: Int, heroicPartner: Boolean, crownPartner: Boolean): Int {
    val count = state.lastEncounter?.let { encounterHp(it).count { hp -> hp > 0 } } ?: 0
    return HeroicRules.damage(warriorDamage(state, baseDamage),
        "Récits héroïques" in state.inventory, heroicPartner, count,
        "Réplique de couronne" in state.inventory, crownPartner)
}

// P. 73 : Prière avant l'affrontement, puissance pour une seule rencontre.
// Si une Prière avait déjà été validée avant départ, pas de second ×2 cumulatif de l'Oracle.
private fun activateOracleCombat(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if (e.safe || "Oracle de Tasmina" !in availableCompanions(s) ||
        s.oracleJourneyActive || s.oracleCombatActive || encounterHp(e).any { it != e.hp }) return s
    return s.copy(oracleCombatActive = true,
        log = (listOf("Oracle de Tasmina (p. 73) : Prière confirmée avant affrontement ; dégâts des attaques ×2 jusqu'à la fin de ce combat.") + s.log).take(100))
}

// P. 129 : dé de quantité −2 sur UNE rencontre choisie pendant le prochain voyage.
// Convention explicitée : résultat minimum de 1 pour garder une quantité valide.
private fun usePaladinQuantity(s: AppState): AppState {
    val e = s.lastEncounter ?: return s
    if (!s.paladinJourneyArmed || s.paladinJourneyUsed || s.destinationKm <= 0.0 ||
        "Le Paladin et le Quêteur" !in availableCompanions(s) || e.safe ||
        e.quantityDie !in 1..6 || e.effect.contains("quantité fixe", ignoreCase = true) ||
        e.hunterUsed || e.mysticUsed || e.bulkerUsed || s.illusionActive ||
        encounterHp(e).any { it != e.hp }) return s
    val afterDie = CompanionRules.paladinDie(e.quantityDie)
    val qty = CompanionRules.enemyCount(afterDie, e.roll, s.bardJourney)
    val rule = encounterRules.getOrNull(e.roll - 7) ?: return s
    val reward = CompanionRules.reward(rule.reward, qty, s.bardJourney)
    return s.copy(lastEncounter = e.copy(quantityDie = afterDie, quantity = qty, initialQuantity = qty,
        remainingHp = qty * e.hp, enemyHp = List(qty) { e.hp }, reward = reward),
        paladinJourneyUsed = true,
        log = (listOf("Paladin/Quêteur (p. 129) : dé de quantité ${e.quantityDie} − 2 → $afterDie ; $qty adversaire(s), $reward PO. Pouvoir consommé pour ce voyage.${if (e.quantityDie <= 2) " Minimum 1 (convention d'application)." else ""}") + s.log).take(100))
}

// Livre p. 41 : téléportation une fois par jour, UNIQUEMENT sur un lien direct
// du graphe transcrit de la carte officielle p. 51 (aucun contrôle manuel contournable).
private fun useCavalierAbility(state: AppState, target: String): AppState {
    val s = dailyState(state)
    if (s.character.profileType != "advanced" || s.character.className != "Cavalier" ||
        s.cavalierUsed || s.destinationKm > 0.0 || s.lastEncounter != null ||
        mapNeighbors(s.character.location).none { it.first == target }) return s
    return s.copy(character = s.character.copy(location = target), cavalierUsed = true, chevalierProtectionActive = false,
        visitedPlaces = (s.visitedPlaces + s.character.location + target).distinct(),
        bardJourney = false, destination = "", destinationKm = 0.0, distance = 0.0,
        routeStops = emptyList(), routeLegKm = emptyList(), routeLegIndex = 0,
        log = (listOf("Cavalier : téléportation depuis ${s.character.location} vers $target (liaison directe confirmée par carte p. 51), utilisation quotidienne consommée. Aucun km ni rencontre.") + s.log).take(100))
}

// Livre p. 41-42 : le pouvoir agit sur UN dé, que ce soit parmi les 7D6
// de type, sur le D6 de quantité, ou sur le D6 de contexte.
private fun useMysticDie(state: AppState, kind: String, dieIndex: Int, delta: Int): AppState {
    val e = state.lastEncounter ?: return state
    val cls = state.character.className
    if (state.character.profileType != "advanced" || cls !in listOf("Mage", "Moine") ||
        e.mysticUsed || e.hunterUsed || e.bulkerUsed || state.illusionActive ||
        encounterHp(e).any { it != e.hp } ||
        delta !in (if (cls == "Mage") listOf(-1, 1) else listOf(-2, -1, 1, 2))) return state
    val before = when (kind) {
        "Type" -> e.typeDice.getOrNull(dieIndex) ?: return state
        "Quantité" -> if (!e.effect.contains("quantité fixe", ignoreCase = true)) e.quantityDie else 0
        "Contexte" -> e.contextDie
        else -> return state
    }
    val after = before + delta
    if (before !in 1..6 || after !in 1..6) return state
    val label = "$cls : dé de ${kind.lowercase()} $before ${if (delta > 0) "+" else ""}$delta = $after${if (cls == "Moine") " (Prière confirmée)" else ""}"
    val updated = when (kind) {
        "Contexte" -> {
            val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
            val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
            val index = (after + e.contextBonus).coerceIn(1, 6) - 1
            e.copy(contextDie = after, context = contexts[index], exercise = effects[index])
        }
        "Quantité" -> {
            val multiplier = (if (e.roll == 7) 10 else 1) * (if (state.bardJourney) 2 else 1)
            val qty = after * multiplier
            val rule = encounterRules.getOrNull(e.roll - 7) ?: return state
            val reward = (rule.reward.toLong() * qty * (if (state.bardJourney) 5L else 4L) / 4L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            e.copy(quantityDie = after, quantity = qty, initialQuantity = qty, remainingHp = qty * e.hp,
                enemyHp = List(qty) { e.hp }, reward = reward)
        }
        "Type" -> {
            if (e.typeDice.size != 7) return state
            val dice = e.typeDice.toMutableList().apply { this[dieIndex] = after }
            val roll = dice.sum()
            val rule = encounterRules.getOrNull(roll - 7) ?: return state
            // Livre p. 42 : on ignore les rencontres dont le type n'existe pas dans la région.
            if (!isFitlandEncounter(rule)) {
                return state.copy(lastEncounter = null, meditationActive = false, vigorActive = false,
                    quantumMode = "", illusionActive = false, warriorActive = false, oracleCombatActive = false, cardioScrollActive = false, forceScrollActive = false, congestionScrollActive = false,
                    log = (listOf("$label ; somme 7D6 = $roll, ${rule.name} absent du Fitland : rencontre ignorée (livre p. 42).") + state.log).take(100))
            }
            val fixed = rule.effect.contains("quantité fixe", ignoreCase = true)
            val baseDie = if (fixed) 0 else if (e.quantityDie in 1..6) e.quantityDie else Random.nextInt(1, 7)
            val qty = if (fixed) 1 else baseDie * (if (roll == 7) 10 else 1) * (if (state.bardJourney) 2 else 1)
            val reward = (rule.reward.toLong() * qty * (if (state.bardJourney && !fixed) 5L else 4L) / 4L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
            val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
            val contextBonus = e.contextBonus - (if (e.roll == 15) 2 else 0) + (if (roll == 15) 2 else 0)
            val contextIndex = (e.contextDie + contextBonus).coerceIn(1, 6) - 1
            e.copy(typeDice = dice, roll = roll, name = rule.name, hp = rule.hp, quantity = qty,
                quantityDie = baseDie, initialQuantity = qty, enemyHp = List(qty) { rule.hp },
                remainingHp = rule.hp * qty, reward = reward, effect = rule.effect,
                safe = rule.effect.startsWith("Inoffensif"), contextBonus = contextBonus,
                context = contexts[contextIndex], exercise = effects[contextIndex])
        }
        else -> return state
    }
    val detail = if (kind == "Type") "$label ; somme 7D6 = ${updated.roll}, rencontre : ${updated.name} ×${updated.quantity}." else
        if (kind == "Quantité") "$label ; ${updated.quantity} adversaire(s), butin ${updated.reward} PO." else "$label ; nouveau contexte : ${updated.context}."
    return state.copy(lastEncounter = updated.copy(mysticUsed = true, mysticDescription = detail),
        log = (listOf(detail) + state.log).take(100))
}

private fun isFitlandEncounter(rule: EncounterRule): Boolean =
    (rule.zones == "Toutes" || rule.zones.contains("Fitland")) && rule.zones != "Toutes sauf Fitland"
