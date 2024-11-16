package model;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@EqualsAndHashCode
public abstract class Account {
    private int id;
    private String name;
    private String email;
    private String username;
    private Date creationDate;

    // Constructor for Account that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public Account(int id, String name, String email, String username, Date creationDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.creationDate = creationDate;
    }

    public Account(String name, String email, String username) {
        this.name = name;
        this.email = email;
        this.username = username;
        // id and creationDate handled in database
    }

    /**
     * depending on user removing post it differs:
     * regular user: can only remove own posts
     * admin: can remove any post
     * @param post
     */
    public abstract void removePost(Post post);
}
