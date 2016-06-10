package app.reactive;

import app.ConditionalResources.User;
import rx.Observable;

/**
 * @author Chris.Ge
 */
public class DefaultRxJavaUserService implements RxJavaUserService {
    @Override
    public Observable<User> getUser(String username) {
        return null;
    }

    @Override
    public Observable<User> getUsers() {
        return null;
    }
}
