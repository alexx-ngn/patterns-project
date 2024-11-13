package model;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private int id;
    private String text;
    private int likes;
    private Date datePosted;

    public Post(String text) {
        this.id = 0;
        this.text = text;
        this.likes = 0;
        // id, dateposted handled in database
    }
}
