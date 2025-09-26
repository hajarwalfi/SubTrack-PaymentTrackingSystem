package entity.payment;

import enums.PaymentStatus;
import java.time.LocalDate;

public class Payment {
    private String id_payment;
    private String subscription_id;
    private LocalDate due_date;
    private LocalDate payment_date;
    private String payment_type;
    private PaymentStatus status;

    public Payment(String id_payment, String subscription_id, LocalDate due_date, String payment_type) {
        this.id_payment = id_payment;
        this.subscription_id = subscription_id;
        this.due_date = due_date;
        this.payment_type = payment_type;
        this.status = PaymentStatus.UNPAID;
    }

    public Payment() {
    }

    public String getId_payment() {
        return id_payment;
    }

    public void setId_payment(String id_payment) {
        this.id_payment = id_payment;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(LocalDate payment_date) {
        this.payment_date = payment_date;
    }

    public LocalDate getDue_date() {
        return due_date;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "idPaiement='" + id_payment + '\'' +
                ", idAbonnement='" + subscription_id + '\'' +
                ", dateEcheance=" + due_date +
                ", datePaiement=" + payment_date +
                ", typePaiement='" + payment_type + '\'' +
                ", statut=" + status +
                '}';
    }
}
