package model;

import controller.DatabaseController;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportSystem {
    private List<AdminAccount> adminAccounts;
    private List<UserReport> userReports;
    private List<PostReport> postReports;

    /**
     * Represents the currently authenticated admin within the system.
     * This variable is set when an admin successfully logs in through the
     * {@link ReportSystem#authenticateAdmin(String, String)} method.
     * It is utilized to track which admin user is currently managing the system's reports and operations.
     */
    private AdminAccount currentAdmin = null;
    /**
     * The singleton instance of the ReportSystem class.
     * This static field ensures that only one instance of the ReportSystem is created
     * and provides a global access point to it. The instance is initialized using
     * double-checked locking to ensure thread safety.
     */
    private static ReportSystem instance;

    /**
     * Initializes a new instance of the ReportSystem class.
     * This constructor is private to prevent instantiation from outside the class,
     * ensuring the use of the singleton pattern.
     * It initializes the adminAccounts, userReports, and postReports lists
     * with the data retrieved from the database using the DatabaseController.
     */
    private ReportSystem() {
        this.adminAccounts = DatabaseController.selectAllAdmins();
        this.userReports = DatabaseController.selectAllUserReports();
        this.postReports = DatabaseController.selectAllPostReports();
    }

    /**
     * Retrieves the singleton instance of the ReportSystem class.
     * This method ensures that only one instance of ReportSystem is created
     * using a double-checked locking mechanism to maintain thread safety.
     *
     * @return the singleton instance of ReportSystem
     */
    public static ReportSystem getInstance() {
        if (instance == null) {
            synchronized (ReportSystem.class) {
                if (instance == null) {
                    instance = new ReportSystem();
                }
            }
        }
        return instance;
    }

    /**
     * Authenticates an administrator by checking if the provided username and password
     * match any of the existing admin accounts in the system.
     *
     * @param username the username of the admin attempting to log in
     * @param password the password associated with the provided username
     * @return true if the credentials match an existing admin account, false otherwise
     */
    public boolean authenticateAdmin(String username, String password) {
        boolean success = false;
        for (AdminAccount admin : getInstance().adminAccounts) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                success = true;
                currentAdmin = admin;
                break;
            }
        }
        return success;
    }

    /**
     * Checks if an admin account with the specified username exists in the system.
     *
     * @param username the username of the admin account to be checked
     * @return true if an admin account with the given username exists, false otherwise
     */
    public boolean adminAccountExists(String username) {
        boolean exists = false;
        for (AdminAccount admin : adminAccounts) {
            if (admin.getUsername().equals(username)) {
                exists = true;
            }
        }
        return exists;
    }
}
