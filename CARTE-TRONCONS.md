# Tronçons de la carte du Milieu du Disque-Fonte

Référence : *La Communauté des Abdos*, édition mars 2025, **page 51**.
Les distances ci-dessous ont été relevées **sur les chemins en pointillés** visibles sur la carte. Ce relevé aide à vérifier le routage numérique ; il ne remplace pas les conditions d'accès aux chapitres.

| Origine | Destination | Distance |
|---|---|---:|
| Fontaria | Temple de Tasmina | 10 km |
| Temple de Tasmina | Entrepôts Shaker+ | 15 km |
| Entrepôts Shaker+ | Antre de Mollefesse | 20 km |
| Entrepôts Shaker+ | Dévers du Fitland | 10 km |
| Falaises Olympiques | Dévers du Fitland | 10 km |
| Dévers du Fitland | Ruines du Golem | 5 km |
| Dévers du Fitland | Plaine du Fitland | 10 km |
| Temple de Tasmina | Plaine du Fitland | 10 km |
| Temple de Tasmina | Omega Beach | 10 km |
| Temple de Tasmina | Mont Stepper | 20 km |
| Plaine du Fitland | Omega Beach | 10 km |
| Plaine du Fitland | Sud du Fitland | 10 km |
| Omega Beach | Sud du Fitland | 5 km |
| Sud du Fitland | Forêt de Brise Mollets | 10 km |
| Forêt de Brise Mollets | Dunes des sacs crevés | 5 km |
| Dunes des sacs crevés | Erythropoiéta | 10 km |
| Dunes des sacs crevés | Temple de la Prophétie | 5 km |
| Dunes des sacs crevés | Tique-Couenne | 10 km |
| Tique-Couenne | Oasis Interdite | 10 km |
| Tique-Couenne | Rocher de la Croupe Draconique | 10 km |
| Rocher de la Croupe Draconique | Rives de la Mer de Nuages | 10 km |
| Rocher de la Croupe Draconique | Caverne des Nuages sans fin | 10 km |
| Rocher de la Croupe Draconique | Khôlkozia | 30 km |
| Khôlkozia | Fei Tôro Choh | 20 km |
| Mont Stepper | Tombe d’Arno | 5 km |
| Mont Stepper | Nosferathon | 5 km |
| Nosferathon | Litière Ville | 10 km |
| Litière Ville | Garde Froide | 20 km |

## Limites de l'automatisation

- Les liaisons sont **bidirectionnelles** pour le calcul des distances et la téléportation du Cavalier ; ce choix de modélisation ne débloque pas les chapitres.
- Une **téléportation du Cavalier** n'est autorisée que si un tronçon direct relie les deux points, une fois par jour.
- La **plus courte distance** peut traverser une zone déjà visitée : le joueur reste responsable de suivre l'ordre et les restrictions du livre. Les sites à cadenas ne servent pas de raccourci dans le calcul.
- Les accès restreints sont : Île des poids guidés, Antre de Mollefesse, Falaises Olympiques, Ruines du Golem, Temple de la Prophétie et Caverne des Nuages sans fin. L'application avertit mais **ne valide pas les conditions d'entrée**.
- **Île des poids guidés** : la carte ne montre pas de trajet pointillé la reliant à un autre point. L'application propose seulement la saisie manuelle si le scénario justifie un déplacement.
- Le logiciel n'invente pas de routes ni de vitesses réelles : la distance proposée est celle du graphe de la carte, pas un itinéraire GPS.

## v1.23 — Étapes guidées

Sur un trajet planifié à partir du graphe, l'application affiche chaque tronçon et attend la confirmation de passage à chaque zone intermédiaire. Cette confirmation **ne débloque ni ne termine** le chapitre de la zone. L'utilisateur doit respecter les accès et conditions du livre.

Un trajet raccourci par l'Éclaireur ou le Barde conserve les étapes mais divise la distance de chacun des tronçons par deux. La saisie manuelle reste possible et ne force aucun passage automatique. Lors d'une fuite sur un itinéraire guidé, le dernier jalon confirmé devient le point de retour.
