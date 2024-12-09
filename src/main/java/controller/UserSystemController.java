package controller;

import lombok.Getter;
import model.*;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class UserSystemController {
    private UserSystem userSystem;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static UserSystemController instance;

    private UserSystemController() {
        userSystem = UserSystem.getInstance();
    }

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

    // TODO:

    public void addUser(UserAccount userAccount) {
        // threadPool.submit(() -> {
            userSystem.getUserAccounts().add(userAccount);
            String sql = DatabaseController.generateInsertStatement("users", userAccount.getName(),
                    userAccount.getEmail(), userAccount.getUsername(), userAccount.getPassword(), userAccount.getFollowerCount());
            DatabaseController.insertRecord(sql);
        // });
    }

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

    public void userRemovePost(UserAccount userAccount, Post post) {
        // threadPool.submit(() -> {
            String sql = DatabaseController.generateDeleteStatement("posts", "id", post.getId());
            DatabaseController.deleteRecord(sql);

            userSystem.getAllPosts().remove(post);
            userAccount.removePost(post);

        // });
    }

    public void userFollowUser(UserAccount follower, UserAccount followed) {
        // threadPool.submit(() -> {
        String sql = DatabaseController.generateFullInsertStatement("follows", follower.getId(), followed.getId());
        DatabaseController.insertRecord(sql);

        followed.setFollowerids(DatabaseController.selectAllUserFollowsFromUser(followed));
        followed.followUnfollow();

        String updateSql = DatabaseController.generateUpdateStatement("users", "numFollowers", followed.getFollowerids().size(), "id", followed.getId());
        DatabaseController.updateRecord(updateSql);
        // });
    }

    public void userUnfollowUser(UserAccount unfollower, UserAccount unfollowed) {
        // threadPool.submit(() -> {
        String sql = DatabaseController.generateDeleteStatement("follows", "followerId", unfollower.getId(), "followeeId", unfollowed.getId());
        DatabaseController.deleteRecord(sql);

        unfollowed.setFollowerids(DatabaseController.selectAllUserFollowsFromUser(unfollowed));
        unfollowed.followUnfollow();

        String updateSql = DatabaseController.generateUpdateStatement("users", "numFollowers", unfollowed.getFollowerids().size(), "id", unfollowed.getId());
        DatabaseController.updateRecord(updateSql);
        // });
    }

    public void userLikePost(int userId, Post post) {
        // threadPool.submit(() -> {
        // Insert likes record
        String insertSql = DatabaseController.generateFullInsertStatement("likes", userId, post.getId());
        DatabaseController.insertRecord(insertSql);

        // Update properties of the post
        post.setLikedByUserIds(DatabaseController.selectAllUserLikesFromPost(post));
        post.likeUnlike();

        // Update numLikes column of post record
        String updateSql = DatabaseController.generateUpdateStatement("posts", "numLikes", post.getLikedByUserIds().size(), "id", post.getId());
        DatabaseController.updateRecord(updateSql);
        // });
    }

    public void userUnlikePost(UserAccount userAccount, Post post) {
        // threadPool.submit(() -> {
        // Remove likes record
        String sql = DatabaseController.generateDeleteStatement("likes", "userId", userAccount.getId(), "postId", post.getId());
        DatabaseController.deleteRecord(sql);

        // Update properties of the post
        post.setLikedByUserIds(DatabaseController.selectAllUserLikesFromPost(post));
        post.likeUnlike();

        // Update numLikes column of post record
        String updateSql = DatabaseController.generateUpdateStatement("posts", "numLikes", post.getLikedByUserIds().size(), "id", post.getId());
        DatabaseController.updateRecord(updateSql);
        // });
    }

    public void reportPost(UserAccount reporter, Post post, String reason) {
//        threadPool.submit(() -> {
            PostReport postReport = reporter.reportPost(post, reason);
            String sql = DatabaseController.generateInsertStatement(
                    "post_reports",
                    reason,
                    postReport.getStatus().toString(),
                    Instant.ofEpochMilli(postReport.getDateReported().getTime()).getEpochSecond(),
                    reporter.getId(),
                    post.getId());
            DatabaseController.insertRecord(sql);

            ReportSystem.getInstance().getOpenReports().add(postReport);
//        });

    }

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
        ReportSystem.getInstance().getOpenReports().add(userReport);
    }

    public Post getPostById(int postId) {
        String sql = DatabaseController.generateSelectStatement("posts", "id", postId);
        return DatabaseController.selectPostRecord(sql).getFirst();
    }

    public UserAccount getUserByUsername(String username) {
        String sql = DatabaseController.generateSelectStatement("users", "username", username);
        return DatabaseController.selectUserRecord(sql).getFirst();
    }

    public void adminRemovePost(Post post) {
        threadPool.submit(() -> {
            userSystem.getAllPosts().remove(post);
            String sql = DatabaseController.generateDeleteStatement(
                    "posts",
                    "id", post.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public void adminBanUser(AdminAccount adminAccount, UserAccount userAccount) {
        threadPool.submit(() -> {
            userSystem.getUserAccounts().remove(userAccount);
            String sql = DatabaseController.generateDeleteStatement(
                    "post_reports",
                    "id", userAccount.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public int getLastPostId() {
//        if (userSystem.getAllPosts().isEmpty()) {
////            return 1;
////        }
////        return userSystem.getAllPosts().getLast().getId();
        threadPool.submit(() -> {
            String sql = "SELECT id FROM posts ORDER BY id DESC LIMIT 1";
            return DatabaseController.selectPostRecord(sql).getFirst().getId();
        });
        return 1;
    }
}
