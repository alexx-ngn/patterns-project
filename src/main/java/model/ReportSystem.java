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
    private Queue<Report> openReports;
    private List<Report> closedReports;

    private static ReportSystem instance;

    private ReportSystem() {
        this.adminAccounts = DatabaseController.selectAllAdmins();
        this.openReports = new LinkedList<>();
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

    // TODO: validate Admin credentials
}
