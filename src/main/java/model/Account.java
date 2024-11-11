package model;

import java.sql.Date;

public abstract class Account {
    private int id;
    private String name;
    private String email;
    private String username;
    private Date creationDate;

    public Account(String name, String email, String username) {
        this.name = name;
        this.email = email;
        this.username = username;
        // id and creationDate handled in database
    }

    // TODO
    public void removePost(UserAccount.Post post) {

    }
}
