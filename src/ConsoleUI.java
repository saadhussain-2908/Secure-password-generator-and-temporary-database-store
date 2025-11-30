package ui;

import db.TemporaryDatabase;
import generator.PasswordGenerator;
import generator.PasswordStrengthEstimator;
import validator.PasswordValidator;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleUI {
    private TemporaryDatabase db;
    private Scanner sc;

    public ConsoleUI() throws SQLException {
        db = new TemporaryDatabase();
        sc = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Secure Password Generator ===");
        boolean running = true;

        while (running) {
            System.out.println("\n1. Generate Password");
            System.out.println("2. View Password History");
            System.out.println("3. Delete Password From History");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1" -> generatePassword();
                    case "2" -> viewPasswordHistory();
                    case "3" -> deletePassword();
                    case "4" -> {
                        running = false;
                        db.close();
                        System.out.println("Session ended. Goodbye!");
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void generatePassword() throws SQLException {
        int length = promptInt("Enter password length (8-32): ", 8, 32);
        boolean useUpper = promptBool("Include uppercase letters? (y/n): ");
        boolean useLower = promptBool("Include lowercase letters? (y/n): ");
        boolean useDigits = promptBool("Include digits? (y/n): ");
        boolean useSpecial = promptBool("Include special characters? (y/n): ");
        boolean excludeAmbiguous = promptBool("Exclude ambiguous characters? (y/n): ");

        PasswordGenerator generator = new PasswordGenerator(length, useUpper, useLower, useDigits, useSpecial, excludeAmbiguous);

        try {
            String password = generator.generate();
            String strength = PasswordStrengthEstimator.estimateStrength(password);

            System.out.println("\nGenerated Password: " + password);
            System.out.println("Strength: " + strength);

            List<String> oldPwds = db.getPasswordHistory().stream()
                    .map(row -> row[1])
                    .collect(Collectors.toList());

            PasswordValidator validator = new PasswordValidator(oldPwds);

            if (validator.isReused(password)) {
                System.out.println("Warning: This password has been previously generated.");
            }

            db.savePassword(password, strength);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewPasswordHistory() throws SQLException {
        List<String[]> history = db.getPasswordHistory();
        if (history.isEmpty()) {
            System.out.println("No password history available.");
            return;
        }
        System.out.println("\n--- Password History ---");
        System.out.printf("%-4s %-25s %-8s %-20s%n", "ID", "Password", "Strength", "Generated At");
        for (String[] row : history) {
            System.out.printf("%-4s %-25s %-8s %-20s%n", row[0], row[1], row[2], row[3]);
        }
    }

    private void deletePassword() throws SQLException {
        int id = promptInt("Enter Password ID to delete: ", 1, Integer.MAX_VALUE);
        db.deletePassword(id);
        System.out.println("Deleted password with ID: " + id);
    }

    private int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) return val;
                else System.out.println("Input must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, enter a number.");
            }
        }
    }

    private boolean promptBool(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            else if (input.equals("n")) return false;
            else System.out.println("Input must be 'y' or 'n'.");
        }
    }
}
