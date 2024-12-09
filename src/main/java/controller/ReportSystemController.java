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

    /**
     * Singleton instance of the ReportSystemController, ensuring only one instance
     * is created and used throughout the application. Utilizes a thread-safe approach
     * with double-checked locking to initialize and provide access to the instance.
     */
    private static ReportSystemController instance;

    /**
     * Private constructor for the ReportSystemController class.
     * Initializes the instance variable `reportSystem` by obtaining the singleton
     * instance of the ReportSystem. This constructor ensures that the ReportSystemController
     * cannot be instantiated from outside the class, enforcing the use of the singleton pattern.
     */
    private ReportSystemController() {
        reportSystem = ReportSystem.getInstance();
    }

    /**
     * Provides a singleton instance of the ReportSystemController.
     * Implements a thread-safe, double-checked locking mechanism to ensure
     * that only one instance of ReportSystemController is created.
     *
     * @return The singleton instance of ReportSystemController.
     */
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

    /**
     * Retrieves an AdminAccount object based on the provided username.
     *
     * @param username the username of the admin account to be retrieved.
     * @return an AdminAccount object that corresponds to the provided username,
     *         or null if no such account exists.
     */
    public AdminAccount getAdminByUsername(String username) {
        String sql = DatabaseController.generateSelectStatement("admins", "username", username);
        return DatabaseController.selectAdminRecord(sql).getFirst();
    }

    /**
     * Adds a new admin account to the report system and inserts the account information into the database.
     * The operation is executed asynchronously using the thread pool.
     *
     * @param adminAccount the admin account to be added, including necessary credentials like name, email,
     *                     username, and password.
     */
    public void addAdmin(AdminAccount adminAccount) {
        threadPool.submit(() -> {
            reportSystem.getAdminAccounts().add(adminAccount);
            String sql = DatabaseController.generateInsertStatement("admins", adminAccount.getName(), adminAccount.getEmail(),
                    adminAccount.getUsername(), adminAccount.getPassword());
            DatabaseController.insertRecord(sql);
        });
    }

    /**
     * Submits a user report by adding it to the open reports queue and inserting a record into the database.
     *
     * @param userReport the UserReport object to be submitted, containing details such as the reason, status,
     *                   date reported, reporting user ID, and reported user ID.
     */
    public void submitUserReport(UserReport userReport) {
        threadPool.submit(() -> {
            reportSystem.getOpenReports().add(userReport);
            String sql = DatabaseController.generateInsertStatement("user_reports", userReport.getReason(), userReport.getStatus().name(),
                    userReport.getDateReported(), userReport.getReportingUserId(), userReport.getReportedUserId());
            DatabaseController.insertRecord(sql);
        });
    }

    /**
     * Submits a post report by adding it to the open reports queue and inserting it into the database.
     *
     * @param postReport the PostReport object that contains all the necessary information about the post report
     */
    public void submitPostReport(PostReport postReport) {
        threadPool.submit(() -> {
            reportSystem.getOpenReports().add(postReport);
            String sql = DatabaseController.generateInsertStatement("post_reports", postReport.getReason(), postReport.getStatus().name(),
                    postReport.getDateReported(), postReport.getReportingUserId(), postReport.getReportedPostId());
            DatabaseController.insertRecord(sql);
        });
    }

    /**
     * Removes a user report from the system and database in an asynchronous manner.
     *
     * @param adminAccount The admin account performing the removal of the report.
     *                     This account is responsible for removing the report from its assigned reports.
     * @param userReport   The user report that needs to be removed. This report is removed from the
     *                     list of open reports in the system and deleted from the database.
     */
    public void adminRemoveUserReport(AdminAccount adminAccount, UserReport userReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(userReport);
            reportSystem.getOpenReports().remove(userReport);
            String sql = DatabaseController.generateDeleteStatement("user_reports", "id", userReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    /**
     * Removes a post report from the admin's assigned reports and the system's open reports.
     * Additionally, deletes the associated record from the database.
     * The operation is executed asynchronously.
     *
     * @param adminAccount The admin account processing the report removal.
     * @param postReport The post report to be removed.
     */
    public void adminRemovePostReport(AdminAccount adminAccount, PostReport postReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(postReport);
            reportSystem.getOpenReports().remove(postReport);
            String sql = DatabaseController.generateDeleteStatement("users", "id", postReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    /**
     * Changes the status of a given user report by an admin and updates the database accordingly.
     *
     * @param adminAccount The admin account that is making the change to the user report's status.
     * @param userReport The user report whose status is to be changed.
     * @param status The new status to be assigned to the user report.
     */
    public void adminChangeUserReportStatus(AdminAccount adminAccount, UserReport userReport, Report.Status status) {
        threadPool.submit(() -> {
            adminAccount.changeReportStatus(userReport, status);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", status.name(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Allows an admin to change the status of a post report.
     * This method updates the status of the specified post report and persists the change in the database.
     * The update operation is submitted asynchronously to a thread pool.
     *
     * @param adminAccount The admin account that is changing the status of the report.
     * @param postReport The post report for which the status is to be changed.
     * @param status The new status to be assigned to the post report.
     */
    public void adminChangePostReportStatus(AdminAccount adminAccount, PostReport postReport, Report.Status status) {
        threadPool.submit(() -> {
            adminAccount.changeReportStatus(postReport, status);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", status.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Assigns a user report to another admin for further handling.
     *
     * @param assignerAdmin the admin who is assigning the report
     * @param assignedAdmin the admin to whom the report is being assigned
     * @param userReport the user report that is being assigned
     */
    public void adminAssignUserReportTo(AdminAccount assignerAdmin, AdminAccount assignedAdmin, UserReport userReport) {
        threadPool.submit(() -> {
            assignerAdmin.assignReportTo(assignedAdmin, userReport);
            String sql = DatabaseController.generateUpdateStatement("user_reports", "adminId", assignedAdmin.getId(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Assigns a post report to a specified admin account.
     *
     * @param assignerAdmin The admin account that is currently responsible for the report and will perform the reassignment.
     * @param assignedAdmin The admin account to which the post report will be assigned.
     * @param postReport The post report that is being assigned to the specified admin.
     */
    public void adminAssignPostReportTo(AdminAccount assignerAdmin, AdminAccount assignedAdmin, PostReport postReport) {
        threadPool.submit(() -> {
            assignerAdmin.assignReportTo(assignedAdmin, postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "adminId", assignedAdmin.getId(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Closes a given user report by updating its status to CLOSED and removing it from the admin's assigned reports queue.
     * This operation is performed asynchronously.
     *
     * @param adminAccount the admin account responsible for closing the report
     * @param userReport the user report to be closed
     */
    public void adminCloseUserReport(AdminAccount adminAccount, UserReport userReport) {
        threadPool.submit(() -> {
            adminAccount.closeReport(userReport);
            String sql = DatabaseController.generateUpdateStatement("user_reports", "status", Report.Status.CLOSED.name(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Closes the specified post report by the given admin account and updates the report status in the database.
     *
     * @param adminAccount the admin account responsible for closing the report
     * @param postReport the post report to be closed
     */
    public void adminClosePostReport(AdminAccount adminAccount, PostReport postReport) {
        threadPool.submit(() -> {
            adminAccount.closeReport(postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", Report.Status.CLOSED.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }
}
