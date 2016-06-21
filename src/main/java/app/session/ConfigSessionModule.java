package app.session;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;
import ratpack.session.clientside.ClientSideSessionConfig;
import ratpack.session.clientside.ClientSideSessionModule;

import java.nio.file.Paths;
import java.time.Duration;

/**
 * @author Chris.Ge
 */

public class ConfigSessionModule {

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
        ).moduleConfig(ClientSideSessionModule.class,
            //{    ConfigData configData = ConfigData.of { it.sysProps().build() }

            r.getServerConfig().get("/session", ClientSideSessionConfig.class), c -> {
                //The sessionCookieName property allows you to customize the key under which the session cookie is stored with the client. This value defaults to ratpack_session.
                c.setSessionCookieName("ratpack_session");
                //The secretToken property is the value used to sign the serialized session. This value defaults to a time-based value unless otherwise specified. Signing the serialized session with this value prevents tampering.
                c.setSecretToken(String.valueOf(Math.floor(System.currentTimeMillis()) / 10000));
                //you can specify a value to secretKey to encrypt the client-side session cookie. If no value is specified here, then by default the session cookie will not be encrypted.
                // c.setSecretKey("!c$mB&aGkL112345");
                //If the session cookie is to be encrypted, you can override the MAC.1 This value defaults to HmacSHA1, which uses the SHA-1 cryptographic hashing function to generate the MAC. The value specified here must be one of the values specified by the javax.crypto.Mac class.
                c.setMacAlgorithm("HmacSHA1");
                //You can also override the cipher algorithm that is employed to perform the encryption of the session cookie. This value defaults to AES/CBC/PKCS5Padding, which provides 128-bit encryption of the session cookie. Overrides of this value must be one of the values supported by the javax.crypto.Cipher class.                c.setCipherAlgorithm("AES/CBC/PKCS5Padding");
                c.setCipherAlgorithm("AES/CBC/PKCS5Padding");
                //The maxSessionCookieSize value can be used to specify the maximum size of the client-side session cookie. Any value within the range of 1024 and 4096 are valid for this property, and the default size is specified as 1932.
                c.setMaxSessionCookieSize(1932);
                //The last value, maxInactivityInterval, is pretty straightforward. It specifies the duration under which a session should remain valid.
                c.setMaxInactivityInterval(Duration.ofHours(24));
            }))).handlers());

    }

}
