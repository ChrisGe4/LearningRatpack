package app;

import com.google.inject.Provides;
import ratpack.guice.ConfigurableModule;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

/**
 * @author Chris Ge.
 *         <p>
 *         Many of Ratpack’s framework modules will require you to provide some additional configuration as part of their function.
 *         The most notable example of this is the ratpack-hikari module, which provides connection pooling for database connections.
 *         When utilizing Hikari within your application, you will need to provide it with configuration directives,
 *         including the database connection URL and the datasource driver class name.
 */
public class Main_ConfiguableModule {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(spec -> spec.registry(
            Guice.registry(r -> r.module(StringModule.class, config -> config.value("foo"))))
            /*or you can do this
             .module(StringModule.class)
             .bindInstance(new StringModule.Config().value("bar"))
             */.handlers(chain -> chain.get(ctx -> ctx.render(ctx.get(String.class)))));
    }



    public static class StringModule extends ConfigurableModule<StringModule.Config> {
        protected void configure() {
        }

        @Provides
        String provideString(Config config) {
            return config.value;
        }


        public static class Config {
            private String value;

            public void value(String value) {
                this.value = value;
            }
        }
    }
}
