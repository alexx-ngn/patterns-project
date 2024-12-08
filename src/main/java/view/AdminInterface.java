package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.ResourceBundle;



public class AdminInterface {
    @Getter
    private static AdminInterface instance;

    private AdminInterface() {}

    public static AdminInterface getInstance() {
        if (instance == null) {
            synchronized (AdminInterface.class) {
                if (instance == null) {
                    instance = new AdminInterface();
                }
            }
        }
        return instance;
    }

    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("lang/Admin");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
