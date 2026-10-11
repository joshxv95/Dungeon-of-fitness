# Dungeon of Fitness v0.4

Android Kotlin/Compose, hors ligne. Build APK dans GitHub Actions.

## Nouveautés
- Sélection Novice / Avancé dans Personnage.
- Quatre Prières du profil avancé : Proteidon, Endurox, Dumbella, Athleena ; résilience mentale Arno.
- Classe, compétence et capacité spéciale ; divinité selon le meilleur score (égalités incluses).
- Sauvegarde automatique de toutes les nouvelles données ; compatible avec les données v0.3.
- Réinitialisation intégrale avec boîte de confirmation. Annuler n'efface rien.

## Important
Le livre p. 35 indique littéralement « somme des scores de caractéristiques divisée par dix » : c'est cette formule qui est implémentée, même si elle semble contradictoire avec le niveau maximum 10 mentionné à proximité. La classe est choisie manuellement d'après l'activité qui a obtenu le meilleur score ; le logiciel ne peut pas vérifier cette correspondance sans historique d'épreuves par activité. La progression mentale +1 dépend de l'amélioration d'un rang dans chacune des quatre Prières et doit être validée par le joueur. Les rencontres restent provisoires.

## Installation
Décompresser et transférer le contenu du dossier `dungeon-fitness-app` à la racine du dépôt GitHub. Pousser sur `main`, télécharger l'APK dans Actions > Artifacts. Installer par-dessus la v0.3 pour conserver la sauvegarde.
\n\n## Version 0.5\n- Le choix Novice/Avancé est verrouillé dès le premier enregistrement du profil et conservé après redémarrage.\n- Les épreuves du profil choisi restent actualisables.\n- La réinitialisation complète déverrouille le choix (attention : suppression totale).\n- Les noms des accomplissements des huit classes sont visibles à titre indicatif pour le profil avancé, sans validation automatique pour le moment.\n\n## v0.6 — Classes et accomplissements
- Huit classes et leurs trois accomplissements dans le profil avancé.
- Historique des classes débloquées, préservé lors des changements de discipline.
- Validation manuelle et révocable des accomplissements, persistée localement.
- Migration des sauvegardes v0.5, sans effacement des données.
- Le verrouillage Novice/Avancé est conservé.
- Limite : les conditions officielles détaillées des accomplissements ne sont pas encore intégrées ; aucun gain automatique n'est attribué.
\n## v0.7.0 — correctifs et ergonomie
- Correction effective des cases d'accomplissements, avec sauvegarde persistante.
- Conservation des classes débloquées lors des changements de discipline.
- Résumé des caractéristiques, classes et accomplissements placé avant les formulaires.
- Onglet Application : numéro de version, fonctionnalités présentes et fonctionnalités à venir.
- Compatibilité avec les sauvegardes précédentes.\n\n## v0.8.0
- Affiche seulement les accomplissements de la classe actuelle.
- Au changement effectif de classe lors de l'enregistrement du profil avancé, les validations sont effacées.
- L'historique des classes découvertes est conservé ; pas de perte de sauvegarde.
- Carnet de lieux dans l'onglet Application : aperçu non interactif sans distances inventées.
- Version affichée : 0.8.0.

## v0.9.0 — fidélité des accomplissements
- Les accomplissements validés persistent lors des changements de classe.
- Les trois accomplissements de la classe active restent affichés.
- Seuls les accomplissements validés des anciennes classes sont affichés, regroupés par classe.
- Possibilité de décocher une validation, y compris dans une ancienne classe.
- Les données déjà effacées par la version 0.8 ne peuvent pas être restaurées automatiquement.
- Numéro de version Android corrigé : 0.9.0.
