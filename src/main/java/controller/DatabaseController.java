package controller;

import model.*;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseController {
    // Singleton instance
    private static DatabaseController instance;
    private static ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static ReentrantReadWriteLock.ReadLock READ_LOCK = LOCK.readLock();
    private static ReentrantReadWriteLock.WriteLock WRITE_LOCK = LOCK.writeLock();

    private DatabaseController() {}

    /**
     * Singleton implementation of this class, ensures that only one can be istantiated
     * @return instance of this singleton
     */
    public static DatabaseController getInstance() {
        if (instance == null) {
            synchronized (DatabaseController.class) {
                if (instance == null) {
                    instance = new DatabaseController();
                }
            }
        }
        return instance;
    }

    // Path to the database file
    public static final String BASE_PATH = "jdbc:sqlite:./src/main/resources/";
    public static final String DB_PATH = BASE_PATH + "database.db";

    // SQL statements to create tables
    public static final String CREATE_ADMIN_TABLE = """
            CREATE TABLE IF NOT EXISTS admins (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
            """;

    public static final String CREATE_USER_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                numFollowers INTEGER DEFAULT 0
            )
            """;

    public static final String CREATE_POST_TABLE = """
            CREATE TABLE IF NOT EXISTS posts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                content TEXT NOT NULL,
                numLikes INTEGER DEFAULT 0,
                datePosted INTEGER NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(id)
            )
            """;

    public static final String CREATE_FOLLOW_TABLE = """
            CREATE TABLE IF NOT EXISTS follows (
                followerId INTEGER NOT NULL,
                followeeId INTEGER NOT NULL,
                FOREIGN KEY (followerId) REFERENCES users(id),
                FOREIGN KEY (followeeId) REFERENCES users(id),
                PRIMARY KEY (followerId, followeeId)
            )
            """;

    public static final String CREATE_LIKE_TABLE = """
            CREATE TABLE IF NOT EXISTS likes (
                userId INTEGER NOT NULL,
                postId INTEGER NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(id),
                FOREIGN KEY (postId) REFERENCES posts(id),
                PRIMARY KEY (userId, postId)
            )
            """;

    public static final String CREATE_USER_REPORT_TABLE = """
            CREATE TABLE IF NOT EXISTS "user_reports" (
            	"id"	INTEGER,
            	"reason"	TEXT NOT NULL,
            	"status"	TEXT NOT NULL,
            	"date"	INTEGER NOT NULL,
            	"reporterId"	INTEGER NOT NULL,
            	"reporteeId"	INTEGER NOT NULL,
            	PRIMARY KEY("id"),
            	FOREIGN KEY("reporteeId") REFERENCES "users"("id"),
            	FOREIGN KEY("reporterId") REFERENCES "users"("id")
            );
            """; //Possibly change adminId to be not null?

    public static final String CREATE_POST_REPORT_TABLE = """
            CREATE TABLE IF NOT EXISTS "post_reports" (
            	"id"	INTEGER,
            	"reason"	TEXT NOT NULL,
            	"status"	TEXT NOT NULL,
            	"date"	INTEGER NOT NULL,
            	"reporterId"	INTEGER NOT NULL,
            	"postId"	INTEGER NOT NULL,
            	PRIMARY KEY("id"),
            	FOREIGN KEY("postId") REFERENCES "posts"("id"),
            	FOREIGN KEY("reporterId") REFERENCES "users"("id")
            );
            """; //Possibly change adminId to be not null?

    /**
     * Connects to a local db file
      * @param path the path to the db file
     * @return the connection to the db
     */
    public static Connection connect(String path) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(path);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Creates a table in the database
     * @param sql the CREATE statement to execute
     */
    public static void createTable(String sql) {
        if (!sql.toUpperCase().contains("CREATE")) {
            throw new IllegalArgumentException("SQL statement must be a CREATE statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * creates all tables within the db
     */
    public static void createAllTables() {
        createTable(CREATE_ADMIN_TABLE);
        createTable(CREATE_USER_TABLE);
        createTable(CREATE_POST_TABLE);
        createTable(CREATE_FOLLOW_TABLE);
        createTable(CREATE_LIKE_TABLE);
        createTable(CREATE_USER_REPORT_TABLE);
        createTable(CREATE_POST_REPORT_TABLE);
    }

    /**
     * Alter a table in the database
     * @param sql the ALTER statement to execute
     */
    public static void alterTable(String sql) {
        if (!sql.toUpperCase().contains("ALTER")) {
            throw new IllegalArgumentException("SQL statement must be an ALTER statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * Drops a table in the database
     * @param sql the DROP statement to execute
     */
    public static void dropTable(String sql) {
        if (!sql.toUpperCase().contains("DROP")) {
            throw new IllegalArgumentException("SQL statement must be a DROP statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * Inserts a record into a table
     * @param sql the INSERT statement to execute
     */
    public static void insertRecord(String sql) {
        if (!sql.toUpperCase().contains("INSERT")) {
            throw new IllegalArgumentException("SQL statement must be an INSERT statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * Updates a record in a table
     * @param sql the UPDATE statement to execute
     */
    public static void updateRecord(String sql) {
        if (!sql.toUpperCase().contains("UPDATE")) {
            throw new IllegalArgumentException("SQL statement must be an UPDATE statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * Deletes a record from a table
     * @param sql the DELETE statement to execute
     */
    public static void deleteRecord(String sql) {
        if (!sql.toUpperCase().contains("DELETE")) {
            throw new IllegalArgumentException("SQL statement must be a DELETE statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * Selects a record from a table
     * @param sql the SELECT statement to execute
     */
    public static void selectRecord(String sql) {
        if (!sql.toUpperCase().contains("SELECT")) {
            throw new IllegalArgumentException("SQL statement must be a SELECT statement");
        }
        executeDdlAndDml(sql);
    }

    /**
     * Executes a DDL or DML statement
     * @param sql the statement to execute
     */
    private static void executeDdlAndDml(String sql) {
        WRITE_LOCK.lock();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    /**
     * Generates an INSERT statement for a table
     * @param tableName the table to insert into
     * @param params the values to insert
     * @return the INSERT statement
     */
    public static String generateInsertStatement(String tableName, Object... params) {
        String[] columns = getColumns(tableName);

        if (columns.length - 1 != params.length) {
            throw new IllegalArgumentException("The number of columns must match the number of parameters.");
        }

        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(tableName).append(" (");

        // Append column names, starting from the second column because the first column is the ID
        for (int i = 1; i < columns.length; i++) {
            sb.append(columns[i]);
            if (i < columns.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(") VALUES("); // Append the table name and VALUES keyword

        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            if (i < params.length - 1) { // Append values with a comma
                if (value instanceof String) {
                    sb.append("'").append(value).append("', ");
                } else {
                    sb.append(value).append(", ");
                }
            } else { // Append the last value without a comma
                if (value instanceof String) {
                    sb.append("'").append(value).append("'");
                } else {
                    sb.append(value);
                }
            }
        }
        sb.append(")"); // Close the VALUES clause
        return sb.toString(); // Return the complete INSERT statement
    }

    /**
     * Takes all parameters for insert (usage for many-to-many tables)
     * @param tableName name of table
     * @param params values to insert
     * @return INSERT statement
     */
    public static String generateFullInsertStatement(String tableName, Object... params) {
        StringBuilder sb = new StringBuilder("INSERT INTO "); // Start building the INSERT statement

        sb.append(tableName).append(" VALUES("); // Append the table name and VALUES keyword

        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            if (i < params.length - 1) { // Append values with a comma
                if (value instanceof String) {
                    sb.append("'").append(value).append("', ");
                } else {
                    sb.append(value).append(", ");
                }
            } else { // Append the last value without a comma
                if (value instanceof String) {
                    sb.append("'").append(value).append("'");
                } else {
                    sb.append(value);
                }
            }
        }
        sb.append(")"); // Close the VALUES clause
        return sb.toString(); // Return the complete INSERT statement
    }

    /**
     * Get the column names of a table
     * @param tableName the name of the table
     * @return an array of column names
     */
    private static String[] getColumns(String tableName) {
        READ_LOCK.lock();
        // SQLite database path and table name
        try (Connection conn = connect(DB_PATH)) {
            // Query to fetch one row from the table
            String query = "SELECT * FROM " + tableName + " LIMIT 1";

            // Execute the query and retrieve metadata
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                // Get the ResultSetMetaData
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Create an array to hold the column names
                String[] columnNames = new String[columnCount];

                // Loop through the columns and get their names
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }

                return columnNames;
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            return null;
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * Generates an UPDATE statement for a table
     * @param tableName the table to update
     * @param params the values to update
     * @return the UPDATE statement
     */
    public static String generateUpdateStatement(String tableName, Object... params) {
        StringBuilder sb = new StringBuilder("UPDATE "); // Start building the UPDATE statement
        sb.append(tableName).append(" SET "); // Append the table name and SET keyword

        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            if (i < params.length - 4) { // Handle columns and values before the WHERE clause
                if (i % 2 == 0) {
                    sb.append(value).append(" = "); // Append column name
                } else {
                    if (value instanceof String) {
                        sb.append("'").append(value).append("', "); // Append string value
                    } else {
                        sb.append(value).append(", "); // Append non-string value
                    }
                }
            } else if (i < params.length - 2) { // Handle the last column-value pair before the WHERE clause
                if (i % 2 == 0) {
                    sb.append(value).append(" = "); // Append column name
                } else {
                    if (value instanceof String) {
                        sb.append("'").append(value).append("'"); // Append string value
                    } else {
                        sb.append(value); // Append non-string value
                    }
                }
            } else { // Handle the WHERE clause
                if (i % 2 == 0) {
                    sb.append(" WHERE ").append(value).append(" = "); // Append WHERE column name
                } else {
                    if (value instanceof String) {
                        sb.append("'").append(value).append("'"); // Append string value
                    } else {
                        sb.append(value); // Append non-string value
                    }
                }
            }
        }
        return sb.toString(); // Return the complete UPDATE statement
    }

    /**
     * Generates a DELETE statement for a table
     * @param tableName the table to delete from
     * @param params the values to delete
     * @return the DELETE statement
     */
    public static String generateDeleteStatement(String tableName, Object... params) {
        StringBuilder sb = new StringBuilder("DELETE FROM "); // Start building the DELETE statement
        sb.append(tableName)
                .append(" WHERE "); // Append the WHERE clause

        // Loop through params and construct conditions
        for (int i = 0; i < params.length; i += 2) {
            String column = (String) params[i];
            Object value = params[i + 1];

            // Add column and value condition
            sb.append(column)
                    .append(" = ");

            if (value instanceof String) {
                sb.append("'").append(value).append("'");
            } else {
                sb.append(value);
            }

            // Append "AND" if more conditions follow
            if (i < params.length - 2) {
                sb.append(" AND ");
            }
        }

        return sb.toString(); // Return the complete DELETE statement
    }

    /**
     * Generates a SELECT statement for a table
     * @param tableName the table to select from
     * @param params the values to select
     * @return the SELECT statement
     */
    public static String generateSelectStatement(String tableName, Object... params) {
        StringBuilder sb = new StringBuilder("SELECT * FROM "); // Start building the SELECT statement
        sb.append(tableName)
                .append(" WHERE ") // Append the WHERE clause
                .append(params[0])
                .append(" = ");

        Object value = params[1];
        value = (value instanceof String) ?
                "'" + value + "'" : // Append string value with quotes
                value; // Append non-string value
        sb.append(value);

        return sb.toString(); // Return the complete SELECT statement
    }

    /**
     * Selects all admins from the database
     * @return a list of all admins
     */
    public static List<AdminAccount> selectAllAdmins() {
        READ_LOCK.lock();
        String sql = "SELECT * FROM admins";

        List<AdminAccount> admins = new ArrayList<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                AdminAccount admin = new AdminAccount(id, name, email, username, password);
                admins.add(admin);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return admins;
    }

    /**
     * Selects all user accounts in the database
     * @param sql the SELECT statement to execute
     * @return a list of all user accounts
     */
    private static List<UserAccount> getUserAccounts(String sql) {
        READ_LOCK.lock();
        List<UserAccount> searchResults = new ArrayList<>();

        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int numFollowers = resultSet.getInt("numFollowers");
                UserAccount user = new UserAccount(id, name, email, username, password, numFollowers);
                searchResults.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return searchResults;
    }

    /**
     * Selects all users from the database
     * @return a list of all users
     */
    public static List<UserAccount> selectAllUsers() {
        String sql = "SELECT * FROM users";
        return getUserAccounts(sql);
    }

    /**
     * Selects a user account from the database by username
     * @param searchQuery the username to search for
     * @return a list of user accounts that match the search query
     */
    public static List<UserAccount> selectUserAccountSearchResults(String searchQuery) {
        String sql = "SELECT * FROM users" + " WHERE username LIKE '%'" + searchQuery + '%';
        return getUserAccounts(sql);
    }

    /**
     * Selects all posts within db
     * @return List of all posts
     */
    public static List<Post> selectAllPosts() {
        READ_LOCK.lock();
        String sql = "SELECT * FROM posts";

        List<Post> posts = new ArrayList<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userId = resultSet.getInt("userId");
                String text = resultSet.getString("content");
                int likes = resultSet.getInt("numLikes");
                Date datePosted = new Date(resultSet.getLong("datePosted") * 1000L);
                Post post = new Post(id, userId, text, likes, datePosted);
                posts.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return posts;
    }

    /**
     * Selects all posts from the database for a given user account
     * @param account the user account to select posts for
     * @return a list of all posts for the user account
     */
    public static List<Post> selectAllPosts (UserAccount account) {
        READ_LOCK.lock();
        String sql = "SELECT * FROM posts" + " WHERE userId = " + account.getId();

        List<Post> posts = new ArrayList<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userId = resultSet.getInt("userId");
                String text = resultSet.getString("content");
                int likes = resultSet.getInt("numLikes");
                Date datePosted = new Date(resultSet.getLong("datePosted") * 1000L);
                Post post = new Post(id, userId, text, likes, datePosted);
                posts.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return posts;
    }

    /**
     * Selects either all user reports or all post reports from the database
     * @param reportClass the class of the report to select (UserReport or PostReport)
     * @return a list of all reports
     */
    public static <T extends Report> List<T> selectAllReports(Class<T> reportClass) {
        READ_LOCK.lock();
        String table = switch (reportClass.getSimpleName()) {
            case "UserReport" -> "user_reports";
            case "PostReport" -> "post_reports";
            default -> throw new IllegalArgumentException("Invalid report class");
        };
        String sql = "SELECT * FROM " + table;

        List<T> reports = new ArrayList<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String reason = resultSet.getString("reason");
                String stringStatus = resultSet.getString("status");
                Report.Status status = Report.Status.valueOf(stringStatus);
                Date date = new Date(resultSet.getLong("date") * 1000L);
                int reporterId = resultSet.getInt("reporterId");

                if (reportClass == UserReport.class) {
                    int reporteeId = resultSet.getInt("reporteeId");
                    reports.add(reportClass.cast(new UserReport(id, reporterId, status, reason, date, reporteeId)));
                } else if (reportClass == PostReport.class) {
                    int postId = resultSet.getInt("postId");
                    reports.add(reportClass.cast(new PostReport(id, reporterId, status, reason, date, postId)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return reports;
    }

    /**
     * Selects all user reports from the database
     * @return a list of all user reports
     */
    public static List<UserReport> selectAllUserReports() {
        return selectAllReports(UserReport.class);
    }

    /**
     * Selects all post reports from the database
     * @return a list of all post reports
     */
    public static List<PostReport> selectAllPostReports() {
        return selectAllReports(PostReport.class);
    }

    /**
     * Selects all follower accounts for a given user account
     * @param account the user account to select followers for
     * @return a list of all follower accounts
     */
    public static List<UserAccount> selectAllFollows(UserAccount account) {
        READ_LOCK.lock();
        String sql = "SELECT * FROM follows" + "WHERE followerId = " + account.getId();

        List<UserAccount> follows = new ArrayList<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int followeeId = resultSet.getInt("followeeId");
                UserAccount followee = selectAllUsers().stream()
                        .filter(user -> user.getId() == followeeId)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Followee not found"));
                follows.add(followee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return follows;
    }

    /**
     * Selects all user likes for a given post
     * @param post the post to select likes for
     * @return a set of all user likes (unique)
     */
    public static Set<Integer> selectAllUserLikesFromPost(Post post) {
        READ_LOCK.lock();
        String sql = "SELECT * FROM likes" + " WHERE postId = " + post.getId();

        Set<Integer> likes = new HashSet<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                likes.add(userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return likes;
    }

    /**
     * Selects all user IDs who follow the given userAccount.
     * @param userAccount the user account to select followers for
     * @return a set of user IDs who follow the given userAccount
     */
    public static Set<Integer> selectAllUserFollowsFromUser(UserAccount userAccount) {
        READ_LOCK.lock();
        String sql = "SELECT * FROM follows WHERE followeeId = " + userAccount.getId();

        Set<Integer> followerIds = new HashSet<>();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()) {
                int followerId = resultSet.getInt("followerId");
                followerIds.add(followerId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
        return followerIds;
    }

    /**
     * Executes the select post query but with a check
     * @param sql the SELECT statement to execute
     */
    public static List<Post> selectPostRecord(String sql) {
        if (!sql.toUpperCase().contains("SELECT")) {
            throw new IllegalArgumentException("SQL statement must be a SELECT statement");
        }
        return executeSelectPosts(sql);
    }

    /**
     * Executes a select from posts statement with conditionals (WHERE)
     * @param sql sql string to run
     * @return List of posts that match query
     */
    private static List<Post> executeSelectPosts(String sql) {
        READ_LOCK.lock();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<Post> results = new ArrayList<>();
            while (resultSet.next()) {
                Post post = new Post(
                        resultSet.getInt("id"),
                        resultSet.getInt("userId"),
                        resultSet.getString("content"),
                        resultSet.getInt("numLikes"),
                        new Date(resultSet.getLong("datePosted") * 1000L)
                );
                results.add(post);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * Executes the select user query but with a check
     * @param sql the SELECT statement to execute
     */
    public static List<UserAccount> selectUserRecord(String sql) {
        if (!sql.toUpperCase().contains("SELECT")) {
            throw new IllegalArgumentException("SQL statement must be a SELECT statement");
        }
        return executeSelectUser(sql);
    }

    /**
     * Executes a select from users statement with conditionals (WHERE)
     * @param sql sql string to run
     * @return List of users that match query
     */
    private static List<UserAccount> executeSelectUser(String sql) {
        READ_LOCK.lock();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<UserAccount> results = new ArrayList<>();
            while (resultSet.next()) {
                UserAccount user = new UserAccount(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getInt("numFollowers")
                );
                results.add(user);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * Executes the select admin query but with a check
     * @param sql the SELECT statement to execute
     */
    public static List<AdminAccount> selectAdminRecord(String sql) {
        if (!sql.toUpperCase().contains("SELECT")) {
            throw new IllegalArgumentException("SQL statement must be a SELECT statement");
        }
        return executeSelectAdmin(sql);
    }

    /**
     * Executes a select from admins statement with conditionals (WHERE)
     * @param sql sql string to run
     * @return List of admins that match query
     */
    private static List<AdminAccount> executeSelectAdmin(String sql) {
        READ_LOCK.lock();
        try (Connection connection = connect(DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<AdminAccount> results = new ArrayList<>();
            while (resultSet.next()) {
                AdminAccount admin = new AdminAccount(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                results.add(admin);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            READ_LOCK.unlock();
        }
    }
}