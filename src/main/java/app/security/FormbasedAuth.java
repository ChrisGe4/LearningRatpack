package app.security;

import com.google.common.collect.ImmutableMap;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.pac4j.RatpackPac4j;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;

import java.util.Collections;

import static ratpack.groovy.Groovy.groovyTemplate;

/**
 * @author Chris.Ge
 */
public class FormbasedAuth {
    public static void main(String[] args) throws Exception {

        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
            //As noted, we must have HTTP sessions available to work with authentication. Here, we apply the SessionModule, though your application may leverage any of the session capabilities outlined previously.
            .registry(Guice.registry(bindingsSpec -> bindingsSpec.module(SessionModule.class)
                //For the purposes of this web application, we will leverage Ratpack’s Groovy Text Template support for rendering HTML pages, so we must bind the TextTemplateModule.
                .module(TextTemplateModule.class))).handlers(chain -> {
                    //Within the handler chain, we can construct the FormClient for use as part of the authentication flow. The first argument we supply is the authentication endpoint, defined here as auth; the second argument is the UsernamePasswordAuthenticator. As noted, here we will use the SimpleTestUsernamePasswordAuthenticator again for the purposes of demonstration; the third and final argument we supply is the UsernameProfileCreator, so that we can capture the logged-in user’s details in the protectedIndex.
                    FormClient fc =
                        new FormClient("auth", new SimpleTestUsernamePasswordAuthenticator(),
                            AuthenticatorProfileCreator.INSTANCE);
                    //Here, we attach the Pac4jAuthenticator, which will provide the /auth endpoint for which our login form will supply the username and password parameters from our web interface.
                    chain.all(RatpackPac4j.authenticator("auth", fc))
                        //The /login endpoint will be the route within which we will render our application’s login form. We supply to the login.html template the callbackUrl, which we retrieve from the formClient.loginUrl property. This will be the endpoint to which the login form POSTs the user’s credentials for authentication.
                        .get("login", ctx -> ctx.render(
                            groovyTemplate(Collections.singletonMap("callbackUrl", fc.getLoginUrl()),
                                "login.html")))
                        //Our protectedIndex.html interface will provide not only a user-authenticated interface, but also the means for a user to log out. Here, we define the endpoint that a user will access to invalidate their authentication to our web application.
                        .get("logout",
                            //The RatpackPac4j#logout method invalidates the current session’s request.
                            ctx -> RatpackPac4j.logout(ctx)
                                //After logout, we redirect the user back to /.
                                .then(() -> ctx.redirect("/")))
                        //Finally, we define the landing page interaction. When a user is authenticated and accesses the / endpoint, they will be rendered the protectedIndex.html template; when a user is unauthenticated, they will be rendered our application’s unauthenticated index.html page.
                        .get(ctx -> RatpackPac4j.userProfile(ctx).route(o -> o.isPresent(), o -> ctx
                            .render(groovyTemplate(ImmutableMap.of("position", o.get()),
                                "protectedIndex.html"))).then(userProfile -> ctx
                            .render(groovyTemplate(ImmutableMap.of(), "index.html"))));
                }


            ));
    }

}
