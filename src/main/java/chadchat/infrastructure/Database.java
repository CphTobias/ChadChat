package chadchat.infrastructure;

import chadchat.domain.User;
import chadchat.domain.UserRepository;

import java.sql.*;
import java.util.ArrayList;

public class Database implements UserRepository {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chadchat";

    //  Database credentials
    private static final String USER = "chadchat";

    // Database version
    private static final int version = 1;

    public Database() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        if (getCurrentVersion() != getVersion()) {
            throw new IllegalStateException("Database in wrong state");
        }
    }

    @Override
    public Iterable<User> findAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, null)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM users;");
            while (rs.next()) {
                User u = new User(rs.getInt("id"),
                        rs.getString("name"));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return users;
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, null)) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (name) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return user.withId(rs.getInt(1));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getVersion() {
        return version;
    }

    public static int getCurrentVersion() {
        try (Connection conn = getConnection()) {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT value FROM properties WHERE name = 'version';");
            if(rs.next()) {
                String column = rs.getString("value");
                return Integer.parseInt(column);
            } else {
                System.err.println("No version in properties.");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, null);
    }
}
