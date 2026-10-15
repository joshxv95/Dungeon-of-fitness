package com.dungeonoffitness.app

/** Sorts officiels du Mage, livre p. 176. Fonctions sans dépendance Android. */
internal object MageSpellRules {
    fun mayCast(spell: String, className: String, agility: Int, novice: Boolean = false, adaptation: Int = 0): Boolean {
        if (className != "Mage") return false
        val (agilityRequired, adaptationRequired) = when (spell) {
            "force", "cardio" -> 5 to 8
            "acid" -> 7 to 10
            "rest" -> 2 to 3
            else -> return false
        }
        return if (novice) adaptation >= adaptationRequired else agility >= agilityRequired
    }

    /** Chaque tranche complète de 200 PO consacrée au bijou ajoute +1 au contexte (p. 164). */
    fun jewelBonus(spent: Int): Int = spent.coerceAtLeast(0) / 200

    /** La table de contexte officielle est numérotée de 1 à 6. */
    fun contextAfterCardio(die: Int, bonus: Int): Int = (die + bonus + 1).coerceIn(1, 6)

    data class AttackResult(val remaining: List<Int>, val inflicted: Int, val markedHitCount: Int)

    /** Attaque unique, dégâts directs d'abord, puis +50 sur chaque cible marquée touchée.
     * Le bonus ciblé ne se transfère pas aux autres adversaires ; une cible peut recevoir
     * un bonus au plus une fois par attaque déclarée, même pour une attaque à plusieurs coups.
     */
    fun applyAttack(hp: List<Int>, damage: Int, target: Int?, markedTargets: Collection<Int>): AttackResult {
        val left = hp.map { it.coerceAtLeast(0) }.toMutableList()
        var pending = damage.coerceAtLeast(0)
        if (target != null) {
            if (target !in left.indices || left[target] == 0) return AttackResult(left, 0, 0)
            val dealt = minOf(pending, left[target])
            left[target] -= dealt
        } else {
            for (i in left.indices) {
                val dealt = minOf(pending, left[i])
                left[i] -= dealt
                pending -= dealt
                if (pending == 0) break
            }
        }
        val directlyHit = hp.indices.filter { hp[it] > left[it] }
        var bonusTargets = 0
        for (i in directlyHit) {
            if (i in markedTargets && left[i] > 0) {
                left[i] = (left[i] - 50).coerceAtLeast(0)
                bonusTargets++
            }
        }
        val inflicted = (hp.sumOf { it.toLong() } - left.sumOf { it.toLong() }).coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
        return AttackResult(left, inflicted, bonusTargets)
    }
}
