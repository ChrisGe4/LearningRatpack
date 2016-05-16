package app.servingStaticContent;

import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

/**
 * @author Chris.Ge
 */
public class ShowIndexPage {

    /*
    If you run either of these applications and open a browser to
    http://localhost:5050 you will this time be greeted with a “Hello World!”
    message from the index.html file from the project’s static directory.
     */

    public static void main(String[] args) throws Exception {
        RatpackServer.start(spec -> spec.serverConfig(c -> c.baseDir(BaseDir.find()).build())
            //Within that closure, the dir("static") call is made indicating that the handler should
            //serve assets from the src/ratpack/static directory.
            //Finally, the call to .indexFiles("index.html") is used to indicate that when serving a directory, the index.html file should be served as the directory’s root path.
            .handlers(chain -> chain.files(files -> files.dir("static").indexFiles(
                "index.html"))));// you can use files.path here, see method implementation
    }
}
