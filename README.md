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
