package model;

import controller.UserSystemController;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class Post {
    private int id;
//    private static int nextId = UserSystemController.getInstance().getLastPostId() == 0 ? 1 : UserSystemController.getInstance().getLastPostId();
    private int userId;
    private String text;
    private int likes;
    private Set<Integer> likedByUserIds;
    private Date datePosted;

    public Post(int userId, String text) {
//        this.id = nextId++;
        this.userId = userId;
        this.text = text;
        this.likes = 0;
        this.likedByUserIds = new HashSet<>();
        this.datePosted = new Date();
        // id, dateposted handled in database
    }

    public Post(int id, int userId, String text, int likes, Date datePosted) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.likes = likes;
        this.likedByUserIds = new HashSet<>();
        this.datePosted = datePosted;
    }

    public void like() {
        likes++;
    }

    public void unlike() {
        likes--;
    }
}
