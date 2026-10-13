import com.dungeonoffitness.app.CompanionRules

fun main() {
    // Barde compagnon : demi-distance, non cumul avec classe (un seul raccourci passé au calcul).
    check(CompanionRules.journeyDistance(35.0, true) == 17.5)
    check(CompanionRules.journeyDistance(35.0, false) == 35.0)
    check(CompanionRules.journeyDistance(5.0, true) == 2.5)

    // Oracle : bonus d'attaque, et combinaison avec Guerrier si les deux effets sont actifs.
    check(CompanionRules.attackDamage(150, warrior = false, oracle = true) == 300)
    check(CompanionRules.attackDamage(150, warrior = true, oracle = false) == 300)
    check(CompanionRules.attackDamage(150, warrior = true, oracle = true) == 600)
    check(CompanionRules.attackDamage(Int.MAX_VALUE, warrior = true, oracle = true) == Int.MAX_VALUE)

    // Paladin/Quêteur : dé 6 -> 4, quantité et butin corrélés ; convention minimum 1.
    check(CompanionRules.paladinDie(6) == 4)
    check(CompanionRules.paladinDie(3) == 1)
    check(CompanionRules.paladinDie(1) == 1)
    check(CompanionRules.enemyCount(4, 10, false) == 4)
    check(CompanionRules.reward(10, 4, false) == 40)
    check(CompanionRules.enemyCount(4, 10, true) == 8)
    check(CompanionRules.reward(10, 8, true) == 100)
    check(CompanionRules.enemyCount(4, 7, false) == 40)
    println("OK : 15 assertions — trajets, dégâts, quantité, butin, limites")
}
