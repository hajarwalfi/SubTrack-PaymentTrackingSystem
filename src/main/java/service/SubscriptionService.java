package service;

import dao.PaymentDAO;
import dao.SubscriptionDAO;
import entity.payment.Payment;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionWithCommitment;
import enums.PaymentStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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

    public Optional<Subscription> findSubscriptionById(String id) {
        return subscriptionDAO.findByID(id);
    }

    public List<Subscription> findAllSubscriptions() {
        return subscriptionDAO.findAll();
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
}