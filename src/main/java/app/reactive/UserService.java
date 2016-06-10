package app.reactive;

/**
 * @author Chris.Ge
 */
import app.ConditionalResources.User;
import ratpack.exec.Promise;
import rx.Observable;

import java.util.List;

public interface UserService {
    Promise<User> getUser(String username);

    Promise<List<User>> getUsers();



    Observable<User> getUserss();
}
