package app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Ge.
 */
public class DefaultUserService implements UserService {
    @Override
    public List<User> list() {
        return new ArrayList<>();
    }

    @Override
    public UserProfile getProfileByToken(String token) {
        return null;
    }
}
