import com.dungeonoffitness.app.HeroicRules

fun main() {
    val h = HeroicRules
    // Sans confirmation ni objet, jamais de bonus gratuit.
    check(h.multiplier(false, true, 1, false, true) == 1)
    check(h.multiplier(true, false, 1, true, false) == 1)
    // Deux alliés face à un ennemi ; ne fonctionne pas face à plusieurs ennemis.
    check(h.multiplier(true, true, 1, false, false) == 2)
    check(h.multiplier(true, true, 2, false, false) == 1)
    check(h.multiplier(true, true, 0, false, false) == 1)
    // Réplique : seulement après confirmation d'un AUTRE joueur.
    check(h.multiplier(false, false, 3, true, true) == 2)
    check(h.multiplier(false, false, 1, true, false) == 1)
    // Cumul de deux objets satisfaits, explicitement déclaré comme convention.
    check(h.multiplier(true, true, 1, true, true) == 4)
    check(h.damage(100, true, true, 1, true, true) == 400)
    check(h.damage(100, true, true, 2, true, true) == 200)
    check(h.damage(Int.MAX_VALUE, true, true, 1, true, true) == Int.MAX_VALUE)
    check(h.damage(-20, true, true, 1, true, true) == 0)
    println("OK — 12 contrôles améliorations de groupe")
}
