package view;

import controller.LanguageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class RegisterInterface {
    private static RegisterInterface instance;

    private RegisterInterface() {}

    public static RegisterInterface getInstance() {
        if (instance == null) {
            synchronized (RegisterInterface.class) {
                if (instance == null) {
                    instance = new RegisterInterface();
                }
            }
        }
        return instance;
    }

    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("lang.Register", LanguageManager.getInstance().getCurrentLocale());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
