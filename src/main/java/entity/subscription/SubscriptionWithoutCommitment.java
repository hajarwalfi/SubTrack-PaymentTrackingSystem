package entity.subscription;

import java.time.LocalDate;

public class SubscriptionWithoutCommitment extends Subscription {

    public SubscriptionWithoutCommitment(String service_name, double monthly_amount, LocalDate start_date, LocalDate end_date) {
        super(service_name, monthly_amount, start_date, end_date);
    }

    public SubscriptionWithoutCommitment() {
    }

}