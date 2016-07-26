package app.session;

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.cookie.Cookie;
import ratpack.form.Form;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

import static ratpack.groovy.Groovy.groovyTemplate;

/**
 * @author Chris.Ge
 */
public class UseCookies {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(

            ratpackServerSpec -> ratpackServerSpec
                //may need to set the base dir
                //                .serverConfig(serverConfig -> {
                //                serverConfig
                //                    .baseDir(Paths.get("/Users/z001yk3/IdeaProjects/LearningRatpack/src/main/resources"));
                //            })
                .registry(Guice.registry(r -> r.module(TextTemplateModule.class)))
                .handlers(chain -> chain.get(ctx -> {
                        //Within the get handler, we start processing by pulling the ratpack-view-position cookie from the request object and translating it into an integer. Cookie values will always be string types, and you will recall that the view logic was built to work with a Number type. The oneCookie method extracts a specific cookie from the request. If there is no cookie of that name available, then the value will be null, so here we simply default to zero.
                        String pos = ctx.getRequest().oneCookie("ratpack-view-position");
                        int position = pos == null ? 0 : Integer.parseInt(pos);
                        ctx.render(groovyTemplate(ImmutableMap.of("position", position), "index.html"));

                    }).post("resetView", ctx -> {
                        ctx.parse(Form.class).then(form -> {
                            //It takes no more than calling the expireCookie method on response with the name of the cookie to have it be removed from the client.
                            ctx.getResponse().expireCookie("ratpack-view-position");
                            ctx.redirect("/");
                        });
                    }).post("updatePosition", ctx -> {
                        ctx.parse(Form.class).then(form -> {
                            Cookie cookie = ctx.getResponse()
                                //Next, we use the cookie method on the response object to set the ratpack-view-position cookie to the value provided by the next_pos value from the form
                                .cookie("ratpack-view-position", form.get("next_pos"));
                            cookie.setMaxAge(365 * 24 * 60
                                * 60);//Setting the maxAge property to a number of seconds allows you to tune how long a browser should respect a cookie
                            cookie.setDomain(
                                "localhost");//ensure that cookies are appropriately associated with the domain name for your application.
                            cookie.setHttpOnly(
                                true);//Here, we set the httpOnly flag on the cookie. This indicates whether JavaScript code should be able to access the cookie once it is stored by the browser. A value of true here indicates that the cookie is only valid for request-response lifecycles, and cannot be accessed by the view’s JavaScript. The default value for httpOnly is false.
                            cookie.setSecure(
                                false);//The secure property indicates whether cookies should be transmitted over unencrypted HTTP. By default this is set to false, but if you wish to only transmit cookies when serving your application over HTTPS, then set this value to true. It is generally a bad practice to store sensitive data in cookies, but if your requirements demand it, then ensure you are properly setting this value.
                            ctx.redirect("/");
                        });
                    })

                )


        );
    }

}
