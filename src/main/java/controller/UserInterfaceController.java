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
import model.Post;
import model.UserSystem;
import view.LoginInterface;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
//        for (int i = 0; i < feedVBox.getChildren().size(); i++) {
//            if (feedVBox.getChildren().get(i) instanceof VBox) {
//                feedVBox.getChildren().remove(i);
//                i--;
//            }
//        }
        feedVBox.getChildren().removeIf(node -> node instanceof VBox);

        UserSystem.getInstance().getAllPosts().forEach(post -> {
            // Get the current date and time
            Date postDate = post.getDatePosted();
            String username = UserSystem.getInstance().getUserById(post.getUserId());

            String header = username + " - " + getFormattedDateTime(postDate);
            addPostToFeed(header, post.getText(), post.getId());
        });
    }

    private void loadProfile() {
//        for (int i = 0; i < profileVBox.getChildren().size(); i++) {
//            if (profileVBox.getChildren().get(i) instanceof VBox) {
//                profileVBox.getChildren().remove(i);
//                i--;
//            }
//        }

        profileVBox.getChildren().removeIf(node -> node instanceof VBox);

        UserSystem.getInstance().getPostsByUser(UserSystem.getInstance().getCurrentUser()).forEach(post -> {
            String header = UserSystem.getInstance().getCurrentUser().getName() + " - " + getFormattedDateTime(post.getDatePosted());
            addPostToProfile(header, post.getText(), post.getId());
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
//                int postId = UserSystem.getInstance().getAllPosts().isEmpty() ? 1 : UserSystem.getInstance().getAllPosts().getLast().getId();
//                UserSystem.getInstance().getCurrentUser().post(postContent); // Update the backend
//                Date postDate = new Date();
//
//                String header = UserSystem.getInstance().getCurrentUser().getName() + " - " + getFormattedDateTime(postDate); // Create the header
//                addPostToFeed(header, postContent, postId); // Add to the feed visually
//                addPostToProfile(header, postContent, postId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Post Created");
                alert.setHeaderText(null);
                alert.setContentText(String.valueOf(UserSystem.getInstance().getAllPosts().size()));
                alert.showAndWait();

                loadFeed();
                loadProfile();
            }
        });
    }

    private void addPostToFeed(String headerText, String postContent, int postId) {
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
//        likeButton.setOnAction(event -> handleLikeButton(counterLabel, postId));
        Button reportButton = new Button("⚠");

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, reportButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the feed container
        feedVBox.getChildren().add(1, postBox); // Add at the top of the feed
    }

    // Helper method to add a post to the profileVBox
    private void addPostToProfile(String headerText, String postContent, int postId) {
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
        reactionBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        reactionBox.setSpacing(5);
        reactionBox.setPrefSize(1134, 35);

        // Reaction elements
        Label counterLabel = new Label("0");
//        counterLabel.setText(String.valueOf(handleLikeButton(counterLabel, postId)));
        counterLabel.setStyle("-fx-padding: 10;");
        Button likeButton = new Button("👍");
//        likeButton.setOnAction(event -> handleLikeButton(counterLabel, postId));
        Button removeButton = new Button("🗑");
        removeButton.setOnAction(event -> {
                    handleRemovePost(UserSystemController.getInstance().getPostById(postId));
                    postBox.getChildren().remove(postBox);
                    loadProfile();
                    loadFeed();
        });

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, removeButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the profile container
        profileVBox.getChildren().add(1, postBox);
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

        // Clear the existing content in profileVBox to avoid duplicates
//        profileVBox.getChildren().clear();
//        profileVBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
//        profileVBox.setSpacing(5);
//
//        // Retrieve the current user's posts
//        UserSystem.getInstance().getCurrentUser().getPosts().forEach(post -> {
//            // Create a header with the user's name and the post date
//            String headerText = UserSystem.getInstance().getCurrentUser().getName() + " - " + post.getDatePosted();
//
//            // Add the post to the profile view
//            addPostToProfile(headerText, post.getText(), post.getId());
//        });
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
    void handleLikeButton(Label counterLabel, int postId) {
        Post post = UserSystemController.getInstance().getPostById(postId); // Get post from its id
        UserSystemController.getInstance().userLikePost(UserSystem.getInstance().getCurrentUser(), post); // Like the post within DB
        post = UserSystemController.getInstance().getPostById(postId); // Update the post to the version added to DB from userLikePost method
        counterLabel.setText("" + post.getUsersLiked().size());
    }
}