package model;

import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserAccount extends Account {
    private List<UserAccount> followers;
    private int followerCount;
    private List<Post> posts;

    // Constructor for UserAccount that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public UserAccount(int id, String name, String email, String username, String password, int followerCount) {
        super(id, name, email, username, password);
        this.followerCount = followerCount;
        this.followers = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public UserAccount(String name, String email, String username, String password) {
        super(name, email, username, password);
        this.followers = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    @Override
    public void removePost(Post post) {
        posts.remove(post);
    }

    /**
     * follows another account and updates its followercount
     * @param account account to be followed
     */
    public void follow(UserAccount account) {
        account.getFollowers().add(this);
        account.setFollowerCount(account.getFollowers().size()); // Update follower count of followed account
    }

    /**
     * User creates a post and adds it to its list of posts
     * @param text text of post
     */
    public Post post(String text) {
        Post post = new Post(this.getId(), text);
        posts.add(post);
        return post;
    }

    /**
     * reports an account, and sends it to openreports of reportsystem
     * @param account account reported
     * @param reason report reasoning
     */
    public UserReport reportAccount(UserAccount account, String reason) {
        return new UserReport(account.getId(), Report.Status.CREATED, reason);
    }

    /**
     * reports a post, and sends it to openreports of reportsystem
     * @param post post reported
     * @param reason report reasoning
     */
    public PostReport reportPost(Post post, String reason) {
        return new PostReport(post.getId(), Report.Status.CREATED, reason);
    }
}
