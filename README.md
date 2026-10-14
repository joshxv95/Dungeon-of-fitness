# Dungeon of Fitness — Android

Application de fitness-RPG **hors ligne**, en Kotlin / Jetpack Compose, adaptée pour un usage personnel du livre *La Communauté des Abdos*. L'application ne remplace pas les consignes de sécurité des exercices et les règles maison sont identifiées comme telles.

## Version et historique

- Version actuelle : **2.4.0** (`versionCode` **204**).
- Source de vérité : `app/build.gradle.kts`, champs `versionName` et `versionCode`.
- L'écran **Application** affiche automatiquement `BuildConfig.VERSION_NAME`, sans numéro codé en dur.
- Historique des changements : [CHANGELOG.md](CHANGELOG.md). Carte et 28 tronçons : [CARTE-TRONCONS.md](CARTE-TRONCONS.md). Aucun fichier `VERSION-X.X.txt` n'est nécessaire.

## Nouveautés 2.4.0 — armures et armurerie (p. 164–168)

- **Trois armures** ajoutées dans **Sac → Armures**, avec emplacement indépendant de l'arme et du soutien (convention d'interface). Armure de force 300 PO : contexte +2 et +100 PV par série/exécution lorsque le lest de 10–20 kg est réellement porté et confirmé. Armure de leste 200 PO : contexte +1 et +50 PV dans les mêmes conditions avec 5–10 kg. Les bonus de contexte ne prennent effet que si l'armure était équipée AVANT le tirage.
- **Armure respirante** 100 PO : en portant un sac à dos vide, un second dé de contexte peut être tiré **avant le combat** ; les deux résultats (bonus compris) sont affichés et le joueur conserve celui qu'il souhaite (livre p. 164). Une relance par rencontre, non disponible si l'armure n'était pas équipée à la génération. Le choix et les dés sont sauvegardés, sans retirer l'armure.
- **Excalibrute** (500 PO, 1 000 PV/30 secondes, contexte +1) et **Fléau d'inertie** (200 PO, 100 PV/exécution, contexte +1) ajoutés au catalogue, avec leurs exercices officiels (p. 168).
- **Epée de Tasmina** (500 PV/30 secondes, contexte +2) : objet **non achetable**, attribué une seule fois via le carnet de campagne après confirmation de l'**Exploration du Temple de Tasmina** (p. 126–127).
- La signature debug, les sauvegardes locales, les pas, les rencontres et les notifications restent inchangés. Migration additive : schéma 28. Aucun fichier `TESTS-Vx.x.md` dans la livraison, seulement les tests Kotlin internes pour la régression.
- **Podomètre validé en conditions réelles par l'utilisateur** sur la v2.3, y compris arrêts sur rencontre, étape et destination et notifications correspondantes. Ne pas modifier cette logique sans nécessité.

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

## Nouveauté v2.0 : podomètre hors ligne (Android)

- **Mode facultatif** : démarrer un trajet dans l'onglet Aventure, puis utiliser le panneau **👣 Podomètre** ou les boutons manuels existants (vélo, tapis…). Les deux modes ne comptent pas simultanément afin d'éviter des doublons.
- **Convention propre au jeu : 1 pas détecté = 1 mètre crédité**, sans GPS ; ce n'est pas une mesure scientifique de la distance réelle. Exemple : 150 pas = 0,150 km.
- Le podomètre est réservé pour l'instant au **profil avancé**, car en mode novice les kilomètres de la carte sont convertis en **minutes**, ce qui n'est pas équivalent à des pas.
- Le téléphone doit disposer du capteur Android **TYPE_STEP_COUNTER**, ou à défaut de **TYPE_STEP_DETECTOR**, et recevoir l'autorisation **Activité physique** sur Android 10+. Sur Android 13+, il demande aussi l'autorisation facultative de notifications pour faciliter le suivi.
- La première mesure du capteur cumulatif sert de référence ; **attendre l'état « En marche »** avant de commencer. Certains capteurs émettent des résultats par lots. Avec un capteur absent, non autorisé ou inaccessible, les boutons manuels restent fonctionnels.
- Une session utilise un **service Android au premier plan de type `health`**, avec une notification et des actions **Pause / Reprendre / Arrêter**. Elle peut continuer écran verrouillé, sous réserve des restrictions d'énergie du fabricant. Le service est lancé uniquement depuis une action utilisateur ; il n'est pas automatiquement redémarré après un reboot.
- **Arrêt automatique au prochain seuil de rencontre** (6 km cumulés, ou 3 km avec Troll Conteur), **à l'étape guidée suivante ou à destination**. Les pas ultérieurs ne sont pas pris en compte tant que la rencontre/l'étape n'est pas résolue puis une nouvelle session lancée. Les lots de capteur sont plafonnés au bon seuil.
- Sauvegarde en deux parties : `dof_steps_v2` pour les mesures de capteur ; `dof_save` pour l'aventure et les pas déjà appliqués. L'identifiant de session et le curseur de pas évitent de créditer les mêmes pas après rotation/relance. Réinitialiser le personnage efface les deux.
- Après une interruption du service par le système, le comptage peut manquer des pas ; **il ne faut pas considérer les données comme un podomètre médical**. L'app reste utilisable en saisie manuelle.
- Tests algorithmiques reproductibles : `kotlinc app/src/main/java/com/dungeonoffitness/app/StepLogic.kt tests/StepLogicTest.kt -include-runtime -d step-tests.jar && java -jar step-tests.jar`.
- L’utilisateur a validé le comptage et l’arrêt automatique sur rencontre, étape et arrivée avec notifications, en conditions réelles (v2.3).

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
- Le Mage et le Moine peuvent modifier le type (un des sept dés), la quantité ou le contexte, avec une modification par rencontre comme convention d'interface.
- Les combats spéciaux et effets immédiats de certains ennemis ne sont pas tous automatisés.
- Les trajets guidés reposent sur 28 tronçons relevés de la carte p. 51 ; les liaisons non indiquées restent à saisir manuellement. Le Cavalier utilise les seules adjacences enregistrées.
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

### Nouveautés 2.1.0 — Notifications d'aventure

Pendant une session de podomètre, la notification permanente de marche reste discrète. Lorsque les pas atteignent **le prochain seuil de rencontre**, **une ville intermédiaire** ou **la destination**, le service arrête le comptage et publie une **nouvelle notification prioritaire** dans le canal Android « Événements d’aventure ». Cette notification décrit l'événement ; en la touchant, l'onglet **Aventure** s'ouvre pour résoudre la rencontre ou confirmer le passage/l'arrivée.

La notification exige l'autorisation Android « Notifications » et peut être silencieuse/bloquée par les réglages du téléphone, le mode Ne pas déranger ou les restrictions système. Le fonctionnement du podomètre et le blocage au seuil ne dépendent **pas** de cette autorisation. Les pas supplémentaires après l'arrêt ne sont pas crédités.

### Nouveautés 2.2.0 — Shaker sombre et laboratoire

- Le **Shaker sombre**, gagné au Temple de la prophétie et non vendu en boutique, est utilisable pendant un combat contre un groupe d'adversaires. L'effet de 700 dégâts à distance sur un groupe vient du livre p. 173. **Convention d'implémentation** : 700 dégâts indépendants sur chaque ennemi encore vivant. Une confirmation détaille les cibles et les dégâts avant consommation unique. Les PV/PO sont mis à jour et les bonus d'attaque ne multiplient pas les dégâts fixes du shaker.
- Dans **Application → Mode test**, essayer les trois modèles de notification séparément et tirer une **rencontre fictive sans progresser de distance** ; aperçu de la quantité, des dés, des PV, du butin et des dégâts simulés du Shaker sombre. Aucune donnée de jeu ne change. Ce banc d'essai ne prouve pas l'arrêt physique du podomètre au seuil : le vérifier après une vraie marche.
- Exécuter les tests sans SDK Android : `kotlinc app/src/main/java/com/dungeonoffitness/app/CombatConsumableRules.kt tests/CombatConsumableRulesTest.kt -include-runtime -d combat-tests.jar && java -jar combat-tests.jar`.
