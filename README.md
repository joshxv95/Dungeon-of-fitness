# Dungeon of Fitness — Android

Application de fitness-RPG **hors ligne**, en Kotlin / Jetpack Compose, adaptée pour un usage personnel du livre *La Communauté des Abdos*. L'application ne remplace pas les consignes de sécurité des exercices et les règles maison sont identifiées comme telles.

## Version et historique

- Version actuelle : **1.20.2** (`versionCode` **123**).
- Source de vérité : `app/build.gradle.kts`, champs `versionName` et `versionCode`.
- L'écran **Application** affiche automatiquement `BuildConfig.VERSION_NAME`, sans numéro codé en dur.
- Historique des changements : [CHANGELOG.md](CHANGELOG.md). Aucun fichier `VERSION-X.X.txt` n'est nécessaire.

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
