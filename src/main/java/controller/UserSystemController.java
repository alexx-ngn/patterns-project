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
            userSystem.getAllPosts().remove(post);
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
//        threadPool.submit(() -> {
            Post post = userAccount.post(text);
            String sql = DatabaseController.generateInsertStatement("posts", post.getUserId(), post.getText(),
                post.getUsersLiked().size(), Instant.now().getEpochSecond());
            DatabaseController.insertRecord(sql);

            userSystem.getAllPosts().add(post);
            userAccount.getPosts().add(post);

//        });
    }

    public void userLikePost(UserAccount userAccount, Post post) {
        threadPool.submit(() -> {
            // Insert likes record
            String sql = DatabaseController.generateFullInsertStatement("likes", userAccount.getId(), post.getId());
            DatabaseController.insertRecord(sql);

            // TODO: ALSO NEED TO MODIFY LIKES COUNTER

            // Update usersLiked property of the post
            post.setUsersLiked(DatabaseController.selectAllUserLikesFromPost(post));
        });
    }

    public Post getPostById(int postId) {
        String sql = DatabaseController.generateSelectStatement("posts", "id", postId);
        return DatabaseController.selectPostRecord(sql).getFirst();
    }

    public void adminRemovePost(Post post) {
        threadPool.submit(() -> {
            userSystem.getAllPosts().remove(post);
            String sql = DatabaseController.generateDeleteStatement("posts", "id", post.getId());
            DatabaseController.deleteRecord(sql);
        });
    }

    public void adminBanUser(AdminAccount adminAccount, UserAccount userAccount) {
        threadPool.submit(() -> {
            userSystem.getUserAccounts().remove(userAccount);
            String sql = DatabaseController.generateDeleteStatement("post_reports", "id", userAccount.getId());
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
