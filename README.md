# Dungeon of Fitness — version 0.2.0

Application Android hors ligne (prototype). Cette version ajoute une sauvegarde locale automatique du personnage, de la distance, des rencontres, du journal et de l’inventaire. Le nom du personnage peut être modifié dans l’onglet Personnage.

## Installer la mise à jour

Envoyer **le contenu** de ce dossier à la racine du dépôt GitHub, sur `main`, en remplaçant les fichiers existants. Le workflow Actions `Build APK` crée `app-debug.apk` dans les artefacts du run. Installer l’APK par-dessus l’ancienne version (même `applicationId`) sans désinstaller l’application pour conserver les données déjà enregistrées.

## Limites

La sauvegarde commence à partir de la v0.2. Les actions effectuées dans la v0.1 n’étaient pas persistées. La destination 4 km, les caractéristiques et les rencontres restent des éléments de démonstration, **pas une transcription complète des règles du livre**. Les épreuves, la campagne, les classes et les tableaux officiels seront intégrés progressivement après vérification des règles.

Aucune autorisation réseau, aucun compte, aucun serveur.


## Version 0.3 — profil novice
- Formulaire Prière de Tasmina (pompes, jumping jacks, équilibre).
- Calcul automatique des trois caractéristiques novices et du niveau (moyenne entière).
- Attribution Recrue/Stagiaire/Cadet et disciple de Tasmina.
- Sauvegarde locale rétrocompatible avec la version 0.2.
- Les données du profil avancé et les rencontres du prototype ne sont pas encore conformes au livre.
