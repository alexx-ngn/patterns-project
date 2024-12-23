package model;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
public abstract class Account {
    private int id;
    private String name;
    private String email;
    private String username;
    private String password;

    // Constructor for Account that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public Account(int id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public Account(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        // id and creationDate handled in database
    }
}
