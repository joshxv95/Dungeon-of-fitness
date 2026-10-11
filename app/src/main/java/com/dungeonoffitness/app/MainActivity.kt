package com.dungeonoffitness.app

import android.os.Bundle
import android.content.Context
import org.json.JSONObject
import org.json.JSONArray
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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

private data class Encounter(val name: String, val quantity: Int, val hp: Int, val reward: Int, val context: String, val exercise: String)

private data class AppState(
    val character: Character = Character(),
    val distance: Double = 0.0,
    val destination: String = "Tique-Couenne",
    val destinationKm: Double = 4.0,
    val encounters: Int = 0,
    val lastEncounter: Encounter? = null,
    val log: List<String> = listOf("Le voyage commence. Le Disque-Fonte vous attend."),
    val inventory: List<String> = listOf("10 PO de départ"),
    val quests: List<String> = listOf("Créer et suivre votre profil", "Tenir votre journal d'entraînement")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DungeonApp() }
    }
}

@Composable
fun DungeonApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var state by remember { mutableStateOf(loadState(context)) }
    fun update(next: AppState) { state = next; saveState(context, next) }
    var tab by remember { mutableStateOf("home") }
    var resetDialog by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(bottomBar = { NavigationBar {
            listOf("home" to "Accueil", "adventure" to "Aventure", "character" to "Personnage", "inventory" to "Sac", "journal" to "Journal", "application" to "Application").forEach { (id, label) ->
                NavigationBarItem(selected = tab == id, onClick = { tab = id }, icon = { Text(if (id == "home") "⌂" else if (id == "adventure") "⚔" else if (id == "character") "♙" else if (id == "inventory") "◆" else if (id == "journal") "▤" else "⚙") }, label = { Text(label) })
            }
        }}) { pad ->
            Box(Modifier.padding(pad).fillMaxSize()) {
                when(tab) {
                    "home" -> Home(state) { tab = "adventure" }
                    "adventure" -> Adventure(state, onTravel = { km -> update(travel(state, km)) }, onResolve = { update(resolve(state)) })
                    "character" -> CharacterScreen(state.character, onUpdate = { candidate ->
                        val existing = state.character.profileType
                        if (existing == "prototype" || existing == candidate.profileType) {
                            update(state.copy(character = candidate))
                        }
                    }, onReset = { resetDialog = true })
                    "inventory" -> Inventory(state)
                    "journal" -> Journal(state)
                    "application" -> ApplicationInfo()
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
        item { Text("DUNGEON OF FITNESS", style = MaterialTheme.typography.headlineMedium) }
        item { Text("Le Disque-Fonte devient enfin jouable sans le PDF.", style = MaterialTheme.typography.bodyLarge) }
        item { Card { Column(Modifier.padding(18.dp)) {
            Text(state.character.name, style = MaterialTheme.typography.titleLarge)
            Text("Niveau ${state.character.level} · ${state.character.className} · Disciple de ${state.character.divinity}")
            Spacer(Modifier.height(10.dp)); Text("📍 ${state.character.location}  →  ${state.destination}")
            Text("Distance : ${"%.1f".format(state.distance)} / ${state.destinationKm} km")
        } } }
        item { Button(onClick = start, modifier = Modifier.fillMaxWidth()) { Text("⚔ COMMENCER L'AVENTURE") } }
        item { Text("Dernier événement", style = MaterialTheme.typography.titleMedium) }
        item { Text(state.log.firstOrNull() ?: "Aucun événement") }
    }
}

@Composable private fun Adventure(state: AppState, onTravel: (Double) -> Unit, onResolve: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Expédition", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Destination : ${state.destination} · ${state.destinationKm} km") }
        item { LinearProgressIndicator(progress = { (state.distance / state.destinationKm).coerceIn(0.0,1.0).toFloat() }, modifier = Modifier.fillMaxWidth()) }
        item { Text("${"%.1f".format(state.distance)} / ${state.destinationKm} km") }
        item { Button(onClick = { onTravel(1.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 1 km parcouru") } }
        item { OutlinedButton(onClick = { onTravel(0.1) }, modifier = Modifier.fillMaxWidth()) { Text("+ 100 m") } }
        state.lastEncounter?.let { e ->
            item { HorizontalDivider() }
            item { Text("⚠ RENCONTRE !", style = MaterialTheme.typography.headlineSmall) }
            item { Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${e.name} ×${e.quantity}", style = MaterialTheme.typography.titleLarge)
                Text("PV : ${e.hp} chacun · Récompense : ${e.reward} PO")
                Text("Contexte : ${e.context}")
                Text("Épreuve : ${e.exercise}")
                Button(onClick = onResolve, modifier = Modifier.fillMaxWidth()) { Text("J'AI TERMINÉ") }
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
        item { Text("${c.name} · ${c.className} · ${c.divinity} · Niveau ${c.level}") }
        item { OutlinedButton(onClick = { editing = !editing }) { Text(if (editing) "Fermer" else "Modifier le nom") } }
        if (editing) {
            item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom du personnage") }, singleLine = true) }
            item { Button(onClick = { if (name.isNotBlank()) { onUpdate(c.copy(name = name.trim())); editing = false } }) { Text("Enregistrer") } }
        }
        item { HorizontalDivider() }
        if (profileLocked) {
            item { Text("Caractéristiques", style = MaterialTheme.typography.titleMedium) }
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
@Composable private fun StatRow(name: String, value: Int) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(name); Text(value.toString(), style = MaterialTheme.typography.titleMedium) } }

@Composable private fun ApplicationInfo() {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Application", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Dungeon of Fitness · Version 0.9.0", style = MaterialTheme.typography.titleLarge) }
        item { Text("Fonctionnalités intégrées", style = MaterialTheme.typography.titleMedium) }
        listOf(
            "Navigation entre les écrans",
            "Déplacement manuel et jauge de 4 km (prototype)",
            "Sauvegarde locale automatique",
            "Profils novice et avancé et verrouillage du type",
            "Calcul des caractéristiques et du niveau",
            "Réinitialisation complète avec confirmation",
            "Classes et historique des classes débloquées",
            "Accomplissements conservés lors du changement de classe",
            "Accomplissements non validés des anciennes classes masqués",
            "Carnet des lieux de Fitland (aperçu sans distances inventées)",
            "Fiche : statistiques et accomplissements en haut"
        ).forEach { feature -> item { Text("✓ $feature") } }
        item { HorizontalDivider() }
        item { Text("Carnet des lieux de Fitland", style = MaterialTheme.typography.titleMedium) }
        item { Text("Aperçu des lieux du livre. La carte interactive et les trajets officiels restent à intégrer.") }
        listOf("Fontaria", "Entrepôts Shaker+", "Tique-Couenne", "Nosferathon", "Omega Beach", "Garde Froide", "Litière Ville", "Mont Stepper").forEach { place ->
            item { Text("• $place") }
        }
        item { HorizontalDivider() }
        item { Text("En développement", style = MaterialTheme.typography.titleMedium) }
        listOf(
            "Conditions officielles de validation des accomplissements",
            "Carte complète et campagne du livre",
            "Rencontres et combats fidèles aux tables officielles",
            "Objets, quêtes et entraînements complets"
        ).forEach { feature -> item { Text("○ $feature") } }
        item { Text("Fonctionne hors ligne. Les rencontres et la destination actuelles restent provisoires.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable private fun Inventory(state: AppState) { LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Inventaire", style = MaterialTheme.typography.headlineSmall) }; item { Text("${state.character.po} PO") }; items(state.inventory) { Text("◆ $it") } } }
@Composable private fun Journal(state: AppState) { LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Journal d'aventure", style = MaterialTheme.typography.headlineSmall) }; items(state.log) { Text(it) }; item { Text("Quêtes") }; items(state.quests) { Text("□ $it") } } }

private fun travel(s: AppState, km: Double): AppState {
    val old = s.distance
    val newDistance = (old + km).coerceAtMost(s.destinationKm)
    val oldChecks = kotlin.math.floor(old / 6.0).toInt()
    val newChecks = kotlin.math.floor(newDistance / 6.0).toInt()
    val encounter = if (newChecks > oldChecks) generateEncounter() else s.lastEncounter
    val logs = if (encounter != null && newChecks > oldChecks) listOf("Rencontre : ${encounter.name} ×${encounter.quantity}") + s.log else listOf("+${"%.1f".format(km)} km parcouru") + s.log
    return s.copy(distance = newDistance, lastEncounter = encounter, encounters = s.encounters + if (encounter != null && newChecks > oldChecks) 1 else 0, log = logs.take(100))
}

private fun resolve(s: AppState): AppState = s.lastEncounter?.let { e -> s.copy(character = s.character.copy(po = s.character.po + e.reward), lastEncounter = null, log = listOf("${e.name} vaincu(s). +${e.reward} PO.") + s.log) } ?: s

private fun generateEncounter(): Encounter {
    val roll = (1..7).sumOf { Random.nextInt(1,7) }
    val table = listOf(
        Encounter("Liquebide",1,50,5,"Ils arrivent !","50 Jumping Jacks"),
        Encounter("Chardio",1,50,5,"Ils sont à votre merci.","Rien"),
        Encounter("Gnome Zombie",1,100,10,"Une embuscade !","50 Jumping Jacks"),
        Encounter("Acronide",1,100,10,"Ils arrivent !","50 Jumping Jacks"),
        Encounter("Gobeleau",1,100,10,"Ils sont à votre merci.","Rien"),
        Encounter("Fitnéant",1,100,10,"Vous êtes piégé !","10 Burpees par quantité"),
        Encounter("Plantosaure",1,150,15,"Ils ne vous échapperont pas.","20 Jumping Jacks")
    )
    val base = table[(roll - 7).coerceIn(0, table.lastIndex)]
    val quantity = Random.nextInt(1,7)
    val context = listOf("Vous êtes piégé !","Une embuscade !","Ils arrivent !","Ils ne vous échapperont pas.","Ils sont à votre merci.","Ils n'ont rien vu.")[Random.nextInt(6)]
    val exercise = when(Random.nextInt(6)) { 0 -> "10 Burpees par quantité"; 2 -> "50 Jumping Jacks"; 3 -> "20 Jumping Jacks"; else -> "Aucun exercice imposé" }
    return base.copy(quantity = quantity, context = context, exercise = exercise)
}


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
        put("schema", 4); put("character", character); put("distance", state.distance)
        put("destination", state.destination); put("destinationKm", state.destinationKm)
        put("encounters", state.encounters)
        put("log", JSONArray(state.log)); put("inventory", JSONArray(state.inventory))
        put("quests", JSONArray(state.quests))
        state.lastEncounter?.let { e -> put("lastEncounter", JSONObject().apply {
            put("name", e.name); put("quantity", e.quantity); put("hp", e.hp)
            put("reward", e.reward); put("context", e.context); put("exercise", e.exercise)
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
                it.optInt("reward"), it.optString("context"), it.optString("exercise"))
        }
        val baseState = AppState()
        baseState.copy(character = if (c.profileType == "advanced" && c.className in classAchievements) c.copy(unlockedClasses = (c.unlockedClasses + c.className).distinct()) else c,
            distance = obj.optDouble("distance", 0.0),
            destination = obj.optString("destination", baseState.destination),
            destinationKm = obj.optDouble("destinationKm", baseState.destinationKm),
            encounters = obj.optInt("encounters", 0), lastEncounter = encounter,
            log = strings("log", baseState.log), inventory = strings("inventory", baseState.inventory),
            quests = strings("quests", baseState.quests))
    } catch (_: Exception) { AppState() }
}
