package com.dungeonoffitness.app

import android.os.Bundle
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
    val location: String = "Fontaria"
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
    var state by remember { mutableStateOf(AppState()) }
    var tab by remember { mutableStateOf("home") }
    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(bottomBar = { NavigationBar {
            listOf("home" to "Accueil", "adventure" to "Aventure", "character" to "Personnage", "inventory" to "Sac", "journal" to "Journal").forEach { (id, label) ->
                NavigationBarItem(selected = tab == id, onClick = { tab = id }, icon = { Text(if (id == "home") "⌂" else if (id == "adventure") "⚔" else if (id == "character") "♙" else if (id == "inventory") "◆" else "▤") }, label = { Text(label) })
            }
        }}) { pad ->
            Box(Modifier.padding(pad).fillMaxSize()) {
                when(tab) {
                    "home" -> Home(state) { tab = "adventure" }
                    "adventure" -> Adventure(state, onTravel = { km -> state = travel(state, km) }, onResolve = { state = resolve(state) })
                    "character" -> CharacterScreen(state.character)
                    "inventory" -> Inventory(state)
                    else -> Journal(state)
                }
            }
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

@Composable private fun CharacterScreen(c: Character) {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Fiche de personnage", style = MaterialTheme.typography.headlineSmall) }
        item { Text("${c.name} · ${c.className} · ${c.divinity} · Niveau ${c.level}") }
        listOf("Force" to c.force, "Endurance" to c.endurance, "Agilité" to c.agility, "Dextérité" to c.dexterity, "Mental" to c.mental).forEach { (n,v) -> item { StatRow(n,v) } }
        item { Text("Les tests de caractéristique seront intégrés dans la prochaine couche du moteur.") }
    }
}
@Composable private fun StatRow(name: String, value: Int) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(name); Text(value.toString(), style = MaterialTheme.typography.titleMedium) } }

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
