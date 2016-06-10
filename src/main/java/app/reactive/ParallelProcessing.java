package app.reactive;

import app.ConditionalResources.User;
import app.UserProfile;
import ratpack.func.Pair;
import ratpack.rx.RxRatpack;
import ratpack.server.RatpackServer;
import ratpack.server.Service;
import ratpack.server.StartEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ratpack.jackson.Jackson.json;

/**
 * @author Chris.Ge
 */
public class ParallelProcessing {
    public static void main(String[] args) throws Exception {
        RatpackServer.start(spec -> spec.registryOf(
            r -> r.add(UserService.class, new DefaultUserService())
                .add(UserProfileService.class, new DefaultUserProfileService()).add(new Service() {
                    @Override
                    public void onStart(StartEvent e) {
                        RxRatpack.initialize();
                    }
                })

        ).handlers(chain -> chain.get("user", ctx -> {
                UserService service = ctx.get(UserService.class);
                UserProfileService profile = ctx.get(UserProfileService.class);
                //We can simplify the process of collecting the Observable stream into a List by translating it to a Promise type (which, remember, inherently maps the resulting stream to a List).
                RxRatpack.promise(
                    //The Observable#compose allows us to provide a strategy for parallelizing the stream coming out of the userService.getUsers() call.
                    service.getUserss().compose(RxRatpack::forkEach)
                        .flatMap(user -> profile.getUserProfile(user.id)
                            //We can continue to utilize Ratpack’s functional programming interfaces to help work with our reactive pipeline. Here, we leverage the Pair type, as before, to combine the User and UserProfile objects into a single object for processing downstream.
                            .map(userProfile -> Pair.of(user, userProfile))).map(pair -> {

                        User user = pair.left;
                        UserProfile p = pair.right;
                        Map<String, String> map = new ConcurrentHashMap<>(4);
                        map.put("id", String.valueOf(user.id));
                        map.put("username", p.getUserName());

                        return map;

                    }).serialize()).then(map -> json(map));

            })



        ));

    }

}
