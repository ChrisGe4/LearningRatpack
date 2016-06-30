package app.session;

import ratpack.form.Form;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

/**
 * @author Chris.Ge
 */
public class UseCookies {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(

            ratpackServerSpec -> ratpackServerSpec
                .registry(Guice.registry(r -> r.module(TextTemplateModule.class)))
                .handlers(chain -> chain.post("updatePosition", ctx -> {
                        ctx.parse(Form.class).then(form -> {
                            ctx.getResponse()
                                .cookie("ratpack-view-position", form.get("next_pos"));//????
                            ctx.redirect("/");



                        });
                    }).get(ctx -> {

                        int position =
                            Integer.parseInt(ctx.getRequest().oneCookie("ratpack-view-position"))


                    })

                )


        );
    }

}
