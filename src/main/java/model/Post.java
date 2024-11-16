package model;

import lombok.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private int id;
    private String text;
    private Set<Integer> likes;
    private Date datePosted;

    public Post(String text) {
        this.id = 0;
        this.text = text;
        this.likes = new HashSet<>();
        // id, dateposted handled in database
    }
}
