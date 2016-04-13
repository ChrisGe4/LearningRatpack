package app;


import java.util.List;

/**
 * @author Chris Ge.
 */
public interface UserService {
    public List<User> list();

    public UserProfile getProfileByToken(String token);
}
