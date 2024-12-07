package model;

import controller.DatabaseController;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import view.UserInterface;

import java.util.List;

@Getter
@Setter
public class UserSystem {
    private List<UserAccount> userAccounts;

    private static UserSystem instance;
    private static int currentUserId;

    private UserSystem() {
        this.userAccounts = DatabaseController.selectAllUsers();
        currentUserId = -1;
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

    public static boolean authenticateUser(String username, String password) {
        boolean success = false;
        for (UserAccount user : getInstance().userAccounts) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                success = true;
                currentUserId = user.getId();
                break;
            }
        }
        return success;
    }
}
