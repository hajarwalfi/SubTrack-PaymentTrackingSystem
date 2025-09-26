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

public class SubscriptionService {

    private final SubscriptionDAO subscriptionDAO;
    private final PaymentDAO paymentDAO;

    public SubscriptionService(){
        this.subscriptionDAO = new SubscriptionDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public void createSubscription(Subscription subscription) throws SQLException{
        subscriptionDAO.create(subscription);
        generateScheduledPayments(subscription);
        System.out.println("Abonnement crée from service");
    }

    public void generateScheduledPayments(Subscription subscription) throws SQLException{
        LocalDate currentDate= subscription.getStart_date();
        LocalDate endDate = subscription.getEnd_date();

        if (endDate ==null){
            endDate=currentDate.plusYears(1);
        }
        while(!currentDate.isAfter(endDate)){
            Payment payment = new Payment();
            payment.setId_payment(UUID.randomUUID().toString());
            payment.setSubscription_id(subscription.getId());
            payment.setDue_date(currentDate);
            payment.setPayment_type("Mensuel");
            payment.setStatus(PaymentStatus.UNPAID);
            paymentDAO.create(payment);
            currentDate = currentDate.plusMonths(1);
        }
        System.out.println("Échéances générées avec succès");

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
                }
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
        System.out.println("Montant mensuel: " + subscription.getMonthly_amount() + " €");
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

