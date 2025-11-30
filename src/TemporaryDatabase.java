package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemporaryDatabase {
    private Connection conn;

    public TemporaryDatabase() throws SQLException {
        connect();
        createTables();
    }

    private void connect() throws SQLException {
        // In-memory SQLite database for temporary session storage
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    private void createTables() throws SQLException {
        String passwordsTable = """
            CREATE TABLE IF NOT EXISTS passwords(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                password TEXT NOT NULL,
                strength TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

        Statement stmt = conn.createStatement();
        stmt.execute(passwordsTable);
        stmt.close();
    }

    public void savePassword(String password, String strength) throws SQLException {
        String insert = "INSERT INTO passwords (password, strength) VALUES (?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
            pstmt.setString(1, password);
            pstmt.setString(2, strength);
            pstmt.executeUpdate();
        }
    }

    public List<String[]> getPasswordHistory() throws SQLException {
        List<String[]> result = new ArrayList<>();
        String query = "SELECT id, password, strength, created_at FROM passwords ORDER BY created_at DESC;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.add(new String[]{
                    Integer.toString(rs.getInt("id")),
                    rs.getString("password"),
                    rs.getString("strength"),
                    rs.getString("created_at")
                });
            }
        }
        return result;
    }

    public void deletePassword(int id) throws SQLException {
        String del = "DELETE FROM passwords WHERE id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(del)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void clearAll() throws SQLException {
        String delAll = "DELETE FROM passwords;";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(delAll);
        }
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
