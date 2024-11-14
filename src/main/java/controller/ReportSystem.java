package controller;

import lombok.Getter;
import lombok.Setter;
import model.AdminAccount;
import model.Report;
import model.UserAccount;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class ReportSystem {
    private static ReportSystem instance;
    private List<UserAccount> userAccounts;
    private List<AdminAccount> adminAccounts;
    private Queue<Report> openReports;
    private Queue<Report> processingReports;
    private List<Report> closedReports;

    private ReportSystem() {
        this.userAccounts = new ArrayList<>();
        this.adminAccounts = new ArrayList<>();
        this.openReports = new LinkedList<>();
        this.processingReports = new LinkedList<>();
        this.closedReports = new LinkedList<>();
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

    public void addUser(UserAccount userAccount) {
        userAccounts.add(userAccount);
    }

    public void addAdmin(AdminAccount adminAccount) {
        adminAccounts.add(adminAccount);
    }

    // TODO: database method
    public void searchReport(String keyword) {

    }
}
