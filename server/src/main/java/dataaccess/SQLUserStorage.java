package dataaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import server.UserMemoryStorage.User;

public class SQLUserStorage implements UserStorage {

    public SQLUserStorage() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found!", e);
        }
    }

    @Override
    public boolean addUser(String username, String password, String email) throws DataAccessException {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public List<String> getAllTokens() throws DataAccessException {
        List<String> tokens = new ArrayList<>();
        String query = "SELECT token FROM tokens";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tokens.add(rs.getString("token"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load tokens: " + e.getMessage());
        }
        return tokens;
    }

    @Override
    public String getPassword(String username) throws DataAccessException {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("password") : null; // Return null if user not found
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() throws DataAccessException {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userList.add(new User(rs.getString("username"), rs.getString("password"), rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public void clearAllUsers() throws DataAccessException {
        String query = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsernameFromToken(String token) throws DataAccessException {
        String query = "SELECT username FROM tokens WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Token not found
    }

    @Override
    public void addToken(String token, String username) throws DataAccessException {
        String query = "INSERT INTO tokens (token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Token added: " + token + " for username: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
