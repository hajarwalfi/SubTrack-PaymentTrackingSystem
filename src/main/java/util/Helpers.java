package util;

import enums.PaymentStatus;
import enums.SubscriptionStatus;
import entity.payment.Payment;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionWithCommitment;
import entity.subscription.SubscriptionWithoutCommitment;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Helpers {

    public static Subscription mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        String type = rs.getString("subscription_type");

        if ("with_commitment".equals(type)) {
            SubscriptionWithCommitment subscription = new SubscriptionWithCommitment();
            subscription.setId(rs.getString("id"));
            subscription.setService_name(rs.getString("service_name"));
            subscription.setMonthly_amount(rs.getDouble("monthly_amount"));
            subscription.setStart_date(rs.getDate("start_date").toLocalDate());
            java.sql.Date end_date = rs.getDate("end_date");
            subscription.setEnd_date(end_date != null ? end_date.toLocalDate() : null);
            subscription.setStatus(SubscriptionStatus.valueOf(rs.getString("status")));
            subscription.setCommitment_duration_months(rs.getInt("commitment_duration_months"));

            return subscription;
        } else {
            SubscriptionWithoutCommitment subscription = new SubscriptionWithoutCommitment();
            subscription.setId(rs.getString("id"));
            subscription.setService_name(rs.getString("service_name"));
            subscription.setMonthly_amount(rs.getDouble("monthly_amount"));
            subscription.setStart_date(rs.getDate("start_date").toLocalDate());
            java.sql.Date end_date = rs.getDate("end_date");
            subscription.setEnd_date(end_date != null ? end_date.toLocalDate() : null);
            subscription.setStatus(SubscriptionStatus.valueOf(rs.getString("status")));

            return subscription;
        }
    }


    public static Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId_payment(rs.getString("id_payment"));
        payment.setSubscription_id(rs.getString("subscription_id"));
        payment.setDue_date(rs.getDate("due_date").toLocalDate());

        Date paymentDate = rs.getDate("payment_date");
        payment.setPayment_date(paymentDate != null ? paymentDate.toLocalDate() : null);

        payment.setPayment_type(rs.getString("payment_type"));
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));

        return payment;
    }
}
