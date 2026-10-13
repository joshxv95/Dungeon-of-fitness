# Dungeon of Fitness — Android

Application de fitness-RPG **hors ligne**, en Kotlin / Jetpack Compose, adaptée pour un usage personnel du livre *La Communauté des Abdos*. L'application ne remplace pas les consignes de sécurité des exercices et les règles maison sont identifiées comme telles.

## Version et historique

- Version actuelle : **1.22.0** (`versionCode` **125**).
- Source de vérité : `app/build.gradle.kts`, champs `versionName` et `versionCode`.
- L'écran **Application** affiche automatiquement `BuildConfig.VERSION_NAME`, sans numéro codé en dur.
- Historique des changements : [CHANGELOG.md](CHANGELOG.md). Carte et 28 tronçons : [CARTE-TRONCONS.md](CARTE-TRONCONS.md). Aucun fichier `VERSION-X.X.txt` n'est nécessaire.

## Compilation de l'APK avec GitHub Actions

1. Décompresser cette archive.
2. Copier le contenu **intérieur** du dossier `dungeon-fitness-app/` à la racine d'un dépôt GitHub, en remplaçant les fichiers correspondants. **Supprimer dans GitHub les anciens `VERSION-X.X.txt`**, car un ZIP ne supprime pas les fichiers déjà présents dans un dépôt.
3. Valider les modifications sur la branche `main`.
4. Dans GitHub : **Actions → Build APK → Run workflow** (ou attendre le déclenchement automatique du push).
5. Télécharger l'artifact `dungeon-of-fitness-debug-apk`, puis installer `app-debug.apk` sur le téléphone.

La configuration de compilation est dans `.github/workflows/build-apk.yml`. Un APK de production signé pour le Play Store n'est **pas** fourni.

## Conserver la sauvegarde lors d'une mise à jour

- Le `applicationId` demeure `com.dungeonoffitness.app`.
- La clé de signature **debug de test** `app/debug-signing/dof-test-debug.jks` reste inchangée depuis la v1.10, et le `versionCode` augmente à chaque version.
- Installer par-dessus une version signée avec **cette même clé** pour conserver les données locales.
- Ne pas supprimer l'application avant la mise à jour : une désinstallation effacerait la sauvegarde locale.
- Les clés de test sont incluses dans les sources pour le développement privé ; elles ne conviennent pas à une distribution de production.

## Fonctionnalités majeures

- Huit classes avancées et capacités de déplacement/rencontre progressivement automatisées.
- Parcours réel en kilomètres (ou équivalent en minutes en mode novice), rencontres selon les tables du livre, contextes et exercices.
- Combat par mouvements/exercices associés aux armes, suivi des PV par adversaire et jauges visuelles.
- Inventaire par catégories, équipements, améliorations et consommables.
- Journaux, progression et sauvegarde locale, ainsi que boutons de test des PO et des capacités quotidiennes.
- Huit portraits illustrés distincts dans `app/src/main/res/drawable/portrait_*.png` (450 × 600), embarqués dans l'APK, sans Internet.

## Points non achevés ou conventions

- Le Barde solo utilise une **règle maison** : jusqu'à deux trajets à demi-distance par jour, mode prudent ou mode audacieux (adversaires ×2 et récompense ×2,5 par rapport à la quantité initiale) pour les rencontres qui n'ont pas de quantité fixe.
- Le Mage et le Moine modifient pour l'instant **le dé de contexte**, pas encore ceux de type/quantité.
- Les combats spéciaux et effets immédiats de certains ennemis ne sont pas tous automatisés.
- La carte et les trajets restent majoritairement renseignés manuellement ; les déplacements du Cavalier nécessitent une représentation exacte de l'adjacence des zones.
- Les caractéristiques ne reçoivent pas de nouveau système maison dans cette application : une éventuelle mécanique RPG plus avancée sera abordée dans un **autre projet** utilisant le moteur fitness.

Consulter le livre pour les règles officielles. Les écarts doivent être indiqués comme conventions d'adaptation ou règles maison.


### Nouveautés 1.21.0

- **Mage et Moine :** sur une nouvelle rencontre, sélectionnez le type (un des sept D6), la quantité (un D6 quand la quantité n’est pas fixe) ou le contexte (un D6), puis la modification autorisée. Le Moine confirme une Prière avant validation. Une modification par rencontre : convention d’interface, pas une limite quotidienne officielle.
- **Cavalier en v1.21 :** en l’absence de trajet actif ou de rencontre, sélectionnez une destination **adjacente** et cochez la confirmation après vérification sur la carte papier p. 51. La téléportation est utilisable une fois par jour et peut être réinitialisée dans le mode test.
- **Jauges de caractéristiques :** progression sur 10 de chaque score calculé depuis les tests du livre, sans ajout d'effet mécanique.
- **Anciennes rencontres :** leur sauvegarde fonctionne, mais les dés de type et de quantité ne peuvent pas être reconstitués ; seul le contexte reste modifiable pour ces rencontres.

### Nouveautés 1.22.0

- **Carte locale du livre p. 51 :** bouton dans l’onglet Aventure, zoom et déplacement tactiles.
- **Trajet :** choisir une destination pour voir un itinéraire depuis le lieu actuel ; le trajet calculé propose une distance que l’on peut conserver ou corriger. Les zones à accès restreint ne sont pas automatiquement débloquées.
- **Cavalier :** son sélecteur présente exclusivement les zones immédiatement voisines, selon les tronçons visibles sur cette carte. Une destination éloignée ne peut pas être téléportée, même avec un appel direct au code.
- **Île des poids guidés :** aucun tronçon pointillé sur la carte p. 51 ne la relie ; cette zone nécessite une saisie manuelle selon le scénario.
