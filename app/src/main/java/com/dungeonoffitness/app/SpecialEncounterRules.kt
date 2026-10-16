package com.dungeonoffitness.app

/** Règles p. 43 : transfert immédiat d'un objet, sans achat/vente.
 * Les objets d'inventaire sont identifiés par leur nom interne, avec doublons possibles.
 */
internal object SpecialEncounterRules {
    val fairyItems: Set<String> = linkedSetOf("Arc féérique", "Sabre féérique", "Sceptre féérique")

    /** Retourne null si un choix est impossible, sans toucher à la liste originale. */
    fun exchange(inventory: List<String>, offered: String, reward: String, allowedRewards: Set<String>): List<String>? {
        if (offered.isBlank() || offered !in inventory || reward !in allowedRewards || offered == reward) return null
        // Le moteur n'équipe qu'un exemplaire d'une arme ou d'un bijou unique.
        if (reward in inventory) return null
        val index = inventory.indexOf(offered)
        return inventory.filterIndexed { i, _ -> i != index } + reward
    }

    fun surrender(inventory: List<String>, offered: String): List<String>? {
        if (offered.isBlank() || offered !in inventory) return null
        val index = inventory.indexOf(offered)
        return inventory.filterIndexed { i, _ -> i != index }
    }
}
