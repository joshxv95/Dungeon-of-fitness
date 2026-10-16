package com.dungeonoffitness.app

fun main() {
    var tested = 0
    fun verify(ok: Boolean) { check(ok); tested++ }
    val starting = listOf("Gants", "Shaker poison", "Shaker poison", "Armure de force")
    val elfit = SpecialEncounterRules.exchange(starting, "Gants", "Bijou de mage", setOf("Bijou de mage"))
    verify(elfit == listOf("Shaker poison", "Shaker poison", "Armure de force", "Bijou de mage"))
    verify(SpecialEncounterRules.exchange(starting, "Gants", "Shaker poison", setOf("Bijou de mage")) == null)
    verify(SpecialEncounterRules.exchange(starting, "Inexistant", "Bijou de mage", setOf("Bijou de mage")) == null)
    verify(SpecialEncounterRules.exchange(emptyList(), "Gants", "Bijou de mage", setOf("Bijou de mage")) == null)
    verify(SpecialEncounterRules.exchange(starting + "Bijou de mage", "Gants", "Bijou de mage", setOf("Bijou de mage")) == null)
    verify(SpecialEncounterRules.exchange(starting, "Gants", "Sceptre féérique", SpecialEncounterRules.fairyItems) == listOf("Shaker poison", "Shaker poison", "Armure de force", "Sceptre féérique"))
    verify(SpecialEncounterRules.exchange(starting, "Gants", "Arc khôl", SpecialEncounterRules.fairyItems) == null)
    verify(SpecialEncounterRules.exchange(starting + "Arc féérique", "Gants", "Arc féérique", SpecialEncounterRules.fairyItems) == null)
    verify(SpecialEncounterRules.exchange(starting + "Arc féérique", "Arc féérique", "Arc féérique", SpecialEncounterRules.fairyItems) == null)
    verify(SpecialEncounterRules.fairyItems == setOf("Arc féérique", "Sabre féérique", "Sceptre féérique"))
    verify(SpecialEncounterRules.surrender(starting, "Shaker poison") == listOf("Gants", "Shaker poison", "Armure de force"))
    verify(SpecialEncounterRules.surrender(starting, "Inexistant") == null)
    verify(SpecialEncounterRules.surrender(emptyList(), "Gants") == null)
    verify(starting == listOf("Gants", "Shaker poison", "Shaker poison", "Armure de force"))
    println("SpecialEncounterRules : $tested assertions OK")
}
