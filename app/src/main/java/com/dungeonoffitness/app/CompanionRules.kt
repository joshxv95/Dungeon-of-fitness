package com.dungeonoffitness.app

/** Calculs purs partagés par l'app et les tests sans Android.
 *  Barde compagnon : p. 113 ; Oracle : p. 73 ; Paladin/Quêteur : p. 129.
 */
internal object CompanionRules {
    fun journeyDistance(km: Double, shortened: Boolean): Double = if (shortened) km / 2.0 else km

    fun attackDamage(base: Int, warrior: Boolean, oracle: Boolean): Int =
        (base.coerceAtLeast(0).toLong() * (if (warrior) 2L else 1L) * (if (oracle) 2L else 1L))
            .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    /** Convention d'application explicite : le dé ne descend pas en dessous de 1. */
    fun paladinDie(before: Int): Int = if (before in 1..6) (before - 2).coerceAtLeast(1) else before

    fun enemyCount(die: Int, typeRoll: Int, bardAudacious: Boolean): Int =
        die * (if (typeRoll == 7) 10 else 1) * (if (bardAudacious) 2 else 1)

    fun reward(perEnemy: Int, quantity: Int, bardAudacious: Boolean): Int =
        (perEnemy.toLong() * quantity * (if (bardAudacious) 5L else 4L) / 4L)
            .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}
