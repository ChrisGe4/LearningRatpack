package app.servingStaticContent;

import ratpack.server.RatpackServer;

/**
 * @author Chris.Ge
 */
public class FileSystem {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(
            spec -> spec.registryOf(registrySpec -> registrySpec.add(new FooBarFileHandler()))
                .handlers(chain -> chain.host("www.client1.com",
                    c -> c.fileSystem("client1", c1 -> c1.all(FooBarFileHandler.class)))
                    .host("www.client2.com",
                        c -> c.fileSystem("client2", c1 -> c1.all(FooBarFileHandler.class)))

                ));
    }
}
