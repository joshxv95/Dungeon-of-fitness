package com.dungeonoffitness.app

import kotlin.math.ceil

/** 1 pas = 1 mètre, convention de jeu de la v2.0. */
internal const val METERS_PER_STEP = 1.0

/** Le plafond évite de créditer des pas au-delà d'une rencontre ou d'une étape. */
internal fun stepsUntilBoundary(remainingKm: Double): Int {
    if (!remainingKm.isFinite() || remainingKm <= 0.00000001) return 0
    return ceil(remainingKm * 1000.0 / METERS_PER_STEP - 0.000000001)
        .coerceIn(1.0, Int.MAX_VALUE.toDouble()).toInt()
}

internal fun stepSensorDelta(previous: Float?, current: Float): Int {
    if (previous == null || !previous.isFinite() || !current.isFinite() || current < previous) return 0
    return (current.toDouble() - previous.toDouble()).coerceIn(0.0, Int.MAX_VALUE.toDouble()).toInt()
}

internal fun accumulateSessionSteps(current: Int, incoming: Int, cap: Int): Int =
    (current.toLong() + incoming.coerceAtLeast(0).toLong()).coerceIn(0, cap.coerceAtLeast(0).toLong()).toInt()

internal fun stepsToJourneyKm(steps: Int): Double = steps.coerceAtLeast(0).toDouble() * METERS_PER_STEP / 1000.0
