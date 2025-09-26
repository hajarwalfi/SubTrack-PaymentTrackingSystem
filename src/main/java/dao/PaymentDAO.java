package dao;

import entity.payment.Payment;
import util.DatabaseConnection;
import util.Helpers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDAO {
    public void create(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment(id_payment,subscription_id, due_date, payment_date, payment_type, status) VALUES(?,?,?,?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, payment.getId_payment());
            stmt.setString(2, payment.getSubscription_id());
            stmt.setDate(3, Date.valueOf(payment.getDue_date()));
            stmt.setDate(4, payment.getPayment_date() != null ? Date.valueOf(payment.getPayment_date()) : null);
            stmt.setString(5, payment.getPayment_type());
            stmt.setString(6, payment.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("erreur de la creation de paiement");
            throw e;
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    public Optional<Payment> findByID(String id) throws SQLException {
        String sql = "SELECT * From payment WHERE id_payment=?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(Helpers.mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de recherche de paiment");
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return Optional.empty();
    }

    public List<Payment> findBySubscription(String subscriptionId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE subscription_id = ? ORDER BY due_date DESC";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, subscriptionId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(Helpers.mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de récupération des paiements par abonnement: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return payments;
    }

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment ORDER BY due_date DESC";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(Helpers.mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de récupération de tous les paiements: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return payments;
    }

    public void update(Payment payment) {
        String sql = "UPDATE payment SET subscription_id=?, due_date=?, payment_date=?, payment_type=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE id_payment=?";
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, payment.getSubscription_id());
            stmt.setDate(2, Date.valueOf(payment.getDue_date()));
            stmt.setDate(3, payment.getPayment_date() != null ? Date.valueOf(payment.getPayment_date()) : null);
            stmt.setString(4, payment.getPayment_type());
            stmt.setString(5, payment.getStatus().name());
            stmt.setString(6, payment.getId_payment());
            int rowsUpdated = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("erreur de mise à jour du paiement");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM payment WHERE id_payment = ?";
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("aucun paiement trouvé pour le supprimer");
            }
        } catch (SQLException e) {
            System.err.println("erreur de suppression du paiement");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
    }

    public List<Payment> findUnpaidBySubscription(String subscriptionId) {
        List<Payment> unpaidPayments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE subscription_id = ? AND status IN ('UNPAID', 'LATE') ORDER BY due_date";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, subscriptionId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                unpaidPayments.add(Helpers.mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de récupération des paiements impayés: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return unpaidPayments;
    }

    public List<Payment> findLastPayments(int limit) {
        List<Payment> lastPayments = new ArrayList<>();
        String sql = "SELECT * FROM payment ORDER BY payment_date DESC, due_date DESC LIMIT ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            while (rs.next()) {
                lastPayments.add(Helpers.mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("erreur de récupération des derniers paiements: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("erreur de fermeture des ressources: " + e.getMessage());
            }
        }
        return lastPayments;
    }
}