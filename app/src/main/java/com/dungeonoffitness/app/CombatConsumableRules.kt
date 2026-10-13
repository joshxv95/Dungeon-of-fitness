package com.dungeonoffitness.app

/** Effet de groupe : chaque ennemi encore vivant reçoit au plus [damagePerTarget] dégâts.
 *
 * Le livre (p. 173) donne 700 dégâts sur « un groupe d'adversaires » sans spécifier
 * explicitement la répartition. La distribution par adversaire est donc une
 * convention affichée dans l'interface, et non une précision prétendument officielle.
 */
internal data class GroupSplashResult(val remainingHp: List<Int>, val damageDealt: Int, val hitCount: Int)

internal fun groupSplashDamage(hp: List<Int>, damagePerTarget: Int): GroupSplashResult {
    val hitPoints = hp.map { it.coerceAtLeast(0) }
    val hitCount = hitPoints.count { it > 0 }
    val damage = damagePerTarget.coerceAtLeast(0)
    val after = hitPoints.map { (it - damage).coerceAtLeast(0) }
    val actual = hitPoints.zip(after).sumOf { (before, remaining) -> (before - remaining).toLong() }
    return GroupSplashResult(after, actual.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), hitCount)
}
