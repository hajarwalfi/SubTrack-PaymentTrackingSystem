package dao;

import entity.subscription.Subscription;
import entity.subscription.SubscriptionWithCommitment;
import util.DatabaseConnection;
import util.Helpers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubscriptionDAO {
    public void create(Subscription subscription) throws SQLException {
        String sql = "INSERT INTO subscription(id,service_name,monthly_amount,start_date,end_date,status,subscription_type,commitment_duration_months) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, subscription.getId());
            stmt.setString(2, subscription.getService_name());
            stmt.setDouble(3, subscription.getMonthly_amount());
            stmt.setDate(4, Date.valueOf(subscription.getStart_date()));
            stmt.setDate(5, subscription.getEnd_date() != null ? Date.valueOf(subscription.getEnd_date()) : null);
            stmt.setString(6, subscription.getStatus().name());

            if (subscription instanceof SubscriptionWithCommitment) {
                stmt.setString(7, "with_commitment");
                stmt.setInt(8, ((SubscriptionWithCommitment) subscription).getCommitment_duration_months());
            } else {
                stmt.setString(7, "without_commitment");
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("erreur de creation d'babonnement");
            throw e;
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    public Optional<Subscription> findByID(String id) {
        String sql = "SELECT * FROM subscription WHERE id=?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(Helpers.mapResultSetToAbonnement(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de recherche");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    public List<Subscription> findAll() {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscription ORDER BY service_name";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                subscriptions.add(Helpers.mapResultSetToAbonnement(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de recup des abonnements");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return subscriptions;
    }

    public void update(Subscription subscription) {
        String sql = "UPDATE subscription SET service_name=?,monthly_amount=?,start_date=?,end_date=?,status=?,commitment_duration_months=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, subscription.getService_name());
            stmt.setDouble(2, subscription.getMonthly_amount());
            stmt.setDate(3, Date.valueOf(subscription.getStart_date()));
            stmt.setDate(4, subscription.getEnd_date() != null ? Date.valueOf(subscription.getEnd_date()) : null);
            stmt.setString(5, subscription.getStatus().name());
            if (subscription instanceof SubscriptionWithCommitment) {
                stmt.setInt(6, ((SubscriptionWithCommitment) subscription).getCommitment_duration_months());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, subscription.getId());
            stmt.executeUpdate();
            System.out.println("abonnement mis a jour avec succes");
        } catch (SQLException e) {
            System.err.println("erreur de la mise a jour");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM subscription WHERE id=?";
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Abonnement supprimé");
            } else {
                System.out.println("aucun abonnement trouvé pour le supprimer");
            }
        } catch (SQLException e) {
            System.err.println("erreur de la suppression de l'abonnement");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
    }
}