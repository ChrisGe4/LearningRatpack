package app.servingStaticContent;


import ratpack.handling.Context;
import ratpack.handling.Handler;

/**
 * @author Chris.Ge
 */
public class FooBarFileHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        // capture the "?file=<file>" query param
        String fileParam = ctx.getRequest().getQueryParams().get("file");

        // check to make sure it was either "foo" or "bar"
        if (fileParam == "foo" || fileParam == "bar") {
            // if so, then use the Context.file(..) call to read the requested resource
            ctx.render(ctx.file("/html/${fileParam}.html"));
        } else {
            // if not, then set the status to 404 and render back the error page
            ctx.getResponse().status(404);
            ctx.render(ctx.file("/html/error.html"));
        }
    }
}
