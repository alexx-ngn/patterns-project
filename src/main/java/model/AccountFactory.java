package model;

public class AccountFactory {
    public static Account createAccount(String type, String name, String email, String username, String password) {
        if (type.equalsIgnoreCase("User")) {
            return new UserAccount(name, email, username, password);
        } else if (type.equalsIgnoreCase("Admin")) {
            return new AdminAccount(name, email, username, password);
        }
        throw new IllegalArgumentException("Unknown user type: " + type);
    }
}