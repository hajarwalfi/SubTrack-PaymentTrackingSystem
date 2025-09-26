package service;

import dao.PaymentDAO;
import dao.SubscriptionDAO;
import entity.payment.Payment;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionWithCommitment;
import enums.PaymentStatus;
import enums.SubscriptionStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.stream.Collectors;

public class SubscriptionService {
    private final SubscriptionDAO subscriptionDAO;
    private final PaymentDAO paymentDAO;

    public SubscriptionService() {
        this.subscriptionDAO = new SubscriptionDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public void createSubscription(Subscription subscription) throws SQLException {
        subscriptionDAO.create(subscription);
        generateScheduledPayments(subscription);
        System.out.println("\nAbonnement créé avec succès!");
        System.out.println("ID de l'abonnement: " + subscription.getId()+"\n");
    }

    public void generateScheduledPayments(Subscription subscription) throws SQLException {
        LocalDate currentDate = subscription.getStart_date();
        LocalDate endDate = subscription.getEnd_date();
        if (endDate == null) {
            if (subscription instanceof SubscriptionWithCommitment) {
                int durationMonths = ((SubscriptionWithCommitment) subscription).getCommitment_duration_months();
                endDate = currentDate.plusMonths(durationMonths);
            } else {
                endDate = currentDate.plusYears(1);
            }
        }

        int count = 0;
        while (!currentDate.isAfter(endDate)) {
            Payment payment = new Payment();
            payment.setId_payment(UUID.randomUUID().toString());
            payment.setSubscription_id(subscription.getId());
            payment.setDue_date(currentDate);
            payment.setPayment_type("Mensuel");
            payment.setStatus(PaymentStatus.UNPAID);
            paymentDAO.create(payment);
            currentDate = currentDate.plusMonths(1);
            count++;
        }

        System.out.println(count + " échéances de paiement générées");
    }

    public void updateSubscription(Subscription subscription) {
        subscriptionDAO.update(subscription);
    }

    public void deleteSubscription(String id) {
        subscriptionDAO.delete(id);
    }

    public void cancelSubscription(String id) {
        Optional<Subscription> optionalSubscription = subscriptionDAO.findByID(id);

        if (optionalSubscription.isPresent()) {
            Subscription subscription = optionalSubscription.get();

            if (subscription instanceof SubscriptionWithCommitment) {
                SubscriptionWithCommitment subWithCommitment = (SubscriptionWithCommitment) subscription;
                LocalDate commitmentEndDate = subscription.getStart_date()
                        .plusMonths(subWithCommitment.getCommitment_duration_months());

                if (LocalDate.now().isBefore(commitmentEndDate)) {
                    System.out.println("Attention: Résiliation avant la fin de l'engagement!");
                    System.out.println("Date fin engagement: " + commitmentEndDate);
                    System.out.println("Les échéances restent dues jusqu'à la fin de l'engagement");
                }
            } else {
                List<Payment> futurePayments = paymentDAO.findBySubscription(id).stream()
                        .filter(p -> p.getDue_date().isAfter(LocalDate.now()))
                        .filter(p -> p.getStatus() != PaymentStatus.PAID)
                        .collect(Collectors.toList());
                futurePayments.forEach(p -> paymentDAO.delete(p.getId_payment()));
                System.out.println(futurePayments.size() + " paiements futurs supprimés");
            }

            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setEnd_date(LocalDate.now());
            subscriptionDAO.update(subscription);
            System.out.println("Abonnement résilié avec succès");
        } else {
            System.out.println("Abonnement non trouvé");
        }
    }

    public void suspendSubscription(String id) {
        Optional<Subscription> optionalSubscription = subscriptionDAO.findByID(id);

        if (optionalSubscription.isPresent()) {
            Subscription subscription = optionalSubscription.get();
            subscription.setStatus(SubscriptionStatus.SUSPENDED);
            subscriptionDAO.update(subscription);
            System.out.println("Abonnement suspendu");
        } else {
            System.out.println("Abonnement non trouvé");
        }
    }

    public void reactivateSubscription(String id) {
        Optional<Subscription> optionalSubscription = subscriptionDAO.findByID(id);

        if (optionalSubscription.isPresent()) {
            Subscription subscription = optionalSubscription.get();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionDAO.update(subscription);
            System.out.println("Abonnement réactivé");
        } else {
            System.out.println("Abonnement non trouvé");
        }
    }

    public Optional<Subscription> findSubscriptionById(String id) {
        return subscriptionDAO.findByID(id);
    }

    public List<Subscription> findAllSubscriptions() {
        return subscriptionDAO.findAll();
    }

    public List<Subscription> findActiveSubscriptions() {
        return subscriptionDAO.findActiveSubscriptions();
    }

    public List<Subscription> findSubscriptionsByType(String type) {
        return subscriptionDAO.findByType(type);
    }

    public void displaySubscription(Subscription subscription) {
        System.out.println("\n=== Détails de l'abonnement ===");
        System.out.println("ID: " + subscription.getId());
        System.out.println("Service: " + subscription.getService_name());
        System.out.println("Montant mensuel: " + subscription.getMonthly_amount() + " MAD");
        System.out.println("Date début: " + subscription.getStart_date());
        System.out.println("Date fin: " + subscription.getEnd_date());
        System.out.println("Statut: " + subscription.getStatus());

        if (subscription instanceof SubscriptionWithCommitment) {
            SubscriptionWithCommitment subWithCommitment = (SubscriptionWithCommitment) subscription;
            System.out.println("Type: Avec engagement");
            System.out.println("Durée engagement: " + subWithCommitment.getCommitment_duration_months() + " mois");
        } else {
            System.out.println("Type: Sans engagement");
        }
        System.out.println("==============================\n");
    }
    public void extendSubscription(String subscriptionId, int additionalMonths) throws SQLException {
        Optional<Subscription> optSub = subscriptionDAO.findByID(subscriptionId);

        if (!optSub.isPresent()) {
            System.out.println("Abonnement non trouvé");
            return;
        }
        Subscription subscription = optSub.get();
        if (subscription instanceof SubscriptionWithCommitment) {
            System.out.println("⚠️ Impossible de prolonger un abonnement avec engagement");
            System.out.println("Créez un nouvel abonnement à la place");
            return;
        }
        List<Payment> existingPayments = paymentDAO.findBySubscription(subscriptionId);
        LocalDate lastDate = existingPayments.stream()
                .map(Payment::getDue_date)
                .max(LocalDate::compareTo)
                .orElse(subscription.getStart_date());
        int count = 0;
        for (int i = 1; i <= additionalMonths; i++) {
            Payment payment = new Payment();
            payment.setId_payment(UUID.randomUUID().toString());
            payment.setSubscription_id(subscriptionId);
            payment.setDue_date(lastDate.plusMonths(i));
            payment.setPayment_type("Mensuel");
            payment.setStatus(PaymentStatus.UNPAID);
            paymentDAO.create(payment);
            count++;
        }
        System.out.println(count + " nouveaux paiements ajoutés");
    }
}