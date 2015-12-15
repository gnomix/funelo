package de.zalando.funelo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import de.zalando.funelo.verticle.FuneloApiGateway;
import de.zalando.funelo.verticle.KafkaProducer;
import de.zalando.funelo.verticle.PingPongService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Application {

    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {

        final ApplicationParams params = readParams(args);
        final JsonObject config = readConfig(params.getConf());

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config);

        Vertx vertx = Vertx.vertx();

        logger.info("Deploying {} Verticle.", FuneloApiGateway.class.getSimpleName());
        vertx.deployVerticle(FuneloApiGateway.class.getCanonicalName(), deploymentOptions);

        logger.info("Deploying {} Verticle.", PingPongService.class.getSimpleName());
        vertx.deployVerticle(PingPongService.class.getCanonicalName(), deploymentOptions);

        logger.info("Deploying {} Verticle.", KafkaProducer.class.getSimpleName());
        vertx.deployVerticle(KafkaProducer.class.getCanonicalName(), deploymentOptions);
    }

    private static ApplicationParams readParams(final String[] args) {
        final ApplicationParams params = new ApplicationParams();
        final JCommander jCommander = new JCommander(params);

        jCommander.parse(args);
        return params;
    }

    private static JsonObject readConfig(final String conf) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(conf)));
            return new JsonObject(content);
        } catch (IOException e) {
            throw new FuneloException("-conf option does not point to a file and is not valid JSON: " + conf, e);
        } catch (DecodeException e) {
            throw new FuneloException("Configuration file " + conf + " does not contain a valid JSON object", e);
        }
    }

    static class ApplicationParams {
        @Parameter(names = "-conf",
                required = true,
                description = "Specifies configuration that should be provided to the verticle. \n" +
                        "conf should reference a text file containing a valid JSON  object")
        private String conf;

        private String getConf() {
            return conf;
        }

    }
}