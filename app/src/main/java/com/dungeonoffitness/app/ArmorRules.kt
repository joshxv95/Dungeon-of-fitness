package com.dungeonoffitness.app

/** Règles p. 164 : un jet de contexte de 1D6, modifié comme les autres contextes.
 *  Un deuxième jet d'Armure respirante ne doit pas être fusionné avec l'ancien.
 *  Le joueur choisit explicitement quel dé brut conserver ; le bonus déjà acquis reste identique.
 */
internal object ArmorRules {
    fun effectiveContext(die: Int, bonus: Int): Int =
        (die.coerceIn(1, 6).toLong() + bonus.toLong()).coerceIn(1L, 6L).toInt()

    /** Le livre donne un bonus pour chaque exécution. Les séries à durée comptent
     *  comme unités enregistrées dans l'interface existante. */
    fun bonusForUnits(armorBonus: Int, units: Int, armorConfirmed: Boolean): Int =
        if (!armorConfirmed || units <= 0) 0 else
            (armorBonus.coerceAtLeast(0).toLong() * units.toLong()).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}
