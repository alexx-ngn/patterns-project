package model;

import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class Post {
    private int id;
    private int userId;
    private String text;
    private int likes;
    private Set<UserAccount> usersLiked;
    private Date datePosted;

    public Post(String text) {
        this.id = 0;
        this.text = text;
        this.likes = 0;
        this.usersLiked = new HashSet<>();
        // id, dateposted handled in database
    }

    public Post(int id, int userId, String text, int likes, Date datePosted) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.likes = likes;
        this.usersLiked = new HashSet<>();
        this.datePosted = datePosted;
    }
}
