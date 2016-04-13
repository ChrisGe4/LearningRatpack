package app;

import ratpack.server.RatpackServer;

/**
 * @author Chris Ge
 */
public class Main_registryOf {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
            //we can interface with the user registry through a call to registryOf at <1>. Within this configuration call, we can now provide a binding for use later in the application
            .registryOf(reg -> reg.add(UserService.class, new DefaultUserService()))
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
