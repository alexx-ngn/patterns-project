package model;

import controller.DatabaseController;
import javafx.stage.Stage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import view.UserInterface;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class UserSystem {

    @Getter
    private List<UserAccount> userAccounts;

    private static UserSystem instance;
    @Getter
    private UserAccount currentUser;
    @Getter
    private List<Post> allPosts = DatabaseController.selectAllPosts();

    private UserSystem() {
        this.userAccounts = DatabaseController.selectAllUsers();
        currentUser = null;
    }

    public static UserSystem getInstance() {
        if (instance == null) {
            synchronized (UserSystem.class) {
                if (instance == null) {
                    instance = new UserSystem();
                }
            }
        }
        return instance;
    }

    public boolean authenticateUser(String username, String password) {
        boolean success = false;
        for (UserAccount user : getInstance().userAccounts) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                success = true;
                currentUser = user;
                break;
            }
        }
        return success;
    }

    public List<Post> getPostsByUser(UserAccount userAccount) {
        List<Post> posts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getUserId() == userAccount.getId()) {
                posts.add(post);
            }
        }
        return posts;
    }

    public UserAccount getUserById(int userId) {
        for (UserAccount user : userAccounts) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    public Post getPostById(int postId) {
        for (Post post : allPosts) {
            if (post.getId() == postId) {
                return post;
            }
        }
        return null;
    }
}
