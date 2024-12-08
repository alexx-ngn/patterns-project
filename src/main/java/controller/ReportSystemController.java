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
            String sql = DatabaseController.generateInsertStatement("user_reports", userReport.getReason(), userReport.getStatus().name(),
                    userReport.getDateReported(), userReport.getAdminId(), userReport.getReportingUserId(), userReport.getReportedUserId());
            DatabaseController.insertRecord(sql);
        });
    }

    public void submitPostReport(PostReport postReport) {
        threadPool.submit(() -> {
            reportSystem.getOpenReports().add(postReport);
            String sql = DatabaseController.generateInsertStatement("post_reports", postReport.getReason(), postReport.getStatus().name(),
                    postReport.getDateReported(), postReport.getAdminId(), postReport.getReportingUserId(), postReport.getReportedPostId());
            DatabaseController.insertRecord(sql);
        });
    }

    public void adminRemoveUserReport(AdminAccount adminAccount, UserReport userReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(userReport);
            reportSystem.getOpenReports().remove(userReport);
            String sql = DatabaseController.generateDeleteStatement("user_reports", "id", userReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public void adminRemovePostReport(AdminAccount adminAccount, PostReport postReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(postReport);
            reportSystem.getOpenReports().remove(postReport);
            String sql = DatabaseController.generateDeleteStatement("users", "id", postReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public void adminChangeUserReportStatus(AdminAccount adminAccount, UserReport userReport, Report.Status status) {
        threadPool.submit(() -> {
            adminAccount.changeReportStatus(userReport, status);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", status.name(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    public void adminChangePostReportStatus(AdminAccount adminAccount, PostReport postReport, Report.Status status) {
        threadPool.submit(() -> {
            adminAccount.changeReportStatus(postReport, status);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", status.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    public void adminAssignUserReportTo(AdminAccount assignerAdmin, AdminAccount assignedAdmin, UserReport userReport) {
        threadPool.submit(() -> {
            assignerAdmin.assignReportTo(assignedAdmin, userReport);
            String sql = DatabaseController.generateUpdateStatement("user_reports", "adminId", assignedAdmin.getId(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    public void adminAssignPostReportTo(AdminAccount assignerAdmin, AdminAccount assignedAdmin, PostReport postReport) {
        threadPool.submit(() -> {
            assignerAdmin.assignReportTo(assignedAdmin, postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "adminId", assignedAdmin.getId(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    public void adminCloseUserReport(AdminAccount adminAccount, UserReport userReport) {
        threadPool.submit(() -> {
            adminAccount.closeReport(userReport);
            String sql = DatabaseController.generateUpdateStatement("user_reports", "status", Report.Status.CLOSED.name(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    public void adminClosePostReport(AdminAccount adminAccount, PostReport postReport) {
        threadPool.submit(() -> {
            adminAccount.closeReport(postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", Report.Status.CLOSED.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }
}
