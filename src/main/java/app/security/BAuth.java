package app.security;

import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;
import ratpack.guice.Guice;
import ratpack.pac4j.RatpackPac4j;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;

/**
 * @author Chris.Ge
 */
public class BAuth {

    /*

    If you run this application and open your browser to http://localhost:5050, you will be met with a simple, “Not Authenticated!” message. This demonstrates how we have protected the / endpoint from unauthenticated requests. Redirecting your browser location to http://localhost:5050/auth, you will find that you are met with a basic authentication dialog, where you are asked for a username and password. The demonstration here is using the SimpleTestUsernamePasswordAuthenticator, which simply checks that the username matches the password. As noted in the application listing’s callouts, you should not use this authenticator for anything more than demonstration’s sake. If you type a matching username and password into your browser’s authentication prompt, you will be redirected to the / endpoint, and this time you will be met with the “Hello, <username>!” message.

If you next direct your browser to your application’s /logout endpoint, you will find that you are then redirected back to / and met again with the “Not Authenticated!” message. Your authentication has been invalidated.


This time, if you specify the username and password combination from your configuration file, you will find that you are now validated according to your application’s configuration!

     */


    public static void main(String[] args) throws Exception {
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec.serverConfig(
            //Within the serverConfig block of our application definition (which you should be familiar with now), we specify that we want to consume the app.yml YAML configuration file, and allow configuration overrides via Java System Properties or appropriately named environment variables. Finally, we map the structure of the security configuration from our YAML file to the SecurityConfig class. As you well know by now, the require(..) syntax maps the configuration and places the resulting object in the registry, for use within our application handlers.
            serverConfigBuilder -> serverConfigBuilder.yaml("config.yml").sysProps().env()
                .require("/security", SecurityConfig.class))
            //As noted, we must have HTTP sessions available to work with authentication. Here, we apply the SessionModule, though your application may leverage any of the session capabilities outlined previously.
            .registry(Guice.registry(bindingsSpec -> bindingsSpec.module(SessionModule.class)))
            .handlers(chain -> chain.all(
                //The mechanism that performs authentication should be applied as an all handler, so that different HTTP verbs do not gain unintended access to protected resources. The helper methods from RatpackPac4j provide Handler implementations, so note here that we do not construct a handler ourselves, as we would by providing a Closure. Instead, we simply make a method call to all.
                //RatpackPac4j#authenticator is the mechanism by which we will supply the Pac4j client. The handler provided from here will incorporate the provided Pac4j client into the context registry for use downstream when we wish to actually initiate the login/authentication sequence.

                RatpackPac4j.authenticator(
                    //In this example, we make use of the IndirectBasicAuthClient, which is provided to our project by the pac4j-http dependency.
                    new IndirectBasicAuthClient(
                        /*//(just for testing)The IndirectBasicAuthClient takes a UsernamePasswordAuthenticator implementation to perform the authentication. This gives us a great deal of flexibility in determining the exact manner in which we authenticate a client. For the purposes of demonstration, we will use the SimpleTestUsernamePasswordAuthenticator, though it should be noted that this is strictly for demonstrative purposes and is not something you want to bring into a real-world application.
                        new SimpleTestUsernamePasswordAuthenticator(),*/
                        //Instead of using the SimpleTestUsernamePasswordAuthenticator here, we now supply our MapUsernamePasswordAuthenticator. Note that the argument to the constructor is the userPassMap from our SecurityConfig class, so at this point, we can get the SecurityConfig object (which has our configuration values mapped to it) from the registry, and provide the `` to the MapUsernamePasswordAuthenticator.
                        new MapUsernamePasswordAuthenticator(
                            chain.getRegistry().get(SecurityConfig.class).basic.userPassMap),
                        //Most appropriate for basic authentication is the AuthenticatorProfileCreator, which creates a limited profile with the username data stored. This is provided by pac4j-http and can be used to get access to the authenticated username, as demonstrated further down in the application.
                        AuthenticatorProfileCreator.INSTANCE))

                //Here, we provide an endpoint that initiates user authentication.
                ).get("auth",
                //The RatpackPac4j#login method gets the client implementation that was placed in the context registry earlier and initiates the authentication sequence.
                ctx -> RatpackPac4j.login(ctx, IndirectBasicAuthClient.class)
                    //Upon successful authentication, the request is redirected to the / endpoint, which represents the application’s protected resource in this demonstration.
                    .then(httpProfile -> ctx.redirect("/")))
                    //The handler defined here is the application’s protected resource, and the behavior for interacting with the client is determined by whether they have successfully authenticated or not.
                    .get(
                        //We use the RatpackPac4j#userProfile method to extract the profile that was created by the client. The profile is retrieved from the HTTP session, so subsequent requests do not require re-authentication to gain access to protected resources.
                        ctx -> RatpackPac4j.userProfile(ctx)
                            //The presence of a user profile indicates successful authentication, so here we can use the Promise#route method to route the response according to whether there is a user profile present in the session. The RatpackPac4j#userProfile method returns an Optional<T>, so the predicate condition checks whether the Optional type has a value or not. If it does, then we render a “Hello, <username>!” message back to the client.
                            .route(userProfile -> userProfile.isPresent(),
                                userProfile -> ctx.render("Hello ${userProfile.get().getId}"))
                            //   If there is no user profile in the session, then we render an “unauthenticated” response via the then method.
                            .then(userProfile -> ctx.render("Not Authenticated!")))
                    //This provides the endpoint for a user to log out when they are finished with their session.
                    .get("logout",
                        //The RatpackPac4j#logout method invalidates the current session’s request.
                        ctx -> RatpackPac4j.logout(ctx)
                            //After logout, we redirect the user back to /.
                            .then(() -> ctx.redirect("/")))


            ));
    }
}
