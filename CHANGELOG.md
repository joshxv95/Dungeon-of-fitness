## 2.5.0 — Sorts du Mage et Bijou de mage
- Ajout des quatre sorts conventionnels du livre (p. 176) avec exercices confirmés, prérequis d'Agilité, sélection des cibles et persistance des effets : Boule de force, Onde de cardio, Flèche acido-basique, Second souffle quantique.
- Déplacement et podomètre inchangés ; Second souffle place la session de pas en pause sans perdre les kilomètres ni le combat. La reprise des pas est distincte du retour à l'aventure.
- Ajout du Bijou de mage (p. 164), amélioration progressive par paliers de 200 PO, +1 contexte par tranche de 200 PO investie. Convention : un même bijou peut recevoir plusieurs versements.
- Les attaques touchant une cible marquée bénéficient d'un bonus de +50 PV non transférable, ajouté une seule fois par cible et par attaque enregistrée. Les Shakers à dégâts fixes ne sont pas concernés (convention d'interface).
- Schéma de sauvegarde 29 et versionCode 205 ; signature APK inchangée. Tests de logique des sorts et non-régression ; APK non compilé localement. Aucun fichier `TESTS-Vx.x.md` ajouté.

## 2.4.0 — Armures officielles, armes manquantes et Epée de Tasmina
- Nouveau rayon **Armures** : force (300 PO, +100 PV/exécution/série confirmée, contexte +2), leste (200 PO, +50 PV/exécution/série confirmée, contexte +1), respirante (100 PO, relance du dé de contexte et choix du résultat) ; livre p. 164. Les armures sont distinctes des armes et soutiens (convention d'interface). Les gains de dégâts exigent une confirmation de port du lest à chaque attaque ; le contexte est figé à la génération.
- Armure respirante : une seule relance possible, choisie par le joueur avant les attaques. Les deux jets et le choix sont conservés dans la sauvegarde ; impossible d'obtenir la relance en changeant d'armure après l'apparition de la rencontre.
- Excalibrute (500 PO, 1000 PV/30 sec., contexte +1) et Fléau d'inertie (200 PO, 100 PV/exécution, contexte +1) ajoutés au catalogue avec les exercices du livre p. 168.
- Epée de Tasmina : arme rare non vendable (500 PV/30 sec., contexte +2), à réclamer une fois après le chapitre « Exploration du Temple de Tasmina » (p. 126–127), comme indiqué dans le récit.
- Schéma 28 : nouveaux champs équipés et de relance, chargement rétrocompatible. Pas de changements au podomètre, aux notifications, aux trajets, aux pouvoirs de classes/compagnons ou aux consommables. `versionCode` 204, version `2.4.0`, signature inchangée. Suppression des fichiers de protocole `TESTS-V*.md` ; tests Kotlin internes maintenus.
- Vérifications locales : tests purs des règles d'armure et des moteurs de pas, compagnons, consommables et parchemins ; compilation APK toujours à confirmer sur GitHub Actions.

## 2.3.0 — Les Parchemins comestibles (p. 174) et le Manuel d’agilité (p. 169)
- Quatre consommables officiels vendus 100 PO dans Sac → Consommables. **Cardio** : composante de dégâts des attaques à durée ×2 ; **Force** : composante de dégâts des attaques par exécution ×2 ; **Congestion** : composante du bonus des améliorations ×2 (les soutiens ne sont pas des améliorations) ; **Flexibilité** : contexte +5 avant attaque, avec plafond de 6 issu de la convention de l'application. Les effets sont affichés avant les attaques.
- Manuel d’agilité vendu 1 000 PO dans Sac → Améliorations : +2 permanent au jet de contexte lors de la génération d'une nouvelle rencontre, avec effet visible au jet et dans le laboratoire.
- Les trois parchemins de dégâts sont consommés chacun une fois à l'activation et durent jusqu'à la fin du combat ; un même effet ne peut pas être activé deux fois dans ce combat. Flexibilité s'applique à une seule rencontre, avant résolution du contexte. Les objets sont retirés de l'inventaire lors de l'activation ; les états temporaires sont sauvegardés et effacés à la résolution, à l'évitement ou à la fuite.
- Les bonus d'améliorations restent **une seule fois par attaque enregistrée** (convention validée en v1.6). La saisie manuelle de secours demande un total final, pour ne pas doubler les parchemins. Calculs saturés à Int.MAX_VALUE pour éviter le dépassement d'entiers en cumulant les bonus.
- Aucun changement aux rencontres du podomètre, aux seuils des 6/3 km, aux pouvoirs du Barde et aux sauvegardes antérieures. Schéma 27, versionCode 203, versionName 2.3.0, signature debug identique. Tests de règles Kotlin indépendants (21 assertions), APK Android à compiler sur GitHub Actions.

## 2.2.0 — Shaker sombre et laboratoire de rencontres
- **Shaker sombre** (livre p. 173) : automatisation du consommable rare à 700 dégâts à distance contre un groupe ; confirmation préalable irréversible avant de le retirer de l'inventaire. Distribution des dégâts : **700 PV pour chaque adversaire vivant du groupe** (convention d'adaptation visible, car le livre ne précise pas la répartition). Les dégâts sont plafonnés aux PV individuels, sans transfert du surplus ni multiplicateur des pouvoirs de classe/compagnon ou du Shaker vigueur. Si le groupe est vaincu, les PO sont attribués une seule fois et les effets temporaires du combat sont nettoyés.
- **Laboratoire non destructif** dans Application : tirer une rencontre fictive avec ses 7D6, sa quantité, son contexte, ses PV et son butin potentiel ; simuler l'effet du Shaker sombre sans modifier le trajet, les compteurs, le combat, l'inventaire ou le journal. Un résultat interdit par le filtre Fitland peut être écarté comme dans le jeu. Ce test **ne valide pas** le mécanisme réel du capteur au seuil des 6 km.
- **Trois tests de notifications** : rencontre, étape, arrivée, chacun avec le même libellé de base que l'événement réel, mais portant la mention « TEST » et sans mouvement ni événement enregistré.
- La règle de rencontre tous les 6 km (3 km avec Troll Conteur), les seuils du service de pas, la clé de signature, les sauvegardes et les autres capacités n'ont pas été modifiés. `versionCode` 202, `versionName` 2.2.0. Ajout de calcul pur et tests reproductibles du Shaker sombre ; APK à compiler sur GitHub Actions et tests capteur réels à confirmer.

## 2.1.0 — Notifications des événements du podomètre
- Notification d'**alerte distincte** dans un nouveau canal haute importance « Événements d’aventure » dès que la session atteint son plafond : rencontre, étape intermédiaire ou destination.
- Texte adapté à l'événement : « Rencontre ! », « Étape atteinte ! » ou « Destination atteinte ! ». Le podomètre s'arrête toujours avant de reprendre la marche, aucun combat ni passage n'est validé automatiquement.
- Cliquer l'alerte ouvre directement l'onglet Aventure, même si l'activité Android est déjà ouverte ; la notification est effaçable après consultation.
- Nature de l'arrêt et ville-cible conservées avec la session dans les préférences, même si l'écran est verrouillé ou l'application fermée.
- Bouton **MODE TEST : envoyer une notification** dans Application, sans capteur ni changement de progression, pour vérifier le canal à domicile.
- Autorisation POST_NOTIFICATIONS demandée comme dans la v2.0 ; si refusée ou si le canal est désactivé dans Android, le compteur fonctionne mais l'alerte système peut être invisible. Le statut reste affiché dans Aventure.
- Mode manuel, règles des classes, passages intermédiaires, sauvegardes et signature inchangés. VersionCode 201. Tests de logique des alertes ajoutés ; compilation Android à vérifier sur GitHub Actions.

## 2.0.0 — Comptage des pas réel, sessions de marche et synchronisation du voyage
- Nouveau podomètre facultatif : lecture du capteur matériel `TYPE_STEP_COUNTER` (repli `TYPE_STEP_DETECTOR`), convention **1 pas = 1 mètre** de distance de jeu. Aucun GPS, aucune connexion réseau. Disponible pour le profil avancé ; mode novice inchangé (minutes d'endurance).
- Service Android de premier plan (`health`) avec notification, commandes pause/reprise/arrêt, demande de l'autorisation activité physique et, sur Android 13+, notifications facultatives.
- Session automatiquement plafonnée au **prochain obstacle** : combat à 6 km cumulés ou 3 km avec Troll Conteur, étape intermédiaire de trajet guidé ou destination. Les pas réalisés au-delà de l'arrêt ne sont pas attribués automatiquement et nécessitent une nouvelle session ; pas de combat sauté.
- Les boutons de kilomètres manuels restent disponibles, mais sont désactivés pendant une session de marche et jusqu'à synchronisation, pour éviter les doubles comptes.
- Mesures capteur stockées indépendamment des sauvegardes du RPG ; identifiants de session et curseur `stepAppliedSteps` sauvegardés pour ne pas réappliquer des pas après rotation ou redémarrage. Journal de début/fin de session. La suppression du personnage interrompt le service et efface le compteur.
- Distance affichée au millième de kilomètre pour rendre les petits changements perceptibles. Schéma de sauvegarde 26 compatible avec la 1.26 ; même clé debug et `applicationId`. VersionCode 200.
- 22 tests unitaires de logique capteur, de plafonds de sessions et de conversions passés localement. **La compilation Android complète et l'essai sur capteur physique restent à faire sur GitHub Actions/téléphone.**

## 1.26.0 — Compagnons : Oracle, Paladin/Quêteur et Barde
- **Oracle de Tasmina** (livre p. 73) : Prière confirmée avant un combat (dégâts des attaques ×2 pour celui-ci) OU Prière confirmée avant le départ (dégâts ×2 pendant les combats du voyage). Ne se cumule pas deux fois avec lui-même. Multiplicateur appliqué aux exercices d'armes et à la saisie manuelle de dégâts bruts ; pas aux effets des potions dont les dégâts fixes ne sont pas des attaques. L'effet de combat disparaît à la résolution/fuite, celui du voyage à l'arrivée/fuite.
- **Le Paladin et le Quêteur** (p. 129) : après confirmation d'une Prière avant départ, dé de quantité −2 pour une rencontre au choix du voyage (utilisation volontaire avant le combat). PV, quantité et PO recalculés. Rencontres à quantité fixe exclues. Convention visible : le résultat du dé est plafonné à un minimum de 1, car le livre ne détaille pas les résultats nuls ou négatifs. L'utilisation ne se recharge qu'au prochain départ accompagné d'une nouvelle Prière.
- **Barde compagnon** (p. 113) : en confirmant l'écoute de musique au départ, distance divisée par deux pour ce voyage. Pouvoir indépendant de la classe Barde et sans compteur quotidien ; pas de cumul de demi-distances avec Éclaireur ou Barde joueur (convention d'adaptation). Compatible avec les tronçons guidés.
- Sauvegarde additive (schéma 25), indicateurs de pouvoirs actifs, journal, nettoyage à l'arrivée ou à la fuite. Pas de modifications des règles déjà validées.
- Ajout de `CompanionRules.kt` (calculs purs réutilisables) et de `tests/CompanionRulesTest.kt` vérifié localement avec le compilateur Kotlin. Même signature et applicationId, versionCode 129.
- Les autres effets narratifs spéciaux et le Shaker sombre restent à appliquer en lisant le livre, sans interprétation arbitraire.

## 1.25.0 — Compagnons, conséquences et butin des chapitres
- Nouveau carnet des 8 compagnons cités explicitement dans la campagne (livre p. 73, 75, 76, 93, 113, 117, 119, 129), accessible depuis Aventure.
- Recrutement calculé à partir des chapitres que le joueur a confirmé terminés, avec confirmation manuelle pour les sauvegardes anciennes et retrait manuel en cas d'événement narratif. Les compagnons disparaissent du registre si leur chapitre est dévalidé (sauf recrutement manuel indépendant).
- Nouveaux accès automatiques d'après les chapitres : Nosferathon → Salle secrète de l’Oasis ; Tombe d’Arno → Île des poids guidés (une fois l'épreuve chantée validée) ; Falaises Olympiques → Antre de Mollefesse ; Temple de la prophétie → Exploration du Temple de Tasmina ; Ruines du Golem → Falaises Olympiques. Les trois accès de la v1.24 restent disponibles.
- Pouvoirs de compagnons : Bulker Fruité peut neutraliser un adversaire avant attaque lors d'une rencontre de voyage (butin recalculé) ; La Fée peut téléporter une unique fois dans toute la campagne vers une zone accessible (confirmation irréversible) ; Le Troll Conteur double la fréquence des rencontres (seuil tous les 3 km plutôt que 6).
- Attribution à usage unique des trois objets explicitement indiqués après le Temple de la prophétie (Shaker vigueur, Shaker quantique, Shaker sombre), via une confirmation du joueur dans le carnet, sans création de nouveaux PO. L'effet du Shaker sombre reste à appliquer selon le livre.
- Capacités de l'Oracle de Tasmina, du Barde compagnon et du Paladin/Quêteur consultables dans le carnet mais pas encore automatisées ; pas de cumul de raccourcis inventé.
- Journal enrichi, champs de sauvegarde additifs (schema 24), compatibilité v1.24 et signature APK conservées. versionCode 128.

## 1.24.0 — Carnet des chapitres et suivi des accès
- Carnet de **36 chapitres principaux** de la campagne, avec titre et page selon le sommaire du livre (p. 218-219) : 24 en accès libre, 12 en accès restreint.
- Entrée depuis l'onglet Aventure, filtres « Sur place », « Tous », « Accès restreint », « Terminés », et progression visuelle.
- Le passage dans une ville, un déplacement ou une téléportation ne termine jamais automatiquement un chapitre. Le joueur confirme avoir exécuté les épreuves dans le livre ; impossible de valider au milieu d'un trajet ou d'un combat.
- Les chapitres géolocalisables demandent de se trouver au bon endroit. Pour les scènes hors carte, la confirmation est manuelle après vérification du récit.
- **Accès restreints :** confirmation manuelle possible uniquement si le joueur l'a obtenu dans le livre. Trois accès explicitement conférés par des chapitres achevés sont reconnus (Audience du Roi → Temple de la prophétie, Arène → Ruines du Golem, Île des poids guidés → Caverne des nuages sans fin). Retirer la validation du chapitre retire sa récompense d'accès automatique, sauf si l'accès a aussi été enregistré manuellement.
- Carnet de lieux visités distinct des chapitres terminés, mis à jour lors du passage aux étapes, de l'arrivée et de la téléportation ; la position courante compte comme visitée. Les trajets sauvegardés précédents ne sont pas inventés rétroactivement.
- Sauvegarde locale additive (schéma 23), mêmes signature et identifiant ; versionCode 127. Aucune récompense de chapitre, épreuve ou compagnon inventé/automatisé.

## 1.23.0 — Voyage guidé à étapes et seuils de rencontre
- Suivi réel des étapes intermédiaires des trajets calculés depuis les 28 tronçons de la carte ; passage à confirmer, sans validation automatique du chapitre du livre.
- Les distances raccourcies de l'Éclaireur et du Barde s'appliquent à chaque tronçon.
- Progression bloquée à chaque jalon et à chaque seuil de 6 unités cumulées pour empêcher de sauter des rencontres.
- Journal et accueil enrichis avec les étapes en cours et les passages déjà confirmés.
- Fuite depuis un parcours guidé : retour vers le dernier jalon confirmé, sans générer de rencontre pendant le retour.
- Parcours manuels et sauvegardes v1.22 préservés ; schéma de sauvegarde 22, versionCode 126.
- Mécaniques de combat, butins, équipements et signature APK inchangés.

## 1.22.0 — Carte officielle, itinéraires et Cavalier automatisé
- Carte de la page 51 intégrée hors ligne et consultable par zoom tactile.
- Graphe de 28 liaisons directes transcrites depuis les pointillés et distances de la carte du Milieu du Disque-Fonte ; itinéraire le plus court et distance suggérée entre les villes reliées, sans raccourci via les zones à cadenas.
- Distances de carte automatiquement proposées dans la préparation du trajet, saisie manuelle toujours possible lorsque le chemin n’est pas renseigné ou si le joueur suit un autre itinéraire.
- Cavalier : liste automatique des zones directement adjacentes à la position courante, validation de l’adjacence aussi côté logique du jeu. Pouvoir toujours limité à une fois par jour ; pas de trajet actif ni de rencontre en cours.
- Île des poids guidés : aucun chemin pointillé vers le continent dans la carte p. 51 ; trajets à renseigner manuellement et téléportation automatiquement indisponible depuis ce lieu.
- Zones verrouillées : avertissement explicite, conditions d’accès laissées aux chapitres du livre ; le trajet ne les utilise pas comme raccourcis intermédiaires.
- Aucune modification des combats, classes, objets, signatures debug ou sauvegardes. versionCode 125.

# Historique des versions — Dungeon of Fitness

La version courante est toujours définie dans `app/build.gradle.kts` (`versionName` et `versionCode`), et affichée automatiquement dans l’application via `BuildConfig.VERSION_NAME`.

## 1.21.0 — Dés de rencontre, Cavalier et jauges de caractéristiques
- Mage (p. 41–42) : modifie de ±1 un dé choisi parmi les sept dés du type, le dé de quantité ou le dé de contexte, une fois par rencontre (convention d’interface conservée).
- Moine (p. 41–42) : mêmes trois catégories de dés, avec ±1/±2 et confirmation d’une Prière. Les faces restent dans l’intervalle 1 à 6.
- Les 7D6 de type et le D6 de quantité sont enregistrés dans chaque nouvelle rencontre. Recalcul immédiat de l’adversaire, des PV, de la quantité, des PO, du contexte et des éventuels modificateurs du Trollympien.
- Les rencontres modifiées vers un type indisponible au Fitland sont ignorées conformément à la règle du livre (p. 42) ; les rencontres à quantité fixe n’offrent pas de dé de quantité à modifier.
- Cavalier (p. 41) : une téléportation quotidienne vers une zone adjacente, sans distance ni rencontre. En l’absence de graphe officiel intégré, l’utilisateur doit vérifier et confirmer lui-même l’adjacence sur la carte p. 51.
- Mode test : réinitialisation du compteur Cavalier en plus de ceux de l’Éclaireur, du Barde et du Guerrier.
- Fiche de personnage : jauges des statistiques sur 10 avec nombres conservés (affichage seulement, pas de règles RPG maison ajoutées).
- Compatibilité des sauvegardes v1.20.2 (nouveaux champs avec valeurs par défaut) et signature debug conservée ; versionCode 124.

## 1.20.2 — Portraits et maintenance
- Huit portraits de classes refaits à partir de nouvelles illustrations, enregistrés comme huit fichiers PNG indépendants, sans textes ni portraits voisins.
- Harmonisation du format des illustrations (450 × 600), affichage sans découpe (`ContentScale.Fit`), coins arrondis et encadrement discret de la fiche personnage.
- Numéro de version affiché depuis `BuildConfig.VERSION_NAME`, et non plus codé en dur ; génération de BuildConfig explicitement activée.
- Suppression des anciens `VERSION-X.X.txt` au profit de ce changelog unique.
- `versionCode` 123 ; clé debug de test conservée. Aucune modification des règles ni du format des sauvegardes.

## 1.20.1 — Recadrage provisoire
- Premier correctif des portraits issus de la planche de la v1.20.0.

## 1.20.0 — Améliorations graphiques
- Portraits des huit classes, icônes d’objets, jauges de points de vie individuelles et présentation de l’aventure.

## 1.19.1 — Mode test
- Réinitialisation des capacités quotidiennes sans perte de progression et clarification du bonus du Barde.

## 1.19.0 — Barde solo (règle maison)
- Deux demi-trajets quotidiens ; mode prudent ou audacieux, rencontres doublées et PO × 2,5 par rapport au butin de base dans le mode audacieux.
- Les rencontres à quantité fixe restent exclues du doublement et de la majoration.

## 1.18.0 — Mage, Moine et Barde
- Modification du dé de contexte par le Mage/Moine ; capacité de trajet du Barde (ultérieurement modifiée).

## 1.17.0 — Éclaireur et Guerrier
- Deux raccourcis quotidiens pour l’Éclaireur ; dégâts doublés avec Prière pour le Guerrier, une fois par jour.

## 1.16.0 — Acrobate et Chasseur
- Bonus de contexte/fuite de l’Acrobate et réduction de quantité du Chasseur.

## 1.15.0 — Inventaire et fuite
- Sac classé par catégories, évitement distinct de la fuite, retour vers le départ.

## 1.14.0 — Équipement de soutien
- Six équipements de soutien et leurs conditions d’usage.

## 1.13.0 — Combat à distance
- Ciblage unique des attaques à distance (convention maison).

## 1.12.0 — PV individuels
- Suivi des PV de chaque adversaire et ciblage du Shaker poison.

## 1.11.0 — Illusion
- Application du Shaker illusion selon convention maison.

## 1.10.0 — Installation sans perte de données
- Clé de signature debug fixe pour les mises à jour en place, boutons de PO de test.

## 1.0.0 à 1.9.0 — Fondations
- Carte et distances, rencontres, combat à armes/exercices, inventaire, catalogue, shakers, sauvegardes et premiers bonus d’équipement.

**Règles de provenance :** les écarts au livre *La Communauté des Abdos* doivent rester identifiés comme conventions ou règles maison.
