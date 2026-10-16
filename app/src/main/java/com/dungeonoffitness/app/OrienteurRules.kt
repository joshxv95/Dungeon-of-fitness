package com.dungeonoffitness.app

/** Livre Dungeon of Fitness, table p. 43 et fiche p. 185.
 * La poursuite est un état persistant de l'aventure, distinct du combat en cours.
 * Le renfort ne change ni le dé de quantité, ni les PO gagnés.
 */
internal object OrienteurRules {
    const val PURSUER_HP = 400

    fun beginsPursuit(encounterName: String): Boolean = encounterName == "Orienteur obsédé"

    /** Un seul Orienteur à la fois, mais il revient à chaque rencontre suivante. */
    fun shouldJoin(pursuitActive: Boolean, encounterName: String): Boolean =
        pursuitActive && encounterName != "Orienteur obsédé"

    /** Les choix du Clan sont mutuellement exclusifs : combat, ou fin de poursuite sans butin. */
    fun canNegotiateNazcool(encounterName: String, untouched: Boolean): Boolean =
        encounterName == "Clan Nazcool" && untouched

    /** Une rencontre pacifique n'est pas attaquée : seul le poursuivant est hostile. */
    fun combatTargets(mainHp: List<Int>, followerHp: Int, peaceful: Boolean): List<Int> =
        (if (peaceful && followerHp > 0) List(mainHp.size) { 0 } else mainHp) +
            if (followerHp > 0) listOf(followerHp.coerceIn(0, PURSUER_HP)) else emptyList()

    /** Le dernier indice appartient au poursuivant ; une fois vaincu, il ne bloque plus
     * le combat en cours mais sera recréé à 400 PV lors de la rencontre suivante.
     */
    fun rewardForVictory(mainReward: Int, peaceful: Boolean): Int = if (peaceful) 0 else mainReward.coerceAtLeast(0)

    fun followerAfterDamage(remaining: List<Int>, mainCount: Int, initialFollowerHp: Int): Int =
        if (initialFollowerHp > 0) remaining.getOrNull(mainCount)?.coerceIn(0, PURSUER_HP) ?: 0 else 0
}
