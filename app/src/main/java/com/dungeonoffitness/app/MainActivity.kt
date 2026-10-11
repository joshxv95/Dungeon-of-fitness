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

// Catalogue initial transcrit du Guide touristique, pages 163 à 169.
// Les objets modificateurs et les effets complexes seront intégrés séparément.
private data class Weapon(val name: String, val price: Int, val damage: Int,
    val unit: String, val exercise: String, val contextBonus: Int = 0)
private val weapons = listOf(
    Weapon("Gants", 10, 10, "10 sec.", "Punches : coups de poing rapides et fluides"),
    Weapon("Gants de vétéran", 20, 10, "10 sec.", "Punches : coups de poing rapides et fluides", contextBonus = 1),
    Weapon("Haltère", 20, 20, "1 exécution", "Push Up : une pompe, corps droit, bras près du corps"),
    Weapon("Couteaux de lancer", 50, 10, "1 exécution", "Plank Rotation : en planche, rotation sur un bras"),
    Weapon("Épée d'échauffement", 50, 50, "30 sec.", "Coups de taille avec une bouteille en plastique vide"),
    Weapon("Épée de bois", 50, 50, "30 sec.", "Coups de pointe avec une bouteille en plastique vide"),
    Weapon("Arbalète", 100, 20, "1 exécution", "Rower Biceps Legs : mouvement de rameur avec les jambes en résistance"),
    Weapon("Épée de fonte", 150, 150, "30 sec.", "Coups de pointe avec une bouteille remplie d'eau"),
    Weapon("Arc féérique", 200, 50, "10 sec.", "Maintenir la posture Warrior II (yoga)"),
    Weapon("Arc khôl", 200, 100, "1 exécution", "Archer Push Up : pompe avec un bras tendu sur le côté"),
    Weapon("Épée longue", 200, 200, "30 sec.", "Coups de taille et pointe, bouteille d'eau tenue à deux mains")
)

private data class Encounter(val name: String, val quantity: Int, val hp: Int, val reward: Int, val context: String, val exercise: String, val roll: Int = 0, val remainingHp: Int = hp * quantity, val effect: String = "", val safe: Boolean = false, val contextDie: Int = 0, val contextBonus: Int = 0, val contextWeapon: String = "")

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
                    "adventure" -> Adventure(state,
                        onTravel = { km -> update(travel(state, km)) },
                        onResolve = { damage -> update(resolve(state, damage)) },
                        onWeaponAttack = { name, count ->
                            val weapon = weapons.firstOrNull { it.name == name }
                            if (weapon != null && name in state.inventory && count > 0)
                                update(resolve(state, (weapon.damage.toLong() * count).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()))
                        },
                        onSkip = { update(skipEncounter(state)) },
                        onPlan = { destination, km ->
                            update(state.copy(destination = destination, destinationKm = km, distance = 0.0,
                                log = (listOf("Nouveau trajet vers $destination : ${"%.1f".format(km)} km (distance relevée sur la carte).") + state.log).take(100)))
                        },
                        onArrive = {
                            update(state.copy(character = state.character.copy(location = state.destination),
                                destination = "", destinationKm = 0.0, distance = 0.0,
                                log = (listOf("Arrivée à ${state.destination}.") + state.log).take(100)))
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
                    "inventory" -> Inventory(state, onEquip = { name ->
                        if (name.isEmpty() || name in state.inventory) update(state.copy(equippedWeapon = name))
                    }, onBuy = { weapon ->
                        if (state.character.po >= weapon.price)
                            update(state.copy(character = state.character.copy(po = state.character.po - weapon.price),
                                inventory = state.inventory + weapon.name,
                                log = (listOf("Achat : ${weapon.name} (${weapon.price} PO)") + state.log).take(100)))
                    })
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
            Text(if (state.destinationKm > 0.0) "Distance : ${"%.1f".format(state.distance)} / ${state.destinationKm} ${if (state.character.profileType == "novice") "min" else "km"}" else "Aucun trajet en cours")
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

@Composable private fun Adventure(
    state: AppState,
    onTravel: (Double) -> Unit,
    onResolve: (Int) -> Unit,
    onWeaponAttack: (String, Int) -> Unit,
    onSkip: () -> Unit,
    onPlan: (String, Double) -> Unit,
    onArrive: () -> Unit
) {
    var selectedPlace by remember { mutableStateOf("Fontaria") }
    var expanded by remember { mutableStateOf(false) }
    var distanceInput by remember { mutableStateOf("") }
    var damageInput by remember { mutableStateOf("") }
    var selectedWeapon by remember { mutableStateOf("") }
    var attackCount by remember { mutableStateOf("1") }
    val novice = state.character.profileType == "novice"
    val unit = if (novice) "min" else "km"
    val pending = state.lastEncounter != null
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Expédition", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Position actuelle : ${state.character.location}") }
        item { Text("Selon le livre (p. 41), les trajets sont réalisés réellement à pied, à vélo ou par un autre moyen non motorisé. Pour un novice : 1 km de carte = 1 minute d'endurance.") }
        if (state.destinationKm <= 0.0) {
            item { Text("Préparer un trajet", style = MaterialTheme.typography.titleMedium) }
            item {
                Box {
                    OutlinedButton(onClick = { expanded = true }) { Text("Destination : $selectedPlace ▾") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        fitlandPlaces.filter { it != state.character.location }.forEach { place ->
                            DropdownMenuItem(text = { Text(place) }, onClick = { selectedPlace = place; expanded = false })
                        }
                    }
                }
            }
            item { OutlinedTextField(value = distanceInput, onValueChange = { distanceInput = it },
                label = { Text("Distance sur la carte (km)") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()) }
            item { Text("Reporte la distance indiquée sur la carte du livre, page 51. Les itinéraires ne sont pas encore calculés automatiquement.") }
            item { Button(enabled = (distanceInput.replace(',','.').toDoubleOrNull() ?: 0.0) > 0.0 &&
                selectedPlace != state.character.location,
                onClick = { onPlan(selectedPlace, distanceInput.replace(',','.').toDouble()) },
                modifier = Modifier.fillMaxWidth()) { Text("Démarrer le trajet") } }
        } else {
            item { Text("Destination : ${state.destination} · ${"%.1f".format(state.destinationKm)} $unit") }
            item { LinearProgressIndicator(progress = { (state.distance / state.destinationKm).coerceIn(0.0,1.0).toFloat() }, modifier = Modifier.fillMaxWidth()) }
            item { Text("${"%.1f".format(state.distance)} / ${"%.1f".format(state.destinationKm)} $unit") }
            if (novice) {
                item { Button(enabled = !pending && state.distance < state.destinationKm,
                    onClick = { onTravel(1.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 1 minute d'endurance") } }
                item { OutlinedButton(enabled = !pending && state.distance < state.destinationKm,
                    onClick = { onTravel(5.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 5 minutes d'endurance") } }
            } else {
                item { Button(enabled = !pending && state.distance < state.destinationKm,
                    onClick = { onTravel(1.0) }, modifier = Modifier.fillMaxWidth()) { Text("+ 1 km parcouru") } }
                item { OutlinedButton(enabled = !pending && state.distance < state.destinationKm,
                    onClick = { onTravel(0.1) }, modifier = Modifier.fillMaxWidth()) { Text("+ 100 m") } }
            }
            if (state.distance >= state.destinationKm && !pending) {
                item { Button(onClick = onArrive, modifier = Modifier.fillMaxWidth()) { Text("Valider l'arrivée à ${state.destination}") } }
            }
        }
        item { Text("Progression totale : ${"%.1f".format(state.totalTravelKm)} $unit · Rencontres déclenchées : ${state.encounters}") }
        item { Text("Une rencontre est déclenchée tous les 6 ${unit} cumulés. Les adversaires et contextes proviennent de la table officielle (p. 42-43). Certains effets spéciaux restent manuels.", style = MaterialTheme.typography.bodySmall) }
        state.lastEncounter?.let { e ->
            item { HorizontalDivider() }
            item { Text("⚠ RENCONTRE — TABLE DU LIVRE", style = MaterialTheme.typography.headlineSmall) }
            item { Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${e.name} ×${e.quantity} (jet : ${e.roll})", style = MaterialTheme.typography.titleLarge)
                Text("PV restants : ${e.remainingHp} / ${e.hp * e.quantity} · Butin potentiel : ${e.reward} PO")
                if (e.contextDie > 0) {
                    Text("Dé de contexte : ${e.contextDie} + ${e.contextBonus} = ${(e.contextDie + e.contextBonus).coerceAtMost(6)}")
                    if (e.contextWeapon.isNotBlank()) Text("Bonus d’équipement : ${e.contextWeapon} (+1)")
                    if (e.effect.contains("Contexte +2")) Text("Effet Trollympien : +2 au contexte")
                } else Text("Rencontre enregistrée avant v1.3 : jet de contexte initial indisponible.")
                Text("Contexte : ${e.context}")
                Text("Conséquence : ${e.exercise}")
                if (e.context == "Une embuscade !") Text("Attention : la durée d’utilisation des objets est doublée. Pour les armes à durée, chaque tranche doit durer deux fois plus longtemps ; les dégâts par tranche restent identiques.")
                if (e.effect.isNotBlank()) Text("Effet immédiat : ${e.effect}")
                if (!e.safe) {
                    val owned = weapons.filter { it.name in state.inventory }
                    if (owned.isNotEmpty()) {
                        Text("Attaquer avec l'équipement", style = MaterialTheme.typography.titleMedium)
                        owned.forEach { w ->
                            OutlinedButton(onClick = { selectedWeapon = w.name }, modifier = Modifier.fillMaxWidth()) {
                                Text("${if (selectedWeapon == w.name) "●" else "○"} ${w.name} — ${w.damage} PV / ${w.unit}")
                            }
                        }
                        val w = owned.firstOrNull { it.name == selectedWeapon }
                        if (w != null) {
                            Text("Exercice : ${w.exercise}")
                            if (e.context == "Une embuscade !" && w.unit.contains("sec.")) Text("Durée effective : ${(w.unit.substringBefore(' ').toIntOrNull() ?: 0) * 2} sec. par tranche (embuscade)")
                            OutlinedTextField(value = attackCount, onValueChange = { attackCount = it.filter(Char::isDigit) },
                                label = { Text("Nombre de séries / exécutions de ${w.unit}") },
                                modifier = Modifier.fillMaxWidth(), singleLine = true)
                            Button(enabled = (attackCount.toIntOrNull() ?: 0) in 1..1000,
                                onClick = { onWeaponAttack(w.name, attackCount.toInt()); attackCount = "1" },
                                modifier = Modifier.fillMaxWidth()) { Text("Exercice réalisé — infliger ${w.damage * ((attackCount.toIntOrNull() ?: 0).coerceIn(0,1000))} PV") }
                        }
                    } else Text("Aucune arme achetée : consulte le Sac pour t'équiper.")
                    Text("Saisie manuelle de secours (objets spéciaux non encore intégrés) :", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = damageInput, onValueChange = { damageInput = it.filter(Char::isDigit) },
                        label = { Text("Dégâts infligés avec les objets du livre (PV)") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Button(enabled = (damageInput.toIntOrNull() ?: 0) > 0,
                        onClick = { onResolve(damageInput.toInt()); damageInput = "" },
                        modifier = Modifier.fillMaxWidth()) { Text("Enregistrer les dégâts") }
                }
                OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                    Text(if (e.safe) "Terminer la rencontre pacifique" else "Fuir / abandonner (retour manuel requis)")
                }
                Text("Les exercices et effets immédiats doivent être réalisés manuellement. En cas de fuite, applique le retour vers la zone de départ prévu p. 42.", style = MaterialTheme.typography.bodySmall)
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
        item { Text("Dungeon of Fitness · Version 1.3.0", style = MaterialTheme.typography.titleLarge) }
        item { Text("Fonctionnalités intégrées", style = MaterialTheme.typography.titleMedium) }
        listOf(
            "Navigation entre les écrans",
            "Planification manuelle d'un trajet sur la carte du livre",
            "Suivi des distances cumulées et validation d'arrivée",
            "Rencontres : table officielle Fitland (7D6), quantité, contexte et PV",
            "Arme active et bonus de contexte des gants de vétéran",
            "Historique du dé de contexte et de ses modificateurs",
            "Suivi manuel des dégâts et butin à la victoire",
            "Mode novice : distance de carte convertie en minutes",
            "Position initiale selon la classe à la création du personnage",
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
            "Carte interactive et calcul automatique des itinéraires",
            "Combats automatisés avec dégâts calculés depuis l'équipement",
            "Objets, quêtes et entraînements complets"
        ).forEach { feature -> item { Text("○ $feature") } }
        item { Text("Rencontres : table officielle p. 43 et contextes p. 42. Effets spéciaux et équipement à appliquer manuellement.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable private fun Inventory(state: AppState, onEquip: (String) -> Unit, onBuy: (Weapon) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Sac et armurerie", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Bourse : ${state.character.po} PO") }
        item { Text("Arme active pour les prochains jets de rencontre", style = MaterialTheme.typography.titleMedium) }
        item { Text(if (state.equippedWeapon.isBlank()) "Aucune arme active" else "Active : ${state.equippedWeapon}") }
        item { OutlinedButton(onClick = { onEquip("") }) { Text("Aucune arme active") } }
        items(weapons.filter { it.name in state.inventory }) { w ->
            OutlinedButton(onClick = { onEquip(w.name) }, modifier = Modifier.fillMaxWidth()) {
                Text("${if (state.equippedWeapon == w.name) "●" else "○"} ${w.name}${if (w.contextBonus != 0) " · Contexte +${w.contextBonus}" else ""}")
            }
        }
        item { Text("L'arme doit être sélectionnée AVANT le tirage : changer d'arme ne modifie pas rétroactivement une rencontre en cours.", style = MaterialTheme.typography.bodySmall) }
        item { Text("Équipement possédé", style = MaterialTheme.typography.titleMedium) }
        if (state.inventory.isEmpty()) item { Text("Aucun objet acheté.") }
        items(state.inventory) { Text("◆ $it") }
        item { HorizontalDivider() }
        item { Text("Catalogue — premières armes du livre", style = MaterialTheme.typography.titleMedium) }
        item { Text("Achat irréversible : le livre interdit la revente. Les règles de dotation initiale (10 PO par niveau à dépenser intégralement) ne sont pas encore automatisées.", style = MaterialTheme.typography.bodySmall) }
        items(weapons) { w ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${w.name} — ${w.price} PO", style = MaterialTheme.typography.titleMedium)
                Text("${w.damage} PV / ${w.unit}")
                Text(w.exercise)
                if (w.contextBonus > 0) Text("Effet officiel : +${w.contextBonus} au jet de contexte (si arme active lors du tirage)")
                Button(enabled = state.character.po >= w.price && w.name !in state.inventory,
                    onClick = { onBuy(w) }) { Text(if (w.name in state.inventory) "Déjà possédé" else "Acheter (${w.price} PO)") }
            } }
        }
    }
}

@Composable private fun Journal(state: AppState) { LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Journal d'aventure", style = MaterialTheme.typography.headlineSmall) }; items(state.log) { Text(it) }; item { Text("Quêtes") }; items(state.quests) { Text("□ $it") } } }

private fun travel(s: AppState, km: Double): AppState {
    val old = s.distance
    val newDistance = (old + km).coerceAtMost(s.destinationKm)
    val oldChecks = kotlin.math.floor(s.totalTravelKm / 6.0).toInt()
    val actualProgress = newDistance - old
    val newTotal = s.totalTravelKm + actualProgress
    val newChecks = kotlin.math.floor(newTotal / 6.0).toInt()
    val encounter = if (newChecks > oldChecks) generateEncounter(s.equippedWeapon) else s.lastEncounter
    val logs = if (encounter != null && newChecks > oldChecks) listOf("Rencontre : ${encounter.name} ×${encounter.quantity}") + s.log else listOf("+${"%.1f".format(km)} km parcouru") + s.log
    return s.copy(distance = newDistance, totalTravelKm = newTotal, lastEncounter = encounter, encounters = s.encounters + if (newChecks > oldChecks) 1 else 0, log = logs.take(100))
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

private fun generateEncounter(equippedWeapon: String): Encounter? {
    val roll = (1..7).sumOf { Random.nextInt(1, 7) }
    val rule = encounterRules[roll - 7]
    // Campagne actuelle : Fitland. Les rencontres d'autres régions sont ignorées.
    if (!(rule.zones == "Toutes" || rule.zones.contains("Fitland")) || rule.zones == "Toutes sauf Fitland") return null
    val quantity = if (rule.effect.contains("Quantité fixe", ignoreCase = true) || rule.effect.contains("quantité fixe", ignoreCase = true)) 1 else Random.nextInt(1, 7) * if (roll == 7) 10 else 1
    val contextDie = Random.nextInt(1, 7)
    val equipmentBonus = weapons.firstOrNull { it.name == equippedWeapon }?.contextBonus ?: 0
    val contextBonus = equipmentBonus + if (roll == 15) 2 else 0
    // La table p. 42 s'arrête à 6 : les dépassements sont traités comme 6.
    val contextRoll = (contextDie + contextBonus).coerceAtMost(6)
    val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
    val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
    val safe = rule.effect.startsWith("Inoffensif")
    return Encounter(rule.name, quantity, rule.hp, rule.reward * quantity, contexts[contextRoll - 1], effects[contextRoll - 1], roll, rule.hp * quantity, rule.effect, safe, contextDie, contextBonus, if (equipmentBonus > 0) equippedWeapon else "")
}

private fun resolve(s: AppState, damage: Int): AppState = s.lastEncounter?.let { e ->
    val remaining = (e.remainingHp - damage.coerceAtLeast(0)).coerceAtLeast(0)
    if (remaining > 0) s.copy(lastEncounter = e.copy(remainingHp = remaining), log = (listOf("${e.name} : $damage dégâts, $remaining PV restants") + s.log).take(100))
    else s.copy(character = s.character.copy(po = s.character.po + e.reward), lastEncounter = null,
        log = (listOf("${e.name} vaincu(s), butin : ${e.reward} PO") + s.log).take(100))
} ?: s

private fun skipEncounter(s: AppState): AppState = s.lastEncounter?.let { e ->
    s.copy(lastEncounter = null, log = (listOf("Rencontre terminée sans victoire : ${e.name}. En cas de fuite, retour à la zone de départ à effectuer.") + s.log).take(100))
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
        put("schema", 6); put("character", character); put("distance", state.distance)
        put("destination", state.destination); put("destinationKm", state.destinationKm)
        put("totalTravelKm", state.totalTravelKm)
        put("encounters", state.encounters)
        put("log", JSONArray(state.log)); put("inventory", JSONArray(state.inventory))
        put("equippedWeapon", state.equippedWeapon)
        put("quests", JSONArray(state.quests))
        state.lastEncounter?.let { e -> put("lastEncounter", JSONObject().apply {
            put("name", e.name); put("quantity", e.quantity); put("hp", e.hp)
            put("reward", e.reward); put("context", e.context); put("exercise", e.exercise)
            put("roll", e.roll); put("remainingHp", e.remainingHp); put("effect", e.effect); put("safe", e.safe)
            put("contextDie", e.contextDie); put("contextBonus", e.contextBonus); put("contextWeapon", e.contextWeapon)
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
                it.optInt("contextDie", 0), it.optInt("contextBonus", 0), it.optString("contextWeapon", ""))
        }
        val baseState = AppState()
        baseState.copy(character = if (c.profileType == "advanced" && c.className in classAchievements) c.copy(unlockedClasses = (c.unlockedClasses + c.className).distinct()) else c,
            distance = obj.optDouble("distance", 0.0),
            destination = obj.optString("destination", baseState.destination),
            destinationKm = obj.optDouble("destinationKm", baseState.destinationKm),
            totalTravelKm = obj.optDouble("totalTravelKm", obj.optDouble("distance", 0.0)),
            encounters = obj.optInt("encounters", 0), lastEncounter = encounter,
            equippedWeapon = obj.optString("equippedWeapon", ""),
            log = strings("log", baseState.log), inventory = strings("inventory", baseState.inventory),
            quests = strings("quests", baseState.quests))
    } catch (_: Exception) { AppState() }
}
