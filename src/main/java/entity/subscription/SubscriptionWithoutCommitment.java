package entity.subscription;

import java.time.LocalDate;

public class SubscriptionWithoutCommitment extends Subscription{

    public SubscriptionWithoutCommitment(String service_name, double monthly_amount , LocalDate start_date, LocalDate end_date, int commitment_duration_months){
        super(service_name,monthly_amount, start_date ,end_date);
    }

    public SubscriptionWithoutCommitment() {

    }

    @Override
    public String toString(){
        return "AbonnementSansEngagement{" +
                "id='" + id + '\'' +
                ", nomService='" + service_name + '\'' +
                ", montantMensuel=" + monthly_amount +
                ", dateDebut=" + start_date +
                ", dateFin=" + end_date +
                ", statut=" + status +
                '}';
    }
}
