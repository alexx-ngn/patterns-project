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
    private Button searchButton;

    @FXML
    private Label searchLabel;

    @FXML
    private ListView<?> searchListView;

    @FXML
    private Button searchProfileButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label welcomeLabel;

    private Locale locale;

    private void updateLabels() {
        ResourceBundle bundle = ResourceBundle.getBundle("lang.Admin", locale);
        welcomeLabel.setText(bundle.getString("welcome.label"));
        searchProfileButton.setText(bundle.getString("search.tab"));
        searchLabel.setText(bundle.getString("search.button"));
        searchButton.setText(bundle.getString("search.button"));
//        closedReportsButton.setText(bundle.getString("label.welcome"));
//        closedUserReportsLabel.setText(bundle.getString("label.welcome"));
//        closedPostReportsLabel.setText(bundle.getString("label.welcome"));
        openPostReportsLabel.setText(bundle.getString("openPostReports.label"));
        openReportsButton.setText(bundle.getString("openReports.button"));
        logoutButton.setText(bundle.getString("logout.button"));
        openUserReportslabel.setText(bundle.getString("openUserReports.label"));
    }

    @FXML
    void handleOpenReportsButton() {
        adminTabPane.getSelectionModel().select(0);
    }

    @FXML
    void handleClosedReportsButton() {
        adminTabPane.getSelectionModel().select(1);
    }

    @FXML
    void handleSearchButton() {
        adminTabPane.getSelectionModel().select(2);
    }

    @FXML
    void handleSearchProfileButton() {
        adminTabPane.getSelectionModel().select(2);
    }

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

    @FXML
    public void initialize() {
        locale = LanguageManager.getInstance().getCurrentLocale();
        updateLabels();
        loadPostReports();
        loadUserReports();
    }

    private void loadPostReports() {
        openPostReportsTableView.getItems().clear();
        openPostReportsTableView.getItems().clear();

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
                    openPostReport(selectedReport);
                }
            }
        });

        // Add event handler for openUserReportsTableView
        openUserReportsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click event
                UserReport selectedReport = openUserReportsTableView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    handleReportClick(selectedReport);
                }
            }
        });
    }

    private void handleReportClick(Report report) {
        // Handle the report click event
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Details");
        alert.setHeaderText("Report ID: " + report.getId());
        alert.setContentText("Report Details: " + report.getReason());
        alert.showAndWait();
    }

    private void openPostReport(PostReport report) {
        // Create a new Stage
        Stage reportStage = new Stage();
        reportStage.setTitle("Post Details");

        // Create a VBox to hold the post details
        VBox postVBox = new VBox(10);
        postVBox.setPadding(new Insets(10));

        // Add a title Label
        Label postTitle = new Label("Report ID: " + report.getId());
        postTitle.setFont(new Font("System Bold", 18));
        postVBox.getChildren().add(postTitle);

        // Add reason header
        Label reasonHeader = new Label("Reason:");
        reasonHeader.setFont(new Font("System Bold", 16));
        postVBox.getChildren().add(reasonHeader);

        // Add the report reason
        Label reportReason = new Label(report.getReason());
        reportReason.setWrapText(true);
        postVBox.getChildren().add(reportReason);

        // Add the post header
        Label postHeader = new Label("Post Content:");
        postHeader.setFont(new Font("System Bold", 16));
        postVBox.getChildren().add(postHeader);

        // Add the post content
        Label postContent = new Label(UserSystem.getInstance().getPostById(report.getReportedPostId()).getText());
        postContent.setWrapText(true);
        postVBox.getChildren().add(postContent);

        // Add the post details
        Label postDetails = new Label("Post Details:");
        postDetails.setFont(new Font("System Bold", 16));
        postVBox.getChildren().add(postDetails);

        // Add the post date
        Label postDate = new Label("Reported on: " + getFormattedDateTime(report.getDateReported()));
        postVBox.getChildren().add(postDate);

        // Add the post reporter
        Label postReporter = new Label("Reported by: " + UserSystem.getInstance().getUserById(report.getReportingUserId()));
        postVBox.getChildren().add(postReporter);

        // Add Hbox for buttons
        HBox buttonHBox = new HBox(10);
        postVBox.getChildren().add(buttonHBox);

        // Add the dismiss button
        Button dismissButton = new Button("Dismiss Report");
        dismissButton.setOnAction(event -> {
            report.setStatus(Report.Status.CLOSED);
            report.setDateReported(new Date());
            ReportSystemController.getInstance().closePostReport(report);
            loadPostReports();
            reportStage.close();

        });
        buttonHBox.getChildren().add(dismissButton);

        // Add the delete button
        Button deleteButton = new Button("Delete Post");
        deleteButton.setOnAction(event -> {
            ReportSystemController.getInstance().deletePost(report.getReportedPostId());
            report.setStatus(Report.Status.CLOSED);
            report.setDateReported(new Date());
            ReportSystemController.getInstance().closePostReport(report);
            loadPostReports();
            reportStage.close();
        });
        buttonHBox.getChildren().add(deleteButton);

        // Set the VBox in a Scene
        Scene postScene = new Scene(postVBox, 400, 400);
        reportStage.setScene(postScene);

        // Show the new Stage
        reportStage.show();
    }

    private void openUserProfileWindow(UserAccount user) {
        // Create a new Stage
        Stage profileStage = new Stage();
        profileStage.setTitle(user.getUsername() + "'s Profile");

        // Create a VBox to hold the user's posts
        VBox profileVBox = new VBox(10);
        profileVBox.setPadding(new Insets(10));

        // Add a title Label
        Label profileTitle = new Label("Posts by " + user.getUsername());
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
    }

    private String getFormattedDateTime(Date postDate) {
        // Convert Date to LocalDateTime
        LocalDateTime localDateTime = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Define a formatter for the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Format the date and return the result
        return localDateTime.format(formatter);
    }
}
