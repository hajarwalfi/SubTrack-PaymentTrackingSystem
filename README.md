# 🎯 SubTrack - Système de Suivi de Paiements par Abonnement

<div align="center">

![Java](https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-8%2B-brightgreen?style=flat-square)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-Layered-blue?style=flat-square)](#architecture)

</div>

---

## 📋 Description

**SubTrack** est une application console Java sophistiquée conçue pour la **gestion complète des abonnements** avec un système de suivi automatique des paiements, détection intelligente des impayés et génération de rapports financiers détaillés.

L'application utilise les fonctionnalités avancées de **Java 8** (Stream API, Lambda, Optional) et implémente une **architecture en couches robuste** avec persistance JDBC pour offrir une solution professionnelle de gestion d'abonnements.

### 🎯 Pourquoi SubTrack ?

Dans un monde où les abonnements numériques se multiplient (streaming, SaaS, services mobiles), **SubTrack** répond au besoin croissant de :
- 📊 **Visibilité financière** : Comprendre ses coûts d'abonnement
- ⏰ **Gestion des échéances** : Ne plus manquer de paiements
- 📈 **Analyse prédictive** : Anticiper les dépenses futures
- 🚨 **Détection d'anomalies** : Identifier rapidement les impayés

---

## ✨ Fonctionnalités Principales

<table>
<tr>
<td width="50%">

### 🏢 Gestion des Abonnements
- ✅ **Création** avec/sans engagement
- ✅ **Modification** en temps réel
- ✅ **Suspension** temporaire
- ✅ **Résiliation** avec gestion des pénalités
- ✅ **Classification** automatique par type

</td>
<td width="50%">

### 💳 Suivi des Paiements
- ✅ **Génération automatique** des échéances
- ✅ **Enregistrement** manuel ou automatique
- ✅ **Détection intelligente** des impayés
- ✅ **Historique complet** des transactions
- ✅ **Calculs financiers** précis

</td>
</tr>
<tr>
<td width="50%">

### 📊 Rapports & Analytics
- ✅ **Rapports mensuels/annuels** détaillés
- ✅ **Analyse des tendances** de dépenses
- ✅ **Prévisions budgétaires** intelligentes
- ✅ **Export** des données (CSV/JSON)
- ✅ **Tableaux de bord** visuels

</td>
<td width="50%">

### 🎨 Interface Utilisateur
- ✅ **Navigation intuitive** par menus
- ✅ **Validation** robuste des saisies
- ✅ **Messages d'erreur** explicites
- ✅ **Formatage** professionnel des données
- ✅ **Support multi-langue** (FR/EN)

</td>
</tr>
</table>

---

## 🛠️ Stack Technologique

### Core Technologies
- **☕ Java 8** - Langage principal avec fonctionnalités modernes
- **🔗 JDBC** - Connectivité base de données native
- **🐘 PostgreSQL / 🐬 MySQL** - Bases de données relationnelles
- **📁 Maven** - Gestion des dépendances et build

### Paradigmes de Programmation
- **🔧 Programmation Fonctionnelle** - Stream API, Lambda, Optional
- **🏗️ Architecture en Couches** - Séparation claire des responsabilités
- **🎯 DAO Pattern** - Abstraction de l'accès aux données
- **⚡ SOLID Principles** - Code maintenable et extensible

### Outils de Développement
- **🌿 Git** - Contrôle de version avec commits réguliers
- **📝 Jira** - Organisation et suivi des tâches
- **📚 JavaDoc** - Documentation automatique du code

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    🖥️ UI LAYER                          │
│              (Console Interface)                        │
├─────────────────────────────────────────────────────────┤
│                  ⚡ SERVICE LAYER                        │
│         AbonnementService | PaiementService             │
├─────────────────────────────────────────────────────────┤
│                   📦 ENTITY LAYER                        │
│     Abonnement (Abstract) | Paiement | Enums           │
├─────────────────────────────────────────────────────────┤
│                    🗄️ DAO LAYER                          │
│         AbonnementDAO | PaiementDAO (JDBC)              │
├─────────────────────────────────────────────────────────┤
│                  🔧 UTILITY LAYER                        │
│        DateUtils | ValidationUtils | FormatUtils       │
└─────────────────────────────────────────────────────────┘
                               ⬇️
                    🗃️ DATABASE (PostgreSQL/MySQL)
```

### 📁 Structure du Projet

```
src/main/java/
├── 📦 entities/
│   ├── 🏛️ Abonnement.java (Abstract)
│   ├── 💼 AbonnementAvecEngagement.java
│   ├── 🆓 AbonnementSansEngagement.java
│   ├── 💳 Paiement.java
│   └── 📋 enums/
│       ├── StatutAbonnement.java
│       ├── StatutPaiement.java
│       └── TypeAbonnement.java
├── 🗄️ dao/
│   ├── interfaces/
│   │   ├── AbonnementDAO.java
│   │   └── PaiementDAO.java
│   └── impl/
│       ├── AbonnementDAOImpl.java
│       └── PaiementDAOImpl.java
├── ⚡ services/
│   ├── AbonnementService.java
│   ├── PaiementService.java
│   └── RapportService.java
├── 🖥️ ui/
│   ├── ConsoleUI.java
│   ├── MenuPrincipal.java
│   └── ValidationInput.java
├── 🔧 utils/
│   ├── DateUtils.java
│   ├── DatabaseConnection.java
│   ├── FormatUtils.java
│   └── ValidationUtils.java
└── 🚀 Main.java
```

---

## 🚀 Installation et Configuration

### Prérequis

```bash
# Vérifier Java 8+
java -version
# Output attendu: java version "1.8.0_xxx"

# Vérifier Git
git --version
```

### 📥 Clone du Repository

```bash
git clone https://github.com/TON_USERNAME/SubTrack-PaymentTrackingSystem.git
cd SubTrack-PaymentTrackingSystem
```

### 🗃️ Configuration Base de Données

<details>
<summary>🐘 Configuration PostgreSQL</summary>

```sql
-- Créer la base de données
CREATE DATABASE subtrack_db;

-- Se connecter à la base
\c subtrack_db;

-- Créer les tables
CREATE TABLE abonnement (
    id VARCHAR(36) PRIMARY KEY,
    nom_service VARCHAR(100) NOT NULL,
    montant_mensuel DECIMAL(10,2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    statut VARCHAR(20) CHECK (statut IN ('ACTIVE', 'SUSPENDU', 'RESILIE')),
    type_abonnement VARCHAR(30) CHECK (type_abonnement IN ('AVEC_ENGAGEMENT', 'SANS_ENGAGEMENT')),
    duree_engagement_mois INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE paiement (
    id_paiement VARCHAR(36) PRIMARY KEY,
    id_abonnement VARCHAR(36) NOT NULL,
    date_echeance DATE NOT NULL,
    date_paiement DATE,
    montant DECIMAL(10,2) NOT NULL,
    type_paiement VARCHAR(50),
    statut VARCHAR(20) CHECK (statut IN ('PAYE', 'NON_PAYE', 'EN_RETARD')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_abonnement) REFERENCES abonnement(id) ON DELETE CASCADE
);

-- Créer des index pour les performances
CREATE INDEX idx_abonnement_statut ON abonnement(statut);
CREATE INDEX idx_paiement_echeance ON paiement(date_echeance);
CREATE INDEX idx_paiement_abonnement ON paiement(id_abonnement);
```

</details>

<details>
<summary>🐬 Configuration MySQL</summary>

```sql
-- Créer la base de données
CREATE DATABASE subtrack_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE subtrack_db;

-- [Même structure que PostgreSQL avec syntaxe MySQL]
```

</details>

### ⚙️ Configuration Application

Créer le fichier `src/main/resources/database.properties` :

```properties
# PostgreSQL Configuration
db.url=jdbc:postgresql://localhost:5432/subtrack_db
db.username=your_username
db.password=your_password
db.driver=org.postgresql.Driver

# MySQL Configuration (Alternative)
# db.url=jdbc:mysql://localhost:3306/subtrack_db?useSSL=false&serverTimezone=UTC
# db.username=your_username
# db.password=your_password
# db.driver=com.mysql.cj.jdbc.Driver

# Application Configuration
app.name=SubTrack Payment System
app.version=1.0.0
app.locale=fr_FR
```

---

## 🎮 Utilisation

### Démarrage de l'Application

```bash
# Compiler le projet
javac -cp ".:lib/*" src/main/java/**/*.java -d build/

# Exécuter l'application
java -cp "build:lib/*" Main
```

### 🖥️ Interface Console

```
╔══════════════════════════════════════════════════════════╗
║               🎯 SUBTRACK - MENU PRINCIPAL                ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  📋 1. Gestion des Abonnements                           ║
║      ├── Créer un nouvel abonnement                     ║
║      ├── Modifier un abonnement existant                ║
║      ├── Supprimer un abonnement                        ║
║      └── Lister tous les abonnements                    ║
║                                                          ║
║  💳 2. Gestion des Paiements                             ║
║      ├── Enregistrer un paiement                        ║
║      ├── Consulter l'historique                         ║
║      └── Gérer les impayés                              ║
║                                                          ║
║  📊 3. Rapports et Analyses                              ║
║      ├── Rapport mensuel                                ║
║      ├── Rapport annuel                                 ║
║      ├── Analyse des impayés                            ║
║      └── Prévisions budgétaires                         ║
║                                                          ║
║  ⚙️  4. Paramètres                                        ║
║  🚪 5. Quitter                                           ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

---

## 📊 Exemples de Rapports

### 📈 Rapport Mensuel

```
╔═══════════════════════════════════════════════════════════════╗
║                    📊 RAPPORT MENSUEL                         ║
║                      Octobre 2025                            ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  💰 Revenus Totaux: 1,247.50 €                               ║
║  📋 Abonnements Actifs: 12                                   ║
║  ✅ Paiements Réussis: 11 (91.7%)                            ║
║  ⚠️  Paiements en Retard: 1                                   ║
║                                                               ║
║  🏆 Top Abonnements:                                          ║
║   1. Netflix Premium     - 15.99 €                           ║
║   2. Office 365         - 10.00 €                           ║
║   3. Spotify Family     - 9.99 €                            ║
║                                                               ║
║  📊 Évolution:                                               ║
║   ↗️ +5.2% par rapport au mois dernier                        ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
```

---

## 🔧 Concepts Techniques Avancés

### 🌊 Programmation Fonctionnelle

```java
// Exemple: Calcul du revenu mensuel avec Stream API
public double calculerRevenuMensuel(YearMonth mois) {
    return paiements.stream()
        .filter(p -> YearMonth.from(p.getDatePaiement()).equals(mois))
        .filter(p -> p.getStatut() == StatutPaiement.PAYE)
        .mapToDouble(Paiement::getMontant)
        .sum();
}

// Exemple: Détection des impayés avec Optional
public Optional<List<Paiement>> detecterImpayes(String abonnementId) {
    List<Paiement> impayes = paiements.stream()
        .filter(p -> p.getIdAbonnement().equals(abonnementId))
        .filter(p -> p.getDateEcheance().isBefore(LocalDate.now()))
        .filter(p -> p.getStatut() != StatutPaiement.PAYE)
        .collect(Collectors.toList());
    
    return impayes.isEmpty() ? Optional.empty() : Optional.of(impayes);
}
```

---

## 🧪 Tests et Qualité

### 📋 Scénarios de Test

- ✅ **Tests unitaires** pour chaque service
- ✅ **Tests d'intégration** base de données
- ✅ **Tests fonctionnels** interface utilisateur
- ✅ **Tests de performance** avec gros volumes
- ✅ **Tests de robustesse** gestion d'erreurs

### 📊 Métriques de Qualité

- **Couverture de code**: > 80%
- **Complexité cyclomatique**: < 10
- **Respect des conventions**: 100%
- **Documentation**: JavaDoc complet

---

## 📚 Documentation Technique

### 🔗 Liens Utiles

- [📖 Documentation Java 8 Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
- [🗄️ Guide JDBC](https://docs.oracle.com/javase/tutorial/jdbc/)
- [🏗️ Architecture en Couches](https://martinfowler.com/eaaCatalog/layeredArchitecture.html)
- [📝 Conventions Java](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

### 🎓 Compétences Développées

| Compétence | Niveau | Description |
|------------|--------|-------------|
| Java 8 Features | ⭐⭐⭐ | Stream API, Lambda, Optional |
| Architecture Logicielle | ⭐⭐⭐ | Couches, séparation des responsabilités |
| Base de Données | ⭐⭐ | JDBC, requêtes SQL, transactions |
| Programmation Fonctionnelle | ⭐⭐⭐ | Paradigme fonctionnel en Java |
| Gestion de Projet | ⭐⭐ | Git, Jira, documentation |

---

<div align="center">

</div>