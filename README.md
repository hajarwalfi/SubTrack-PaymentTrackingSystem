# SubTrack - Système de Suivi de Paiements par Abonnement

![Java](https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

## Description

**SubTrack** est une application console Java développée dans le cadre d'un projet académique pour la gestion complète des abonnements. Elle permet de suivre automatiquement les paiements, détecter les impayés et générer des rapports financiers.

L'application utilise **Java 8** avec programmation fonctionnelle (Stream API, Lambda, Optional) et une architecture en couches avec persistance JDBC.

## Fonctionnalités

### Gestion des Abonnements
- Créer des abonnements avec ou sans engagement
- Modifier et supprimer des abonnements
- Suspendre/réactiver des abonnements
- Lister tous les abonnements
- Génération automatique des échéances de paiement

### Gestion des Paiements
- Enregistrer des paiements
- Modifier et supprimer des paiements
- Afficher les paiements d'un abonnement
- Détection automatique des impayés
- Calculer le montant total payé par abonnement
- Afficher les 5 derniers paiements

### Rapports et Statistiques
- Rapport mensuel des paiements
- Rapport annuel des paiements
- Rapport détaillé des impayés
- Analyse par statut de paiement

## Architecture

L'application suit une architecture en couches:

```
src/main/java/
├── UI/                    # Interface utilisateur
│   ├── Main.java
│   └── Menu.java
├── service/               # Couche métier
│   ├── SubscriptionService.java
│   └── PaymentService.java
├── entity/                # Entités métier
│   ├── subscription/
│   │   ├── Subscription.java (abstraite)
│   │   ├── SubscriptionWithCommitment.java
│   │   └── SubscriptionWithoutCommitment.java
│   └── payment/
│       └── Payment.java
├── dao/                   # Accès aux données
│   ├── SubscriptionDAO.java
│   └── PaymentDAO.java
├── enums/                 # Énumérations
│   ├── SubscriptionStatus.java
│   └── PaymentStatus.java
└── util/                  # Utilitaires
    ├── DatabaseConnection.java
    ├── InputValidator.java
    └── Helpers.java
```

## Base de Données

### Tables
**subscription**: stockage des abonnements
- `id` (VARCHAR(36) PRIMARY KEY)
- `service_name` (VARCHAR(100))
- `monthly_amount` (DECIMAL(10,2))
- `start_date`, `end_date` (DATE)
- `status` (VARCHAR(20))
- `subscription_type` (VARCHAR(20))
- `commitment_duration_months` (INT)

**payment**: stockage des paiements
- `id_payment` (VARCHAR(36) PRIMARY KEY)
- `subscription_id` (VARCHAR(36) FK)
- `due_date`, `payment_date` (DATE)
- `payment_type` (VARCHAR(50))
- `status` (VARCHAR(20))

## Installation

### Prérequis
- Java 8+
- PostgreSQL ou MySQL
- IDE Java (IntelliJ IDEA, Eclipse, VS Code)

### Configuration Base de Données

1. Créer une base de données `subtrack_db`
2. Exécuter le script SQL fourni dans `sql/script.sql`
3. Configurer le fichier `db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/subtrack_db
db.user=your_username
db.password=your_password
```

### Lancement
1. Cloner le projet
2. Configurer la base de données
3. Compiler et exécuter `UI.Main.java`

## Utilisation

L'application propose un menu console interactif:

```
SYSTÈME DE SUIVI DES ABONNEMENTS
MENU PRINCIPAL
1. Gestion des abonnements
2. Gestion des paiements  
3. Rapports et statistiques
4. Détection des impayés
0. Quitter
```

### Exemples d'utilisation

**Créer un abonnement:**
1. Choisir "1. Gestion des abonnements"
2. Sélectionner "1. Créer un abonnement"
3. Renseigner le type (avec/sans engagement)
4. Saisir les informations (nom service, montant, etc.)

**Détecter les impayés:**
1. Choisir "4. Détection des impayés"
2. Le système affiche automatiquement tous les paiements en retard avec détails

## Technologies Utilisées

- **Java 8**: Langage principal
- **JDBC**: Connexion base de données
- **PostgreSQL**: Base de données relationnelle
- **Stream API**: Programmation fonctionnelle
- **Optional**: Gestion des valeurs nulles
- **Lambda**: Expressions fonctionnelles

## Programmation Fonctionnelle

Exemples d'utilisation dans le projet:

```java
// Filtrage et calcul avec Stream API
List<Payment> unpaidPayments = allPayments.stream()
    .filter(p -> p.getStatus() == PaymentStatus.UNPAID || p.getStatus() == PaymentStatus.LATE)
    .collect(Collectors.toList());

// Utilisation d'Optional
Optional<Subscription> optSub = subscriptionDAO.findByID(subscriptionId);
optSub.ifPresent(sub -> {
    // traitement si présent
});

// Groupement avec Collectors
Map<String, List<Payment>> paymentsBySubscription = payments.stream()
    .collect(Collectors.groupingBy(Payment::getSubscription_id));
```

## Auteur

Développé dans le cadre d'un projet académique - Formation Concepteur Développeur d'Applications

## Statut du Projet

Projet terminé et fonctionnel - toutes les exigences du brief sont implémentées et testées.