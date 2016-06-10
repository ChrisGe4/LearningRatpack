package app.reactive;

import app.ConditionalResources.User;
import ratpack.exec.Promise;
import ratpack.rx.RxRatpack;
import ratpack.server.RatpackServer;
import rx.Observable;

import java.util.List;

import static ratpack.jackson.Jackson.json;


/**
 * @author Chris.Ge
 */
public class RXtoPromise {
    //observable to promise
    public static void main(String[] args) throws Exception {
        //It only needs to be called once per JVM, regardless of how many Ratpack applications are running within the JVM.
        RxRatpack.initialize();

        RatpackServer.start(spec -> spec.registryOf(registrySpec -> registrySpec
            .add(RxJavaUserService.class, new DefaultRxJavaUserService())).handlers(chain -> chain
            //*An object that can deal with errors that occur during the processing of an exchange.
            //            .register(registrySpec -> registrySpec.add(ServerErrorHandler.class,
            //                (ctx, throwable) -> ctx
            //                    .render("caught by error handler: " + throwable.getMessage())))
            //            .get(ctx -> Observable.<String>error(new Exception("!")).subscribe(ctx::render))));

            .prefix("user", pchain -> pchain.get(":username", ctx -> {

                RxJavaUserService userService = ctx.get(RxJavaUserService.class);
                String username = ctx.getPathTokens().get("username");
                Observable<User> userObs = userService.getUser(
                    username);//Using the RxRatpack#promiseSingle method, we map the Observable to a Promise type and subscribe to it, just as we normally would.
                RxRatpack.promiseSingle(userObs).then(user -> ctx.render(json(user)));

            })).get(ctx -> {
                RxJavaUserService userService = ctx.get(RxJavaUserService.class);
                Observable<User> usersObs = userService.getUsers();
                //Thus, we use the RxRatpack#promise method, which will transform the Observable<User> stream to a Promise<List<User>>, wherein all User objects will be collected and emitted as a single value from the Promise.
                RxRatpack.promise(usersObs)
                    //Because Promise types are guaranteed to always emit a single item, they will also emit null values. Here, we apply a null value check against the user, and map it accordingly or pass-through the null value otherwise.
                    //Using the route method, we supply two parameters: a predicate function and an action function. If the predicate function returns a true response, the action function is invoked.
                    /** It can be useful at the handler layer to provide common validation.*/
                    .route(users -> users == null, users -> {
                        ctx.getResponse().status(404);
                        ctx.render("not found");
                    })
                    // or use .onNull()
                    .then(users -> ctx.render(json(users)));
            })

        ));
    }

    //promise to observable
    public static void main2(String[] args) throws Exception {
        RxRatpack.initialize();
        RatpackServer.start(
            spec -> spec.registryOf(r -> r.add(UserService.class, new DefaultUserService()))
                .handlers(chain -> chain.prefix("user", pchain -> pchain.get(":username", ctx -> {
                    UserService userService = ctx.get(UserService.class);
                    String username = ctx.getPathTokens().get("username");
                    Promise<User> userPromise = userService.getUser(username);
                    //We use the RxRatpack#observe method to map a single object Promise type to an Observable, then we subscribe to the Observable to get the User object.
                    RxRatpack.observe(userPromise).subscribe(user -> ctx.render(json(user)));
                }).get(ctx -> {
                    UserService userService = ctx.get(UserService.class);
                    Promise<List<User>> usersPromise = userService.getUsers();
                    //We can map the Promise<List<User>> type to Observable<User> using the RxRatpack#observeEach.
                    RxRatpack.observeEach(usersPromise).toList()
                        .subscribe(users -> ctx.render(json(users)));
                }))));
    }


}
