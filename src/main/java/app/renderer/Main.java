package app.renderer;

import app.DefaultUserService;
import app.UserService;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

/**
 * @author Chris.Ge
 */
public class Main {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(spec -> spec.registry(Guice.registry(
            bindingsSpec -> bindingsSpec.bind(UserService.class, DefaultUserService.class)
                .bind(UserRenderer.class)))
            .handlers(chain -> chain.prefix("users", chain1 -> chain1.get(":username", ctx -> {
                UserService userService = ctx.get(UserService.class);
                userService.getUser(ctx.getPathTokens().get("username")).then(ctx::render);
            }))));
    }
}
