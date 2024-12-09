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
    private static int nextId;
    private int userId;
    private String text;
    private int likes;
    private Set<Integer> likedByUserIds;
    private Date datePosted;

    public Post(int userId, String text) {
        nextId = UserSystem.getInstance().getAllPosts().size() + 1;
        this.id = nextId++;
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

    /**
     * Updates the number of likes on the post by setting it to the current size
     * of the set containing user IDs who have liked the post. This method is used
     * to synchronize the like count variable with the actual number of unique
     * users who have expressed a like for the post.
     */
    public void likeUnlike() {
        likes = likedByUserIds.size();
    }
}
