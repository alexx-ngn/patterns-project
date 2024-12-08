package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.UserAccount;
import view.LoginInterface;

import java.util.Locale;
import java.util.ResourceBundle;

public class RegisterInterfaceController {
    @FXML
    public Label nameLabel;

    @FXML
    public TextField nameTextField;

    @FXML
    private Label YLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailTextField;

    @FXML
    private ComboBox<String> languageComboBox;

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

    private Locale locale;

    @FXML
    void handleRegisterButton(ActionEvent event) {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            // Show an alert if any field is empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill out all fields to register.");
            alert.showAndWait();
        } else {
            // Call the UserSystemController singleton to handle registration
            UserSystemController controller = UserSystemController.getInstance();
            controller.addUser(new UserAccount(name, email, username, password));

            // Show a success popup
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Confirmed");
            alert.setHeaderText(null);
            alert.setContentText("You have successfully registered!");
            alert.showAndWait(); // Wait for user to click "OK"

            // Close the current window and load the login scene
            this.registerPane.getScene().getWindow().hide();
            try {
                LoginInterface.getInstance().start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void handleLoginLink(ActionEvent event) {
        this.registerPane.getScene().getWindow().hide();
        try {
            LoginInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleLanguageSelector(ActionEvent event) {
        String selectedLanguage = languageComboBox.getValue();

        Locale locale = "French".equals(selectedLanguage) ? Locale.of("fr","CA") : Locale.of("en","US");
        LanguageManager.getInstance().setLocale("Login", locale);

        updateLabels();
    }

    private void updateLabels() {
        ResourceBundle bundle = ResourceBundle.getBundle("lang.Register", locale);
        nameLabel.setText(bundle.getString("name.label"));
        usernameLabel.setText(bundle.getString("username.label"));
        emailLabel.setText(bundle.getString("email.label"));
        passwordLabel.setText(bundle.getString("password.label"));
        registerButton.setText(bundle.getString("register.button"));
        loginLink.setText(bundle.getString("login.link"));
    }

    @FXML
    public void initialize() {
        ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Register");
        languageComboBox.getItems().addAll("English", "French");
        languageComboBox.setValue(bundle.getString("language.selector")); // Default selection
        locale = LanguageManager.getInstance().getCurrentLocale();
        updateLabels();
    }
}
