package app;

import ratpack.handling.Context;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;

/**
 * @author Chris Ge.
 *         <p>
 *         Every request that comes into your handler chain has a new context object created, and thus a new registry,
 *         so the context can serve as a way for upstream handlers in your chain to communicate downstream
 *         <p>
 *         <p>
 *         Consider the scenario that your application needs to perform security authorization on a request prior to serving data.
 *         Assume that an access token will be supplied as a header in the request, and from within a resource’s handler we need
 *         to ensure that the client has access to the data. Multiple handlers will likely need to perform this type of authorization check,
 *         so a good practice in this case is to create a top-level handler that lookups up the user’s profile based on the provided token,
 *         and provide that profile to downstream handlers
 */
public class Main_toplevelhandler_authorization {

    private static final String AUTH_HEADER = "x-auth-token";


    public static void main(String[] args) {
        RatpackServer.start(
            //We start the above handler chain by attaching an all handler, which inspects every incoming request
            spec -> spec.registryOf(r -> r.add(UserService.class, new DefaultUserService()))
                .handlers(chain -> chain.all(ctx -> {
                    if (ctx.getResponse().getHeaders().contains(AUTH_HEADER)) {
                        String token = ctx.getRequest().getHeaders().get(AUTH_HEADER);
                        UserService userService = ctx.get(UserService.class);
                        userService.getProfileByToken(token)
                            //Once we get the user’s profile, we can manage data flow to the downstream handler by creating a single object registry and joining it to the context registry through this
                            .then(profile -> ctx.next(Registry.single(profile)));
                        /*
                        The new single-object registry that we have attached to the context object acts as a child registry.
                        The registry contract provides that objects will be first resolved from child registries;
                        if the object does not exist in a child registry, then the parent’s registry will be asked for the object.
                        This pattern is what allows the context to remain an immutable registry, while still affording data flow through the handler chain.
                         */

                    } else {
                        unauthorized(ctx);
                    }
                }).get("users/:username", ctx -> {
                    //we can now extract the UserProfile class that was provided upstream,
                    UserProfile profile = ctx.get(UserProfile.class);
                    if (profile.isAuthorized("showuser", profile.getUserName())) {
                        UserService userService = ctx.get(UserService.class);
                        userService.getUser(ctx.getPathTokens().get("username")).then(

                            user -> {

                                ctx.getResponse().contentType("application/json");
                                ctx.render(jsonify(user));

                            }

                        )
                    } else {
                        unauthorized(ctx);
                    }
                })));
    }

    private static void unauthorized(Context ctx) {
        ctx.getResponse().status(401);
        ctx.getResponse().send();
        

    }

    private static String jsonify(User user) {
        return "{ \"username\": \"" + user.getName() + "\", \"email\": \"" + user.getEmail()
            + "\" }";
    }
}
