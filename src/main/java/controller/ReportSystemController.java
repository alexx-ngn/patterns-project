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

    public AdminAccount getAdminByUsername(String username) {
        String sql = DatabaseController.generateSelectStatement("admins", "username", username);
        return DatabaseController.selectAdminRecord(sql).getFirst();
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
            reportSystem.getUserReports().add(userReport);
            String sql = DatabaseController.generateInsertStatement("user_reports", userReport.getReason(), userReport.getStatus().name(),
                    userReport.getDateReported(), userReport.getReportingUserId(), userReport.getReportedUserId());
            DatabaseController.insertRecord(sql);
        });
    }

    public void submitPostReport(PostReport postReport) {
        threadPool.submit(() -> {
            reportSystem.getPostReports().add(postReport);
            String sql = DatabaseController.generateInsertStatement("post_reports", postReport.getReason(), postReport.getStatus().name(),
                    postReport.getDateReported(), postReport.getReportingUserId(), postReport.getReportedPostId());
            DatabaseController.insertRecord(sql);
        });
    }

    public void adminRemoveUserReport(AdminAccount adminAccount, UserReport userReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(userReport);
            reportSystem.getUserReports().remove(userReport);
            String sql = DatabaseController.generateDeleteStatement("user_reports", "id", userReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public void adminRemovePostReport(AdminAccount adminAccount, PostReport postReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(postReport);
            reportSystem.getPostReports().remove(postReport);
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

    public void closePostReport(PostReport postReport) {
//        threadPool.submit(() -> {
//            adminAccount.closeReport(postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", Report.Status.CLOSED.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
//        });
    }

    public void closeUserReport(UserReport userReport) {
        String sql = DatabaseController.generateUpdateStatement("user_reports", "status", Report.Status.CLOSED.name(), "id", userReport.getId());
        DatabaseController.updateRecord(sql);
    }

    public void deletePost(int reportedPostId) {
        Post post = UserSystem.getInstance().getPostById(reportedPostId);
        UserSystem.getInstance().getAllPosts().remove(post);
        String sql = DatabaseController.generateDeleteStatement("posts", "id", reportedPostId);
        DatabaseController.deleteRecord(sql);
    }

    public void deleteUser(int reportedUserId) {
        UserAccount user = UserSystem.getInstance().getUserById(reportedUserId);
        UserSystem.getInstance().getUserAccounts().remove(user);
        String sql = DatabaseController.generateDeleteStatement("users", "id", reportedUserId);
        DatabaseController.deleteRecord(sql);

        for (Post post : UserSystem.getInstance().getPostsByUser(user)) {
            UserSystem.getInstance().getAllPosts().remove(post);
            String postSql = DatabaseController.generateDeleteStatement("posts", "id", post.getId());
            DatabaseController.deleteRecord(postSql);
        }
    }
}
