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

    /**
     * Represents the selected locale for the user interface within the
     * RegisterInterfaceController class. This Locale object is used to manage
     * the language and regional settings, impacting how information such as
     * messages, labels, and other localizable content are displayed according
     * to the user's language preferences.
     *
     * The Locale is typically set based on user selection from a language
     * dropdown (languageComboBox) and is used to adjust the application's
     * resource bundle accordingly through methods in the LanguageManager class.
     */
    private Locale locale;

    /**
     * Represents the type of account selected or used in the registration process.
     * This variable is intended to store a string identifying the category of account,
     * such as "Admin", "User", or other predefined account types.
     */
    private String accountType;

    /**
     * Handles the registration process when the register button is pressed.
     * Performs validation on input fields, checks for the existence of the username,
     * and registers a new account if validations pass. Displays alert dialogs
     * for error or success notifications based on the registration outcome.
     *
     * @param event the ActionEvent triggered by pressing the register button
     */
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

    /**
     * Handles the event triggered by clicking the login link.
     * This method hides the current registration pane and opens the login interface.
     *
     * @param event the action event triggered by clicking the login link
     */
    @FXML
    void handleLoginLink(ActionEvent event) {
        this.registerPane.getScene().getWindow().hide();
        try {
            LoginInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the action event triggered by the language selector component.
     * This method updates the application's locale based on the selected language
     * from the languageComboBox and refreshes the UI labels accordingly.
     *
     * @param event the ActionEvent object representing the event that invoked this method, typically generated when the user selects a language from the languageComboBox
     */
    @FXML
    void handleLanguageSelector(ActionEvent event) {
        String selectedLanguage = languageComboBox.getValue();

        Locale locale = "French".equals(selectedLanguage) ? Locale.of("fr","CA") : Locale.of("en","US");
        LanguageManager.getInstance().setLocale("Login", locale);

        updateLabels();
    }

    /**
     * Handles the selection of an account type from the accountComboBox.
     * This method is triggered when an action event occurs on the accountComboBox,
     * and it updates the accountType field with the selected value.
     *
     * @param event the action event triggered when an item is selected from the accountComboBox
     */
    @FXML
    void handleAccountSelector(ActionEvent event) {
        accountType = accountComboBox.getValue();
    }

    /**
     * Updates the text of various UI labels and buttons using strings from a
     * resource bundle associated with the "Register" scene.
     * The method retrieves the current locale-specific resource bundle through
     * the LanguageManager singleton instance. It sets the text for name, username,
     * email, and password labels, as well as the register button and login link,
     * based on the localized strings obtained from the resource bundle.
     */
    private void updateLabels() {
        ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Register");
        nameLabel.setText(bundle.getString("name.label"));
        usernameLabel.setText(bundle.getString("username.label"));
        emailLabel.setText(bundle.getString("email.label"));
        passwordLabel.setText(bundle.getString("password.label"));
        registerButton.setText(bundle.getString("register.button"));
        loginLink.setText(bundle.getString("login.link"));
    }

    /**
     * Initializes the RegisterInterfaceController with default settings and localization.
     * This method is automatically invoked when the associated FXML file is loaded.
     *
     * The method performs the following actions:
     * - Retrieves a resource bundle for the "Register" scene.
     * - Populates the languageComboBox with available language options and sets a default value
     *   based on the current localization settings.
     * - Populates the accountComboBox with account type options and sets a default value.
     * - Fetches the current locale from the LanguageManager and updates labels on the interface
     *   using the updateLabels method.
     */
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
