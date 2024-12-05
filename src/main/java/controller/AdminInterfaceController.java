package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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
}
