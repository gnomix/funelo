package de.zalando.funelo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.funelo.domain.Endpoint;
import de.zalando.funelo.verticle.FuneloApiGateway;
import de.zalando.funelo.verticle.PingPongService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Application {

    private final static Logger logger = LoggerFactory.getLogger(Application.class);
    private static DeploymentOptions deploymentOptions = new DeploymentOptions();

    public static void main(String[] args) {

        final ApplicationParams params = new ApplicationParams();
        final JCommander jCommander = new JCommander(params);

        jCommander.parse(args);

        final JsonObject configObject = readConfig(params.getConf());
        final String endpoints = readEndpoints(params.getEndpoints());

        configObject.put("endpoints", endpoints);
        deploymentOptions.setConfig(configObject);

        Vertx vertx = Vertx.vertx();

        logger.info("Deploying {} Verticle.", FuneloApiGateway.class.getSimpleName());
        vertx.deployVerticle(FuneloApiGateway.class.getCanonicalName(), deploymentOptions);

        logger.info("Deploying {} Verticle.", PingPongService.class.getSimpleName());
        vertx.deployVerticle(PingPongService.class.getCanonicalName(), deploymentOptions);
    }

    private static JsonObject readConfig(final String conf) {
        if (conf != null) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(conf)));
                return new JsonObject(content);
            } catch (IOException e) {
                logger.error("-conf option does not point to a file and is not valid JSON: " + conf, e);
            } catch (DecodeException e) {
                logger.error("Configuration file " + conf + " does not contain a valid JSON object", e);
            }
        }
        return null;
    }

    private static String readEndpoints(final String conf) {
        if (conf != null) {
            try {
                return new String(Files.readAllBytes(Paths.get(conf)));
            } catch (IOException e) {
                logger.error("-conf option does not point to a file and is not valid JSON: " + conf, e);
            } catch (DecodeException e) {
                logger.error("Configuration file " + conf + " does not contain a valid JSON object", e);
            }
        }
        return null;
    }

    static class ApplicationParams {
        @Parameter(names = "-conf", description = "Specifies configuration that should be provided to the verticle. \n" +
                "conf should reference a text file containing a valid JSON  object")
        private String conf;

        @Parameter(names = "-endpoints", description = "Specifies endpoints configuration that should be provided to the verticle. \n" +
                "conf should reference a text file containing a valid JSON  object")
        private String endpoints;

        private String getConf() {
            return conf;
        }

        private String getEndpoints() {
            return endpoints;
        }
    }
}
