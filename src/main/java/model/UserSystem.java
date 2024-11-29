package model;

import controller.DatabaseController;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSystem {
    private List<UserAccount> userAccounts;

    private static UserSystem instance;

    public UserSystem() {
        this.userAccounts = DatabaseController.selectAllUsers();
    }

    public static UserSystem getInstance() {
        if (instance == null) {
            synchronized (UserSystem.class) {
                if (instance == null) {
                    instance = new UserSystem();
                }
            }
        }
        return instance;
    }

    // TODO: validate User credentials
}
