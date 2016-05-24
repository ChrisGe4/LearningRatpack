package app.dataDrivenApplication.databaseBinding;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

/**
 * @author Chris.Ge
 */
public class BlockingDatabaseCall {
    public static void main(String[] args) {

        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
            .registry(Guice.registry(r -> r.module(SqlModule.class))))

    }
}
