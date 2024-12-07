package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.UserSystem;
import view.LoginInterface;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class UserInterfaceController {
    @FXML
    private TabPane userTabPane;

    @FXML
    private Label YLabel;

    @FXML
    private Button homeButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label overviewLabel;

    @FXML
    private Button postButton;

    @FXML
    private Label postsLabel;

    @FXML
    private Button profileButton;

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

    @FXML
    private VBox feedVBox;

    private Locale locale;

    public void setLocale(Locale locale) {
        this.locale = locale;
        updateLabels();
    }

    private void updateLabels() {
        ResourceBundle bundle = ResourceBundle.getBundle("lang.User", locale);
        welcomeLabel.setText(bundle.getString("welcome.label") + " " + UserSystem.getCurrentUser().getName());
        searchProfileButton.setText(bundle.getString("search.button"));
        searchLabel.setText(bundle.getString("search.label"));
        searchButton.setText(bundle.getString("search.button"));
        profileButton.setText(bundle.getString("profile.button"));
        postsLabel.setText(bundle.getString("myPosts.label"));
        postButton.setText(bundle.getString("post.button"));
        overviewLabel.setText(bundle.getString("overview.label"));
        logoutButton.setText(bundle.getString("logout.button"));
        homeButton.setText(bundle.getString("home.button"));
    }

    private void hideTabs() {

    }

    private void loadFeed() {
        for (int i = 0; i < feedVBox.getChildren().size(); i++) {
            if (feedVBox.getChildren().get(i) instanceof VBox) {
                feedVBox.getChildren().remove(i);
                i--;
            }
        }
        UserSystem.getCurrentUser().getPosts().forEach(post -> {
            AnchorPane postPane = new AnchorPane();
            Label postLabel = new Label(post.getText());
            postPane.getChildren().add(postLabel);
            feedVBox.getChildren().add(postPane);
        });
    }

    @FXML
    public void initialize() {
        if (locale == null) {
            locale = LanguageManager.getCurrentLocale();
        }
        loadFeed();
        updateLabels();
        hideTabs();
    }

    @FXML
    void handlePostButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Post");
        dialog.setHeaderText("What's on your mind?");

        // Replace the default input field with a TextArea
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        dialog.getDialogPane().setContent(textArea);

        dialog.showAndWait().ifPresent(text -> {
            String postContent = textArea.getText();
            if (!postContent.trim().isEmpty()) {
                UserSystemController.getInstance().userPost(UserSystem.getCurrentUser(), postContent); // Update the backend
                UserSystem.getCurrentUser().post(postContent); // Update the backend

                // Get the current date and time
                LocalDateTime now = LocalDateTime.now();

                // Define a formatter for the desired format (yyyy-MM-dd HH:mm)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                // Format the current date and time
                String formattedDateTime = now.format(formatter);

                String header = UserSystem.getCurrentUser().getName() + " - " + formattedDateTime; // Create the header
                addPostToFeed(header, postContent); // Add to the feed visually
            }
        });
    }

    private void addPostToFeed(String headerText, String postContent) {
        // Create a VBox for the post
        VBox postBox = new VBox();
        postBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        postBox.setSpacing(5); // Optional: Add spacing between elements

        // Create the header Label
        Label headerLabel = new Label(headerText);
        headerLabel.setFont(new Font("System Bold", 18));
        headerLabel.setPrefSize(250, 18);

        // Create the content Label
        Label contentLabel = new Label(postContent);
        contentLabel.setWrapText(true);
        contentLabel.setPrefSize(1123, 150);

        // Create the HBox for reactions
        HBox reactionBox = new HBox();
        reactionBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        reactionBox.setSpacing(5);
        reactionBox.setPrefSize(1134, 35);

        // Reaction elements
        Label counterLabel = new Label("0");
        counterLabel.setStyle("-fx-padding: 10;");
        Button likeButton = new Button("👍");
        Button reportButton = new Button("⚠");

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, reportButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the feed container
        feedVBox.getChildren().add(1, postBox); // Add at the top of the feed
    }

    @FXML
    void handleHomeButton() {
        userTabPane.getSelectionModel().select(0);
    }

    @FXML
    void handleSearchButton() {
        userTabPane.getSelectionModel().select(1);
    }

    @FXML
    void handleProfileButton() {
        userTabPane.getSelectionModel().select(2);
    }

    @FXML
    void handleLogoutButton() {
//        UserSystem.logout();
        this.userTabPane.getScene().getWindow().hide();
        try {
            LoginInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
