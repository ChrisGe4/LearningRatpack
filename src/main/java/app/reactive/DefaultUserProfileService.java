package app.reactive;

import app.UserProfile;
import rx.Observable;

/**
 * @author Chris.Ge
 */
public class DefaultUserProfileService implements UserProfileService {
    @Override
    public Observable<UserProfile> getUserProfile(Long id) {
        return null;
    }
}
