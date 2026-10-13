# Dungeon of Fitness — Android

Application de fitness-RPG **hors ligne**, en Kotlin / Jetpack Compose, adaptée pour un usage personnel du livre *La Communauté des Abdos*. L'application ne remplace pas les consignes de sécurité des exercices et les règles maison sont identifiées comme telles.

## Version et historique

- Version actuelle : **1.26.0** (`versionCode` **129**).
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
- Carnet de campagne avec les 36 chapitres du livre, accès restreints, pages, validations manuelles et suivi des lieux visités.
- Carnet de huit compagnons : Oracle de Tasmina, Barde, Paladin/Quêteur, Fée, Troll Conteur, Bulker Fruité et personnages narratifs. Les confirmations de Prières, de musique et les utilisations spéciales sont enregistrées.
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

### Nouveautés 1.23.0 — Étapes guidées et rencontres

- Lorsqu'un trajet est proposé depuis la carte, l'option **Suivre chaque étape** décompose la distance en tronçons. Le trajet attend une confirmation à chaque ville intermédiaire, sans valider le chapitre narratif de cette zone.
- Les Éclaireurs et Bardes à demi-distance voient la réduction répartie sur **chaque tronçon**, sans perte ni double-comptage des kilomètres.
- Chaque saisie de progression est plafonnée au prochain arrêt **ou** au prochain multiple cumulé de 6 km pour empêcher de sauter une rencontre, même avec une avancée importante.
- Le journal et l'accueil affichent la prochaine étape, les passages sont enregistrés ; en cas de fuite, le retour part du **dernier point d'étape confirmé**.
- Pour les trajets débutés avant la v1.23, le suivi reste manuel afin de ne pas inventer d'étapes au milieu d'une sauvegarde.
- Pour modifier la distance proposée, la saisie manuelle désactive le suivi automatique.

### Nouveautés 1.24.0 — Carnet de campagne

- Dans **Aventure → 📖 Chapitres, accès et progression**, consultez les **36 chapitres principaux** (pages imprimées 68 à 138, sommaire p. 218-219) et sélectionnez un filtre.
- **Valider** un chapitre seulement après avoir effectué ses épreuves dans le livre. Un déplacement, un passage dans une ville ou une victoire ne suffit pas.
- Les chapitres liés à un lieu ne sont validables qu'en étant sur place, hors trajet et combat. Les scènes finales ou purement narratives demandent une confirmation explicite et une vérification de leurs conditions dans le livre.
- Pour les chapitres **à accès restreint**, utilisez « J'ai obtenu cet accès dans le livre » après obtention effective. Les accès gagnés dans trois chapitres vérifiés sont appliqués automatiquement après validation manuelle de ceux-ci : p.110, 112 et 116.
- Les compagnons, les récompenses et les autres effets narratifs ne sont **pas** accordés automatiquement. Le PDF/livre est nécessaire pour jouer ces scènes ; ce carnet sert à en suivre l'avancement.
- Les sauvegardes plus anciennes sont reprises sans inventer des chapitres ou lieux précédemment accomplis.

### Nouveautés 1.26.0 — Compagnons

- Ajouter/recruter les compagnons depuis **Aventure → Compagnons** après vérification du chapitre dans le livre.
- Préparer un trajet dans Aventure : cocher si l'on a effectué la **Prière de l'Oracle** (×2 dégâts du voyage), celle du **Paladin/Quêteur** (−2 sur un dé de quantité à choisir), ou si l'on écoutera de la **musique avec le Barde compagnon** (÷2 sur la distance).
- Pendant la rencontre, l'Oracle peut également être activé après une Prière, **avant la première attaque**, pour le combat seulement.
- Le Paladin/Quêteur peut modifier un dé de quantité une seule fois pendant le voyage. Convention : résultat du dé au minimum 1. Les quantités fixes ne sont pas concernées.
- Les dégâts entrés manuellement sont considérés **bruts** et les bonus actifs Guerrier/Oracle y sont appliqués automatiquement ; les dégâts fixes des Shakers restent inchangés.
- Les états sont sauvegardés, les pouvoirs du voyage expirent à l'arrivée ou à la fuite. Un raccourci de Barde compagnon ne se cumule pas avec une capacité de classe.
- Exécuter les tests purs sans Android : `kotlinc app/src/main/java/com/dungeonoffitness/app/CompanionRules.kt tests/CompanionRulesTest.kt -include-runtime -d tests.jar && java -jar tests.jar`.
