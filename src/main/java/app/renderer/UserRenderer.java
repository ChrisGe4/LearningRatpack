package app.renderer;

import app.User;
import ratpack.handling.Context;
import ratpack.render.Renderer;

import static ratpack.jackson.Jackson.toJson;

/**
 * @author Chris.Ge
 */
public class UserRenderer implements Renderer<User> {
    @Override
    public Class<User> getType() {
        return null;
    }

    @Override
    public void render(Context context, User user) throws Exception {
        boolean showAll = context.getRequest().getQueryParams().containsKey("showAll")
            && context.getRequest().getQueryParams().get("showAll") == "true";

        if (showAll) {
            context.render(toJson(context).apply(user));
        } else {
            context.render(toJson(context).apply(user.getName()));
        }
    }


}
