package UI;

import util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SYSTÈME DE SUIVI DES ABONNEMENTS ===");
        System.out.println("Initialisation de l'application...\n");

        if (DatabaseConnection.testConnection()) {
           Menu menu = new Menu();
            menu.displayMainMenu();
        } else {
            System.err.println("Impossible de démarrer l'application - Erreur de connexion à la base de données");
        }

        DatabaseConnection.closeConnection();
        System.out.println("\nApplication terminée. Au revoir!");
    }
}
