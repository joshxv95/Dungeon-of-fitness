package com.dungeonoffitness.app

private fun assertCheck(test: String, actual: Any, expected: Any) {
    if (actual != expected) error("$test : attendu=$expected, obtenu=$actual")
}

fun main() {
    // Conversion conventionnelle pas -> mètres -> kilomètres.
    assertCheck("1000 pas = 1 km", stepsToJourneyKm(1000), 1.0)
    assertCheck("250 pas", stepsToJourneyKm(250), 0.25)
    assertCheck("valeur négative", stepsToJourneyKm(-42), 0.0)
    // Arrêt au prochain obstacle avec arrondi vers le haut.
    assertCheck("palier 6 km", stepsUntilBoundary(6.0), 6000)
    assertCheck("palier 3 km avec Troll", stepsUntilBoundary(3.0), 3000)
    assertCheck("étape à 750 m", stepsUntilBoundary(0.75), 750)
    assertCheck("distance fractionnaire", stepsUntilBoundary(0.0004), 1)
    assertCheck("étape nulle", stepsUntilBoundary(0.0), 0)
    assertCheck("distance invalide", stepsUntilBoundary(Double.NaN), 0)
    assertCheck("trajet raccourci 17.5 km", stepsUntilBoundary(17.5), 17500)
    // STEP_COUNTER donne des valeurs cumulées depuis le dernier redémarrage de l'OS.
    assertCheck("première valeur = base", stepSensorDelta(null, 2344f), 0)
    assertCheck("nouveau lot de pas", stepSensorDelta(2344f, 2356f), 12)
    assertCheck("aucun nouveau pas", stepSensorDelta(2356f, 2356f), 0)
    assertCheck("reboot compteur", stepSensorDelta(2356f, 8f), 0)
    assertCheck("valeur non numérique", stepSensorDelta(1f, Float.NaN), 0)
    // Détection en lots et plafond à 6km/à la prochaine étape.
    assertCheck("accumulation", accumulateSessionSteps(400, 10, 6000), 410)
    assertCheck("un lot traverse seuil, pas de dépassement", accumulateSessionSteps(5996, 20, 6000), 6000)
    assertCheck("pas négatifs ignorés", accumulateSessionSteps(14, -10, 200), 14)
    assertCheck("déjà terminé reste terminé", accumulateSessionSteps(750, 10, 750), 750)
    assertCheck("un seul pas à la fin", accumulateSessionSteps(749, 1, 750), 750)
    // Simulation : 10 000 pas ne peuvent pas traverser automatiquement 2 rencontres.
    val trip = accumulateSessionSteps(0, 10000, stepsUntilBoundary(6.0))
    assertCheck("stop au premier combat", stepsToJourneyKm(trip), 6.0)
    // Pause -> nouvelle base brute : les pas pendant la pause ne sont pas crédités.
    val stepsBeforePause = 100
    val stepsAfterResume = accumulateSessionSteps(stepsBeforePause, stepSensorDelta(10140f, 10150f), 6000)
    assertCheck("pas avant + après pause", stepsAfterResume, 110)
    println("22 tests de logique podomètre : OK")
}
