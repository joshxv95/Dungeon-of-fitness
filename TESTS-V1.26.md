# Tests fonctionnels — v1.26.0

## Mise à jour
1. Installer le nouvel APK par-dessus la v1.25 sans désinstaller ; vérifier version 1.26.0, personnage, sauvegarde, compagnon et inventaire.
2. Dans Aventure → Compagnons, confirmer le recrutement manuel de l'Oracle, du Barde compagnon, du Paladin et du Quêteur si les chapitres ne sont pas validés ; vérifier l'affichage des trois pouvoirs.

## Oracle p. 73
3. Démarrer un trajet **sans** la case de Prière Oracle ; déclencher une rencontre. Avant toute attaque, confirmer la Prière de l'Oracle. Une attaque de base 100 doit afficher 200 dégâts. Après victoire, activer à nouveau sur un nouveau combat : le pouvoir de la rencontre précédente ne doit pas persister.
4. Démarrer un autre trajet après Prière Oracle au départ ; les attaques sont doublées sur les rencontres de tout le voyage. La case de Prière en combat n'apparaît pas car le pouvoir est déjà actif. Après l'arrivée, vérifier qu'il a disparu.
5. En présence d'une capacité Guerrier ×2 sur la même rencontre, vérifier 100 → 400 ; ceci reflète le cumul de deux multiplicateurs distincts, non un doublement de l'Oracle lui-même.
6. Essayer d'activer l'Oracle après des dégâts déjà infligés : cela doit être indisponible.

## Paladin/Quêteur p. 129
7. Confirmer la Prière avant de partir ; attendre une rencontre avec dé de quantité normal (ex. résultat 6) ; cliquer Paladin : résultat 4, quantité, PV et PO ajustés. Le bouton est ensuite bloqué sur toutes les autres rencontres du voyage.
8. Vérifier une rencontre fixe : aucune application. Sur un dé 1 ou 2, convention minimum 1 affichée et appliquée.
9. Sans avoir coché la Prière au départ : aucune réduction de quantité proposée. Nouvelle Prière à un nouveau départ = pouvoir de nouveau disponible.

## Barde compagnon p. 113
10. Avec une autre classe (Guerrier ou Mage), choisir Barde compagnon + musique pour 12 km : 6 km ; itinéraire guidé conservé. Le pouvoir ne consomme pas d'utilisation quotidienne de la classe Barde.
11. Avec une classe Éclaireur ou Barde qui utilise son propre raccourci, vérifier qu'il n'y a **pas** de quart de distance. Mode Barde audacieux inchangé.

## Persistance et fuites
12. Quitter et rouvrir l'appli au milieu d'un trajet avec Oracle et Paladin actifs ; reprendre la sauvegarde et vérifier les effets.
13. Fuir un combat : le bonus de combat Oracle et les effets du voyage abandonné disparaissent ; retour sans nouvelle rencontre.
14. Arriver à destination et vérifier la désactivation des effets temporaires. Les compagnons eux-mêmes restent recrutés.

## Calculs purs
Lancer : `kotlinc app/src/main/java/com/dungeonoffitness/app/CompanionRules.kt tests/CompanionRulesTest.kt -include-runtime -d /tmp/dof-tests.jar && java -jar /tmp/dof-tests.jar`.

**Rappels :** la Prière et l'écoute de musique sont confirmées manuellement ; l'application ne vérifie pas les exercices. La règle Barde solo reste une règle maison. L'ordre des pouvoirs et les dégâts des objets fixes restent soumis aux indications du livre.
