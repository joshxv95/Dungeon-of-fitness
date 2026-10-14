package com.dungeonoffitness.app

/** Guide touristique, p. 174 : dégâts par durée, exécution et amélioration.
 *  Les bonus d'amélioration s'appliquent UNE fois par attaque validée
 *  (convention de l'application conservée depuis la v1.6).
 */
object ScrollCombatRules {
    fun agilityBonus(inventory: List<String>): Int = if ("Manuel d’agilité" in inventory) 2 else 0

    /** Les jets de contexte sont plafonnés à 6 : convention existante de l'application. */
    fun flexibilityContext(die: Int, previousBonus: Int): Int =
        (die + previousBonus + 5).coerceIn(1, 6)

    fun weaponDamage(
        basePerUnit: Int, units: Int, improvementBonus: Int, supportBonus: Int,
        isTimed: Boolean, cardioActive: Boolean, forceActive: Boolean,
        congestionActive: Boolean, vigorActive: Boolean
    ): Int {
        if (units <= 0 || basePerUnit < 0) return 0
        // Saturation avant chaque multiplication : les valeurs issues du champ de saisie
        // ne doivent jamais faire déborder un Long, même avec plusieurs pouvoirs actifs.
        val cap = Int.MAX_VALUE.toLong()
        val rawBase = (basePerUnit.toLong() * units.toLong()).coerceAtMost(cap)
        val base = (rawBase * (if ((isTimed && cardioActive) || (!isTimed && forceActive)) 2L else 1L)).coerceAtMost(cap)
        val improvements = improvementBonus.coerceAtLeast(0).toLong() *
            (if (congestionActive) 2L else 1L)
        val support = supportBonus.coerceAtLeast(0).toLong()
        val beforeVigor = (base + improvements + support).coerceAtMost(cap)
        return (beforeVigor * (if (vigorActive) 2L else 1L)).coerceAtMost(cap).toInt()
    }
}
