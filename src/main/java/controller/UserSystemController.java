package controller;

import lombok.Getter;
import model.*;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class UserSystemController {
    private UserSystem userSystem;

    /**
     * The singleton instance of the UserSystemController class.
     *
     * This instance is used to coordinate actions across the user system,
     * such as user management, post interactions, and administrative functions.
     * It ensures that there is only one instance of UserSystemController
     * throughout the application, providing a centralized point of access
     * for managing user-related operations.
     */
    private static UserSystemController instance;

    /**
     * Private constructor for UserSystemController, initializing the userSystem instance.
     * This constructor ensures that the UserSystemController follows the singleton design pattern
     * by being inaccessible from outside the class and using the UserSystem singleton instance.
     * The userSystem is initialized by fetching the single instance of UserSystem.
     */
    private UserSystemController() {
        userSystem = UserSystem.getInstance();
    }

    /**
     * Returns the singleton instance of the UserSystemController. This method
     * implements a double-checked locking mechanism to ensure that only one
     * instance of UserSystemController is created during the application's
     * lifetime. If the instance is null, it initializes it within a synchronized
     * block to ensure thread safety.
     *
     * @return the single instance of UserSystemController.
     */
    public static UserSystemController getInstance() {
        if (instance == null) {
            synchronized (UserSystemController.class) {
                if (instance == null) {
                    instance = new UserSystemController();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a new user account to the system and inserts a corresponding record into the database.
     *
     * @param userAccount the UserAccount object representing the user to be added
     */
    public void addUser(UserAccount userAccount) {
        userSystem.getUserAccounts().add(userAccount);
        String sql = DatabaseController.generateInsertStatement("users", userAccount.getName(),
                userAccount.getEmail(), userAccount.getUsername(), userAccount.getPassword(), userAccount.getFollowerCount());
        DatabaseController.insertRecord(sql);
    }

    /**
     * Allows a user account to create and store a post with the specified text.
     *
     * @param userAccount the user account creating the post
     * @param text the text content of the post
     */
    public void userPost(UserAccount userAccount, String text) {
        // threadPool.submit(() -> {
        Post post = userAccount.post(text);
        String sql = DatabaseController.generateInsertStatement("posts", post.getUserId(), post.getText(),
                post.getLikedByUserIds().size(), Instant.now().getEpochSecond());
        DatabaseController.insertRecord(sql);

        userSystem.getAllPosts().add(post);
        userAccount.getPosts().add(post);
        // });
    }

    /**
     * Removes a post from a user's account and the system's list of posts.
     * It deletes the post record from the database and ensures it's no longer
     * associated with the provided user account.
     *
     * @param userAccount The user account from which the post is to be removed.
     * @param post The post to be removed from the user's account and the system.
     */
    public void userRemovePost(UserAccount userAccount, Post post) {
        String sql = DatabaseController.generateDeleteStatement("posts", "id", post.getId());
        DatabaseController.deleteRecord(sql);

        userSystem.getAllPosts().remove(post);
        userAccount.removePost(post);
    }

    /**
     * Allows one user to follow another user within the application. This method handles
     * the database operations required to establish the follow relationship between the users,
     * updates the list of follower IDs for the followed user, and adjusts the follower count accordingly.
     *
     * @param follower the UserAccount who is initiating the follow action
     * @param followed the UserAccount who is being followed
     */
    public void userFollowUser(UserAccount follower, UserAccount followed) {
        String sql = DatabaseController.generateFullInsertStatement("follows", follower.getId(), followed.getId());
        DatabaseController.insertRecord(sql);

        followed.setFollowerids(DatabaseController.selectAllUserFollowsFromUser(followed));
        followed.followUnfollow();

        String updateSql = DatabaseController.generateUpdateStatement("users", "numFollowers", followed.getFollowerids().size(), "id", followed.getId());
        DatabaseController.updateRecord(updateSql);
    }

    /**
     * Facilitates the action of a user unfollowing another user in the system.
     * Updates the database records and the in-memory user account data to reflect the change.
     *
     * @param unfollower The UserAccount instance representing the user who is initiating the unfollow action.
     * @param unfollowed The UserAccount instance representing the user who is being unfollowed.
     */
    public void userUnfollowUser(UserAccount unfollower, UserAccount unfollowed) {
        String sql = DatabaseController.generateDeleteStatement("follows", "followerId", unfollower.getId(), "followeeId", unfollowed.getId());
        DatabaseController.deleteRecord(sql);

        unfollowed.setFollowerids(DatabaseController.selectAllUserFollowsFromUser(unfollowed));
        unfollowed.followUnfollow();

        String updateSql = DatabaseController.generateUpdateStatement("users", "numFollowers", unfollowed.getFollowerids().size(), "id", unfollowed.getId());
        DatabaseController.updateRecord(updateSql);
    }

    /**
     * Allows a user to like a post. This method will insert a like record into
     * the database, update the post's liked user IDs, adjust the number of likes,
     * and update the post's record in the database.
     *
     * @param userId the ID of the user who is liking the post
     * @param post the Post object representing the post being liked
     */
    public void userLikePost(int userId, Post post) {
        // Insert likes record
        String insertSql = DatabaseController.generateFullInsertStatement("likes", userId, post.getId());
        DatabaseController.insertRecord(insertSql);

        // Update properties of the post
        post.setLikedByUserIds(DatabaseController.selectAllUserLikesFromPost(post));
        post.likeUnlike();

        // Update numLikes column of post record
        String updateSql = DatabaseController.generateUpdateStatement("posts", "numLikes", post.getLikedByUserIds().size(), "id", post.getId());
        DatabaseController.updateRecord(updateSql);
    }

    /**
     * Allows a user to unlike a post. This method removes the like record
     * from the database, updates the post properties by fetching the current
     * list of users who liked the post, and updates the number of likes
     * associated with the post in the database.
     *
     * @param userAccount the user account of the user unliking the post
     * @param post the post that is being unliked
     */
    public void userUnlikePost(UserAccount userAccount, Post post) {
        // Remove likes record
        String sql = DatabaseController.generateDeleteStatement("likes", "userId", userAccount.getId(), "postId", post.getId());
        DatabaseController.deleteRecord(sql);

        // Update properties of the post
        post.setLikedByUserIds(DatabaseController.selectAllUserLikesFromPost(post));
        post.likeUnlike();

        // Update numLikes column of post record
        String updateSql = DatabaseController.generateUpdateStatement("posts", "numLikes", post.getLikedByUserIds().size(), "id", post.getId());
        DatabaseController.updateRecord(updateSql);
    }

    /**
     * Submits a report for a specified post made by a user. The report includes the post in question,
     * the reason for reporting, and the reporter's details. The report is then persisted in the database
     * and added to the open reports pool in the ReportSystem.
     *
     * @param reporter The user account submitting the report.
     * @param post The post being reported.
     * @param reason The reason provided by the reporter for reporting the post.
     */
    public void reportPost(UserAccount reporter, Post post, String reason) {
        PostReport postReport = reporter.reportPost(post, reason);
        String sql = DatabaseController.generateInsertStatement(
                "post_reports",
                reason,
                postReport.getStatus().toString(),
                Instant.ofEpochMilli(postReport.getDateReported().getTime()).getEpochSecond(),
                reporter.getId(),
                post.getId());
        DatabaseController.insertRecord(sql);

        ReportSystem.getInstance().getPostReports().add(postReport);
    }

    /**
     * Reports a user for a specific reason, generating a user report and inserting it into the database.
     * The report is added to the system's list of open reports.
     *
     * @param reporter the user account that is reporting another user
     * @param target the user account being reported
     * @param reportReason the reason given for reporting the user
     */
    public void reportUser(UserAccount reporter, UserAccount target, String reportReason) {
        UserReport userReport = reporter.reportAccount(target, reportReason);
        String sql = DatabaseController.generateInsertStatement(
                "user_reports",
                reportReason,
                userReport.getStatus().toString(),
                Instant.ofEpochMilli(userReport.getDateReported().getTime()).getEpochSecond(),
                reporter.getId(),
                target.getId());
        DatabaseController.insertRecord(sql);

        ReportSystem.getInstance().getUserReports().add(userReport);
    }

    /**
     * Retrieves a UserAccount object based on the provided username.
     *
     * @param username the username of the user whose account information is to be retrieved
     * @return the UserAccount associated with the given username
     */
    public UserAccount getUserByUsername(String username) {
        String sql = DatabaseController.generateSelectStatement("users", "username", username);
        return DatabaseController.selectUserRecord(sql).getFirst();
    }
}
