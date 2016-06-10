package app.reactive;

import app.UserProfile;
import rx.Observable;

/**
 * @author Chris.Ge
 */
public interface UserProfileService {
    Observable<UserProfile> getUserProfile(Long id);

}
