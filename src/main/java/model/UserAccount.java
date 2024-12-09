package model;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserAccount extends Account {
    private Set<Integer> followerids;
    private int followerCount;
    private List<Post> posts;

    // Constructor for UserAccount that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public UserAccount(int id, String name, String email, String username, String password, int followerCount) {
        super(id, name, email, username, password);
        this.followerCount = followerCount;
        this.followerids = new HashSet<>();
        this.posts = new ArrayList<>();
    }

    public UserAccount(String name, String email, String username, String password) {
        super(name, email, username, password);
        this.followerids = new HashSet<>();
        this.posts = new ArrayList<>();
    }

    /**
     * Removes a specified post from the user's list of posts.
     *
     * @param post the post to be removed from the user's list of posts
     */
    public void removePost(Post post) {
        posts.remove(post);
    }

    /**
     * Updates the follower count of the user account to reflect the current size
     * of the set of follower IDs. This method should be called whenever the
     * list of followers is modified to ensure the follower count remains accurate.
     */
    public void followUnfollow() {
        followerCount = followerids.size();
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
        return new UserReport(account.getId(), Report.Status.OPENED, reason);
    }

    /**
     * reports a post, and sends it to openreports of reportsystem
     * @param post post reported
     * @param reason report reasoning
     */
    public PostReport reportPost(Post post, String reason) {
        return new PostReport(this.getId(), Report.Status.OPENED, reason, post.getId());
    }
}
