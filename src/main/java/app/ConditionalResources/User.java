package app.ConditionalResources;

/**
 * @author Chris.Ge
 */
public class User {

    String username;

    public User(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return username == "admin";

    }
}
