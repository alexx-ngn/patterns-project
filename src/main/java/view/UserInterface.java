package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class UserInterface {
    private static UserInterface instance;
    private UserInterface() {}

    public static UserInterface getInstance() {
        if (instance == null) {
            synchronized (UserInterface.class) {
                if (instance == null) {
                    instance = new UserInterface();
                }
            }
        }
        return instance;
    }

    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("lang.User");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
