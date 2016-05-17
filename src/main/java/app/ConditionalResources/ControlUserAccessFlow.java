package app.ConditionalResources;

import ratpack.form.Form;
import ratpack.guice.Guice;
import ratpack.handling.Context;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.session.Session;
import ratpack.session.SessionModule;

/**
 * @author Chris.Ge
 */
public class ControlUserAccessFlow {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(

            ratpackServerSpec -> ratpackServerSpec
                .registry(Guice.registry(r -> r.module(SessionModule.class)))
                .handlers(chain -> chain.post("login", ctx -> {
                        //Build a POST handler to the /login endpoint and access the Session object from the registry;
                        Session session = ctx.get(Session.class);
                        //parse the urlencoded form variables;
                        //flatMap: This is useful when the transformation involves an asynchronous operation.
                        ctx.parse(Form.class).flatMap(form -> session.getData().map(
                            sessionData -> {//access the Session’s data (the getData() call returns a Promise, because it may involve a blocking operation);
                                if (form.get("username") == "admin"
                                    && form.get("password") == "password") {
                                    sessionData.set("username", "admin");
                                } else {
                                    sessionData.set("username", "anonymous");

                                }
                                return ctx;
                                //???????????????
                            })).then(ctx1 -> ctx1
                            .redirect("/"));//once the work is done on the session, redirect to /.


                /*    ctx.get(Session.class).getData().flatMap(sessionData -> {

                        ctx.parse(Form.class).map(form -> {
                            if (form.get("username") == "admin"
                                && form.get("password") == "password") {
                                sessionData.set("username", "admin");
                            } else {
                                sessionData.set("username", "anonymous");

                            }
                            return ;
                        });
                        return sessionData;
                    });*/


                    }).all(ctx -> {

                        ctx.get(Session.class).getData().then(sessionData -> {
                            User user = new User((String) sessionData.require("username"));
                            ctx.next(Registry.single(User.class, user));

                        });

                    }



                    ).when(context -> {

                        Context ctx1 = (Context) context;

                    })



                ));
    }

}
