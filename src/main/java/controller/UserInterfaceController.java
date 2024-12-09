package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

    /**
     * Represents the locale settings used by the UserInterfaceController to
     * localize and update the user interface text according to the specified
     * language and region.
     *
     * The locale is critical in determining the ResourceBundle that is loaded
     * to fetch the correct localized strings for various UI components like
     * labels and buttons.
     */
    private Locale locale;


    /**
     * Sets the locale for the user interface and updates all labels accordingly.
     * This method changes the language or region settings, which adjusts the text labels
     * in the application to the appropriate language defined by the given locale.
     *
     * @param locale the Locale to be set for the application, which determines the language
     *               and regional settings for the user interface text elements.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        updateLabels();
    }

    /**
     * Updates the text labels and buttons in the user interface to reflect the current locale settings.
     * This method retrieves the appropriate localized strings from a ResourceBundle and sets them on
     * various UI components such as labels and buttons, ensuring that all user interface elements
     * are displayed in the correct language.
     */
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

    /**
     * Hides all the tabs in the user interface. This method is typically used
     * during the initialization process to ensure that tabs are not visible
     * to the user until they are explicitly needed. The exact implementation
     * details depend on the UserInterfaceController's configuration, including
     * which tabs are available and how they should be controlled.
     * The method is called as part of the setup process after components such
     * as the feed, search, and profile views are loaded.
     */
    private void hideTabs() {

    }

    /**
     * Loads and displays the user's feed with posts from all users.
     *
     * This method clears the existing feed display by removing all children
     * nodes that are instances of VBox from feedVBox. It retrieves all posts
     * from the UserSystem's singleton instance and processes each post to
     * extract relevant information such as the post's date, and the username of
     * the user who published the post. These details are then formatted into
     * a header string. Finally, each post is added to the feed display by
     * calling addPostToFeed, which integrates the constructed header, the
     * content of the post, and the post object itself.
     */
    private void loadFeed() {
        feedVBox.getChildren().removeIf(node -> node instanceof VBox);

        UserSystem.getInstance().getAllPosts().forEach(post -> {
            // Get the current date and time
            Date postDate = post.getDatePosted();
            String username = UserSystem.getInstance().getUserById(post.getUserId()).getUsername();

            String header = username + " - " + getFormattedDateTime(postDate);
            addPostToFeed(header, post.getText(), post);
        });
    }

    /**
     * Clears the searchVBox of all nodes except the first one, then populates it
     * with user profiles by iterating through each user account in the UserSystem.
     * Each user account is passed to the addProfilesToSearch method to be
     * displayed in the search interface.
     */
    private void loadSearch() {
        searchVBox.getChildren().removeIf(node -> searchVBox.getChildren().indexOf(node) > 0);

        UserSystem.getInstance().getUserAccounts().forEach(this::addProfilesToSearch);
    }

    /**
     * Loads the current user's profile by clearing existing posts from the profile view
     * and adding the user's posts from the system to the profile VBox.
     *
     * The method first removes any nodes of type VBox from the profileVBox, ensuring
     * that the view is cleared before new posts are loaded.
     * It then retrieves the current user from the UserSystem singleton instance
     * and gets all posts associated with this user.
     * For each post, the method creates a header using the user's name and the formatted
     * date of the post, and then adds this information along with the post's text
     * to the profile view via the addPostToProfile helper method.
     */
    private void loadProfile() {
        profileVBox.getChildren().removeIf(node -> node instanceof VBox);

        UserSystem.getInstance().getPostsByUser(UserSystem.getInstance().getCurrentUser()).forEach(post -> {
            String header = UserSystem.getInstance().getCurrentUser().getName() + " - " + getFormattedDateTime(post.getDatePosted());
            addPostToProfile(header, post.getText(), post);
        });
    }

    /**
     * Converts a given Date object to a formatted string representation
     * using the pattern "yyyy-MM-dd HH:mm".
     *
     * @param postDate the Date object to be formatted. Must not be null.
     * @return a string formatted as "yyyy-MM-dd HH:mm" representing the
     *         specified date and time.
     * @throws IllegalArgumentException if the postDate is null.
     * @throws UnsupportedOperationException if the conversion of postDate
     *         is not supported.
     */
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

    /**
     * Initializes the user interface components and settings for the application.
     * This method is automatically called when the FXML file is loaded.
     *
     * The following operations are performed during initialization:
     * 1. Sets the locale for the application if it has not been previously set.
     * 2. Loads the feed content into the user interface.
     * 3. Loads the search functionalities and profiles.
     * 4. Loads the user's profile details and posts.
     * 5. Updates all UI labels to match the current locale settings.
     * 6. Hides certain tabs based on user permissions or initial settings.
     */
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

    /**
     * Handles the action event triggered by clicking the "Post" button in the user interface.
     * This method opens a dialog for the user to input a post. If the input is not empty,
     * it processes and posts the content using the current user's account.
     *
     * The method performs the following actions:
     * 1. Presents a `TextInputDialog` with a title and header fetched from resource bundles
     *    to accommodate locale-specific language settings.
     * 2. Replaces the default input field of the dialog with a `TextArea` for a better user experience.
     * 3. Waits for the user to submit the content, and if the content is non-empty, uses
     *    `UserSystemController` to send the post content for the current user to the backend.
     * 4. Refreshes the user's feed and profile view to reflect the newly posted content.
     */
    @FXML
    void handlePostButton() {
        TextInputDialog dialog = new TextInputDialog();
        ResourceBundle bundle = ResourceBundle.getBundle("lang.User", locale);
        dialog.setTitle(bundle.getString("post.title"));
        dialog.setHeaderText(bundle.getString("post.header"));

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

    /**
     * Adds a post to the feed display. This method creates a new visual representation of a post
     * in a VBox and adds it to the user interface. The post includes a header, content, and a set of
     * reaction controls such as like and report buttons.
     *
     * @param headerText The text to be displayed in the header of the post. Typically includes the
     *                   username and the date/time the post was made.
     * @param postContent The content of the post to be displayed.
     * @param post The Post object containing data about likes and other metadata.
     */
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
        counterLabel.setText("" + post.getLikes());

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, reportButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the feed container
        feedVBox.getChildren().add(1, postBox); // Add at the top of the feed
    }

    /**
     * Handles the action of reporting a post by displaying a dialog
     * for the user to specify a reason. If a valid reason is provided,
     * the post is reported through the UserSystemController, and the
     * feed view is updated.
     *
     * @param post the post to be reported
     */
    private void handleReportButton(Post post) {
        TextInputDialog dialog = new TextInputDialog();
        ResourceBundle bundle = ResourceBundle.getBundle("lang.User", locale);
        dialog.setTitle(bundle.getString("postReport.title"));
        dialog.setHeaderText(bundle.getString("postReport.header"));

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

    /**
     * Handles the report button action for reporting a user account. It presents a dialog to input
     * the reason for reporting the user and then processes the report if a valid reason is provided.
     *
     * @param user the user account that is being reported
     */
    private void handleReportButton(UserAccount user) {
        TextInputDialog dialog = new TextInputDialog();
        ResourceBundle bundle = ResourceBundle.getBundle("lang.User", locale);
        dialog.setTitle(bundle.getString("userReport.title"));
        dialog.setHeaderText(bundle.getString("userReport.header"));

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


    /**
     * Adds a post to the user's profile view by creating a visual representation of the post
     * using JavaFX components such as VBox, Label, and HBox. The method sets up the layout
     * and interactions for the post's display, including headers, content, and reaction buttons.
     *
     * @param headerText   the text to be displayed as the header of the post, usually containing
     *                     the username and the post date.
     * @param postContent  the main content text of the post.
     * @param post         the Post object that contains details and functionality related to the
     *                     post such as like count and user interactions.
     */
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
        counterLabel.setStyle("-fx-padding: 10;");
        Button likeButton = new Button("👍");
        likeButton.setOnAction(event -> handleLikeButton(counterLabel, post));
        Button removeButton = new Button("🗑");
        removeButton.setOnAction(event -> handleRemovePost(post));

        // Update counterLabel value
        counterLabel.setText("" + post.getLikes());

        // Add reaction elements to HBox
        reactionBox.getChildren().addAll(counterLabel, likeButton, removeButton);

        // Add all elements to the post VBox
        postBox.getChildren().addAll(headerLabel, contentLabel, reactionBox);

        // Add the post VBox to the profile container
        profileVBox.getChildren().add(1, postBox);
    }

    /**
     * Adds a user's profile to the search results displayed in the user interface.
     * This method creates and configures GUI components to represent the user's profile,
     * including a label with the user's username and a button to report the user.
     *
     * @param user the user account whose profile should be added to the search results
     */
    private void addProfilesToSearch(UserAccount user) {
        // Create Label
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.setFont(new Font(20.0));
        // Add click event to the usernameLabel to open profile
        usernameLabel.setOnMouseClicked(event -> openUserProfileWindow(user));

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

    /**
     * Opens a new window displaying the profile of the specified user.
     * The window contains the user's username, follower count, a follow/unfollow button,
     * and a list of the user's posts. The posts are displayed with a header containing
     * the date posted and the content of the post in a styled format.
     * The follow/unfollow button updates in real-time to reflect the current following status.
     *
     * @param user the user account whose profile is to be displayed
     */
    private void openUserProfileWindow(UserAccount user) {
        // Create a new Stage
        Stage profileStage = new Stage();
        profileStage.setTitle(user.getUsername() + "'s Profile");

        // Create a VBox to hold the user's posts
        VBox profileVBox = new VBox(10);
        profileVBox.setPadding(new Insets(10));

        // Username and Follow Button
        HBox headerLayout = new HBox(10);
        headerLayout.setAlignment(Pos.CENTER_LEFT);

        // Create a spacer Region to push elements to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Username label
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Follower count label
        Label followersLabel = new Label("Followers: " + user.getFollowerCount());
        followersLabel.setStyle("-fx-font-size: 16px;");

        // Follow button
        Button followButton = new Button(followOrUnfollow(user));
        followButton.setPrefWidth(80);
        followButton.setOnAction(e -> handleFollowButton(followersLabel, followButton, user));

        headerLayout.getChildren().addAll(usernameLabel, followButton, spacer, followersLabel);

        // Add a title Label
        Label postTitle = new Label("Posts: ");
        postTitle.setFont(new Font("System Bold", 18));

        profileVBox.getChildren().addAll(headerLayout, postTitle);

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

    /**
     * Handles the action for the home button in the user interface.
     * This method sets the selection of the userTabPane to the first tab,
     * which typically represents the home or default view for the user.
     */
    @FXML
    void handleHomeButton() {
        userTabPane.getSelectionModel().select(0);
    }

    /**
     * Handles the action event triggered by the search button.
     * This method selects the tab at index 1 in the userTabPane,
     * which is presumed to be associated with the search functionality of the application.
     */
    @FXML
    void handleSearchButton() {
        userTabPane.getSelectionModel().select(1);
    }

    /**
     * Handles the action triggered by the profile button.
     * Selects the profile tab in the user interface by changing the selection model
     * of the userTabPane to the third tab, which corresponds to the user's profile view.
     */
    @FXML
    void handleProfileButton() {
        userTabPane.getSelectionModel().select(2); // Switch to the profile tab
    }

    /**
     * Handles the event triggered by clicking the "Search Profile" button.
     * This method searches for user profiles whose usernames contain the text
     * entered in the search text field, and displays the matching profiles.
     *
     * It first clears any existing search results by removing HBox nodes from
     * the searchVBox. Then, it iterates through all user accounts retrieved
     * from the UserSystem singleton instance. For each user, if the username
     * contains the text entered in the searchTextField, the user profile is
     * added to the search results using the private method addProfilesToSearch.
     */
    @FXML
    void handleSearchProfileButton() {
        searchVBox.getChildren().removeIf(node -> node instanceof HBox);

        UserSystem.getInstance().getUserAccounts().forEach(user -> {
            if (user.getUsername().contains(searchTextField.getText())) {
                addProfilesToSearch(user);
            }
        });
    }

    /**
     * Handles the removal of a specified post. This method removes the post,
     * updates the backend system through the UserSystemController, and refreshes
     * the profile and feed views to reflect the changes.
     *
     * @param post the Post object to be removed
     */
    // Method to handle removing a post
    private void handleRemovePost(Post post) {
        // Logic to remove a post (e.g., from the backend and update the UI)
        UserSystemController.getInstance().userRemovePost(UserSystem.getInstance().getCurrentUser(), post);

        // Refresh the profile view to reflect the changes
        loadProfile();
        loadFeed();
    }

    /**
     * Handles the action event for the logout button. This method closes the
     * current application window associated with the user interface and relaunches
     * the login interface.
     *
     * It retrieves the current stage (window) from the user interface context,
     * closes it, and then attempts to start a new instance of the `LoginInterface`
     * to present the login screen. In the event of an exception during the
     * relaunch of the login screen, it propagates the exception as a
     * `RuntimeException`.
     *
     * @throws RuntimeException if an exception occurs while starting the login interface
     */
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

    /**
     * Handles the action of clicking the like button on a post. This method toggles the like state
     * of the current user on the specified post, either adding or removing the user's like. It
     * updates the post's like counter label to reflect the current number of likes.
     *
     * @param counterLabel the label displaying the number of likes on the post
     * @param post the post being liked or unliked by the current user
     */
    @FXML
    private void handleLikeButton(Label counterLabel, Post post) {
        // Check if current user has liked already to then either like or dislike the post
        if (!post.getLikedByUserIds().contains(UserSystem.getInstance().getCurrentUser().getId())) {
            UserSystemController.getInstance().userLikePost(UserSystem.getInstance().getCurrentUser().getId(), post);
        } else if (post.getLikedByUserIds().contains(UserSystem.getInstance().getCurrentUser().getId())){
            UserSystemController.getInstance().userUnlikePost(UserSystem.getInstance().getCurrentUser(), post);
        }

        // Refresh to load changes
        Platform.runLater(() -> counterLabel.setText("" + post.getLikes()));
        loadFeed();
    }

    /**
     * Handles the action of following or unfollowing a user when the follow button is clicked.
     * Adjusts the follow button text and updates the followers count displayed on the label.
     *
     * @param followersLabel the label displaying the number of followers, which will be updated after the action
     * @param followButton the button that initiates the follow/unfollow action and whose text will be updated
     * @param followed the user account that is being followed or unfollowed
     */
    @FXML
    private void handleFollowButton(Label followersLabel, Button followButton, UserAccount followed) {
        if (!followed.getFollowerids().contains(UserSystem.getInstance().getCurrentUser().getId())) {
            UserSystemController.getInstance().userFollowUser(UserSystem.getInstance().getCurrentUser(), followed);
        } else if (followed.getFollowerids().contains(UserSystem.getInstance().getCurrentUser().getId())) {
            UserSystemController.getInstance().userUnfollowUser(UserSystem.getInstance().getCurrentUser(), followed);
        }
        Platform.runLater(() -> {
            followersLabel.setText("Followers:" + followed.getFollowerCount());
            followButton.setText(followOrUnfollow(followed));
        });
    }

    /**
     * Determines whether the current user should follow or unfollow a specified user account.
     *
     * @param user the UserAccount to check for a following relationship with the current user
     * @return "Follow" if the current user does not follow the specified user, "Unfollow" if the current user already follows the specified user, or null if neither condition is
     *  met
     */
    private String followOrUnfollow(UserAccount user) {
        if (user.getFollowerids().contains(UserSystem.getInstance().getCurrentUser().getId())) {
            return "Unfollow";
        } else if (!user.getFollowerids().contains(UserSystem.getInstance().getCurrentUser().getId())) {
            return "Follow";
        }
        return null;
    }
}