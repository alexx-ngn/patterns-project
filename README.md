# 𝕐 - A Twitter Clone

𝕐 is a social media platform where users can create accounts, curate profiles by posting text-based content, follow other accounts, and view a home feed of posts from users they follow. Users can like posts and report posts or users for inappropriate content. Admins can review reports and take action by deleting posts or banning users.

## Features

### User Capabilities
- **Create Account:** Users can sign up with basic information and log in with their credentials.
- **Create Post:** Users can submit text-based posts, which will appear on their profile and in the home feed of their followers.
- **Like Posts:** Users can like posts from others, and the number of likes will be reflected in real time.
- **Follow Accounts:** Users can follow other accounts to see their posts in the home feed.
- **View Profiles and Posts:** Users can visit other profiles to view their posts.
- **Report Posts or Users:** Users can report posts or accounts with specific reasoning.

### Admin Capabilities
- **View Reports:** Admins can review reports submitted by users through an admin-only report interface.
- **Delete Posts / Ban Accounts:** Admins have the authority to delete problematic posts and ban users based on reports.

### Tech Stack
- **Backend:** Java
- **Frontend:** Java Swing (or another UI framework)
- **Pattern:** MVC (Model-View-Controller)

## Design Paradigm: MVC Architecture

The application is built using the **Model-View-Controller (MVC)** architecture:

- **Model:**  
  Manages the core data and business logic of the application. It handles interactions with the account and post databases.
  
  - `User Model`: Manages user data, such as usernames, profiles, and follow lists.
  - `Post Model`: Handles posts, timestamps, likes, and associations with users.
  - `Report Model`: Tracks user reports and their statuses.

- **View:**  
  The GUI, responsible for displaying user interfaces such as the login page, home feed, profile pages, and admin reports.

- **Controller:**  
  Handles user inputs and system interactions. It processes actions such as creating accounts, posting, liking, reporting, and admin actions (like deleting posts and banning users).