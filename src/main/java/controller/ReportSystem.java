package controller;

import lombok.Getter;
import lombok.Setter;
import model.AdminAccount;
import model.Report;
import model.UserAccount;

import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class ReportSystem {
    private List<UserAccount> userAccounts;
    private List<AdminAccount> adminAccounts;
    private Queue<Report> openReports;
    private Queue<Report> processingReports;
    private List<Report> closedReports;

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
