package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Report;
import model.ReportSystem;
import model.UserSystem;
import view.RegisterInterface;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginInterfaceController {
    @FXML
    public Label usernameLabel;

    @FXML
    public Label passwordLabel;

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

    /**
     * Handles the action triggered by pressing the login button.
     * This method attempts to authenticate the user as an admin or a standard user.
     * Upon successful authentication, it navigates to the respective dashboard based on the user type.
     * If authentication fails, an error alert is displayed.
     *
     * @param event the ActionEvent triggered by the login button press
     */
    @FXML
    void handleLoginButton(ActionEvent event) {
        boolean adminLoginSuccessful = ReportSystem.getInstance().authenticateAdmin(usernameTextField.getText(), passwordField.getText());
        boolean userLoginSuccessful = UserSystem.getInstance().authenticateUser(usernameTextField.getText(), passwordField.getText());

        if (adminLoginSuccessful) {
            // Set current admin of application
            ReportSystem.getInstance().setCurrentAdmin(ReportSystemController.getInstance().getAdminByUsername(usernameTextField.getText()));
            Locale currentLocale = LanguageManager.getInstance().getCurrentLocale();

            this.loginPane.getScene().getWindow().hide();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
                loader.setResources(ResourceBundle.getBundle("lang.Admin", currentLocale));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (userLoginSuccessful) {
            // Set current user of application
            UserSystem.getInstance().setCurrentUser(UserSystemController.getInstance().getUserByUsername(usernameTextField.getText()));
            Locale currentLocale = LanguageManager.getInstance().getCurrentLocale();

            this.loginPane.getScene().getWindow().hide();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
                loader.setResources(ResourceBundle.getBundle("lang.User", currentLocale));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Login");
            alert.setTitle(bundle.getString("alert.title"));
            alert.setHeaderText(bundle.getString("alert.header"));
            alert.setContentText(bundle.getString("alert.content"));
            alert.showAndWait();
        }
    }

    /**
     * Handles the action event triggered when the register hyperlink is clicked.
     * This method hides the current login window and opens the registration interface.
     *
     * @param event the action event triggered by clicking the register hyperlink
     */
    @FXML
    void handleRegisterHyperlink(ActionEvent event) {
        this.loginPane.getScene().getWindow().hide();
        try {
            RegisterInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the action event triggered by changing the language selection in the languageComboBox.
     * Updates the application's locale based on the selected language and refreshes the user interface labels.
     *
     * @param event the ActionEvent that triggered this handler
     */
    @FXML
    void handleLanguageSelector(ActionEvent event) {
        String selectedLanguage = languageComboBox.getValue();

        Locale locale = "French".equals(selectedLanguage) ? Locale.of("fr","CA") : Locale.of("en","CA");
        LanguageManager.getInstance().setLocale("Login", locale);

        updateLabels();
    }

    /**
     * Updates the text and prompt text of user interface components with the current locale's resource bundle values.
     *
     * This method fetches the appropriate language resources from a resource bundle
     * specific to the "Login" scene and updates the text for the login button,
     * register hyperlink, username label, password label, and the prompt text for
     * username and password input fields using the retrieved language strings.
     * It synchronizes the UI text components with the localized string values, allowing
     * the interface to be displayed in the correct language.
     */
    private void updateLabels() {
        ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Login");
        loginButton.setText(bundle.getString("login.button"));
        registerHyperlink.setText(bundle.getString("register.link"));
        usernameLabel.setText(bundle.getString("username.label"));
        passwordLabel.setText(bundle.getString("password.label"));
        usernameTextField.setPromptText(bundle.getString("username.label"));
        passwordField.setPromptText(bundle.getString("password.label"));
    }

    /**
     * Initializes the components of the login interface. This method sets up
     * the localization resources and default language selections for the user interface.
     * It populates the language selection combo box with available language options
     * and sets the default selected value. Updates the labels of UI elements based on
     * the localized resource bundle to ensure the interface is displayed in the correct
     * language at startup.
     */
    @FXML
    public void initialize() {
        ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Login");
        languageComboBox.getItems().addAll("English", "French");
        languageComboBox.setValue(bundle.getString("language.selector")); // Default selection
        updateLabels();
    }
}
