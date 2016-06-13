package app.async;

import app.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import ratpack.exec.ExecController;
import ratpack.exec.Execution;
import ratpack.http.client.HttpClient;
import ratpack.server.Service;
import ratpack.server.StartEvent;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A perfect example of background processing that needs Ratpack’s execution model is a periodic thread that uses the
 * framework’s nonblocking HTTP client library to call an external HTTP service and store the resulting data in local cache.
 * The background thread will not already have an execution bound, so we can create one on our own and bind it accordingly.
 * As is true with most things in Ratpack, the semantics for accomplishing this are concise.
 *
 * @author Chris.Ge
 */

//The UserService class starts out by implementing the ratpack.server.Service special type and the Runnable interface.
// We will use the class itself as the Runnable for the periodic operation. Through the Service interface,
// Ratpack provides startup and shutdown hooks to when the application starts and stops, respectively.
public class ExecutionForking implements Service, Runnable {

    static String USER_SERVICE_URI = "https://user-service.internal";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final Map<String, User> userCache = Maps.newConcurrentMap();

    @Inject
    public ExecutionForking(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        Execution.fork().onError(throwable -> throwable
            .printStackTrace())//Prior to the call to start, we attach our execution-wide error handler using the onError method,error handling is applied in the context of the forked execution
            .start(
                execution -> {//Because the thread that is produced by the executor is outside the context of a request’s lifecycle, we need to explicitly start a new execution on it. Ratpack provides a static method, Execution.fork(), to build an execution on the current thread. When we call the start method on the ExecutionBuilder, the execution is tied to the current thread. We are given access to the execution object, denoted by the e variable, but we do not explicitly need it, as Promise types will automatically be tied to the thread’s current execution.
                    httpClient.get(new URI(USER_SERVICE_URI + "/users"))
                        //.onError(throwable -> throwable.printStackTrace())  //you can use this on any promise
                        .map(receivedResponse -> {
                            return (List<User>) mapper
                                .readValue(receivedResponse.getBody().getBytes(),
                                    new TypeReference<List<User>>() {
                                    });
                        }).then(users -> {
                        for (User user : users) {
                            userCache.put(user.getName(), user);
                        }
                    });
                }

            );
    }


    @Override
    public void onStart(StartEvent event) throws Exception {
        ExecController execController = event.getRegistry().get(ExecController.class);
        execController.getExecutor().scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
    }

    public User findByUsername(String username) {
        return userCache.containsKey(username) ? userCache.get(username) : null;
    }

    public List<User> list() {
        return Lists.newArrayList(userCache.values());
    }
}
