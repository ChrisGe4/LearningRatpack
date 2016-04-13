package app;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

/**
 * @author Chris Ge.
 *         <p>
 *         When employing the Guice support, we can make use of a factory method off of the ratpack.guice.Guice class
 *         to construct a Guice Injector and build the registry implementation that will resolve components through it.
 */
public class Main_GuiceInjector {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(spec -> spec
            .registry(Guice.registry(r -> r.bind(UserService.class, DefaultUserService.class)))
            //if want to use module, call r.module(module.class)
            .handlers(chain -> chain.get(ctx -> {
                UserService userService = ctx.get(UserService.class);
                userService.list().then(users -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append('[');
                    for (User user : users) {
                        sb.append(jsonify(user));
                    }
                    sb.append(']');
                    ctx.getResponse().contentType("application/json");
                    ctx.render(sb.toString());
                });
            })));
    }

    private static String jsonify(User user) {
        return "{ \"username\": \"" + user.getName() + "\", \"email\": \"" + user.getEmail()
            + "\" }";
    }
}
