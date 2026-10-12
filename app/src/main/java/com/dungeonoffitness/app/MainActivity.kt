package com.dungeonoffitness.app

import android.os.Bundle
import android.content.Context
import org.json.JSONObject
import org.json.JSONArray
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
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
    name.contains("Bouclier", true) -> "🛡️"
    name.contains("Bottes", true) -> "🥾"
    name.contains("Casque", true) -> "🪖"
    name.contains("Bracelets", true) || name.contains("Gants", true) -> "🥊"
    name.contains("Arc", true) || name.contains("Arbalète", true) || name.contains("Sarbacane", true) -> "🏹"
    name.contains("Épée", true) || name.contains("Sabre", true) || name.contains("Couteaux", true) -> "⚔️"
    name.contains("Haltère", true) || name.contains("Masse", true) -> "🏋️"
    name.contains("Lance", true) || name.contains("Hallebarde", true) || name.contains("Perche", true) -> "🔱"
    name.contains("Manuel", true) -> "📜"
    name.contains("Pattes", true) -> "🐾"
    name.contains("Tapis", true) -> "🧘"
    else -> "✨"
}
@Composable private fun ClassPortrait(className: String, modifier: Modifier = Modifier) {
    Image(painter = painterResource(portraitFor(className)),
        contentDescription = "Illustration de la classe $className",
        contentScale = ContentScale.Crop,
        modifier = modifier)
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
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF24232F))) {
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
    Weapon("Lance", 100, 100, "30 sec.", "Coups de pointe avec un manche à balai"),
    Weapon("Masse Gainer", 300, 300, "30 sec.", "Coups de taille avec un manche lesté ou une barre vide"),
    Weapon("Masse haltère", 150, 150, "30 sec.", "Coups de taille avec une bouteille remplie d'eau"),
    Weapon("Perche Lance", 200, 200, "30 sec.", "Coups de pointe avec un manche à balai"),
    Weapon("Sarbacane", 50, 50, "10 sec.", "Vacuum : aspirer l’abdomen vers la colonne pour travailler le transverse"),
    Weapon("Sabre féérique", 200, 50, "1 exécution", "Turkish Get Up : se relever du sol en utilisant une jambe et un bras"),
    Weapon("Sceptre féérique", 200, 200, "10 sec.", "Climbers : ramener les genoux vers la poitrine en position de planche"),
    Weapon("Tapis de mage de guerre", 500, 300, "1 exécution", "Enchaîner Warrior I, II et III, 10 secondes chacune, sans interruption", ranged = true)
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
    CombatUpgrade("Manuel de méditation", 1000, 100, "Méditer 60 secondes par adversaire pour +100 PV sur chacune des attaques du combat.")
)
// Le bonus fixe est ajouté une fois à chaque attaque déclarée, pas à chaque répétition.
private fun attackDamage(weapon: Weapon, count: Int, inventory: List<String>, pattesUsed: Boolean, meditationActive: Boolean = false, vigorActive: Boolean = false, supportBonus: Int = 0): Int {
    val bonus = (if ("Manuel de bottes de Maître" in inventory) 100 else 0) +
        (if ("Manuel de combat à mains nues" in inventory && weapon.name.startsWith("Gants")) 50 else 0) +
        (if (pattesUsed && "Pattes d’ours" in inventory) 50 else 0) +
        (if (meditationActive && "Manuel de méditation" in inventory) 100 else 0) + supportBonus
    return ((weapon.damage.toLong() * count + bonus) * (if (vigorActive) 2L else 1L)).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}

private data class Encounter(val name: String, val quantity: Int, val hp: Int, val reward: Int, val context: String, val exercise: String, val roll: Int = 0, val remainingHp: Int = hp * quantity, val effect: String = "", val safe: Boolean = false, val contextDie: Int = 0, val contextBonus: Int = 0, val contextWeapon: String = "", val initialQuantity: Int = quantity, val illusionDie: Int = 0, val enemyHp: List<Int> = emptyList(), val hunterUsed: Boolean = false, val mysticUsed: Boolean = false, val mysticDescription: String = "")

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
    val meditationActive: Boolean = false,
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
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFFE5B66C), onPrimary = Color(0xFF211609),
        secondary = Color(0xFF80B9BB), surface = Color(0xFF191B25),
        background = Color(0xFF11141D), surfaceVariant = Color(0xFF292A36)
    )) {
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
                        onWarrior = { update(activateWarrior(state)) },
                        onResolve = { damage -> update(resolve(state, damage)) },
                        onWeaponAttack = { name, count, pattesUsed, target, supportConfirmed ->
                            val weapon = weapons.firstOrNull { it.name == name }
                            if (weapon != null && name in state.inventory && count > 0)
                                {
                                    val support = supports.firstOrNull { it.name == state.equippedSupport && it.name in state.inventory }
                                    val damage = warriorDamage(state, attackDamage(weapon, count, state.inventory, pattesUsed, state.meditationActive, state.vigorActive, if (supportConfirmed) support?.bonus ?: 0 else 0))
                                    val e = state.lastEncounter
                                    if (weapon.ranged && e != null) {
                                        val hp = encounterHp(e)
                                        if (target != null && target in hp.indices && hp[target] > 0) {
                                            val after = resolveTarget(state, damage, target)
                                            update(after.copy(log = (listOf("${weapon.name} : cible ${e.name} n°${target + 1}, ${minOf(damage, hp[target])} PV infligés (sans transfert).") + after.log).take(100)))
                                        }
                                    } else if (!weapon.ranged) update(resolve(state, damage))
                                }
                        },
                        onSkip = { update(skipEncounter(state)) },
                        onFlee = { towardDestination -> update(fleeEncounter(state, towardDestination)) },
                        onHunt = { update(useHunterAbility(state)) },
                        onMystic = { delta -> update(useMysticContext(state, delta)) },
                        onMeditate = {
                            val e = state.lastEncounter
                            if (e != null && !e.safe && "Manuel de méditation" in state.inventory && !state.meditationActive)
                                update(state.copy(meditationActive = true, log = (listOf("Méditation effectuée : ${60 * e.quantity} secondes pour ${e.quantity} adversaire(s).") + state.log).take(100)))
                        },
                        onUsePersuasion = {
                            val e = state.lastEncounter
                            if (e != null && "Shaker persuasion" in state.inventory)
                                update(state.copy(lastEncounter = null, meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false,
                                    inventory = removeOne(state.inventory, "Shaker persuasion"),
                                    log = (listOf("Shaker persuasion consommé : combat contre ${e.name} ignoré, sans récompense.") + state.log).take(100)))
                        },
                        onUseConsumable = { name, mode ->
                            val e = state.lastEncounter
                            if (e != null && !e.safe && name in state.inventory) {
                                when (name) {
                                    "Shaker poison" -> {
                                        val current = encounterHp(e)
                                        val target = mode.toIntOrNull()?.coerceIn(0, current.lastIndex) ?: current.indexOfFirst { it > 0 }.coerceAtLeast(0)
                                        val poisonDamage = minOf(700, current[target])
                                        val after = resolveTarget(state, poisonDamage, target)
                                        update(after.copy(inventory = removeOne(after.inventory, name),
                                            log = (listOf("Shaker poison : $poisonDamage dégâts à distance sur ${e.name} n°${target + 1} (maximum 700, sans transfert aux autres ennemis).") + after.log).take(100)))
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
                                }
                            }
                        },
                        onPlan = { destination, km, scout, bardAudacious ->
                            update(planJourney(state, destination, km, scout, bardAudacious))
                        },
                        onArrive = {
                            update(state.copy(character = state.character.copy(location = state.destination),
                                destination = "", destinationKm = 0.0, distance = 0.0, bardJourney = false, retreatActive = false, retreatOrigin = "",
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
                    "inventory" -> Inventory(state, onTestGold = { amount ->
                        if (amount in 1..10000 && state.character.po <= Int.MAX_VALUE - amount)
                            update(state.copy(character = state.character.copy(po = state.character.po + amount),
                                log = (listOf("MODE TEST : +$amount PO ajoutés (hors règles officielles).") + state.log).take(100)))
                    }, onConsumableBuy = { name ->
                        if (name in listOf("Shaker persuasion", "Shaker poison", "Shaker vigueur", "Shaker quantique", "Shaker illusion") && state.character.po >= 100)
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
                        if (state.character.po >= weapon.price)
                            update(state.copy(character = state.character.copy(po = state.character.po - weapon.price),
                                inventory = state.inventory + weapon.name,
                                log = (listOf("Achat : ${weapon.name} (${weapon.price} PO)") + state.log).take(100)))
                    })
                    "journal" -> Journal(state)
                    "application" -> ApplicationInfo(state, onResetDailyTests = {
                        val current = dailyState(state)
                        update(current.copy(scoutUses = 0, bardUses = 0, warriorUsed = false,
                            log = (listOf("MODE TEST : compteurs quotidiens Éclaireur, Barde et Guerrier remis à zéro (sans modifier l’aventure).") + current.log).take(100)))
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
    onWarrior: () -> Unit,
    onResolve: (Int) -> Unit,
    onWeaponAttack: (String, Int, Boolean, Int?, Boolean) -> Unit,
    onSkip: () -> Unit,
    onFlee: (Boolean) -> Unit,
    onHunt: () -> Unit,
    onMystic: (Int) -> Unit,
    onMeditate: () -> Unit,
    onUsePersuasion: () -> Unit,
    onUseConsumable: (String, String) -> Unit,
    onPlan: (String, Double, Boolean, Boolean) -> Unit,
    onArrive: () -> Unit
) {
    var selectedPlace by remember { mutableStateOf("Fontaria") }
    var expanded by remember { mutableStateOf(false) }
    var distanceInput by remember { mutableStateOf("") }
    var scoutChosen by remember { mutableStateOf(false) }
    var bardAudacious by remember { mutableStateOf(false) }
    var monkPrayerConfirmed by remember(state.lastEncounter?.roll, state.lastEncounter?.contextDie) { mutableStateOf(false) }
    var damageInput by remember { mutableStateOf("") }
    var selectedWeapon by remember { mutableStateOf("") }
    var rangedTarget by remember { mutableIntStateOf(-1) }
    var attackCount by remember { mutableStateOf("1") }
    var pattesUsed by remember { mutableStateOf(false) }
    var supportConfirmed by remember { mutableStateOf(false) }
    val novice = state.character.profileType == "novice"
    val unit = if (novice) "min" else "km"
    val pending = state.lastEncounter != null
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("🗺️ Expédition", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineSmall) }
        item { Text("Position actuelle : ${state.character.location}") }
        item { Text("Classe : ${state.character.className} · Niveau ${state.character.level}") }
        if (state.character.specialAbility.isNotBlank()) item { Text("Mouvement personnel : ${state.character.specialAbility}", style = MaterialTheme.typography.bodySmall) }
        if (state.character.profileType == "advanced" && state.character.className == "Acrobate") item { Text("Acrobate (livre p. 41) : +2 au jet de contexte ; en cas de fuite, retour au départ OU poursuite vers la destination.") }
        if (state.character.profileType == "advanced" && state.character.className == "Chasseur") item { Text("Chasseur (livre p. 41) : peut réduire de 1 le dé de quantité d’une rencontre choisie pendant un déplacement.") }
        if (state.character.profileType == "advanced" && state.character.className == "Éclaireur") item { Text("Éclaireur : distance divisée par 2 pour un déplacement, deux fois par jour. Utilisations restantes : ${2 - dailyState(state).scoutUses}.") }
        if (state.character.profileType == "advanced" && state.character.className == "Mage") item { Text("Mage (p. 41) : ajuster un dé de rencontre de ±1. Ajustement du contexte disponible dans cette version.") }
        if (state.character.profileType == "advanced" && state.character.className == "Moine") item { Text("Moine (p. 41) : ajuster un dé de rencontre de ±1 ou ±2 contre une Prière. Ajustement du contexte disponible dans cette version.") }
        if (state.character.profileType == "advanced" && state.character.className == "Barde") item { Text("Barde (règle maison solo) : distance ÷2 deux fois par jour. Mode prudent : quantité et butin normaux ; mode audacieux : quantité ×2 et butin total ×2,5. Utilisations restantes : ${2 - dailyState(state).bardUses}.") }
        if (state.character.profileType == "advanced" && state.character.className == "Guerrier") item { Text("Guerrier : double les dégâts d’une rencontre contre une Prière, une fois par jour. ${if (dailyState(state).warriorUsed) "Déjà utilisée aujourd’hui." else "Disponible."}") }
        if (state.retreatActive) item { Text("RETOUR DE FUITE : rejoignez ${state.retreatOrigin}. Aucune nouvelle rencontre pendant ce trajet.", color = MaterialTheme.colorScheme.error) }
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
            item { Button(enabled = (distanceInput.replace(',','.').toDoubleOrNull() ?: 0.0) > 0.0 &&
                selectedPlace != state.character.location,
                onClick = { onPlan(selectedPlace, distanceInput.replace(',','.').toDouble(), scoutChosen, scoutChosen && bardAudacious) ; scoutChosen = false; bardAudacious = false },
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
            item { Text("⚔️ RENCONTRE — TABLE DU LIVRE", color = Color(0xFFEBC582), style = MaterialTheme.typography.headlineSmall) }
            item { Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${e.name} ×${e.quantity} (jet : ${e.roll})", style = MaterialTheme.typography.titleLarge)
                HealthGauge("❤️ Santé totale des ennemis", encounterHp(e).sum(), e.hp * e.quantity)
                Text("🪙 Butin potentiel : ${e.reward} PO")
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
                if (state.character.profileType == "advanced" && state.character.className in listOf("Mage", "Moine") && !e.mysticUsed && e.contextDie > 0 && encounterHp(e).all { it == e.hp }) {
                    Text(if (state.character.className == "Moine") "Moine : effectuez une Prière avant de modifier ce dé de contexte (p. 41)." else "Mage : modifiez le dé de contexte de ±1 (p. 41).")
                    if (state.character.className == "Moine") Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = monkPrayerConfirmed, onCheckedChange = { monkPrayerConfirmed = it })
                        Text("J’ai effectué la Prière")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val deltas = if (state.character.className == "Moine") listOf(-2, -1, 1, 2) else listOf(-1, 1)
                        deltas.forEach { d -> OutlinedButton(onClick = { onMystic(d) }, enabled = (e.contextDie + d) in 1..6 && (state.character.className != "Moine" || monkPrayerConfirmed)) { Text(if (d > 0) "+$d" else "$d") } }
                    }
                    Text("Les modifications des dés de type et de quantité seront intégrées séparément ; le livre permet de modifier un dé de rencontre, pas uniquement le contexte.", style = MaterialTheme.typography.bodySmall)
                }
                if (state.character.profileType == "advanced" && state.character.className == "Chasseur" && !e.safe) {
                    val unhurt = encounterHp(e).all { it == e.hp }
                    if (!e.hunterUsed && unhurt && e.quantity > (if (e.roll == 7) 10 else 1) && !e.effect.contains("quantité fixe", ignoreCase = true)) {
                        Text("Capacité Chasseur : réduire de 1 le résultat du dé de quantité (avant toute attaque).")
                        OutlinedButton(onClick = onHunt, modifier = Modifier.fillMaxWidth()) { Text("Chasseur — écarter 1 adversaire") }
                    } else if (e.hunterUsed) Text("Capacité Chasseur déjà utilisée sur cette rencontre.")
                    else if (e.quantity <= 1) Text("Chasseur : quantité minimale de 1 retenue pour l’application.")
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
                    if ("Shaker vigueur" in state.inventory && !state.vigorActive) {
                        Button(onClick = { onUseConsumable("Shaker vigueur", "") }, modifier = Modifier.fillMaxWidth()) { Text("Boire un Shaker vigueur — dégâts ×2") }
                    }
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
                            val n = (attackCount.toIntOrNull() ?: 0).coerceIn(0, 1000)
                            val damagePreview = if (n > 0) warriorDamage(state, attackDamage(w, n, state.inventory, pattesUsed, state.meditationActive, state.vigorActive, if (supportConfirmed) activeSupport?.bonus ?: 0 else 0)) else 0
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
                                onClick = { onWeaponAttack(w.name, n, pattesUsed, if (w.ranged) rangedTarget else null, supportConfirmed); attackCount = "1"; pattesUsed = false; supportConfirmed = false },
                                modifier = Modifier.fillMaxWidth()) { Text("Exercice réalisé — infliger $damagePreview PV") }
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

@Composable private fun ApplicationInfo(state: AppState, onResetDailyTests: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Application", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Dungeon of Fitness · Version 1.19.1", style = MaterialTheme.typography.titleLarge) }
        item { Text("Mode test — capacités quotidiennes", style = MaterialTheme.typography.titleMedium) }
        item { Text("Éclaireur : ${dailyState(state).scoutUses}/2 · Barde : ${dailyState(state).bardUses}/2 · Guerrier : ${if (dailyState(state).warriorUsed) "utilisé" else "disponible"}.") }
        item { OutlinedButton(onClick = onResetDailyTests, modifier = Modifier.fillMaxWidth()) { Text("MODE TEST : remettre les utilisations du jour à zéro") } }
        item { Text("Ne supprime ni le personnage, ni les PO, ni l’inventaire, ni les kilomètres, ni le trajet ou le combat en cours. Le bonus Guerrier déjà activé pendant le combat reste actif.", style = MaterialTheme.typography.bodySmall) }
        item { Text("Fonctionnalités intégrées", style = MaterialTheme.typography.titleMedium) }
        listOf(
            "Navigation entre les écrans",
            "Planification manuelle d'un trajet sur la carte du livre",
            "Suivi des distances cumulées et validation d'arrivée",
            "Rencontres : table officielle Fitland (7D6), quantité, contexte et PV",
            "Arme active et bonus de contexte des gants de vétéran",
            "Historique du dé de contexte et de ses modificateurs",
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

@Composable private fun Inventory(state: AppState, onEquip: (String) -> Unit, onBuy: (Weapon) -> Unit, onUpgradeBuy: (CombatUpgrade) -> Unit, onSupportBuy: (Support) -> Unit, onEquipSupport: (String) -> Unit, onConsumableBuy: (String) -> Unit, onTestGold: (Int) -> Unit) {
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
            listOf("Armes", "Soutien", "Améliorations", "Consommables").forEach { category ->
                androidx.compose.material3.TextButton(onClick = { bagTab = category }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp)) {
                    Text(if (bagTab == category) "●$category" else category, style = MaterialTheme.typography.labelSmall)
                }
            }
        } }
        item { Text("Équipement possédé dans cette catégorie", style = MaterialTheme.typography.titleMedium) }
        val owned = state.inventory.filter { name -> when(bagTab) {
            "Armes" -> weapons.any { it.name == name }
            "Soutien" -> supports.any { it.name == name }
            "Améliorations" -> combatUpgrades.any { it.name == name }
            else -> name.startsWith("Shaker ")
        } }
        if (owned.isEmpty()) item { Text("Aucun objet dans cette catégorie.") }
        items(owned.groupingBy { it }.eachCount().toList().sortedBy { it.first }) { (name, quantity) ->
            Text("${itemSymbol(name)}  $name${if (quantity > 1) " ×$quantity" else ""}")
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
        item { Text("Améliorations de combat", style = MaterialTheme.typography.titleMedium) }
        item { Text("Bonus fixes appliqués une fois par attaque, et non à chaque répétition. Les Pattes d’ours nécessitent de confirmer le comptage oral.", style = MaterialTheme.typography.bodySmall) }
        items(combatUpgrades) { u ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${itemSymbol(u.name)} ${u.name} — ${u.price} PO", style = MaterialTheme.typography.titleMedium)
                Text("+${u.bonus} PV · ${u.description}")
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
            "Shaker illusion" to "Règle maison : 1d6 ennemis écartés, minimum 1 restant ; butin réduit"
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
        item { Text("21 armes et exercices, d’après le Guide touristique p. 163–175.", style = MaterialTheme.typography.bodySmall) }
        item { Text("Achat irréversible : le livre interdit la revente. Les règles de dotation initiale (10 PO par niveau à dépenser intégralement) ne sont pas encore automatisées.", style = MaterialTheme.typography.bodySmall) }
        items(weapons) { w ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("${itemSymbol(w.name)} ${w.name} — ${w.price} PO", style = MaterialTheme.typography.titleMedium)
                Text("${w.damage} PV / ${w.unit}")
                Text(w.exercise)
                if (w.ranged) Text("Effet officiel : dégâts à distance")
                if (w.contextBonus > 0) Text("Effet officiel : +${w.contextBonus} au jet de contexte (si arme active lors du tirage)")
                Button(enabled = state.character.po >= w.price && w.name !in state.inventory,
                    onClick = { onBuy(w) }) { Text(if (w.name in state.inventory) "Déjà possédé" else "Acheter (${w.price} PO)") }
            } }
        }
        }
    }
}

@Composable private fun Journal(state: AppState) { LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Journal d'aventure", style = MaterialTheme.typography.headlineSmall) }; items(state.log) { Text(it) }; item { Text("Quêtes") }; items(state.quests) { Text("□ $it") } } }

private fun travel(s: AppState, km: Double): AppState {
    val old = s.distance
    val newDistance = (old + km).coerceAtMost(s.destinationKm)
    val oldChecks = kotlin.math.floor(s.totalTravelKm / 6.0).toInt()
    val actualProgress = newDistance - old
    val newTotal = s.totalTravelKm + if (s.retreatActive) 0.0 else actualProgress
    val newChecks = kotlin.math.floor(newTotal / 6.0).toInt()
    val encounter = if (!s.retreatActive && newChecks > oldChecks) generateEncounter(s.equippedWeapon, s.equippedSupport, s.character, s.bardJourney) else s.lastEncounter
    val logs = if (encounter != null && !s.retreatActive && newChecks > oldChecks) listOf(
        "Rencontre : ${encounter.name} ×${encounter.quantity}" + if (s.bardJourney) {
            if (encounter.effect.contains("quantité fixe", ignoreCase = true)) " — Barde audacieux : quantité fixe, pas de bonus."
            else " — Barde audacieux : quantité doublée et butin ×2,5."
        } else ""
    ) + s.log else listOf("+${"%.1f".format(km)} km parcouru") + s.log
    return s.copy(distance = newDistance, totalTravelKm = newTotal, lastEncounter = encounter, meditationActive = if (newChecks > oldChecks) false else s.meditationActive, warriorActive = if (newChecks > oldChecks) false else s.warriorActive,
        vigorActive = if (newChecks > oldChecks) false else s.vigorActive,
        quantumMode = if (newChecks > oldChecks) "" else s.quantumMode, encounters = s.encounters + if (!s.retreatActive && newChecks > oldChecks) 1 else 0, log = logs.take(100))
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

private fun generateEncounter(equippedWeapon: String, equippedSupport: String, character: Character, bardJourney: Boolean = false): Encounter? {
    val roll = (1..7).sumOf { Random.nextInt(1, 7) }
    val rule = encounterRules[roll - 7]
    // Campagne actuelle : Fitland. Les rencontres d'autres régions sont ignorées.
    if (!(rule.zones == "Toutes" || rule.zones.contains("Fitland")) || rule.zones == "Toutes sauf Fitland") return null
    val fixedQuantity = rule.effect.contains("Quantité fixe", ignoreCase = true)
    val quantity = if (fixedQuantity) 1 else Random.nextInt(1, 7) * (if (roll == 7) 10 else 1) * (if (bardJourney) 2 else 1)
    // Mode audacieux : +25 % sur le butin des adversaires doublés, arrondi à l'entier inférieur.
    // Une rencontre à quantité fixe n'est pas doublée et ne bénéficie pas de ce bonus.
    val reward = (rule.reward.toLong() * quantity * (if (bardJourney && !fixedQuantity) 5L else 4L) / 4L).toInt()
    val contextDie = Random.nextInt(1, 7)
    val equipmentBonus = weapons.firstOrNull { it.name == equippedWeapon }?.contextBonus ?: 0
    val supportBonus = supports.firstOrNull { it.name == equippedSupport }?.context ?: 0
    val acrobatBonus = if (character.profileType == "advanced" && character.className == "Acrobate") 2 else 0
    val contextBonus = equipmentBonus + supportBonus + (if (roll == 15) 2 else 0) + acrobatBonus
    // La table p. 42 s'arrête à 6 : les dépassements sont traités comme 6.
    val contextRoll = (contextDie + contextBonus).coerceAtMost(6)
    val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
    val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
    val safe = rule.effect.startsWith("Inoffensif")
    return Encounter(rule.name, quantity, rule.hp, reward, contexts[contextRoll - 1], effects[contextRoll - 1], roll, rule.hp * quantity, rule.effect, safe, contextDie, contextBonus, listOfNotNull(if (equipmentBonus > 0) equippedWeapon else null, if (supportBonus > 0) equippedSupport else null, if (acrobatBonus > 0) "Acrobate +2" else null).joinToString(" + "), enemyHp = List(quantity) { rule.hp })
}

// Les anciennes sauvegardes n'avaient qu'une réserve commune : reconstruction déterministe.
private fun encounterHp(e: Encounter): List<Int> {
    if (e.enemyHp.size == e.quantity) return e.enemyHp
    var remaining = e.remainingHp.coerceAtLeast(0)
    return List(e.quantity) {
        val hp = minOf(e.hp, remaining)
        remaining -= hp
        hp
    }
}

private fun applyDamage(s: AppState, damage: Int, target: Int?): AppState = s.lastEncounter?.let { e ->
    val hp = encounterHp(e).toMutableList()
    var pending = damage.coerceAtLeast(0)
    if (target != null) {
        if (target !in hp.indices || hp[target] <= 0) return@let s
        val dealt = minOf(pending, hp[target])
        hp[target] -= dealt
        pending = dealt
    } else {
        // Compatibilité avec le combat existant : dégâts répartis, sans perte entre cibles.
        for (i in hp.indices) {
            val dealt = minOf(pending, hp[i])
            hp[i] -= dealt
            pending -= dealt
            if (pending == 0) break
        }
    }
    val remaining = hp.sum()
    val dealt = if (target == null) damage.coerceAtLeast(0) - pending else pending
    if (remaining > 0) s.copy(lastEncounter = e.copy(remainingHp = remaining, enemyHp = hp),
        log = (listOf("${e.name} : $dealt dégâts, $remaining PV restants (${hp.count { it > 0 }} debout)") + s.log).take(100))
    else s.copy(character = s.character.copy(po = s.character.po + e.reward), lastEncounter = null,
        meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false,
        log = (listOf("${e.name} vaincu(s), butin : ${e.reward} PO") + s.log).take(100))
} ?: s

private fun resolve(s: AppState, damage: Int): AppState = applyDamage(s, damage, null)
private fun resolveTarget(s: AppState, damage: Int, index: Int): AppState = applyDamage(s, damage, index)

private fun removeOne(items: List<String>, name: String): List<String> {
    val index = items.indexOf(name)
    return if (index < 0) items else items.filterIndexed { i, _ -> i != index }
}

private fun skipEncounter(s: AppState): AppState = s.lastEncounter?.let { e ->
    if (!e.safe && e.context != "Ils n'ont rien vu.") return@let s
    s.copy(lastEncounter = null, meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false, log = (listOf("Rencontre évitée ou pacifique : ${e.name}. Aucun butin ni retour obligatoire.") + s.log).take(100))
} ?: s

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
    val returnDistance = if (acrobatForward) (s.destinationKm - s.distance).coerceAtLeast(0.0) else s.distance.coerceAtLeast(0.0)
    val start = if (acrobatForward) s.destination else s.character.location
    s.copy(lastEncounter = null, meditationActive = false, vigorActive = false, quantumMode = "", illusionActive = false, warriorActive = false,
        retreatActive = returnDistance > 0.0, retreatOrigin = start,
        destination = start, destinationKm = returnDistance, distance = 0.0,
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
        put("schema", 19); put("character", character); put("distance", state.distance)
        put("destination", state.destination); put("destinationKm", state.destinationKm)
        put("totalTravelKm", state.totalTravelKm)
        put("encounters", state.encounters); put("retreatActive", state.retreatActive); put("retreatOrigin", state.retreatOrigin)
        put("log", JSONArray(state.log)); put("inventory", JSONArray(state.inventory))
        put("equippedWeapon", state.equippedWeapon); put("equippedSupport", state.equippedSupport); put("meditationActive", state.meditationActive)
        put("vigorActive", state.vigorActive); put("quantumMode", state.quantumMode); put("illusionActive", state.illusionActive)
        put("abilityDay", state.abilityDay); put("scoutUses", state.scoutUses); put("warriorUsed", state.warriorUsed); put("warriorActive", state.warriorActive); put("bardUses", state.bardUses); put("bardJourney", state.bardJourney)
        put("quests", JSONArray(state.quests))
        state.lastEncounter?.let { e -> put("lastEncounter", JSONObject().apply {
            put("name", e.name); put("quantity", e.quantity); put("hp", e.hp)
            put("reward", e.reward); put("context", e.context); put("exercise", e.exercise)
            put("roll", e.roll); put("remainingHp", e.remainingHp); put("effect", e.effect); put("safe", e.safe)
            put("contextDie", e.contextDie); put("contextBonus", e.contextBonus); put("contextWeapon", e.contextWeapon)
            put("initialQuantity", e.initialQuantity); put("illusionDie", e.illusionDie); put("hunterUsed", e.hunterUsed); put("mysticUsed", e.mysticUsed); put("mysticDescription", e.mysticDescription)
            put("enemyHp", JSONArray(encounterHp(e)))
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
                it.optBoolean("hunterUsed", false), it.optBoolean("mysticUsed", false), it.optString("mysticDescription", ""))
        }
        val baseState = AppState()
        baseState.copy(character = if (c.profileType == "advanced" && c.className in classAchievements) c.copy(unlockedClasses = (c.unlockedClasses + c.className).distinct()) else c,
            distance = obj.optDouble("distance", 0.0),
            destination = obj.optString("destination", baseState.destination),
            destinationKm = obj.optDouble("destinationKm", baseState.destinationKm),
            totalTravelKm = obj.optDouble("totalTravelKm", obj.optDouble("distance", 0.0)),
            encounters = obj.optInt("encounters", 0), lastEncounter = encounter,
            retreatActive = obj.optBoolean("retreatActive", false), retreatOrigin = obj.optString("retreatOrigin", ""),
            equippedWeapon = obj.optString("equippedWeapon", ""), equippedSupport = obj.optString("equippedSupport", ""), meditationActive = obj.optBoolean("meditationActive", false),
            vigorActive = obj.optBoolean("vigorActive", false), quantumMode = obj.optString("quantumMode", ""), illusionActive = obj.optBoolean("illusionActive", false),
            log = strings("log", baseState.log), inventory = strings("inventory", baseState.inventory),
            abilityDay = obj.optString("abilityDay", ""), scoutUses = obj.optInt("scoutUses", 0),
            warriorUsed = obj.optBoolean("warriorUsed", false), warriorActive = obj.optBoolean("warriorActive", false), bardUses = if (obj.has("bardUses")) obj.optInt("bardUses", 0) else if (obj.optBoolean("bardUsed", false)) 1 else 0, bardJourney = obj.optBoolean("bardJourney", false),
            quests = strings("quests", baseState.quests))
    } catch (_: Exception) { AppState() }
}

// Limites quotidiennes calculées sur la date locale de l'appareil (mode hors ligne).
private fun dailyState(s: AppState): AppState {
    val today = java.time.LocalDate.now().toString()
    return if (s.abilityDay == today) s else s.copy(abilityDay = today, scoutUses = 0, warriorUsed = false, bardUses = 0)
}

private fun planJourney(state: AppState, destination: String, km: Double, scout: Boolean, bardAudacious: Boolean): AppState {
    val s = dailyState(state)
    val allowed = scout && s.character.profileType == "advanced" && s.character.className == "Éclaireur" && s.scoutUses < 2
    val bard = scout && s.character.profileType == "advanced" && s.character.className == "Barde" && s.bardUses < 2
    val effective = if (allowed || bard) km / 2.0 else km
    val audacious = bard && bardAudacious
    val note = when {
        allowed -> " (Éclaireur : demi-distance, ${s.scoutUses + 1}/2 aujourd’hui)"
        audacious -> " (Barde, règle maison : demi-distance ${s.bardUses + 1}/2 ; mode audacieux, quantité ×2, butin ×2,5)"
        bard -> " (Barde, règle maison : demi-distance ${s.bardUses + 1}/2 ; mode prudent, rencontres normales)"
        else -> ""
    }
    return s.copy(destination = destination, destinationKm = effective, distance = 0.0,
        scoutUses = s.scoutUses + if (allowed) 1 else 0,
        bardUses = s.bardUses + if (bard) 1 else 0,
        bardJourney = audacious,
        log = (listOf("Nouveau trajet vers $destination : ${"%.2f".format(effective)} km$note.") + s.log).take(100))
}

private fun activateWarrior(state: AppState): AppState {
    val s = dailyState(state)
    if (s.character.profileType != "advanced" || s.character.className != "Guerrier" || s.lastEncounter == null || s.lastEncounter.safe || s.warriorUsed) return s
    return s.copy(warriorUsed = true, warriorActive = true,
        log = (listOf("Guerrier : Prière confirmée ; dégâts des attaques avec arme ×2 pendant cette rencontre (1 utilisation quotidienne).") + s.log).take(100))
}

private fun warriorDamage(state: AppState, damage: Int): Int =
    if (state.warriorActive) (damage.toLong() * 2).coerceAtMost(Int.MAX_VALUE.toLong()).toInt() else damage

// Livre p. 41-42 : ajustement d'un dé de rencontre ; ici le dé de contexte uniquement.
private fun useMysticContext(state: AppState, delta: Int): AppState {
    val e = state.lastEncounter ?: return state
    val cls = state.character.className
    if (state.character.profileType != "advanced" || cls !in listOf("Mage", "Moine") || e.mysticUsed ||
        encounterHp(e).any { it != e.hp } || e.contextDie <= 0 ||
        delta !in (if (cls == "Mage") listOf(-1, 1) else listOf(-2, -1, 1, 2)) ||
        e.contextDie + delta !in 1..6) return state
    val index = (e.contextDie + delta + e.contextBonus).coerceIn(1, 6) - 1
    val contexts = listOf("Vous êtes piégé !", "Une embuscade !", "Ils arrivent !", "Ils ne vous échapperont pas.", "Ils sont à votre merci.", "Ils n'ont rien vu.")
    val effects = listOf("10 burpees par adversaire", "Durée d'utilisation des objets ×2", "50 jumping jacks", "20 jumping jacks", "Aucun exercice", "Rencontre évitable")
    val detail = "$cls : dé de contexte ${e.contextDie} ${if (delta > 0) "+" else ""}$delta = ${e.contextDie + delta}${if (cls == "Moine") " (Prière confirmée)" else ""}"
    return state.copy(lastEncounter = e.copy(contextDie = e.contextDie + delta, context = contexts[index], exercise = effects[index], mysticUsed = true, mysticDescription = detail), log = (listOf(detail) + state.log).take(100))
}
