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
    private static DeploymentOptions deploymentOptions = new DeploymentOptions();

    public Application() {
    }

    public static void main(String[] args) {

        ApplicationParams params = new ApplicationParams();
        JCommander jCommander = new JCommander(params);

        jCommander.parse(args);

        System.out.println(params.getConf());

        JsonObject configObject = readConfig(params.getConf());
        deploymentOptions.setConfig(configObject);

        Vertx vertx = Vertx.vertx();

        logger.info("Deploying {} Verticle.", FuneloApiGateway.class.getSimpleName());
        vertx.deployVerticle(FuneloApiGateway.class.getCanonicalName(), deploymentOptions);

        logger.info("Deploying {} Verticle.", PingPongService.class.getSimpleName());
        vertx.deployVerticle(PingPongService.class.getCanonicalName(), deploymentOptions);

        logger.info("Deploying {} Verticle.", KafkaProducer.class.getSimpleName());
        vertx.deployVerticle(KafkaProducer.class.getCanonicalName(), deploymentOptions);
    }

    private static JsonObject readConfig(final String conf) {
        if (conf == null) {
            return null;
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(conf)));
            return new JsonObject(content);
        } catch (IOException e) {
            logger.error("-conf option does not point to a file and is not valid JSON: " + conf);
        } catch (DecodeException e) {
            logger.error("Configuration file " + conf + " does not contain a valid JSON object");
        }
        return null;
    }

    static class ApplicationParams {
        @Parameter(names = "-conf", description = "Specifies configuration that should be provided to the verticle. \n" +
                "conf should reference a text file containing a valid JSON  object")
        private String conf;

        private String getConf() {
            return conf;
        }
    }
}
