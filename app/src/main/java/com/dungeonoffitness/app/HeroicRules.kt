package com.dungeonoffitness.app

/** Améliorations du catalogue, p. 171. Les partenaires sont confirmés par le joueur.
 * Chaque effet officiel double les dégâts d'une attaque si sa condition est remplie.
 * Le cumul éventuel des deux objets et des autres multiplicateurs est une convention
 * d'adaptation : multiplication successive, avec saturation pour éviter un overflow.
 */
object HeroicRules {
    fun multiplier(heroicOwned: Boolean, twoAgainstOneConfirmed: Boolean, livingEnemies: Int,
        crownOwned: Boolean, realPlayerConfirmed: Boolean): Int {
        val heroic = heroicOwned && twoAgainstOneConfirmed && livingEnemies == 1
        val crown = crownOwned && realPlayerConfirmed
        return (if (heroic) 2 else 1) * (if (crown) 2 else 1)
    }
    fun damage(base: Int, heroicOwned: Boolean, twoAgainstOneConfirmed: Boolean,
        livingEnemies: Int, crownOwned: Boolean, realPlayerConfirmed: Boolean): Int =
        (base.coerceAtLeast(0).toLong() * multiplier(heroicOwned, twoAgainstOneConfirmed,
            livingEnemies, crownOwned, realPlayerConfirmed)).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}
