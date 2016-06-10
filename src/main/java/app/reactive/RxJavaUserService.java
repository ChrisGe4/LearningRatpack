package app.reactive;

/**
 * @author Chris.Ge
 */
import app.ConditionalResources.User;
import rx.Observable;

public interface RxJavaUserService {
    Observable<User> getUser(String username);

    Observable<User> getUsers();
}
