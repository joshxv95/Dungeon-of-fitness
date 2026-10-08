# Dungeon of Fitness — Android

Application personnelle hors ligne destinée à transformer les règles de Dungeon of Fitness en interface mobile jouable.

## Build GitHub

Le workflow `.github/workflows/build-apk.yml` compile automatiquement l'APK debug à chaque push sur `main` et sur demande via **Actions → Build APK → Run workflow**.

L'APK est disponible dans les **Artifacts** du run.

## État 0.1

- Interface Android Jetpack Compose
- Navigation Accueil / Aventure / Personnage / Sac / Journal
- Déplacement et suivi de distance
- Première implémentation du système de rencontres tous les 6 km
- Inventaire et PO locaux en mémoire
- Structure prête pour intégrer les tables et la campagne complète

Cette version est volontairement un moteur de départ : les tables complètes du livre, les profils, tests, objets, cartes et chapitres seront ajoutés comme données structurées afin de conserver une séparation claire entre règles et interface.
