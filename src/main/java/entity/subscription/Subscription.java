package entity.subscription;

import entity.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Subscription {
    protected String id;
    protected String service_name;
    protected double monthly_amount;
    protected LocalDate start_date;
    protected LocalDate end_date;
    protected SubscriptionStatus status;


    public Subscription(){
        this.id = UUID.randomUUID().toString();
    }

    public Subscription(String service_name,double monthly_amount , LocalDate start_date, LocalDate end_date){
        this.id = UUID.randomUUID().toString();
        this.service_name = service_name;
        this.monthly_amount = monthly_amount;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = SubscriptionStatus.ACTIVE;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public double getMonthly_amount() {
        return monthly_amount;
    }

    public void setMonthly_amount(double monthly_amount) {
        this.monthly_amount = monthly_amount;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Abonnement{" +
                "id='" + id + '\'' +
                ", nomService='" + service_name + '\'' +
                ", montantMensuel=" + monthly_amount +
                ", dateDebut=" + start_date +
                ", dateFin=" + end_date +
                ", statut=" + status +
                '}';
    }
}
