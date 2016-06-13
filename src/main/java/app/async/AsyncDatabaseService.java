package app.async;

import app.User;
import app.UserProfile;

import java.util.function.Consumer;

/**
 * @author Chris.Ge
 */
public interface AsyncDatabaseService {

    void findByUsername(String username, Consumer<User> callback, Consumer<Throwable> error);

    void loadUserProfile(Long profileId, Consumer<UserProfile> callback, Consumer<Throwable> error);
}
