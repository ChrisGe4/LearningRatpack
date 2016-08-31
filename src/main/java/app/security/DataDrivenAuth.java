package app.security;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import groovy.sql.Sql;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;
import ratpack.groovy.sql.SqlModule;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.hikari.HikariModule;
import ratpack.pac4j.RatpackPac4j;
import ratpack.server.RatpackServer;
import ratpack.service.Service;
import ratpack.service.StartEvent;
import ratpack.session.SessionModule;

import java.util.Collections;
import java.util.Map;

import static ratpack.groovy.Groovy.groovyTemplate;


/**
 * @author Chris.Ge
 */
public class DataDrivenAuth {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
            .registry(Guice.registry(bindingsSpec -> bindingsSpec.module(SessionModule.class)
                //Remember that we need to apply the SqlModule, so that the Sql object will be properly constructed with our DataSource.
                .module(SqlModule.class).module(TextTemplateModule.class)
                //We apply the HikariModule and configure it here to construct an H2 in-memory embedded database.
                .module(HikariModule.class, hikariConfig -> {
                    hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
                    hikariConfig.addDataSourceProperty("URL", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
                    hikariConfig.setUsername("sa");
                    hikariConfig.setPassword("");

                }).bindInstance(
                    //As you’re already familiar, we use a ratpack.service.Service instance to bootstrap the auth data into our database.
                    new Service() {
                        @Override
                        public void onStart(StartEvent event) throws Exception {
                            Sql sql = event.getRegistry().get(Sql.class);
                            sql.execute(
                                "CREATE TABLE USER_AUTH(USER VARCHAR(255), PASS VARCHAR(255))");
                            //Here, we will add a username/password combination of learningratpack/r4tp@CKrul3z!. We use the same mechanism for hashing the password as from the prior section.
                            sql.execute(
                                "INSERT INTO USER_AUTH (USER, PASS) " + "VALUES('learningratpack', "
                                    + "'768122eeeebdafa3eb878f868b0e4e6a4944367aa635538f')");

                        }
                        //This line binds our DatabaseUsernamePasswordAuthenticator into the user registry.
                    }).bind(DatabaseUsernamePasswordAuthenticator.class)

            )).handlers(chain -> {
                    //Here, we set the callbackUrl, which will specify the route that the HTML form should POST to, and we ensure it is bound to the authenticator.
                    String callbackUrl = "auth";


                    FormClient fc = new FormClient(callbackUrl,
                        //In the handler chain, we can access components from the user registry via the registry.get(..) call, as shown here.
                        chain.getRegistry().get(DatabaseUsernamePasswordAuthenticator.class),
                        AuthenticatorProfileCreator.INSTANCE);
                    chain.all(RatpackPac4j.authenticator(callbackUrl, fc)).get("login", ctx -> ctx
                        .render(groovyTemplate(Collections.singletonMap("callbackUrl", callbackUrl),
                            "login.html"))).get("logout",
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


            )

        );
    }

    private static Map<String, Object> getResponseMap(Boolean status, String message) {
        Map<String, Object> response = Maps.newHashMap();
        response.put("success", status);
        response.put("error", message);
        return response;
    }

}
