package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;
import model.Post;
import model.UserAccount;
import model.UserSystem;
import view.LoginInterface;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.layout.Priority.ALWAYS;

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
    private Button searchProfileButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox feedVBox;

    @FXML
    private VBox searchVBox;

    @FXML
    private VBox profileVBox;

    private Locale locale;


    public void setLocale(Locale locale) {
        this.locale = locale;
        updateLabels();
    }

    private void updateLabels() {
        ResourceBundle bundle = ResourceBundle.getBundle("lang.User", locale);
        welcomeLabel.setText(bundle.getString("welcome.label") + " " + UserSystem.getInstance().getCurrentUser().getName());
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
        feedVBox.getChildren().removeIf(node -> node instanceof VBox);

        UserSystem.getInstance().getAllPosts().forEach(post -> {
            // Get the current date and time
            Date postDate = post.getDatePosted();
            String username = UserSystem.getInstance().getUserById(post.getUserId());

            String header = username + " - " + getFormattedDateTime(postDate);
            addPostToFeed(header, post.getText(), post);
        });
    }

    private void loadSearch() {
        searchVBox.getChildren().removeIf(node -> searchVBox.getChildren().indexOf(node) > 0);

        UserSystem.getInstance().getUserAccounts().forEach(this::addProfilesToSearch);
    }

    private void loadProfile() {
        profileVBox.getChildren().removeIf(node -> node instanceof VBox);

        UserSystem.getInstance().getPostsByUser(UserSystem.getInstance().getCurrentUser()).forEach(post -> {
            String header = UserSystem.getInstance().getCurrentUser().getName() + " - " + getFormattedDateTime(post.getDatePosted());
            addPostToProfile(header, post.getText(), post);
        });
    }

    private String getFormattedDateTime(Date postDate) {
        if (postDate == null) {
            throw new IllegalArgumentException("postDate cannot be null");
        }

        try {
            // Convert Date to LocalDateTime
            LocalDateTime localDateTime = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Define a formatter for the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Format the date and return the result
            return localDateTime.format(formatter);

        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("postDate is not supported for conversion: " + postDate, e);
        }
    }

    @FXML
    public void initialize() {
        if (locale == null) {
            locale = LanguageManager.getInstance().getCurrentLocale();
        }
        loadFeed();
        loadSearch();
        loadProfile();
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
                var currentUser = UserSystem.getInstance().getCurrentUser();
                UserSystemController.getInstance().userPost(currentUser, postContent); // Update the backend

                loadFeed();
                loadProfile();
            }
        });
    }

    private void addPostToFeed(String headerText, String postContent, Post post) {
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
        reactionBox.setAlignment(Pos.CENTER_RIGHT);
        reactionBox.setSpacing(5);
        reactionBox.setPrefSize(1134, 35);

        // Reaction elements
        Label counterLabel = new Label("0");
        counterLabel.setStyle("-fx-padding: 10;");
        Button likeButton = new Button("👍");
        likeButton.setOnAction(event -> handleLikeButton(counterLabel, post));
        Button reportButton = new Button("⚠");
        reportButton.setOnAction(event -> handleReportButton(post));

        // Update counterLabel value
//        Post post = UserSystemController.getInstance().getPostById(post.getId());
        counterLabel.setText("" + post.getLikes());

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, reportButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the feed container
        feedVBox.getChildren().add(1, postBox); // Add at the top of the feed
    }

    private void handleReportButton(Post post) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Post");
        dialog.setHeaderText("Please provide a reason for reporting this post:");

        TextArea reasonTextArea = new TextArea();
        reasonTextArea.setWrapText(true);
        dialog.getDialogPane().setContent(reasonTextArea);

        dialog.showAndWait().ifPresent(reason -> {
            String reportReason = reasonTextArea.getText();
            if (!reportReason.trim().isEmpty()) {
                UserSystemController.getInstance().reportPost(UserSystem.getInstance().getCurrentUser(), post, reportReason);
                loadFeed();
            }
        });

        // Refresh the feed view to reflect the changes
        loadFeed();
    }

    private void handleReportButton(UserAccount user) {
        TextInputDialog dialog = new TextInputDialog();
        //TODO SET LOCALIZATION
        dialog.setTitle("Report User");
        dialog.setHeaderText("Please provide a reason for reporting this user:");

        TextArea reasonTextArea = new TextArea();
        reasonTextArea.setWrapText(true);
        dialog.getDialogPane().setContent(reasonTextArea);

        dialog.showAndWait().ifPresent(reason -> {
            String reportReason = reasonTextArea.getText();
            if (!reportReason.trim().isEmpty()) {
                UserSystemController.getInstance().reportUser(UserSystem.getInstance().getCurrentUser(), user, reportReason);
                loadSearch();
            }
        });

        // Refresh the search view to reflect the changes
        loadSearch();
    }


    // Helper method to add a post to the profileVBox
    private void addPostToProfile(String headerText, String postContent, Post post) {
        // Create a VBox for the post
        VBox postBox = new VBox();
        postBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        postBox.setSpacing(5);

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
        reactionBox.setAlignment(Pos.CENTER_RIGHT);
        reactionBox.setSpacing(5);
        reactionBox.setPrefSize(1134, 35);

        // Reaction elements
        Label counterLabel = new Label("0");
//        counterLabel.setText(String.valueOf(handleLikeButton(counterLabel, postId)));
        counterLabel.setStyle("-fx-padding: 10;");
        Button likeButton = new Button("👍");
        likeButton.setOnAction(event -> handleLikeButton(counterLabel, post));
        Button removeButton = new Button("🗑");
        removeButton.setOnAction(event -> handleRemovePost(post));

        // Update counterLabel value
//        Post updatedPost = UserSystemController.getInstance().getPostById(post.getId());
        counterLabel.setText("" + post.getLikes());

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, removeButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the profile container
        profileVBox.getChildren().add(1, postBox);
    }

    private void addProfilesToSearch(UserAccount user) {
        // Create Label
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.setFont(new Font(20.0));

        //Create Region
        Region region = new Region();
        HBox.setHgrow(region, ALWAYS);

        // Create Button
        Button warningButton = new Button("⚠");
        warningButton.setMnemonicParsing(false);
        warningButton.setPrefSize(54.0, 33.0);

        // Handle the report button
        warningButton.setOnAction(event -> handleReportButton(user));

        // Create HBox
        HBox hbox = new HBox(10.0); // Spacing of 10.0
        hbox.setPrefSize(1150.0, 17.0);
        hbox.getChildren().addAll(usernameLabel, region, warningButton);

        // Add HBox to searchVBox
        searchVBox.getChildren().add(hbox);
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
        userTabPane.getSelectionModel().select(2); // Switch to the profile tab
    }

    // Method to handle removing a post
    private void handleRemovePost(Post post) {
        // Logic to remove a post (e.g., from the backend and update the UI)
        UserSystemController.getInstance().userRemovePost(UserSystem.getInstance().getCurrentUser(), post);

        // Refresh the profile view to reflect the changes
        loadProfile();
        loadFeed();
    }

    @FXML
    void handleLogoutButton() {
        Stage currentStage = (Stage) this.userTabPane.getScene().getWindow();
        currentStage.close();
        try {
            LoginInterface.getInstance().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleLikeButton(Label counterLabel, Post post) {
        // Check if current user has liked already to then either like or dislike the post
        if (!post.getLikedByUserIds().contains(UserSystem.getInstance().getCurrentUser().getId())) {
            UserSystemController.getInstance().userLikePost(UserSystem.getInstance().getCurrentUser(), post);
        } else if (post.getLikedByUserIds().contains(UserSystem.getInstance().getCurrentUser().getId())){
            UserSystemController.getInstance().userUnlikePost(UserSystem.getInstance().getCurrentUser(), post);
        }

        // Refresh to load changes
        Platform.runLater(() -> counterLabel.setText("" + post.getLikedByUserIds().size()));
        loadFeed();
    }
}