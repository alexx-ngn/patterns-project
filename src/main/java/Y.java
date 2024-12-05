import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginInterface;

public class Y extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // TODO implement here
        LoginInterface.getInstance().start(stage);
    }
}
