package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view.RegisterInterface;

public class LoginInterfaceController {
    @FXML
    private Label YLabel;

    @FXML
    private ComboBox<?> languageComboBox;

    @FXML
    private Button loginButton;

    @FXML
    private Pane loginPane;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Hyperlink registerHyperlink;


    @FXML
    void handleLoginButton(ActionEvent event) {
        // TODO implement here
    }

    @FXML
    void handleRegisterHyperlink(ActionEvent event) {
        this.loginPane.getScene().getWindow().hide();
        try {
            RegisterInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleLanguageSelector(ActionEvent event) {
        // TODO implement here
    }
}
