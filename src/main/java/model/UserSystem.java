package model;

import controller.DatabaseController;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
     * Singleton instance of the UserSystem class. It ensures that only one instance
     * of UserSystem is created throughout the application. This instance is accessed
     * via the {@link #getInstance()} method which implements the lazy initialization
     * and double-checked locking for ensuring thread safety.
     */
    private static UserSystem instance;

    /**
     * Initializes a new instance of the UserSystem class. This constructor is
     * private to enforce the singleton pattern. It loads all user accounts
     * from the database and sets the current user to null.
     */
    private UserSystem() {
        this.userAccounts = DatabaseController.selectAllUsers();
        currentUser = null;
    }

    /**
     * Retrieves the singleton instance of the UserSystem class. This method ensures that
     * only one instance of UserSystem is created throughout the application. It uses
     * double-checked locking to maintain thread safety during the instance creation.
     *
     * @return the singleton instance of the UserSystem class
     */
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
     * Authenticates a user by checking if the provided username and password match
     * any existing user accounts. If authentication is*/
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
     * Checks if a user account with the specified username exists in the system.
     *
     * @param username the*/
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
     * Retrieves a list of posts authored by the specified user account.
     *
     **/
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
     * Retrieves a UserAccount object that matches the given user ID.
     *
     * @param userId the unique identifier of the user to be retrieved*/
    public UserAccount getUserById(int userId) {
        for (UserAccount user : userAccounts) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves a post by its unique identifier from the list of all posts.
     *
     */
    public Post getPostById(int postId) {
        for (Post post : allPosts) {
            if (post.getId() == postId) {
                return post;
            }
        }
        return null;
    }
}
