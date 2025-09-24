package util;

import entity.enums.SubscriptionStatus;
import entity.subscription.Subscription;
import entity.subscription.SubscriptionWithCommitment;
import entity.subscription.SubscriptionWithoutCommitment;

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
        }else {
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
}
