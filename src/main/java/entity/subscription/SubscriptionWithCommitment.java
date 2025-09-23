package entity.subscription;

import java.time.LocalDate;

public class SubscriptionWithCommitment extends Subscription {
    private int commitment_duration_months;

    public SubscriptionWithCommitment(String service_name, double monthly_amount, LocalDate start_date, LocalDate end_date, int commitment_duration_months) {
        super(service_name, monthly_amount, start_date, end_date);
        this.commitment_duration_months = commitment_duration_months;
    }

    public SubscriptionWithCommitment() {
    }

    public int getCommitment_duration_months() {
        return commitment_duration_months;
    }

    public void setCommitment_duration_months(int commitment_duration_months) {
        this.commitment_duration_months = commitment_duration_months;
    }

}
