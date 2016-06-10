package app.reactive;

import app.ConditionalResources.User;
import ratpack.exec.Promise;

import java.util.List;

/**
 * @author Chris.Ge
 */
public class DefaultUserService implements UserService {
    @Override
    public Promise<User> getUser(String username) {
        return null;
    }

    @Override
    public Promise<List<User>> getUsers() {
        return null;
    }
}
