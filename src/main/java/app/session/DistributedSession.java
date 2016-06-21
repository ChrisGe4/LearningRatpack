package app.session;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;
import ratpack.session.store.RedisSessionModule;

import java.nio.file.Paths;
import java.time.Duration;

/**
 * @author Chris.Ge
 */

public class DistributedSession {

    public static void main(String[] args) throws Exception {

        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec.serverConfig(
            serverConfigBuilder -> serverConfigBuilder.yaml(Paths.get("config.yml")).env()
                .sysProps()).registry(Guice.registry(r -> r.module(SessionModule.class, c -> c
                .expires(Duration.ofDays(
                    7))//Setting the expires value to a java.time.Duration value allows you to specify how long a user’s session should remain valid.
                .domain(
                    "https://my-domain.com")//You can set the domain for which the session ID cookie should be valid. This can be useful when used in conjunction with additional application configuration for ensuring that multitenant deployments do not share sessions across domains.
                .path(
                    "/")//Using the path setting, you can configure under what request paths the session ID cookie should be retrieved. If only a portion of your application requires sessions, this can be a useful setting to configure.
                .idName(
                    "JSESSIONID")//The default key for session cookie is JSESSIONID, which is similar to other Java web applications. You can override the key for the cookie using the idName configuration method if you wish to customize this value.
                .httpOnly(
                    true)//By default, session cookies are allowed to be transmitted over unencrypted HTTP. If you wish to override this configuration, you can do so by setting httpOnly to false.
                .secure(false)
            //If you wish to have session cookies only transmitted over encrypted HTTP (HTTPS), you can configure the secure setting to true (the default is false).
        )//the SessionModule must come before the RedisSessionModule.
            //moduleConfig(ClientSideSessionModule.class,r.getServerConfig().get("/session", RedisSessionModule.class)
            .module(RedisSessionModule.class, c -> {
                    //This defaults to localhost.
                    c.setHost("127.0.0.1");
                    //This defaults to 6379, which is Redis’ default binding port.
                    c.setPort(6379);
                    //The optional password field for the Redis instance. This defaults to null.
                    c.setPassword("");
                }



            ))).handlers());

    }

}
