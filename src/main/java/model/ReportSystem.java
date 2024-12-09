package model;

import controller.DatabaseController;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class ReportSystem {
    private List<AdminAccount> adminAccounts;
    private List<UserReport> userReports;
    private List<PostReport> postReports;

    @Getter
    private AdminAccount currentAdmin = null;

    private static ReportSystem instance;

    private ReportSystem() {
        this.adminAccounts = DatabaseController.selectAllAdmins();
        this.userReports = DatabaseController.selectAllUserReports();
        this.postReports = DatabaseController.selectAllPostReports();
    }

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
