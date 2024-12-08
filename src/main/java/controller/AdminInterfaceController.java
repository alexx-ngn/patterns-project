package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import view.LoginInterface;

import java.util.Locale;
import java.util.ResourceBundle;

public class AdminInterfaceController {
    @FXML
    public TabPane adminTabPane;

    @FXML
    private Label YLabel;

    @FXML
    private Label closedPostReportsLabel;

    @FXML
    private ListView<?> closedPostReportsListView;

    @FXML
    private Button closedReportsButton;

    @FXML
    private Label closedUserReportsLabel;

    @FXML
    private ListView<?> closedUserReportsListView;

    @FXML
    private Button logoutButton;

    @FXML
    private Label openPostReportsLabel;

    @FXML
    private ListView<?> openPostReportsListView;

    @FXML
    private Button openReportsButton;

    @FXML
    private ListView<?> openUserReportsListView;

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
    }
}
