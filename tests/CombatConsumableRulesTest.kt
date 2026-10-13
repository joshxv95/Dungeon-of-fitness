package com.dungeonoffitness.app

private fun checkEqual(name: String, actual: Any, expected: Any) {
    if (actual != expected) error("$name : attendu $expected, obtenu $actual")
}

fun main() {
    val group = groupSplashDamage(listOf(300, 1500, 700, 0), 700)
    checkEqual("PV résiduels", group.remainingHp, listOf(0, 800, 0, 0))
    checkEqual("dégâts effectifs", group.damageDealt, 300 + 700 + 700)
    checkEqual("ennemis touchés", group.hitCount, 3)
    val death = groupSplashDamage(listOf(200, 550, 0), 700)
    checkEqual("groupe éliminé", death.remainingHp, listOf(0, 0, 0))
    checkEqual("dégâts plafonnés aux PV", death.damageDealt, 750)
    checkEqual("ennemis initialement vivants", death.hitCount, 2)
    val legacy = groupSplashDamage(listOf(-10, 100), 700)
    checkEqual("PV sauvés non négatifs", legacy.remainingHp, listOf(0, 0))
    checkEqual("ancien enregistrement", legacy.damageDealt, 100)
    val noOp = groupSplashDamage(listOf(200, 300), 0)
    checkEqual("aucun dégât", noOp.remainingHp, listOf(200, 300))
    checkEqual("zéro encaissé", noOp.damageDealt, 0)
    val single = groupSplashDamage(listOf(2000), 700)
    checkEqual("cible unique dans groupe", single.remainingHp, listOf(1300))
    checkEqual("cible unique dégât", single.damageDealt, 700)
    val rare = groupSplashDamage(List(60) { 3000 }, 700)
    checkEqual("groupe étendu", rare.remainingHp.size, 60)
    checkEqual("groupe étendu dégâts", rare.damageDealt, 42000)
    println("13 vérifications Shaker sombre : OK")
}
