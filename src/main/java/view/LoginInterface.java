package view;

import controller.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.Locale;
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
        Locale locale = Locale.of("en","CA");
        ResourceBundle bundle = ResourceBundle.getBundle("lang.Login", locale);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
