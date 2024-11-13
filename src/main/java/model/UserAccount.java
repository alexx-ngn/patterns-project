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
