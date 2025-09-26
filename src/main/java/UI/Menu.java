package UI;

import entity.payment.Payment;
import entity.subscription.*;
import enums.PaymentStatus;
import service.SubscriptionService;
import service.PaymentService;
import util.InputValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Menu {
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    public Menu() {
        this.subscriptionService = new SubscriptionService();
        this.paymentService = new PaymentService();
    }

    public void displayMainMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println(" MENU PRINCIPAL ");
            System.out.println("1. Gestion des abonnements");
            System.out.println("2. Gestion des paiements");
            System.out.println("3. Rapports et statistiques");
            System.out.println("4. Détection des impayés");
            System.out.println("0. Quitter");

            int choice = InputValidator.readInt("\nVotre choix");

            switch (choice) {
                case 1:
                    subscriptionMenu();
                    break;
                case 2:
                    paymentMenu();
                    break;
                case 3:
                    reportsMenu();
                    break;
                case 4:
                    paymentService.detectOverduePayments();
                    paymentService.displayMissedPaymentsReport();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void subscriptionMenu() {
        boolean back = false;

        while (!back) {
            System.out.println(" GESTION DES ABONNEMENTS ");
            System.out.println("1. Créer un abonnement");
            System.out.println("2. Modifier un abonnement");
            System.out.println("3. Supprimer un abonnement");
            System.out.println("4. Résilier un abonnement");
            System.out.println("5. Suspendre un abonnement");
            System.out.println("6. Réactiver un abonnement");
            System.out.println("7. Consulter tous les abonnements");
            System.out.println("8. Consulter les abonnements actifs");
            System.out.println("9. Rechercher par type");
            System.out.println("10. Afficher les paiements d'un abonnement");
            System.out.println("11. Prolonger un abonnement (sans engagement uniquement)");
            System.out.println("0. Retour");

            int choice = InputValidator.readInt("\nVotre choix");

            switch (choice) {
                case 1:
                    createSubscription();
                    break;
                case 2:
                    updateSubscription();
                    break;
                case 3:
                    deleteSubscription();
                    break;
                case 4:
                    cancelSubscription();
                    break;
                case 5:
                    suspendSubscription();
                    break;
                case 6:
                    reactivateSubscription();
                    break;
                case 7:
                    displayAllSubscriptions();
                    break;
                case 8:
                    displayActiveSubscriptions();
                    break;
                case 9:
                    searchByType();
                    break;
                case 10:
                    displaySubscriptionPayments();
                    break;
                case 11:
                    extendSubscription();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void createSubscription() {
        try {
            System.out.println("\n--- CRÉER UN ABONNEMENT ---");

            String serviceName = InputValidator.readString("Nom du service");
            double monthlyAmount = InputValidator.readDouble("Montant mensuel (MAD)");
            LocalDate startDate = InputValidator.readDate("Date de début (jj/mm/aaaa)");
            LocalDate endDate = InputValidator.readOptionalDate("Date de fin (jj/mm/aaaa) [Entrée pour aucune]");
            int type = InputValidator.readInt("Type d'abonnement (1=Avec engagement, 2=Sans engagement)");

            Subscription subscription;

            if (type == 1) {
                int commitmentDuration = InputValidator.readInt("Durée d'engagement (mois)");
                subscription = new SubscriptionWithCommitment(serviceName, monthlyAmount, startDate, endDate, commitmentDuration);

                System.out.println("\n=== RÉCAPITULATIF ===");
                System.out.println("Service: " + serviceName);
                System.out.println("Montant mensuel: " + monthlyAmount + " MAD");
                System.out.println("Date début: " + startDate);
                System.out.println("Date fin: " + (endDate != null ? endDate : "Non spécifiée"));
                System.out.println("Type: Avec engagement (" + commitmentDuration + " mois)");
                System.out.println("=====================\n");
            } else {
                subscription = new SubscriptionWithoutCommitment(serviceName, monthlyAmount, startDate, endDate);

                System.out.println("\n=== RÉCAPITULATIF ===");
                System.out.println("Service: " + serviceName);
                System.out.println("Montant mensuel: " + monthlyAmount + " MAD");
                System.out.println("Date début: " + startDate);
                System.out.println("Date fin: " + (endDate != null ? endDate : "Non spécifiée"));
                System.out.println("Type: Sans engagement");
                System.out.println("=====================\n");
            }

            if (InputValidator.readConfirmation("Confirmer la création ? (oui/non): ")) {
                subscriptionService.createSubscription(subscription);
            } else {
                System.out.println("✗ Création annulée");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création: " + e.getMessage());
        }
    }

    private void updateSubscription() {
        String id = InputValidator.readString("\nID de l'abonnement à modifier");
        Optional<Subscription> optSub = subscriptionService.findSubscriptionById(id);

        if (optSub.isPresent()) {
            Subscription subscription = optSub.get();
            subscriptionService.displaySubscription(subscription);

            String serviceName = InputValidator.readString("Nouveau nom du service [Entrée pour garder]");
            String newName = serviceName.isEmpty() ? subscription.getService_name() : serviceName;

            String amountStr = InputValidator.readString("Nouveau montant mensuel [Entrée pour garder]");
            double newAmount = amountStr.isEmpty() ? subscription.getMonthly_amount() : Double.parseDouble(amountStr);

            if (subscription instanceof SubscriptionWithCommitment) {
                System.out.println("\n⚠️ Note : La durée d'engagement ne peut pas être modifiée (contrat fixe)");
            }

            System.out.println("\n=== RÉCAPITULATIF DES MODIFICATIONS ===");
            System.out.println("Ancien nom: " + subscription.getService_name() + " → Nouveau: " + newName);
            System.out.println("Ancien montant: " + subscription.getMonthly_amount() + " → Nouveau: " + newAmount + " MAD");
            System.out.println("=======================================\n");

            if (InputValidator.readConfirmation("Confirmer les modifications ? (oui/non): ")) {
                if (!serviceName.isEmpty()) {
                    subscription.setService_name(serviceName);
                }
                if (!amountStr.isEmpty()) {
                    subscription.setMonthly_amount(Double.parseDouble(amountStr));
                }
                subscriptionService.updateSubscription(subscription);
                System.out.println("✓ Abonnement modifié avec succès!");
            } else {
                System.out.println("✗ Modification annulée");
            }
        } else {
            System.out.println("Abonnement non trouvé!");
        }
    }

    private void deleteSubscription() {
        String id = InputValidator.readString("\nID de l'abonnement à supprimer");

        if (InputValidator.readConfirmation("Êtes-vous sûr? (oui/non): ")) {
            subscriptionService.deleteSubscription(id);
            System.out.println("✓ Abonnement supprimé!");
        } else {
            System.out.println("Suppression annulée");
        }
    }

    private void cancelSubscription() {
        String id = InputValidator.readString("\nID de l'abonnement à résilier");
        subscriptionService.cancelSubscription(id);
    }

    private void suspendSubscription() {
        String id = InputValidator.readString("\nID de l'abonnement à suspendre");
        subscriptionService.suspendSubscription(id);
    }

    private void reactivateSubscription() {
        String id = InputValidator.readString("\nID de l'abonnement à réactiver");
        subscriptionService.reactivateSubscription(id);
    }

    private void displayAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.findAllSubscriptions();

        if (subscriptions.isEmpty()) {
            System.out.println("\nAucun abonnement trouvé");
        } else {
            System.out.println("\n=== LISTE DES ABONNEMENTS ===");
            subscriptions.forEach(subscriptionService::displaySubscription);
        }
    }

    private void displayActiveSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.findActiveSubscriptions();

        if (subscriptions.isEmpty()) {
            System.out.println("\nAucun abonnement actif");
        } else {
            System.out.println("\n=== ABONNEMENTS ACTIFS ===");
            subscriptions.forEach(subscriptionService::displaySubscription);
        }
    }

    private void searchByType() {
        System.out.println("\n1. Avec engagement");
        System.out.println("2. Sans engagement");

        int choice = InputValidator.readInt("Choix");
        String type = choice == 1 ? "with_commitment" : "without_commitment";

        List<Subscription> subscriptions = subscriptionService.findSubscriptionsByType(type);

        if (subscriptions.isEmpty()) {
            System.out.println("\nAucun abonnement trouvé");
        } else {
            subscriptions.forEach(subscriptionService::displaySubscription);
        }
    }

    private void displaySubscriptionPayments() {
        String id = InputValidator.readString("\nID de l'abonnement");
        Optional<Subscription> optSub = subscriptionService.findSubscriptionById(id);

        if (optSub.isPresent()) {
            Subscription sub = optSub.get();
            System.out.println("\n=== PAIEMENTS DE " + sub.getService_name() + " ===");

            paymentService.displayPaymentsBySubscription(id);
            paymentService.displayTotalPaidForSubscription(id);
        } else {
            System.out.println("Abonnement non trouvé!");
        }
    }

    private void extendSubscription() {
        try {
            String id = InputValidator.readString("\nID de l'abonnement à prolonger");
            int months = InputValidator.readInt("Nombre de mois à ajouter");

            subscriptionService.extendSubscription(id, months);

        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }




    private void paymentMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("          GESTION DES PAIEMENTS             ");
            System.out.println("1. Enregistrer un paiement");
            System.out.println("2. Modifier un paiement");
            System.out.println("3. Supprimer un paiement");
            System.out.println("4. Afficher les 5 derniers paiements");
            System.out.println("5. Afficher la somme payée d'un abonnement");
            System.out.println("6. Consulter les paiements manqués");
            System.out.println("0. Retour");

            int choice = InputValidator.readInt("\nVotre choix");

            switch (choice) {
                case 1:
                    recordPayment();
                    break;
                case 2:
                    updatePayment();
                    break;
                case 3:
                    deletePayment();
                    break;
                case 4:
                    paymentService.displayLastPayments(5);
                    break;
                case 5:
                    displayTotalPaid();
                    break;
                case 6:
                    paymentService.displayUnpaidPaymentsWithTotal();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void recordPayment() {
        try {
            System.out.println("\n--- ENREGISTRER UN PAIEMENT ---");
            String paymentId = InputValidator.readString("ID du paiement à enregistrer");

            Optional<Payment> optPayment = paymentService.getPaymentById(paymentId);

            if (!optPayment.isPresent()) {
                System.out.println("Paiement non trouvé avec cet ID");
                return;
            }

            LocalDate paymentDate = InputValidator.readDate("Date de paiement (jj/mm/aaaa)");

            System.out.println("\n=== RÉCAPITULATIF ===");
            System.out.println("ID paiement: " + paymentId);
            System.out.println("Date de paiement: " + paymentDate);
            System.out.println("=====================\n");

            if (InputValidator.readConfirmation("Confirmer l'enregistrement ? (oui/non): ")) {
                paymentService.recordPayment(paymentId, paymentDate);
            } else {
                System.out.println("✗ Enregistrement annulé");
            }

        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void updatePayment() {
        try {
            System.out.println("\n--- MODIFIER UN PAIEMENT ---");
            String paymentId = InputValidator.readString("ID du paiement à modifier");

            Optional<Payment> optPayment = paymentService.getPaymentById(paymentId);

            if (!optPayment.isPresent()) {
                System.out.println("❌ Paiement non trouvé");
                return;
            }

            Payment payment = optPayment.get();

            System.out.println("\n=== Informations actuelles ===");
            System.out.println("Échéance: " + payment.getDue_date());
            System.out.println("Date paiement: " + payment.getPayment_date());
            System.out.println("Statut: " + payment.getStatus());
            System.out.println("===============================\n");


            String dateStr = InputValidator.readString("Nouvelle date de paiement (jj/mm/aaaa) [Entrée pour garder]");
            LocalDate newPaymentDate = payment.getPayment_date();

            if (!dateStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    newPaymentDate = LocalDate.parse(dateStr, formatter);
                } catch (Exception e) {
                    System.err.println("Format invalide, date inchangée");
                }
            }

            System.out.println("\n1.Payé");
            System.out.println("2.Non payé");
            System.out.println("3.En retard");
            System.out.println("0. Garder le statut actuel");
            int statusChoice = InputValidator.readInt("Nouveau statut");

            PaymentStatus newStatus = payment.getStatus();
            switch (statusChoice) {
                case 1: newStatus = PaymentStatus.PAID; break;
                case 2: newStatus = PaymentStatus.UNPAID; break;
                case 3: newStatus = PaymentStatus.LATE; break;
            }

            System.out.println("\n=== RÉCAPITULATIF ===");
            System.out.println("Date paiement: " + payment.getPayment_date() + " → " + newPaymentDate);
            System.out.println("Statut: " + payment.getStatus() + " → " + newStatus);
            System.out.println("=====================\n");

            if (InputValidator.readConfirmation("Confirmer les modifications ? (oui/non): ")) {
                payment.setPayment_date(newPaymentDate);
                payment.setStatus(newStatus);
                paymentService.updatePayment(payment);
                System.out.println("✓ Paiement modifié avec succès!");
            } else {
                System.out.println("✗ Modification annulée");
            }

        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void deletePayment() {
        String id = InputValidator.readString("\nID du paiement à supprimer");

        if (InputValidator.readConfirmation("Êtes-vous sûr? (oui/non): ")) {
            paymentService.deletePayment(id);
        } else {
            System.out.println("Suppression annulée");
        }
    }

    private void displayTotalPaid() {
        String id = InputValidator.readString("\nID de l'abonnement");
        paymentService.displayTotalPaidForSubscription(id);
    }

    private void reportsMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("    RAPPORTS ET STATISTIQUES          ");
            System.out.println("1. Rapport mensuel");
            System.out.println("2. Rapport annuel");
            System.out.println("3. Rapport des impayés");
            System.out.println("0. Retour");

            int choice = InputValidator.readInt("\nVotre choix");

            switch (choice) {
                case 1:
                    generateMonthlyReport();
                    break;
                case 2:
                    generateAnnualReport();
                    break;
                case 3:
                    paymentService.generateUnpaidReport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void generateMonthlyReport() {
        int year = InputValidator.readInt("\nAnnée");
        int month = InputValidator.readInt("Mois (1-12)");
        paymentService.generateMonthlyReport(year, month);
    }

    private void generateAnnualReport() {
        int year = InputValidator.readInt("\nAnnée");
        paymentService.generateAnnualReport(year);
    }
}