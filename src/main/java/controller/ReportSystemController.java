package controller;

import lombok.Getter;
import lombok.Setter;
import model.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class ReportSystemController {
    private ReportSystem reportSystem;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static ReportSystemController instance;

    private ReportSystemController() {
        reportSystem = ReportSystem.getInstance();
    }

    public static ReportSystemController getInstance() {
        if (instance == null) {
            synchronized (ReportSystemController.class) {
                if (instance == null) {
                    instance = new ReportSystemController();
                }
            }
        }
        return instance;
    }

    public void addAdmin(AdminAccount adminAccount) {
        threadPool.submit(() -> {
            reportSystem.getAdminAccounts().add(adminAccount);
            String sql = DatabaseController.generateInsertStatement("admins", adminAccount.getName(), adminAccount.getEmail(),
                    adminAccount.getUsername(), adminAccount.getPassword());
            DatabaseController.insertRecord(sql);
        });
    }

    public void submitUserReport(UserReport userReport) {
        threadPool.submit(() -> {
            reportSystem.getOpenReports().add(userReport);
            String sql = DatabaseController.generateInsertStatement("user_reports", userReport.getReason(), userReport.getStatus(),
                    userReport.getDateReported(), userReport.getAdminId(), userReport.getReportingUserId(), userReport.getReportedUserId());
            DatabaseController.insertRecord(sql);
        });
    }

    public void submitPostReport(PostReport postReport) {
        threadPool.submit(() -> {
            reportSystem.getOpenReports().add(postReport);
            String sql = DatabaseController.generateInsertStatement("post_reports", postReport.getReason(), postReport.getStatus(),
                    postReport.getDateReported(), postReport.getAdminId(), postReport.getReportingUserId(), postReport.getReportedPostId());
            DatabaseController.insertRecord(sql);
        });
    }

    // TODO: All the admin methods
}
