package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.*;
import view.LoginInterface;

import java.util.Locale;
import java.util.ResourceBundle;

public class RegisterInterfaceController {
    @FXML
    public Label nameLabel;

    @FXML
    public TextField nameTextField;

    @FXML
    public ComboBox<String> accountComboBox;

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

    private String accountType;

    @FXML
    void handleRegisterButton(ActionEvent event) {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Register");

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || accountType == null) {
            // Show an alert if any field is empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("alertError.title"));
            alert.setHeaderText(bundle.getString("alertError.header"));
            alert.setContentText(bundle.getString("alertError.content"));
            alert.showAndWait();
        } else if (ReportSystem.getInstance().adminAccountExists(username) || UserSystem.getInstance().userAccountExists(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("alertError.title"));
            alert.setHeaderText(bundle.getString("alertError.existsHeader"));
            alert.setContentText(bundle.getString("alertError.existsContent"));
            alert.showAndWait();
        } else {
            // Call the AccountFactory and User/Report-SystemController singleton to handle registration
            Account account = AccountFactory.createAccount(accountType, name, email, username, password);

            // Determine appropriate controller based on account type
            if (account instanceof UserAccount) {
                UserSystemController.getInstance().addUser((UserAccount) account);
            } else if (account instanceof AdminAccount) {
                ReportSystemController.getInstance().addAdmin((AdminAccount) account);
            }

            // Show a success popup
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("alertSuccess.title"));
            alert.setHeaderText(null);
            alert.setContentText(bundle.getString("alertSuccess.content"));
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

    @FXML
    void handleAccountSelector(ActionEvent event) {
        accountType = accountComboBox.getValue();
    }

    private void updateLabels() {
        ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Register");
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
        accountComboBox.getItems().addAll("User", "Admin");
        accountComboBox.setValue(bundle.getString("account.selector")); // Default selection

        locale = LanguageManager.getInstance().getCurrentLocale();
        updateLabels();
    }
}
