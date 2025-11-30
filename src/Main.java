import ui.ConsoleUI;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (SQLException e) {
            System.err.println("Failed to start application. Database error: " + e.getMessage());
        }
    }
}
