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
    private List<UserAccount> userAccounts;
    private UserAccount currentUser;
    private List<Post> allPosts = DatabaseController.selectAllPosts();

    /**
     * A static instance of the UserSystem class implementing the Singleton pattern.
     * This ensures there is only one instance of UserSystem throughout the application.
     * The instance is initialized lazily and is thread-safe.
     */
    private static UserSystem instance;

    /**
     * Initializes a new instance of the UserSystem class.
     * This constructor is private to enforce the Singleton design pattern.
     * It initializes the userAccounts list by fetching all users from the database
     * and sets the currentUser to null.
     */
    private UserSystem() {
        this.userAccounts = DatabaseController.selectAllUsers();
        currentUser = null;
    }

    /**
     * Returns the singleton instance of the UserSystem class. If the instance
     * does*/
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

    /**
     * Authenticates a user by checking the provided username and password against stored user accounts.
     * If authentication is successful, sets the current user to the authenticated user.
     *
     * @param username the username*/
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

    /**
     * Checks if a user account with the*/
    public boolean userAccountExists(String username) {
        boolean exists = false;
        for (UserAccount user : userAccounts) {
            if (user.getUsername().equals(username)) {
                exists = true;
            }
        }
        return exists;
    }

    /**
     * Retrieves a list of posts created by a specified user account.
     *
     * @param userAccount the user account whose*/
    public List<Post> getPostsByUser(UserAccount userAccount) {
        List<Post> posts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getUserId() == userAccount.getId()) {
                posts.add(post);
            }
        }
        return posts;
    }

    /**
     **/
    public String getUserById(int userId) {
        for (UserAccount user : userAccounts) {
            if (user.getId() == userId) {
                return user.getName();
            }
        }
        return null;
    }
}
