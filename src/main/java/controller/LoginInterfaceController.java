package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.UserAccount;
import model.UserSystem;
import view.LoginInterface;
import view.RegisterInterface;
import view.UserInterface;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static model.UserSystem.authenticateUser;

public class LoginInterfaceController {
    @FXML
    private Label YLabel;

    @FXML
    private ComboBox<String> languageComboBox;

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
        // TODO: ADMIN LOGIN
        boolean loginSuccessful = authenticateUser(usernameTextField.getText(), passwordField.getText());

        if (loginSuccessful) {
            Locale currentLocale = LanguageManager.getCurrentLocale();

            this.loginPane.getScene().getWindow().hide();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/UserInterface.fxml"));
                loader.setResources(ResourceBundle.getBundle("messages", currentLocale));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                UserInterfaceController controller = loader.getController();
                controller.setLocale(currentLocale);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Incorrect Information");
            alert.setContentText("Please fill out form again or register.");
            alert.showAndWait();
        }
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
        String selectedLanguage = languageComboBox.getValue();

        Locale locale = "French".equals(selectedLanguage) ? new Locale("fr", "CA") : new Locale("en", "US");
        LanguageManager.setLocale("Login", locale);

        updateLabels();
    }

    private void updateLabels() {
        ResourceBundle bundle = LanguageManager.getResourceBundle("Login");
        loginButton.setText(bundle.getString("login.button"));
        registerHyperlink.setText(bundle.getString("register.link"));
        usernameTextField.setPromptText(bundle.getString("username.label"));
        passwordField.setPromptText(bundle.getString("password.label"));
    }

    @FXML
    public void initialize() {
        ResourceBundle bundle = LanguageManager.getResourceBundle("Login");
        languageComboBox.getItems().addAll("English", "French");
        languageComboBox.setValue(bundle.getString("language.selector")); // Default selection
        updateLabels();
    }
}
