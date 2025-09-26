package service;

import dao.PaymentDAO;
import dao.SubscriptionDAO;
import entity.payment.Payment;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionWithCommitment;
import enums.PaymentStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final SubscriptionDAO subscriptionDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.subscriptionDAO = new SubscriptionDAO();
    }

    public void createPayment(Payment payment) throws SQLException {
        paymentDAO.create(payment);
    }

    public void recordPayment(String paymentId, LocalDate paymentDate) throws SQLException {
        Optional<Payment> optionalPayment = paymentDAO.findByID(paymentId);

        optionalPayment.ifPresent(
                payment -> {
                    payment.setPayment_date(paymentDate);
                    payment.setStatus(enums.PaymentStatus.PAID);
                    paymentDAO.update(payment);
                    System.out.println("Paiement enregistré avec succès");
                }
        );
    }

    public void updatePayment(Payment payment) {
        paymentDAO.update(payment);
    }

    public void deletePayment(String id) {
        paymentDAO.delete(id);
    }

    public void detectOverduePayments() {
        List<Payment> allPayments = paymentDAO.findAll();
        LocalDate today = LocalDate.now();

        List<Payment> overduePayments = allPayments.stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.UNPAID)
                .filter(p -> p.getDue_date().isBefore(today))
                .peek(p -> {
                    p.setStatus(enums.PaymentStatus.LATE);
                    paymentDAO.update(p);
                })
                .collect(Collectors.toList());

        if (overduePayments.isEmpty()) {
            System.out.println("Aucun paiement en retard détecté");
        } else {
            System.out.println(overduePayments.size() + " paiements en retard détectés et mis à jour");
        }
    }

    public List<Payment> getUnpaidPaymentsBySubscription(String subscriptionId) {
        return paymentDAO.findBySubscription(subscriptionId).stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.UNPAID || p.getStatus() == enums.PaymentStatus.LATE)
                .collect(Collectors.toList());
    }

    public void displayUnpaidPaymentsWithTotal() {
        List<Payment> allPayments = paymentDAO.findAll();

        Map<String, List<Payment>> unpaidBySubscription = allPayments.stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.UNPAID || p.getStatus() == enums.PaymentStatus.LATE)
                .collect(Collectors.groupingBy(Payment::getSubscription_id));

        if (unpaidBySubscription.isEmpty()) {
            System.out.println("Aucun paiement impayé");
            return;
        }

        System.out.println("\n=== PAIEMENTS IMPAYÉS ===");

        unpaidBySubscription.forEach((subId, payments) -> {
            Optional<Subscription> optSub = subscriptionDAO.findByID(subId);

            optSub.ifPresent(subscription -> {
                System.out.println("\nAbonnement: " + subscription.getService_name());
                System.out.println("ID: " + subId);

                if (subscription instanceof SubscriptionWithCommitment) {
                    double totalUnpaid = payments.stream()
                            .mapToDouble(p -> subscription.getMonthly_amount())
                            .sum();

                    System.out.println("Nombre de paiements impayés: " + payments.size());
                    System.out.println("Montant total impayé: " + totalUnpaid + " €");

                    payments.forEach(p ->
                            System.out.println("  - Échéance: " + p.getDue_date() +
                                    " | Statut: " + p.getStatus())
                    );
                } else {
                    System.out.println("Type: Sans engagement");
                    payments.forEach(p ->
                            System.out.println("  - Échéance: " + p.getDue_date() +
                                    " | Statut: " + p.getStatus())
                    );
                }
            });
        });

        System.out.println("\n========================\n");
    }

    public double calculateTotalPaidForSubscription(String subscriptionId) {
        Optional<Subscription> optSub = subscriptionDAO.findByID(subscriptionId);

        return optSub.map(subscription ->
                paymentDAO.findBySubscription(subscriptionId).stream()
                        .filter(p -> p.getStatus() == PaymentStatus.PAID)
                        .mapToDouble(p -> subscription.getMonthly_amount())
                        .sum()
        ).orElse(0.0);
    }

    public void displayTotalPaidForSubscription(String subscriptionId) {
        Optional<Subscription> optSub = subscriptionDAO.findByID(subscriptionId);

        optSub.ifPresent(
                subscription -> {
                    double totalPaid = calculateTotalPaidForSubscription(subscriptionId);
                    System.out.println("\n=== TOTAL PAYÉ ===");
                    System.out.println("Abonnement: " + subscription.getService_name());
                    System.out.println("Montant total payé: " + totalPaid + " €");
                    System.out.println("==================\n");
                }
        );
    }

    public void displayLastPayments(int limit) {
        List<Payment> lastPayments = paymentDAO.findLastPayments(limit);

        if (lastPayments.isEmpty()) {
            System.out.println("Aucun paiement trouvé");
            return;
        }

        System.out.println("\n=== " + limit + " DERNIERS PAIEMENTS ===");

        lastPayments.forEach(payment -> {
            Optional<Subscription> optSub = subscriptionDAO.findByID(payment.getSubscription_id());
            optSub.ifPresent(sub ->
                    System.out.println("Service: " + sub.getService_name() +
                            " | Échéance: " + payment.getDue_date() +
                            " | Payé le: " + payment.getPayment_date() +
                            " | Statut: " + payment.getStatus())
            );
        });

        System.out.println("============================\n");
    }

    public void generateMonthlyReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Payment> monthlyPayments = paymentDAO.findAll().stream()
                .filter(p -> !p.getDue_date().isBefore(startDate) && !p.getDue_date().isAfter(endDate))
                .collect(Collectors.toList());

        Map<enums.PaymentStatus, List<Payment>> paymentsByStatus = monthlyPayments.stream()
                .collect(Collectors.groupingBy(Payment::getStatus));

        System.out.println("\n=== RAPPORT MENSUEL " + month + "/" + year + " ===");

        long totalPayments = monthlyPayments.size();
        long paidCount = paymentsByStatus.getOrDefault(enums.PaymentStatus.PAID, Collections.emptyList()).size();
        long unpaidCount = paymentsByStatus.getOrDefault(enums.PaymentStatus.UNPAID, Collections.emptyList()).size();
        long lateCount = paymentsByStatus.getOrDefault(enums.PaymentStatus.LATE, Collections.emptyList()).size();

        System.out.println("Total paiements: " + totalPayments);
        System.out.println("Payés: " + paidCount);
        System.out.println("Non payés: " + unpaidCount);
        System.out.println("En retard: " + lateCount);

        double totalAmount = monthlyPayments.stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.PAID)
                .mapToDouble(p -> {
                    Optional<Subscription> optSub = subscriptionDAO.findByID(p.getSubscription_id());
                    return optSub.map(Subscription::getMonthly_amount).orElse(0.0);
                })
                .sum();

        System.out.println("Montant total encaissé: " + totalAmount + " €");
        System.out.println("===================================\n");
    }

    public void generateAnnualReport(int year) {
        List<Payment> annualPayments = paymentDAO.findAll().stream()
                .filter(p -> p.getDue_date().getYear() == year)
                .collect(Collectors.toList());

        Map<enums.PaymentStatus, Long> countByStatus = annualPayments.stream()
                .collect(Collectors.groupingBy(Payment::getStatus, Collectors.counting()));

        System.out.println("\n=== RAPPORT ANNUEL " + year + " ===");

        System.out.println("Total paiements: " + annualPayments.size());
        countByStatus.forEach((status, count) ->
                System.out.println(status + ": " + count)
        );

        double totalPaid = annualPayments.stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.PAID)
                .mapToDouble(p -> {
                    Optional<Subscription> optSub = subscriptionDAO.findByID(p.getSubscription_id());
                    return optSub.map(Subscription::getMonthly_amount).orElse(0.0);
                })
                .sum();

        System.out.println("Montant total encaissé: " + totalPaid + " €");

        Map<Integer, Double> monthlyRevenue = annualPayments.stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.PAID && p.getPayment_date() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPayment_date().getMonthValue(),
                        Collectors.summingDouble(p -> {
                            Optional<Subscription> optSub = subscriptionDAO.findByID(p.getSubscription_id());
                            return optSub.map(Subscription::getMonthly_amount).orElse(0.0);
                        })
                ));

        System.out.println("\nRépartition mensuelle:");
        monthlyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println("Mois " + entry.getKey() + ": " + entry.getValue() + " €")
                );

        System.out.println("==============================\n");
    }

    public void generateUnpaidReport() {
        List<Payment> allPayments = paymentDAO.findAll();

        List<Payment> unpaidPayments = allPayments.stream()
                .filter(p -> p.getStatus() == enums.PaymentStatus.UNPAID || p.getStatus() == enums.PaymentStatus.LATE)
                .sorted(Comparator.comparing(Payment::getDue_date))
                .collect(Collectors.toList());

        if (unpaidPayments.isEmpty()) {
            System.out.println("Aucun paiement impayé");
            return;
        }

        System.out.println("\n=== RAPPORT DES IMPAYÉS ===");
        System.out.println("Nombre total d'impayés: " + unpaidPayments.size());

        double totalUnpaid = unpaidPayments.stream()
                .mapToDouble(p -> {
                    Optional<Subscription> optSub = subscriptionDAO.findByID(p.getSubscription_id());
                    return optSub.map(Subscription::getMonthly_amount).orElse(0.0);
                })
                .sum();

        System.out.println("Montant total impayé: " + totalUnpaid + " €");

        System.out.println("\nDétail par abonnement:");
        unpaidPayments.stream()
                .collect(Collectors.groupingBy(Payment::getSubscription_id))
                .forEach((subId, payments) -> {
                    Optional<Subscription> optSub = subscriptionDAO.findByID(subId);
                    optSub.ifPresent(sub -> {
                        System.out.println("\n" + sub.getService_name() + ":");
                        payments.forEach(p ->
                                System.out.println("  - Échéance: " + p.getDue_date() +
                                        " | Statut: " + p.getStatus())
                        );
                    });
                });

        System.out.println("\n===========================\n");
    }
}