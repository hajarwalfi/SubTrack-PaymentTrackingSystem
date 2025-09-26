package UI;

import entity.subscription.*;
import entity.payment.Payment;
import enums.SubscriptionStatus;
import enums.PaymentStatus;
import service.SubscriptionService;
import service.PaymentService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Menu {

    private final Scanner scanner;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final DateTimeFormatter dateFormatter;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.subscriptionService = new SubscriptionService();
        this.paymentService = new PaymentService();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void displayMainMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║        MENU PRINCIPAL                  ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Gestion des abonnements");
            System.out.println("2. Gestion des paiements");
            System.out.println("3. Rapports et statistiques");
            System.out.println("4. Détection des impayés");
            System.out.println("0. Quitter");
            System.out.print("\nVotre choix: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

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
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Choix invalide!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide!");
            }
        }
    }

    private void subscriptionMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     GESTION DES ABONNEMENTS            ║");
            System.out.println("╚════════════════════════════════════════╝");
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
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

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
                    case 0:
                        back = true;
                        break;
                    default:
                        System.out.println("Choix invalide!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide!");
            }
        }
    }

    private void createSubscription() {
        try {
            System.out.println("\n--- CRÉER UN ABONNEMENT ---");

            System.out.print("Nom du service: ");
            String serviceName = scanner.nextLine();

            System.out.print("Montant mensuel (€): ");
            double monthlyAmount = Double.parseDouble(scanner.nextLine());

            System.out.print("Date de début (jj/mm/aaaa): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine(), dateFormatter);

            System.out.print("Date de fin (jj/mm/aaaa) [Entrée pour aucune]: ");
            String endDateStr = scanner.nextLine();
            LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr, dateFormatter);

            System.out.print("Type d'abonnement (1=Avec engagement, 2=Sans engagement): ");
            int type = Integer.parseInt(scanner.nextLine());

            Subscription subscription;

            if (type == 1) {
                System.out.print("Durée d'engagement (mois): ");
                int commitmentDuration = Integer.parseInt(scanner.nextLine());
                subscription = new SubscriptionWithCommitment(serviceName, monthlyAmount, startDate, endDate, commitmentDuration);
            } else {
                subscription = new SubscriptionWithoutCommitment(serviceName, monthlyAmount, startDate, endDate);
            }

            subscriptionService.createSubscription(subscription);
            System.out.println("✓ Abonnement créé avec succès!");

        } catch (DateTimeParseException e) {
            System.err.println("Format de date invalide! Utilisez jj/mm/aaaa");
        } catch (NumberFormatException e) {
            System.err.println("Valeur numérique invalide!");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création: " + e.getMessage());
        }
    }

    private void updateSubscription() {
        System.out.print("\nID de l'abonnement à modifier: ");
        String id = scanner.nextLine();

        Optional<Subscription> optSub = subscriptionService.findSubscriptionById(id);

        if (optSub.isPresent()) {
            Subscription subscription = optSub.get();
            subscriptionService.displaySubscription(subscription);

            try {
                System.out.print("Nouveau nom du service [Entrée pour garder]: ");
                String serviceName = scanner.nextLine();
                if (!serviceName.isEmpty()) {
                    subscription.setService_name(serviceName);
                }

                System.out.print("Nouveau montant mensuel [Entrée pour garder]: ");
                String amountStr = scanner.nextLine();
                if (!amountStr.isEmpty()) {
                    subscription.setMonthly_amount(Double.parseDouble(amountStr));
                }

                subscriptionService.updateSubscription(subscription);
                System.out.println("✓ Abonnement modifié avec succès!");

            } catch (NumberFormatException e) {
                System.err.println("Valeur numérique invalide!");
            }
        } else {
            System.out.println("Abonnement non trouvé!");
        }
    }

    private void deleteSubscription() {
        System.out.print("\nID de l'abonnement à supprimer: ");
        String id = scanner.nextLine();

        System.out.print("Êtes-vous sûr? (oui/non): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("oui")) {
            subscriptionService.deleteSubscription(id);
            System.out.println("✓ Abonnement supprimé!");
        } else {
            System.out.println("Suppression annulée");
        }
    }

    private void cancelSubscription() {
        System.out.print("\nID de l'abonnement à résilier: ");
        String id = scanner.nextLine();
        subscriptionService.cancelSubscription(id);
    }

    private void suspendSubscription() {
        System.out.print("\nID de l'abonnement à suspendre: ");
        String id = scanner.nextLine();
        subscriptionService.suspendSubscription(id);
    }

    private void reactivateSubscription() {
        System.out.print("\nID de l'abonnement à réactiver: ");
        String id = scanner.nextLine();
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
        System.out.print("Choix: ");

        int choice = Integer.parseInt(scanner.nextLine());
        String type = choice == 1 ? "with_commitment" : "without_commitment";

        List<Subscription> subscriptions = subscriptionService.findSubscriptionsByType(type);

        if (subscriptions.isEmpty()) {
            System.out.println("\nAucun abonnement trouvé");
        } else {
            subscriptions.forEach(subscriptionService::displaySubscription);
        }
    }

    private void displaySubscriptionPayments() {
        System.out.print("\nID de l'abonnement: ");
        String id = scanner.nextLine();

        Optional<Subscription> optSub = subscriptionService.findSubscriptionById(id);

        if (optSub.isPresent()) {
            Subscription sub = optSub.get();
            System.out.println("\n=== PAIEMENTS DE " + sub.getService_name() + " ===");

            paymentService.displayLastPayments(5);
            paymentService.displayTotalPaidForSubscription(id);
        } else {
            System.out.println("Abonnement non trouvé!");
        }
    }

    private void paymentMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║      GESTION DES PAIEMENTS             ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Enregistrer un paiement");
            System.out.println("2. Modifier un paiement");
            System.out.println("3. Supprimer un paiement");
            System.out.println("4. Afficher les 5 derniers paiements");
            System.out.println("5. Afficher la somme payée d'un abonnement");
            System.out.println("6. Consulter les paiements manqués");
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

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
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide!");
            }
        }
    }

    private void recordPayment() {
        try {
            System.out.println("\n--- ENREGISTRER UN PAIEMENT ---");

            System.out.print("ID du paiement à enregistrer: ");
            String paymentId = scanner.nextLine();

            System.out.print("Date de paiement (jj/mm/aaaa): ");
            LocalDate paymentDate = LocalDate.parse(scanner.nextLine(), dateFormatter);

            paymentService.recordPayment(paymentId, paymentDate);

        } catch (DateTimeParseException e) {
            System.err.println("Format de date invalide!");
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void updatePayment() {
        System.out.println("\nFonctionnalité à implémenter selon besoins");
    }

    private void deletePayment() {
        System.out.print("\nID du paiement à supprimer: ");
        String id = scanner.nextLine();

        System.out.print("Êtes-vous sûr? (oui/non): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("oui")) {
            paymentService.deletePayment(id);
        } else {
            System.out.println("Suppression annulée");
        }
    }

    private void displayTotalPaid() {
        System.out.print("\nID de l'abonnement: ");
        String id = scanner.nextLine();
        paymentService.displayTotalPaidForSubscription(id);
    }

    private void reportsMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    RAPPORTS ET STATISTIQUES            ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Rapport mensuel");
            System.out.println("2. Rapport annuel");
            System.out.println("3. Rapport des impayés");
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

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
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide!");
            }
        }
    }

    private void generateMonthlyReport() {
        try {
            System.out.print("\nAnnée: ");
            int year = Integer.parseInt(scanner.nextLine());

            System.out.print("Mois (1-12): ");
            int month = Integer.parseInt(scanner.nextLine());

            paymentService.generateMonthlyReport(year, month);

        } catch (NumberFormatException e) {
            System.err.println("Valeur numérique invalide!");
        }
    }

    private void generateAnnualReport() {
        try {
            System.out.print("\nAnnée: ");
            int year = Integer.parseInt(scanner.nextLine());

            paymentService.generateAnnualReport(year);

        } catch (NumberFormatException e) {
            System.err.println("Valeur numérique invalide!");
        }
    }
}