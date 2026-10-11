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
\n## v1.0.0 — Expéditions de Fitland
- Choix d'une destination parmi les lieux de la carte (livre p. 51).
- Saisie manuelle de la distance du trajet selon la carte, sans inventer de connexions.
- Position de départ selon la classe lors de la première création du profil (livre p. 41).
- Mode novice : 1 km de carte équivaut à 1 minute d'endurance (livre p. 41).
- Progression cumulée entre trajets, rencontre à chaque seuil de 6 unités (livre p. 42).
- Validation de l'arrivée et mise à jour de la position.
- Sauvegardes v0.9 compatibles, accomplissements préservés.
- LIMITES : rencontres encore fictives/provisoires ; pas de calcul automatique d'itinéraire, ni de pouvoirs de déplacement des classes.

## v1.1.0 — Rencontres officielles
- 7D6, table 7–42 p. 43 ; quantité 1D6 et contexte 1D6 p. 42.
- Filtrage des rencontres non accessibles en Fitland.
- Points de vie cumulés, dégâts manuels, récompense seulement après victoire.
- Effets immédiats affichés, à appliquer manuellement ; rencontres inoffensives séparées.
- Sauvegarde de la rencontre en cours et des PV restants.
- Les pouvoirs de classe, objets, fuite et effets persistants ne sont pas encore automatisés.

## v1.2.0 — Armurerie et attaques
- 11 armes tarifées et leurs exercices, transcrites du catalogue du livre p. 163-169.
- Achat avec PO, sans revente, inventaire sauvegardé.
- Sélection d'une arme possédée pendant le combat ; nombre de répétitions ou de tranches de temps réalisées ; calcul des PV.
- Saisie manuelle conservée pour les objets non intégrés et les effets spéciaux.
- Compatibilité des sauvegardes v1.1.
- Limites : pas encore de dotation initiale recalculée, ni catalogue complet, ni automatisation des modificateurs de contexte.

## v1.3.0 — Modificateur de contexte
- Gants de vétéran : +1 au dé de contexte (p. 168).
- Choix explicite de l'arme active dans le Sac AVANT la rencontre.
- Affichage du dé de contexte et des bonus, y compris Trollympien +2.
- Le dépassement de 6 est plafonné à 6 faute de ligne supérieure dans la table.
- Embuscade : rappel des durées doublées et durée effective affichée pour les armes temporisées.
- Sauvegarde compatible avec v1.2 ; les rencontres antérieures n'ont pas de dé initial récupérable.
- Les autres effets d'objets et capacités de classe restent manuels.

## v1.4.0 — Extension de l'armurerie
- Six armes supplémentaires du catalogue (Culotte de mage, Hallebarde lourde, Lance, Masse Gainer, Masse haltère, Perche Lance).
- Bonus de contexte +2 de la Hallebarde lourde si active avant le tirage.
- Identification explicite des armes à dégâts à distance (sans inventer de mécanique supplémentaire).
- Affichage du bonus réel de l'arme active pendant la rencontre.
- Données et sauvegardes des versions antérieures conservées.
- Limites : les effets d'autres équipements, l'équipement initial et les règles complètes de combat restent à implémenter.


## v1.5.0
- Ajout des armes Sarbacane, Sabre féérique, Sceptre féérique et Tapis de mage de guerre, tirées du Guide touristique p. 172 et 175.
- Le contexte « Vous êtes piégé » affiche le total de burpees selon la quantité d'adversaires.
- Les sauvegardes v1.4 restent compatibles.
- Les effets complexes et objets consommables ne sont pas encore automatisés.


## v1.6 — Objets d'amélioration (PDF p. 170-171)
- Pattes d’ours : 50 PO, +50 PV à l'attaque si le comptage oral est confirmé.
- Manuel de bottes de Maître : 1000 PO, +100 PV par attaque avec une arme.
- Manuel de combat à mains nues : 500 PO, +50 PV par attaque avec des gants.
- Les bonus fixes sont comptés une fois par attaque enregistrée, non par répétition.
- Achats et objets conservés dans les sauvegardes antérieures.
- Non inclus : Manuel de méditation, autres consommables, interactions avancées.
