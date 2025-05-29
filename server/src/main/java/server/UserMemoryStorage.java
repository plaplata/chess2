package server;
import java.util.*;

import dataaccess.DataAccessException;
import dataaccess.UserStorage;

public class UserMemoryStorage implements UserStorage {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> authTokens = new HashMap<>();

    @Override
    public boolean addUser(String username, String password, String email) {
        if (users.containsKey(username) || users.values().stream().anyMatch(u -> u.email.equals(email))) {
            return false;
        }
        users.put(username, new User(username, password, email));
        return true;
    }
    
    @Override
    public String getPassword(String username) {
        User user = users.get(username);
        return user != null ? user.password : null;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clearAllUsers() {
        users.clear();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return authTokens.get(token);
    }
    
    @Override
    public void addToken(String token, String username) {
        authTokens.put(token, username);
        System.out.println("Token added: " + token + " for username: " + username);
    }

    @Override
    public List<String> getAllTokens() throws DataAccessException {
        return new ArrayList<>(authTokens.keySet()); // Return all stored tokens
    }

    public static class User {
        String username;
        String password;
        String email;

        public User(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }
    }
}