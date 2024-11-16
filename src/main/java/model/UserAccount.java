package model;

import controller.ReportSystem;
import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Getter
@Setter
public class UserAccount extends Account {
    private List<UserAccount> followers;
    private int followerCount;
    private List<Post> posts;

    // Constructor for UserAccount that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public UserAccount(int id, String name, String email, String username, Date creationDate, int followerCount) {
        super(id, name, email, username, creationDate);
        this.followerCount = followerCount;
        this.followers = new ArrayList<>();
        this.posts = new Stack<>();
    }

    public UserAccount(String name, String email, String username) {
        super(name, email, username);
        this.followers = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    @Override
    public void removePost(Post post) {
        posts.remove(post); // TODO: equals method for post
        // TODO: insert query
    }

    /**
     * follows another account and updates its followercount
     * @param account account to be followed
     */
    public void follow(UserAccount account) {
        account.getFollowers().add(this);
        account.setFollowerCount(account.getFollowers().size());
        // TODO: insert query
    }

    /**
     * User creates a post and adds it to its list of posts
     * @param text text of post
     */
    public void post(String text) {
        posts.add(new Post(text));
        // TODO: insert query
    }

    /**
     * this user likes a post
     * @param post post to be liked
     */
    public void like(Post post) {
        post.getLikes().add(this.getId());
        // TODO: insert query
    }

    /**
     * reports an account, and sends it to openreports of reportsystem
     * @param account account reported
     * @param reason report reasoning
     */
    public void reportAccount(UserAccount account, String reason) {
        ReportSystem reportSystem = ReportSystem.getInstance();
        reportSystem.getOpenReports().add(new UserReport(account.getId(), Report.Status.CREATED, reason));
        // TODO: insert query
    }

    /**
     * reports a post, and sends it to openreports of reportsystem
     * @param post post reported
     * @param reason report reasoning
     */
    public void reportPost(Post post, String reason) {
        ReportSystem reportSystem = ReportSystem.getInstance();
        reportSystem.getOpenReports().add(new PostReport(post.getId(), Report.Status.CREATED, reason));
        // TODO: insert query
    }
}
