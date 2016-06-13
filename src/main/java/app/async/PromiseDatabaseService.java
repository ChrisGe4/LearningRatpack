package app.async;

import app.User;
import app.UserProfile;
import ratpack.exec.Promise;

/**
 * @author Chris.Ge
 */
public class PromiseDatabaseService {

    private final AsyncDatabaseService db;
    public Promise<User> find

    public PromiseDatabaseService(AsyncDatabaseService db) {
        this.db = db;
    }

    public Promise<User> findByUsername(String username) {
        //Promise#async method, to which we are given access to an object we can use to fulfill the promise once the data becomes available,
        //shown here as down.success(result).
        return Promise.async(downstream -> db
            .findByUsername(username, user -> downstream.success(user),
                error -> downstream.error(error)));
    }

    public Promise<UserProfile> loadUserProfile(Long profileId) {
        return Promise.async(down -> db.loadUserProfile(profileId, profile -> down.success(profile),
            error -> down.error(error)));

    /*
        It is important to note that when adapting asynchronous libraries that have their own threading and execution model,
        once the data from their thread of execution fulfills the promise, the processing is returned to the originating thread of
        execution in Ratpack’s execution model. While this will eventually make downstream processing of multiple data objects faster lower in the execution chain, you will likely experience a CPU context switch when moving data between threads
         */
    }

}