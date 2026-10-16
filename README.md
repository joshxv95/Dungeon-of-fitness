# Dungeon of Fitness — Android

Application de fitness-RPG **hors ligne**, en Kotlin / Jetpack Compose, adaptée pour un usage personnel du livre *La Communauté des Abdos*. L'application ne remplace pas les consignes de sécurité des exercices et les règles maison sont identifiées comme telles.

## Version et historique

- Version actuelle : **2.7.0** (`versionCode` **208**).
- Source de vérité : `app/build.gradle.kts`, champs `versionName` et `versionCode`.
- L'écran **Application** affiche automatiquement `BuildConfig.VERSION_NAME`, sans numéro codé en dur.
- Historique des changements : [CHANGELOG.md](CHANGELOG.md). Carte et 28 tronçons : [CARTE-TRONCONS.md](CARTE-TRONCONS.md). Aucun fichier `VERSION-X.X.txt` n'est nécessaire.

## Nouveautés 2.7.0 — échanges et confiscations (p. 43, 172, 178, 181)
- **Elfit** : échange facultatif d'un objet possédé contre un **Bijou de mage niv.1** ; le bijou reçu ne coûte pas de PO. La base de niveau 1 est initialisée avec une valeur fictive de 200 PO uniquement pour le calcul du bonus. Un seul bijou évolutif est géré : échange désactivé si tu en possèdes déjà un.
- **Amazone** : échange facultatif d'un objet du sac contre **Arc féérique**, **Sabre féérique** ou **Sceptre féérique**, trois armes du catalogue officiel p. 172. Pas de PO. Il n'est pas possible de recevoir un doublon d'une arme déjà possédée ; une seule copie de l'objet offert est retirée.
- **Bibilithe** : avant le premier coup, choix entre **céder un objet ET l'intégralité des PO**, ou combattre normalement. En cas de reddition, pas de butin ; si le sac est vide, on ne peut pas céder d'objet.
- Lorsqu'un objet cédé était équipé, le slot concerné est vidé. Si c'était le Bijou de mage, son niveau/bonus est supprimé. Confirmation irréversible avant tout échange ou paiement. Les rencontres pacifiques n'affichent plus un « butin potentiel » fictif.
- Aucun changement au tirage des 7d6, au podomètre, aux compagnons, à la magie ni au stockage des rencontres. Schéma 31 (lecture rétrocompatible du schéma 30). Même paquet Android et même clé de signature. L'Orienteur obsédé et le Clan Nazcool ne sont **pas** encore automatisés : leurs effets restent à gérer manuellement avec le livre.
- Tests de règles sous `tests/SpecialEncounterRulesTest.kt`. Aucun fichier `TESTS-Vx.x.md`.

## Nouveautés 2.6.0 — améliorations et rencontres spéciales (p. 43 et 171)
- **Récits héroïques** et **Réplique de couronne** (1 000 PO chacun) : multiplicateurs conditionnels de dégâts ; le nombre d'adversaires est vérifié et la présence d'un allié ou d'un autre joueur est confirmée à chaque attaque, jamais déduite automatiquement. Le cumul de multiplicateurs est une convention d'adaptation signalée.
- **Chevalier templiométrique** : escorte facultative sans nouvelles rencontres jusqu'à l'arrivée à destination, sans supprimer les kilomètres réellement parcourus ni les étapes intermédiaires. Le podomètre reste fonctionnel ; la protection est sauvegardée, puis supprimée à l'arrivée.
- **Bulker Fruité** : donne un shaker choisi lors de la rencontre pacifique, sans PO. **Gardes du Fitland** : payer tous ses PO ou combattre ; confirmation explicite avant tout paiement.
- Format de sauvegarde rétrocompatible (schéma 30), même identifiant et clé de signature. Aucun fichier `TESTS-Vx.x.md`.

## Nouveautés 2.5.1
- Le sac indique désormais **Bijou de mage niv.1**, **niv.2**, etc., en fonction des tranches de 200 PO investies. Les bonus et la sauvegarde sont inchangés.

## Nouveautés 2.5.0 — magie conventionnelle et Bijou de mage (p. 164, 176–177)

- **Quatre sorts de Mage** dans Aventure (p. 176), sans achat : **Boule de force** (Agilité 5, FORCE tracé avec le corps, 50 PV à distance sur un adversaire) ; **Onde de cardio** (Agilité 5, CARDIO tracé, +1 au jet de contexte avant attaque) ; **Flèche acido-basique** (Agilité 7, ACIDOBASIQUE tracé, cible marquée pour +50 PV sur chacune des attaques suivantes qui la touchent) ; **Second souffle quantique** (Agilité 2, SECONDSOUFFLE tracé, pause/reprise de la partie et mise en pause du podomètre s'il compte des pas).
- Interface : choix de la cible vivante, confirmation du rituel à chaque lancer, seuils de caractéristiques explicités. Les sorts n'appliquent jamais une récompense par eux-mêmes : seule une victoire contre les monstres conserve ses PO habituels.
- La **Flèche acido-basique** ajoute 50 PV une seule fois pour chaque cible déjà marquée atteinte par une attaque déclarée, pas par répétition. Elle n'augmente pas les dégâts fixes d'un Shaker poison ou sombre. Bonus sans transfert sur une autre cible ; convention d'adaptation dans le cas d'une attaque traversant plusieurs ennemis.
- **Bijou de mage** (p. 164) : achat de 200 PO, puis investissement facultatif de 200 PO supplémentaires ; +1 de contexte par tranche de 200 PO dépensée pour ce bijou, appliqué lors des futurs jets. La forme « investissement progressif sur le même bijou » est une convention d'interface, fidèle au barème du livre. Bonus non rétroactif sur une rencontre déjà générée.
- La table officielle de contexte 1–6 reste plafonnée à 6, comme les autres bonus de l'app ; ceci est une convention préexistante. Le livre n'indique pas de limite par combat pour les sorts, ils sont relançables avec un nouvel exercice confirmé.
- **Second souffle quantique** est une commande de pause/reprise, sans inventer de dégâts ni de guérison. Le suivi podomètre en cours est mis en pause (il faut le reprendre séparément depuis le panneau de marche lors du retour).
- Champs de sauvegarde ajoutés de manière rétrocompatible, schéma 29 : PO investis, repos magique, cibles marquées et utilisations de CARDIO pendant la rencontre. La signature et les capacités de classes, les seuils du podomètre et les notifications n'ont pas changé.
- Pas de fichiers `TESTS-Vx.x.md` dans le dépôt. Tests de logique Kotlin : `MageSpellRules.kt` et `tests/MageSpellRulesTest.kt`. Compilation APK toujours à confirmer via GitHub Actions.

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
