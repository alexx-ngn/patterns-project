package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.ResourceBundle;

public class LoginInterface {
    private static LoginInterface instance;
    private LoginInterface() {}

    public static LoginInterface getInstance() {
        if (instance == null) {
            synchronized (LoginInterface.class) {
                if (instance == null) {
                    instance = new LoginInterface();
                }
            }
        }
        return instance;
    }

    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("lang/Login");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
