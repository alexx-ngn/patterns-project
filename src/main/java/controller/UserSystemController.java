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
        threadPool.submit(() -> {
            Post post = userAccount.post(text);
            userSystem.getAllPosts().add(post);
            userAccount.getPosts().add(post);
            String sql = DatabaseController.generateInsertStatement("posts", post.getUserId(), post.getText(),
                    post.getLikedByUserIds().size(), Instant.now().getEpochSecond());
            DatabaseController.insertRecord(sql);
        });
    }

    public void userLikePost(UserAccount userAccount, Post post) {
        // threadPool.submit(() -> { TODO: Prevention of user only liking post once only works without threadPool.submit()
            // Insert likes record
            String insertSql = DatabaseController.generateFullInsertStatement("likes", userAccount.getId(), post.getId());
            DatabaseController.insertRecord(insertSql);

            // Update properties of the post
            post.setLikedByUserIds(DatabaseController.selectAllUserLikesFromPost(post));
            post.like();
            System.out.println("userLikePost, post.likes: " + post.getLikes());
            System.out.println("userLikePost, post.likedbyidsize: " + post.getLikedByUserIds().size());

            // Update numLikes column of post record
            String updateSql = DatabaseController.generateUpdateStatement("posts", "numLikes", post.getLikedByUserIds().size(), "id", post.getId());
            DatabaseController.updateRecord(updateSql);
        // });
    }

    public void userUnlikePost(UserAccount userAccount, Post post) {
        // threadPool.submit(() -> {
            // Insert likes record
            String sql = DatabaseController.generateDeleteStatement("likes", "userId", 123, "postId", 456);
            System.out.println(sql);
            //DatabaseController.deleteRecord(sql);

            // Update properties of the post
            post.setLikedByUserIds(DatabaseController.selectAllUserLikesFromPost(post));
            post.unlike();
            System.out.println("userLikePost, post.likes: " + post.getLikes());
            System.out.println("userLikePost, post.likedbyidsize: " + post.getLikedByUserIds().size());

            // Update numLikes column of post record
            String updateSql = DatabaseController.generateUpdateStatement("posts", "numLikes", post.getLikedByUserIds().size(), "id", post.getId());
            DatabaseController.updateRecord(updateSql);
        // });
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
}
