package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AdminInterface {
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("lang/Admin");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
