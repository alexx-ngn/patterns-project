package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view.LoginInterface;

public class RegisterInterfaceController {
    @FXML
    private Label YLabel;

    @FXML
    private DatePicker dateBirthDatePicker;

    @FXML
    private Label dateBirthLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailTextField;

    @FXML
    private ComboBox<?> languageComboBox;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Pane registerPane;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    void handleLoginLink(ActionEvent event) {
        this.registerPane.getScene().getWindow().hide();
        try {
            LoginInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
