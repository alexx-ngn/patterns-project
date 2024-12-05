package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Locale;
import java.util.ResourceBundle;

public class AdminInterfaceController {
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

    public void setLocale(Locale locale) {
        this.locale = locale;
        updateLabels();
    }

    private void updateLabels() {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
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
    public void initialize() {
        if (locale == null) {
            locale = LanguageManager.getCurrentLocale();
        }
        updateLabels();
    }
}
