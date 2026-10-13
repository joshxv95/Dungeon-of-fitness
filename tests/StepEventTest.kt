package com.dungeonoffitness.app

private fun expect(name: String, actual: Any, expected: Any) {
    if (actual != expected) error("$name : attendu=$expected, obtenu=$actual")
}

fun main() {
    expect("rencontre avant arrivée", stepStopKind(0.8, 0.8, false), "encounter")
    expect("rencontre prioritaire à la destination", stepStopKind(6.0, 6.0, false), "encounter")
    expect("rencontre prioritaire à l'étape", stepStopKind(3.0, 3.0, true), "encounter")
    expect("rencontre après prochaine étape", stepStopKind(0.8, 2.0, true), "waypoint")
    expect("destination avant rencontre", stepStopKind(0.5, 3.0, false), "arrival")
    expect("retour sans rencontre", stepStopKind(0.5, Double.POSITIVE_INFINITY, false), "arrival")
    expect("palier 3 km", stepStopKind(3.0, 3.0, false), "encounter")
    expect("distance rencontre plus loin", stepStopKind(0.5, 0.5001, false), "arrival")
    expect("titre combat", stepEventMessage("encounter", "Fontaria").first, "⚔️ Rencontre !")
    expect("titre étape", stepEventMessage("waypoint", "Mont Stepper").first, "🧭 Étape atteinte !")
    expect("titre arrivée", stepEventMessage("arrival", "Nosferathon").first, "🏁 Destination atteinte !")
    check(stepEventMessage("waypoint", "Mont Stepper").second.contains("Mont Stepper"))
    check(stepEventMessage("arrival", "Nosferathon").second.contains("Nosferathon"))
    check(stepEventMessage("encounter", "").second.contains("rencontre"))
    println("14 assertions sur les alertes d'aventure : OK")
}
