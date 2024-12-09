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
     * Singleton instance of the ReportSystemController class.
     * This static field ensures that only one instance of the ReportSystemController
     * is created and provides a global access point to it. It follows the singleton
     * pattern for controlling access to report management functionalities.
     */
    private static ReportSystemController instance;

    /**
     * Constructs a new instance of the ReportSystemController class.
     * This constructor is private to implement the singleton pattern for the
     * ReportSystemController. It initializes the report system by retrieving
     * the singleton instance of the ReportSystem.
     */
    private ReportSystemController() {
        reportSystem = ReportSystem.getInstance();
    }

    /**
     * Returns the singleton instance of the ReportSystemController.
     * If the instance does not already exist, it is created and initialized.
     *
     * @return the singleton instance of ReportSystemController
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
     * Retrieves an AdminAccount object associated with the specified username.
     *
     * @param username the username of the admin whose account is to be retrieved
     * @return the AdminAccount object corresponding to the given username
     */
    public AdminAccount getAdminByUsername(String username) {
        String sql = DatabaseController.generateSelectStatement("admins", "username", username);
        return DatabaseController.selectAdminRecord(sql).getFirst();
    }

    /**
     * Adds a new admin account to the system and inserts the corresponding record into the database.
     * This method adds the provided AdminAccount object to the list of admin accounts in the report system
     * and constructs an SQL insert statement to store the admin account details in the database.
     * The entire operation is performed asynchronously using a thread pool.
     *
     * @param adminAccount the AdminAccount object containing details such as name, email, username, and password
     *                      to be added to the report system and persisted in the database.
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
     * Submits a user report for processing. The report is added to the list of current user reports,
     * and an asynchronous process inserts the report details into the database.
     *
     * @param userReport the user report to be submitted, containing details such as the reason for
     *                   reporting, status, report date, reporting user ID, and reported user ID.
     */
    public void submitUserReport(UserReport userReport) {
        threadPool.submit(() -> {
            reportSystem.getUserReports().add(userReport);
            String sql = DatabaseController.generateInsertStatement("user_reports", userReport.getReason(), userReport.getStatus().name(),
                    userReport.getDateReported(), userReport.getReportingUserId(), userReport.getReportedUserId());
            DatabaseController.insertRecord(sql);
        });
    }

    /**
     * Submits a post report for processing by adding it to the report system's list
     * of post reports and inserting the report details into the database.
     *
     * @param postReport the PostReport object containing details of the reported post
     */
    public void submitPostReport(PostReport postReport) {
        threadPool.submit(() -> {
            reportSystem.getPostReports().add(postReport);
            String sql = DatabaseController.generateInsertStatement("post_reports", postReport.getReason(), postReport.getStatus().name(),
                    postReport.getDateReported(), postReport.getReportingUserId(), postReport.getReportedPostId());
            DatabaseController.insertRecord(sql);
        });
    }

    /**
     * Removes a user report from the assigned reports of an administrator
     * and from the system's user reports list, then deletes the report
     * from the database.
     *
     * @param adminAccount the administrator account responsible for removing the report
     * @param userReport the user report to be removed and deleted
     */
    public void adminRemoveUserReport(AdminAccount adminAccount, UserReport userReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(userReport);
            reportSystem.getUserReports().remove(userReport);
            String sql = DatabaseController.generateDeleteStatement("user_reports", "id", userReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    /**
     * Removes a post report from the system and the assigned reports of a given admin.
     * This action deletes the report from the database as well.
     *
     * @param adminAccount the AdminAccount who is removing the post report
     * @param postReport the PostReport to be removed from the system
     */
    public void adminRemovePostReport(AdminAccount adminAccount, PostReport postReport) {
        threadPool.submit(() -> {
            adminAccount.removeReport(postReport);
            reportSystem.getPostReports().remove(postReport);
            String sql = DatabaseController.generateDeleteStatement("users", "id", postReport.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    /**
     * Changes the status of a user report in the system by an admin, and updates the database record.
     *
     * @param adminAccount the admin account performing the status change
     * @param userReport the user report whose status is to be changed
     * @param status the new status to be set for the user report
     */
    public void adminChangeUserReportStatus(AdminAccount adminAccount, UserReport userReport, Report.Status status) {
        threadPool.submit(() -> {
            adminAccount.changeReportStatus(userReport, status);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", status.name(), "id", userReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Changes the status of a given PostReport by an AdminAccount and updates the database accordingly.
     * This action is submitted to a thread pool for asynchronous processing.
     *
     * @param adminAccount The AdminAccount performing the status change.
     * @param postReport   The PostReport whose status is to be changed.
     * @param status       The new status to set for the PostReport.
     */
    public void adminChangePostReportStatus(AdminAccount adminAccount, PostReport postReport, Report.Status status) {
        threadPool.submit(() -> {
            adminAccount.changeReportStatus(postReport, status);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", status.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Assigns a user report from one admin to another and updates the database
     * to reflect the change in assignment.
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
     * Assigns a post report from one admin to another asynchronously.
     *
     * @param assignerAdmin the admin who is assigning the report
     * @param assignedAdmin the admin to whom the report is being assigned
     * @param postReport the post report to be assigned
     */
    public void adminAssignPostReportTo(AdminAccount assignerAdmin, AdminAccount assignedAdmin, PostReport postReport) {
        threadPool.submit(() -> {
            assignerAdmin.assignReportTo(assignedAdmin, postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "adminId", assignedAdmin.getId(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
        });
    }

    /**
     * Closes a user report by an admin. The report is marked as closed,
     * and this status change is updated in the database.
     *
     * @param adminAccount the admin account performing the action
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
     * Closes a given post report by updating its status to 'CLOSED' in the database.
     *
     * @param postReport the PostReport object that needs to be closed. This object contains
     *                   the necessary information such as the report ID which is used to
     *                   identify and update the correct record in the database.
     */
    public void closePostReport(PostReport postReport) {
//        threadPool.submit(() -> {
//            adminAccount.closeReport(postReport);
            String sql = DatabaseController.generateUpdateStatement("post_reports", "status", Report.Status.CLOSED.name(), "id", postReport.getId());
            DatabaseController.updateRecord(sql);
//        });
    }

    /**
     * Closes the specified user report by updating its status to 'CLOSED' in the database.
     *
     * @param userReport the user report to be closed
     */
    public void closeUserReport(UserReport userReport) {
        String sql = DatabaseController.generateUpdateStatement("user_reports", "status", Report.Status.CLOSED.name(), "id", userReport.getId());
        DatabaseController.updateRecord(sql);
    }

    /**
     * Deletes a post from the system and the database.
     *
     * This method retrieves the post associated with the given ID and removes it from
     * the collection of posts maintained by the UserSystem. It also constructs an SQL
     * delete statement to permanently remove the post record from the database.
     *
     * @param reportedPostId the unique identifier of the post to be deleted
     */
    public void deletePost(int reportedPostId) {
        Post post = UserSystem.getInstance().getPostById(reportedPostId);
        UserSystem.getInstance().getAllPosts().remove(post);
        String sql = DatabaseController.generateDeleteStatement("posts", "id", reportedPostId);
        DatabaseController.deleteRecord(sql);
    }

    /**
     * Deletes a user account and all associated posts from the system and the database.
     *
     * @param reportedUserId The unique identifier of the user to be deleted.
     */
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
