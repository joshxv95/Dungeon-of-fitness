# Historique des versions — Dungeon of Fitness

La version courante est toujours définie dans `app/build.gradle.kts` (`versionName` et `versionCode`), et affichée automatiquement dans l’application via `BuildConfig.VERSION_NAME`.

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
