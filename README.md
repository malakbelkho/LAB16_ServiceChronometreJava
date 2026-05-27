# PulseChrono – LAB 16

Application Android développée en Java permettant de maîtriser l’utilisation des **Services Android** à travers un chronomètre fonctionnant en arrière-plan avec une notification persistante.

Ce laboratoire met en pratique un **Foreground Service** et un **Bound Service** afin de comprendre comment une application peut exécuter une tâche continue même lorsque l’interface principale n’est plus visible.

---

## Objectif:

Le but de ce laboratoire est de :

- Comprendre le rôle des Services dans une application Android
- Créer un Foreground Service conforme aux restrictions Android récentes
- Afficher une notification persistante pendant l’exécution du service
- Utiliser un Bound Service pour communiquer entre l’Activity et le Service
- Mettre à jour un chronomètre en temps réel
- Démarrer et arrêter proprement un service depuis l’interface
- Comprendre les méthodes importantes :
  - `onCreate()`
  - `onStartCommand()`
  - `onBind()`
  - `onDestroy()`
- Utiliser `START_STICKY` pour gérer le redémarrage du service
- Personnaliser l’interface avec un design moderne et coloré

---

## Description de l’application:

**PulseChrono** est une application Android simple mais complète qui permet de lancer un chronomètre en arrière-plan.

L’application contient :

- Un écran principal moderne
- Un affichage du temps écoulé
- Un bouton pour démarrer le service
- Un bouton pour arrêter le service
- Un statut indiquant l’état du service
- Une notification persistante affichant le temps en direct

Le chronomètre continue de fonctionner même si l’utilisateur quitte l’application, grâce au **Foreground Service**.

---

## Fonctionnalités:

- Démarrage d’un service Android depuis l’interface
- Exécution du chronomètre en arrière-plan
- Affichage d’une notification persistante
- Mise à jour du temps dans la notification
- Connexion entre l’Activity et le Service avec un Binder
- Mise à jour du `TextView` en temps réel
- Arrêt propre du service
- Suppression de la notification après arrêt
- Gestion de la permission `POST_NOTIFICATIONS`
- Interface moderne avec :
  - Dégradé de fond
  - Carte centrale arrondie
  - Effet glassmorphism
  - Badge coloré pour le chronomètre
  - Boutons avec gradients
  - Statut dynamique du service

---

## Technologies utilisées:

- Android Studio
- Java
- XML
- Android SDK
- API minimum : 24
- Foreground Service
- Bound Service
- NotificationChannel
- NotificationCompat
- Handler
- ScheduledExecutorService

---

## Aperçu de l’application:

▶️ Une démonstration vidéo complète est disponible dans le dossier **Demo** du repository.

⚠️ En cas de problème de lecture depuis le repository :

👉 [▶️ Voir la démo sur Google Drive](https://drive.google.com/file/d/1lYjBjKFDbDQ8VcbScsewvd6IQ3yLkMFI/view?usp=sharing)

---

## Structure du projet:

```text
PulseChrono/
│
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   │
│   ├── java/
│   │   └── com.example.servicechronometrejava/
│   │       ├── MainActivity.java
│   │       └── PulseChronoService.java
│   │
│   └── res/
│       ├── layout/
│       │   └── activity_main.xml
│       │
│       ├── drawable/
│       │   ├── bg_pulse_screen.xml
│       │   ├── bg_glass_panel.xml
│       │   ├── bg_time_badge.xml
│       │   ├── bg_status_chip.xml
│       │   ├── bg_start_button.xml
│       │   └── bg_stop_button.xml
│       │
│       └── values/
│           └── colors.xml
```
## Fichiers principaux:

### AndroidManifest.xml

Ce fichier déclare les permissions nécessaires et le service utilisé par l’application.

Il contient notamment :

- `POST_NOTIFICATIONS` pour afficher les notifications sur Android 13+
- `FOREGROUND_SERVICE` pour autoriser l’utilisation d’un Foreground Service
- `FOREGROUND_SERVICE_DATA_SYNC` pour la compatibilité avec Android 14+
- La déclaration du service `PulseChronoService`

Le service est déclaré avec :

```xml
<service
    android:name=".PulseChronoService"
    android:foregroundServiceType="dataSync"
    android:exported="false" />
```

`exported="false"` permet d’empêcher d’autres applications d’accéder directement au service.

---

### MainActivity.java

`MainActivity` représente l’écran principal de l’application.

Elle permet de :

- Récupérer les vues depuis le layout
- Demander la permission des notifications
- Démarrer le service
- Se connecter au service avec `bindService()`
- Afficher le temps du chronomètre en direct
- Arrêter le service
- Nettoyer la connexion au service avec `unbindService()`

Elle utilise un `ServiceConnection` pour établir la communication avec le service.

---

### PulseChronoService.java

`PulseChronoService` est le service principal de l’application.

Il permet de :

- Lancer le chronomètre en arrière-plan
- Créer une notification persistante
- Mettre à jour la notification chaque seconde
- Fournir le temps actuel à l’Activity grâce au Bound Service
- Arrêter proprement le traitement lorsque l’utilisateur clique sur le bouton d’arrêt

Le service utilise :

- `onCreate()` pour initialiser le `NotificationManager`
- `onStartCommand()` pour démarrer le Foreground Service
- `startForeground()` pour afficher la notification obligatoire
- `ScheduledExecutorService` pour incrémenter le temps chaque seconde
- `onBind()` pour permettre la communication avec l’Activity
- `onDestroy()` pour arrêter le thread et supprimer la notification

---

## Layout principal:

### activity_main.xml

Ce fichier définit l’interface principale de l’application.

Il contient :

- Un titre principal : **PulseChrono**
- Un sous-titre indiquant le concept du lab
- Un statut du service
- Un affichage central du chronomètre
- Un bouton pour démarrer le service
- Un bouton pour arrêter le service

Le layout utilise :

- `ScrollView`
- `LinearLayout`
- `TextView`
- `Button`
- Des backgrounds personnalisés depuis `res/drawable`

---

## Design de l’application:

### bg_pulse_screen.xml

Définit le fond principal de l’application avec un dégradé moderne.

### bg_glass_panel.xml

Crée une carte centrale avec un effet clair et arrondi.

### bg_time_badge.xml

Utilisé pour afficher le temps du chronomètre dans un badge coloré.

### bg_status_chip.xml

Utilisé pour afficher l’état actuel du service.

### bg_start_button.xml

Définit le style du bouton de démarrage avec un gradient vert/cyan.

### bg_stop_button.xml

Définit le style du bouton d’arrêt avec un gradient rose/orange.

---

## Fonctionnement général:

Le fonctionnement de l’application suit ce pipeline :

```text
Utilisateur clique sur DÉMARRER LE SERVICE
        ↓
MainActivity lance PulseChronoService
        ↓
Le service démarre en mode Foreground
        ↓
Une notification persistante apparaît
        ↓
Le chronomètre commence à compter
        ↓
MainActivity se connecte au service avec bindService()
        ↓
L’interface récupère le temps en direct
        ↓
Le service continue même si l’application est fermée
        ↓
Utilisateur clique sur ARRÊTER LE SERVICE
        ↓
Le service s’arrête
        ↓
La notification disparaît
```

---

## Notions importantes du lab:

### Foreground Service

Un Foreground Service est un service visible pour l’utilisateur grâce à une notification persistante.

Il est utilisé lorsqu’une tâche doit continuer même si l’application n’est plus au premier plan.

Exemples d’utilisation :

- Application de musique
- Application GPS
- Téléchargement
- Chronomètre
- Synchronisation de données

---

### Bound Service

Un Bound Service permet à une Activity de se connecter à un Service.

Dans ce projet, il permet à `MainActivity` de récupérer le temps actuel depuis `PulseChronoService`.

La communication se fait grâce à un `Binder`.

---

### START_STICKY

`START_STICKY` indique au système Android que le service peut être recréé si le système le détruit pour libérer de la mémoire.

Dans ce lab, cela permet de rendre le comportement du service plus stable.

---

### NotificationChannel

Depuis Android 8.0, les notifications doivent appartenir à un `NotificationChannel`.

Dans cette application, un canal spécifique est créé pour afficher la notification du chronomètre.

---

## Tests réalisés:

Les tests effectués sont :

- Lancement de l’application
- Démarrage du service
- Vérification de l’apparition de la notification
- Vérification de l’incrémentation du temps
- Fermeture de l’application pendant l’exécution
- Vérification que le service continue en arrière-plan
- Réouverture de l’application
- Arrêt du service depuis l’interface
- Vérification de la suppression de la notification

---

## Résultat obtenu:

L’application permet de démarrer un chronomètre qui continue de fonctionner en arrière-plan.

Le temps est visible à deux endroits :

- Dans l’interface principale
- Dans la notification persistante

Lorsque le service est arrêté, le chronomètre revient à `00:00` et la notification disparaît.

---

## Compétences acquises:

À travers ce lab, j’ai appris à :

- Créer un Service Android en Java
- Utiliser un Foreground Service
- Créer une notification persistante
- Gérer les permissions liées aux notifications
- Comprendre le cycle de vie d’un service
- Utiliser un Bound Service
- Communiquer entre une Activity et un Service
- Mettre à jour une interface en temps réel
- Nettoyer correctement les ressources dans `onDestroy()`
- Améliorer l’interface utilisateur avec des drawables XML personnalisés

---

## Conclusion:

Ce laboratoire m’a permis de comprendre le fonctionnement des Services Android à travers une application concrète de chronomètre.

L’application combine un **Foreground Service** pour garder le chronomètre actif en arrière-plan et un **Bound Service** pour permettre la communication avec l’Activity.

Ce mécanisme est très utilisé dans les applications modernes qui doivent continuer à exécuter une tâche même lorsque l’utilisateur quitte l’écran principal.

Grâce à cette version personnalisée, l’application ne se limite pas à un simple chronomètre fonctionnel : elle propose aussi une interface moderne, colorée et agréable à utiliser.
