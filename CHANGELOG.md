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
