package app;

import ratpack.config.ConfigData;
import ratpack.server.RatpackServer;

import static ratpack.jackson.Jackson.toJson;

/**
 * @author Chris.Ge
 */
public class ApplicationConfig {


    public static void main(String[] args) throws Exception {
        ConfigData configData = ConfigData.of(configDataBuilder -> configDataBuilder
            .props(ClassLoader.getSystemResource("application.properties")).sysProps().env()
            .build());
        RatpackServer.of(ratpackServerSpec -> /*ratpackServerSpec.serverConfig(
            serverConfigBuilder -> serverConfigBuilder.props(ImmutableMap
                .of("server.publicAddress", "http://app.example.com", "app.org", "Ratpack"))
                .props(getResource("application.properties")).sysProps().env()*/
                //.require("/app", Org.class)

            {

                ratpackServerSpec.registryOf(
                    registrySpec -> registrySpec.add(Config.class, configData.get(Config.class)));
                ratpackServerSpec.handlers(chain -> chain.get("config", ctx -> {

                    ctx.render(toJson(ctx).apply(ctx.get(Config.class)));

                }));

            }

        );



    }


    class Config {

        Org org;
        Repo repo;


    }


    class Org {
        int port = 0000;
        String org = "default";
    }


    class Repo {
        String repos = "default";
    }
}
