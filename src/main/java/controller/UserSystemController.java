package controller;

import model.Post;
import model.UserAccount;
import model.UserSystem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserSystemController {
    private UserSystem userSystem;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public UserSystemController() {
        userSystem = UserSystem.getInstance();
    }

    public void addUser(UserAccount userAccount) {
        threadPool.submit(() -> {
            userSystem.getUserAccounts().add(userAccount);
            String sql = DatabaseController.generateInsertStatement("users", userAccount.getName(),
                    userAccount.getEmail(), userAccount.getUsername(), userAccount.getPassword(), userAccount.getFollowerCount());
            DatabaseController.insertRecord(sql);
        });
    }

    public void userRemovePost(UserAccount userAccount, Post post) {
        threadPool.submit(() -> {
            userAccount.removePost(post);
            String sql = DatabaseController.generateDeleteStatement("posts", "id", post.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public void userFollowUser(UserAccount follower, UserAccount followed) {
        threadPool.submit(() -> {
            follower.follow(followed);
            String sql = DatabaseController.generateFullInsertStatement("follows", follower.getId(), followed.getId());
            DatabaseController.insertRecord(sql);
        });
    }

    public void userPost(UserAccount userAccount, String text) {
        threadPool.submit(() -> {
            Post post = userAccount.post(text);
            String sql = DatabaseController.generateInsertStatement("posts", post.getUserId(), post.getText(), post.getUsersLiked());
            DatabaseController.insertRecord(sql);
        });
    }

    public void userLikePost(UserAccount userAccount, Post post) {
        threadPool.submit(() -> {
            // Insert likes record
            String sql = DatabaseController.generateFullInsertStatement("likes", userAccount.getId(), post.getId());
            DatabaseController.insertRecord(sql);

            // Update usersLiked property of the post
            post.setUsersLiked(DatabaseController.selectAllUserLikesFromPost(post));
        });
    }
}
