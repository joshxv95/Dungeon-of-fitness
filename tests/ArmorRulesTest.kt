package com.dungeonoffitness.app

fun main() {
    check(ArmorRules.effectiveContext(1, 0) == 1)
    check(ArmorRules.effectiveContext(6, 0) == 6)
    check(ArmorRules.effectiveContext(1, 2) == 3)
    check(ArmorRules.effectiveContext(5, 2) == 6)
    check(ArmorRules.effectiveContext(6, 20) == 6)
    // Le jet peut rester moins favorable : le livre laisse le choix au joueur.
    check(ArmorRules.effectiveContext(3, 1) == 4)
    check(ArmorRules.effectiveContext(1, 1) == 2)
    check(ArmorRules.bonusForUnits(100, 1, false) == 0)
    check(ArmorRules.bonusForUnits(100, 1, true) == 100)
    check(ArmorRules.bonusForUnits(50, 3, true) == 150)
    check(ArmorRules.bonusForUnits(100, 1000, true) == 100000)
    check(ArmorRules.bonusForUnits(Int.MAX_VALUE, 1000, true) == Int.MAX_VALUE)
    println("12 tests ArmorRules validés.")
}
