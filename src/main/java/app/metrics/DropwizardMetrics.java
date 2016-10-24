package app.metrics;

import com.codahale.metrics.MetricRegistry;
import ratpack.dropwizard.metrics.DropwizardMetricsModule;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris.Ge
 */
public class DropwizardMetrics {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
            //By default, the DropwizardMetricsModule prepends a RequestTimingHandler to your application’s handler chain
            .registry(Guice.registry(bindingsSpec -> bindingsSpec
                .module(DropwizardMetricsModule.class,
                    dropwizardMetricsConfig -> dropwizardMetricsConfig
                        //Nothing more than a call to jmx() inside the module configuration is necessary to enable JMX reporting. When a JMX server management port is specified as part of your application’s startup parameters, you will be able to attach to your process to get your application metrics.
                        .jmx().jvmMetrics(true).console()
                        //Within the configuration we call the csv method, to which we provide an Action that supplies to us the CsvConfig.
                        .csv(csvConfig -> {
                            File reportingDir = new File("metrics");
                            reportingDir.mkdir();
                            //On the CsvConfig, we can provide the reportDirectory method with a File to the directory where we want metric CSV files written.
                            csvConfig.reportDirectory(reportingDir);
                        }).graphite(graphiteConfig -> graphiteConfig.prefix("myapp")
                            //Here, we can configure the TimeUnit value that is to be used to convert rate units.
                            .rateUnit(TimeUnit.MILLISECONDS)
                            //Similarly, for metrics that supply durations, we can specify a TimeUnit value to ensure the metrics are converted properly before they are reported.
                            .durationUnit(TimeUnit.MILLISECONDS)))))

            .handlers(chain -> chain.get(ctx -> {
                MetricRegistry registry = ctx.get(MetricRegistry.class);
                //Here, we use the counter method off of the MetricRegistry, which gives us a Counter type that we can call inc() on to increment.
                registry.counter("myapp.user.hits").inc();

                ctx.render("hello");

            })));

    }

}
