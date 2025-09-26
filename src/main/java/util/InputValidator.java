package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Erreur: Veuillez entrer un nombre entier valide");
            }
        }
    }

    public static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message + ": ");
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Erreur: Veuillez entrer un nombre décimal valide");
            }
        }
    }

    public static String readString(String message) {
        System.out.print(message + ": ");
        return scanner.nextLine();
    }

    public static LocalDate readDate(String message) {
        while (true) {
            try {
                System.out.print(message + ": ");
                return LocalDate.parse(scanner.nextLine(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.err.println("Erreur: Format de date invalide. Utilisez jj/mm/aaaa");
            }
        }
    }

    public static LocalDate readOptionalDate(String message) {
        System.out.print(message + ": ");
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(input, dateFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Format invalide, date ignorée");
            return null;
        }
    }

    public static boolean readConfirmation(String message) {
        System.out.print(message);
        String response = scanner.nextLine();
        return response.equalsIgnoreCase("oui") || response.equalsIgnoreCase("o");
    }
}