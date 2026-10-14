package com.dungeonoffitness.app

private fun eq(label: String, actual: Int, expected: Int) {
    if (actual != expected) error("$label : attendu $expected, obtenu $actual")
}
fun main() {
    fun calc(base: Int, count: Int, upgrade: Int, support: Int, timed: Boolean,
        cardio: Boolean = false, force: Boolean = false, congestion: Boolean = false, vigor: Boolean = false): Int =
        ScrollCombatRules.weaponDamage(base, count, upgrade, support, timed, cardio, force, congestion, vigor)

    eq("Temps, standard", calc(100,3,50,25,true),375)
    eq("Cardio double base minutée",calc(100,3,50,25,true,cardio=true),675)
    eq("Cardio n'affecte pas exécutions",calc(100,3,50,25,false,cardio=true),375)
    eq("Force double base exécutions",calc(100,3,50,25,false,force=true),675)
    eq("Force n'affecte pas durée",calc(100,3,50,25,true,force=true),375)
    eq("Congestion double uniquement améliorations",calc(100,3,50,25,true,congestion=true),425)
    eq("Congestion sans bonus",calc(100,3,0,25,false,congestion=true),325)
    eq("Cardio+congestion",calc(100,3,50,25,true,cardio=true,congestion=true),725)
    eq("Force+vigueur",calc(100,3,50,25,false,force=true,vigor=true),1350)
    eq("Cardio+congestion+vigueur",calc(100,3,50,25,true,cardio=true,congestion=true,vigor=true),1450)
    eq("Sans séries",calc(100,0,50,25,true,cardio=true,congestion=true),0)
    eq("Écrêtage entier",calc(Int.MAX_VALUE,Int.MAX_VALUE,999,999,false,force=true,vigor=true),Int.MAX_VALUE)
    eq("Bonus pattes une seule fois",calc(10,10,50,0,true,congestion=true),200)
    eq("Soutien non doublé",calc(10,1,0,100,true,congestion=true),110)
    eq("Parchemins croisés",calc(10,1,0,100,true,cardio=true,force=true),120)
    eq("Manuel absent", ScrollCombatRules.agilityBonus(emptyList()), 0)
    eq("Manuel acquis", ScrollCombatRules.agilityBonus(listOf("Manuel d’agilité")), 2)
    eq("Manuel unique", ScrollCombatRules.agilityBonus(listOf("Manuel d’agilité", "Manuel d’agilité")), 2)
    eq("Flexibilité sans autre bonus", ScrollCombatRules.flexibilityContext(1, 0), 6)
    eq("Flexibilité plafonnée", ScrollCombatRules.flexibilityContext(6, 3), 6)
    eq("Flexibilité avec bonus négatif théorique", ScrollCombatRules.flexibilityContext(1, -10), 1)
    println("21 contrôles des parchemins, du manuel et des dégâts : OK")
}
