package app.session;

import ratpack.exec.Promise;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import ratpack.session.Session;
import ratpack.session.SessionModule;

import java.util.Optional;

import static ratpack.jackson.Jackson.json;

/**
 * @author Chris.Ge
 */
public class CheckViewCount {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
            .registry(Guice.registry(bindingsSpec -> bindingsSpec.module(SessionModule.class)))
            .handlers(chain -> chain.all(ctx -> {
                    Session session = ctx.get(Session.class);
                    session.get("view-tracker").flatMap(o -> {
                        Optional<ViewTracker> vto = (Optional<ViewTracker>) o;
                        ViewTracker tracker = vto.orElse(new ViewTracker());
                        tracker.increment(ctx.getRequest().getUri());
                        //Note that Session#set is an Operation type, so we must map it to a Promise type using promise() to be compatible with flatMap.
                        return Promise.value(session.set("view-tracker", tracker));
                    }).then(
                        p -> ctx.next());// we delegate processing downstream. NOT SURE THIS IS RIGHT
                }

                ).all(ctx -> {
                    Session session = ctx.get(Session.class);
                    session.get("view-tracker").then(o -> ctx.render(json(o.get())));
                })


            ));
    }

}
