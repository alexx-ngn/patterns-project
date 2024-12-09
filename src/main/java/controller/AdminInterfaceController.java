package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;
import view.LoginInterface;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdminInterfaceController {
    @FXML
    private Label YLabel;

    @FXML
    private TabPane adminTabPane;

    @FXML
    private TableColumn<PostReport, String> closedPostReportsDate;

    @FXML
    private TableColumn<PostReport, Integer> closedPostReportsId;

    @FXML
    private Label closedPostReportsLabel;

    @FXML
    private TableView<PostReport> closedPostReportsTableView;

    @FXML
    private Button closedReportsButton;

    @FXML
    private TableColumn<UserReport, String> closedUserReportsDate;

    @FXML
    private TableColumn<UserReport, Integer> closedUserReportsId;

    @FXML
    private Label closedUserReportsLabel;

    @FXML
    private TableView<UserReport> closedUserReportsTableView;

    @FXML
    private Button logoutButton;

    @FXML
    private TableColumn<PostReport, String> openPostReportsDate;

    @FXML
    private TableColumn<PostReport, Integer> openPostReportsId;

    @FXML
    private Label openPostReportsLabel;

    @FXML
    private TableView<PostReport> openPostReportsTableView;

    @FXML
    private Button openReportsButton;

    @FXML
    private TableColumn<UserReport, String> openUserReportsDate;

    @FXML
    private TableColumn<UserReport, Integer> openUserReportsId;

    @FXML
    private TableView<UserReport> openUserReportsTableView;

    @FXML
    private Label openUserReportslabel;

    @FXML
    private Label welcomeLabel;

    /**
     * Represents the current locale setting used in the AdminInterfaceController.
     * The locale is utilized to manage and update the internationalization
     * (i18n) resources such as labels, buttons, and other UI components
     * according to the selected language and region preferences.
     */
    private Locale locale;

    /**
     * Updates the labels and button texts within the admin interface using the current locale settings.
     * This method retrieves the appropriate translations for label and button texts from the
     * resource bundle for the admin interface based on the current locale.
     * It updates the text properties of various UI elements such as welcome label, open post
     * reports label, open reports button, logout button, and open user reports label.
     * This ensures that the interface displays the correct language as per the user's locale setting.
     */
    private void updateLabels() {
        locale = LanguageManager.getInstance().getCurrentLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("lang.Admin", locale);
        welcomeLabel.setText(bundle.getString("welcome.label"));
        openPostReportsLabel.setText(bundle.getString("openPostReports.label"));
        openReportsButton.setText(bundle.getString("openReports.button"));
        logoutButton.setText(bundle.getString("logout.button"));
        openUserReportslabel.setText(bundle.getString("openUserReports.label"));
    }

    /**
     * Handles the action of clicking the "Open Reports" button.
     * This method selects the first tab in the adminTabPane,
     * presumably to display open reports.
     */
    @FXML
    void handleOpenReportsButton() {
        adminTabPane.getSelectionModel().select(0);
    }

    /**
     * Handles the action of pressing the "Closed Reports" button within the admin interface.
     * This method selects the second tab in the admin tab pane, which is assumed
     * to be associated with closed reports, allowing the user to view them.
     */
    @FXML
    void handleClosedReportsButton() {
        adminTabPane.getSelectionModel().select(1);
    }

    /**
     * Handles the action event triggered by the search button.
     * This method selects the third tab in the adminTabPane,
     * indicating a transition to a different view or section within the interface.
     */
    @FXML
    void handleSearchButton() {
        adminTabPane.getSelectionModel().select(2);
    }

    /**
     * Handles the event triggered by clicking the "Search Profile" button.
     *
     * This method is responsible for switching the current selection in the
     * admin tab pane to the third tab, typically associated with profile search functionality.
     * It ensures that the user interface updates accordingly to reflect this change.
     */
    @FXML
    void handleSearchProfileButton() {
        adminTabPane.getSelectionModel().select(2);
    }

    /**
     * Handles the logout button action.
     *
     * This method is triggered when the logout button is clicked in the admin interface.
     * It closes the current admin stage and opens a new login interface stage.
     * If an error occurs while starting the login interface, it throws a RuntimeException.
     */
    @FXML
    void handleLogoutButton() {
        Stage currentStage = (Stage) this.adminTabPane.getScene().getWindow();
        currentStage.close();
        try {
            LoginInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the admin interface controller by setting the current locale,
     * updating UI labels, and loading user and post reports. This method is
     * automatically invoked after the FXML file has been loaded.
     *
     * The method performs the following actions:
     * 1. Retrieves the current locale from the LanguageManager and assigns it
     *    to the instance variable for localization purposes.
     * 2. Calls the updateLabels() method to refresh the UI labels based on
     *    the current locale.
     * 3. Invokes loadPostReports() to populate the post reports section,
     *    organizing them into open and closed categories.
     * 4. Calls loadUserReports() to set up the user reports section,
     *    organizing the reports by their open and closed status.
     */
    @FXML
    public void initialize() {
        locale = LanguageManager.getInstance().getCurrentLocale();
        updateLabels();
        loadPostReports();
        loadUserReports();
    }

    /**
     * The resource bundle for the "Admin" scene, retrieved based on the current locale.
     * This bundle contains localized resources such as text for the Admin interface.
     * These resources are used to ensure the UI can adapt to different languages and regions
     * depending on the user's settings.
     */
    ResourceBundle bundle = LanguageManager.getInstance().getResourceBundle("Admin");

    /**
     * Loads post reports into the specified table views, dividing them into open and closed reports.
     *
     * This method clears any existing items in the open and closed post reports table views. It then
     * sets up cell value factories for the report IDs and report dates, formatting the dates as needed.
     * Reports are retrieved from the {@link ReportSystem} singleton and categorized into open and closed
     * based on their status, which are then added to the respective table view.
     *
     * Additionally, double-click event handlers are attached to both open and closed post reports table
     * views, allowing the user to open a detailed view of a report when double-clicked.
     */
    private void loadPostReports() {
        openPostReportsTableView.getItems().clear();
        closedPostReportsTableView.getItems().clear();

        openPostReportsId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        openPostReportsDate.setCellValueFactory(
                cellData -> new SimpleStringProperty(getFormattedDateTime(cellData.getValue().getDateReported())));
        closedPostReportsId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        closedPostReportsDate.setCellValueFactory(
                cellData -> new SimpleStringProperty(getFormattedDateTime(cellData.getValue().getDateReported())));

        // Load open reports
        for (PostReport report : ReportSystem.getInstance().getPostReports()) {
            if (report.getStatus() == Report.Status.OPENED) {
                openPostReportsTableView.getItems().add(report);
            } else {
                closedPostReportsTableView.getItems().add(report);
            }
        }

        // Add event handler for openPostReportsTableView
        openPostReportsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click event
                PostReport selectedReport = openPostReportsTableView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    openReport(selectedReport);
                }
            }
        });

        closedPostReportsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click event
                PostReport selectedReport = closedPostReportsTableView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    openReport(selectedReport);
                }
            }
        });
    }

    /**
     * Loads user reports into the respective table views for open and closed reports.
     * The method clears existing items from the tables, sets up cell value factories
     * for report ID and formatted report date, and sorts the reports based on their status.
     * Open reports are added to the openUserReportsTableView, while closed reports
     * are added to the closedUserReportsTableView. It also sets a double-click mouse
     * event handler on both tables to open a selected report when double-clicked.
     */
    private void loadUserReports() {
        openUserReportsTableView.getItems().clear();
        closedUserReportsTableView.getItems().clear();

        openUserReportsId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        openUserReportsDate.setCellValueFactory(
                cellData -> new SimpleStringProperty(getFormattedDateTime(cellData.getValue().getDateReported())));
        closedUserReportsId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        closedUserReportsDate.setCellValueFactory(
                cellData -> new SimpleStringProperty(getFormattedDateTime(cellData.getValue().getDateReported())));

        // Load open reports
        for (UserReport report : ReportSystem.getInstance().getUserReports()) {
            if (report.getStatus() == Report.Status.OPENED) {
                openUserReportsTableView.getItems().add(report);
            } else {
                closedUserReportsTableView.getItems().add(report);
            }
        }

        // Add event handler for openUserReportsTableView
        openUserReportsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click event
                UserReport selectedReport = openUserReportsTableView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    openReport(selectedReport);
                }
            }
        });

        closedUserReportsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click event
                UserReport selectedReport = closedUserReportsTableView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    openReport(selectedReport);
                }
            }
        });
    }

    /**
     * Opens a detailed report window for the given report. The window displays various details about the
     * report including the report ID, reason for the report, content details (either post or user),
     * status, and options to dismiss or delete the report if it is open.
     *
     * @param report the report object containing information about the post or user being reported.
     */
    private void openReport(Report report) {
        // Create a new Stage
        Stage reportStage = new Stage();
        reportStage.setTitle(bundle.getString("title.details"));

        // Create a VBox to hold the post details
        VBox postVBox = new VBox(10);
        postVBox.setPadding(new Insets(10));

        // Add a title Label
        Label postTitle = new Label("ID: " + report.getId());
        postTitle.setFont(new Font("System Bold", 18));
        postVBox.getChildren().add(postTitle);

        // Add reason header
        Label reasonHeader = new Label(bundle.getString("reason.details"));
        reasonHeader.setFont(new Font("System Bold", 16));
        postVBox.getChildren().add(reasonHeader);

        // Add the report reason
        Label reportReason = new Label(report.getReason());
        reportReason.setWrapText(true);
        postVBox.getChildren().add(reportReason);

        // Add the header
        Label postHeader = new Label(bundle.getString("content.details"));
        postHeader.setFont(new Font("System Bold", 16));
        postVBox.getChildren().add(postHeader);

        // Add the content
        if (report instanceof PostReport postReport) {
            Label postContent;
            try {
                postContent = new Label(UserSystem.getInstance().getPostById(postReport.getReportedPostId()).getText());
            } catch (NullPointerException e) {
                postContent = new Label(bundle.getString("removed.details"));
            }
            postContent.setWrapText(true);
            postVBox.getChildren().add(postContent);
        } else if (report instanceof UserReport userReport) {
            Hyperlink postContent;
            try {
                var user = UserSystem.getInstance().getUserById(userReport.getReportedUserId());
                postContent = new Hyperlink(user.getUsername());
                postContent.setOnAction(event -> {
                    openUserProfileWindow(user);
                });
            } catch (NullPointerException e) {
                postContent = new Hyperlink(bundle.getString("removed.details"));
            }
            postVBox.getChildren().add(postContent);
        }

        // Add the post details
        Label postDetails = new Label(bundle.getString("title.details"));
        postDetails.setFont(new Font("System Bold", 16));
        postVBox.getChildren().add(postDetails);

        // Add the post date
        Label postDate = new Label(bundle.getString("reportedDate.details") + getFormattedDateTime(report.getDateReported()));
        postVBox.getChildren().add(postDate);

        // Add the post reporter
        Label postReporter = new Label(bundle.getString("reportedBy.details") + UserSystem.getInstance().getUserById(report.getReportingUserId()).getUsername());
        postVBox.getChildren().add(postReporter);

        // Add the post status
        Label postStatus = new Label(bundle.getString("status.details") + report.getStatus().toString());
        postVBox.getChildren().add(postStatus);

        // Add Hbox for buttons
        HBox buttonHBox = new HBox(10);
        postVBox.getChildren().add(buttonHBox);

        if (report.getStatus().equals(Report.Status.OPENED)) {
            // Add the dismiss button
            Button dismissButton = new Button(bundle.getString("dismiss.details"));
            dismissButton.setOnAction(event -> {
                report.setStatus(Report.Status.CLOSED);
                report.setDateReported(new Date());
                if (report instanceof PostReport postReport) {
                    ReportSystemController.getInstance().closePostReport(postReport);
                } else if (report instanceof UserReport userReport) {
                    ReportSystemController.getInstance().closeUserReport(userReport);
                }
                loadPostReports();
                loadUserReports();
                reportStage.close();
            });
            buttonHBox.getChildren().add(dismissButton);

            // Add the delete button
            Button deleteButton = new Button(bundle.getString("remove.details"));
            deleteButton.setOnAction(event -> {
                if (report instanceof PostReport postReport) {
                    ReportSystemController.getInstance().deletePost(postReport.getReportedPostId());
                    ReportSystemController.getInstance().closePostReport(postReport);
                } else if (report instanceof UserReport userReport) {
                    ReportSystemController.getInstance().deleteUser(userReport.getReportedUserId());
                    ReportSystemController.getInstance().closeUserReport(userReport);
                }
                report.setStatus(Report.Status.CLOSED);
                report.setDateReported(new Date());
                loadPostReports();
                loadUserReports();
                reportStage.close();
            });
            buttonHBox.getChildren().add(deleteButton);
        }

        // Set the VBox in a Scene
        Scene postScene = new Scene(postVBox, 400, 400);
        reportStage.setScene(postScene);

        // Show the new Stage
        reportStage.show();
    }

    /**
     * Converts a given date to a formatted string representation.
     *
     * @param postDate the date to be formatted
     * @return a formatted date string in the pattern "yyyy-MM-dd HH:mm"
     */
    private String getFormattedDateTime(Date postDate) {
        // Convert Date to LocalDateTime
        LocalDateTime localDateTime = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Define a formatter for the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Format the date and return the result
        return localDateTime.format(formatter);
    }

    /**
     * Opens a new window displaying the user's profile, including their username
     * and a list of their posts. The profile window is created using a new Stage
     * and includes a VBox layout to organize the user's post headers and content.
     *
     * @param user the UserAccount object representing the user whose profile
     *             should be displayed
     */
    private void openUserProfileWindow(UserAccount user) {
        // Create a new Stage
        Stage profileStage = new Stage();
        profileStage.setTitle(user.getUsername());

        // Create a VBox to hold the user's posts
        VBox profileVBox = new VBox(10);
        profileVBox.setPadding(new Insets(10));

        // Add a title Label
        Label profileTitle = new Label(bundle.getString("postsBy.profile") + user.getUsername());
        profileTitle.setFont(new Font("System Bold", 18));

        profileVBox.getChildren().add(profileTitle);

        // Fetch the user's posts and add them to the VBox
        UserSystem.getInstance().getPostsByUser(user).forEach(post -> {
            String header = user.getUsername() + " - " + getFormattedDateTime(post.getDatePosted());
            Label postHeader = new Label(header);
            postHeader.setFont(new Font(16.0));

            Label postContent = new Label(post.getText());
            postContent.setWrapText(true);

            VBox postBox = new VBox(5, postHeader, postContent);
            postBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
            profileVBox.getChildren().add(postBox);
        });

        // Set the VBox in a Scene
        Scene profileScene = new Scene(profileVBox, 400, 600);
        profileStage.setScene(profileScene);

        // Show the new Stage
        profileStage.show();
    }
}
