package model;

import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Getter
@Setter
public class UserAccount extends Account {
    private int followerCount;
    private List<UserAccount> followers;
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
        this.followerCount = 0;
        this.followers = new ArrayList<>();
        this.posts = new Stack<>();
    }

    // TODO: public void follow(UserAccount account)

    // TODO: public void post(String text)

    // TODO: public void like(Post post)

    // TODO: public void reportAccount(UserAccount account, String reason)

    // TODO: public void reportPost(Post post, String reason)
}
