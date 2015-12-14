package de.zalando.funelo;

import de.zalando.funelo.verticle.FuneloApiGateway;
import de.zalando.funelo.verticle.PingPongService;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        logger.info("Deploying {} Verticle.", FuneloApiGateway.class.getSimpleName());
        vertx.deployVerticle(FuneloApiGateway.class.getCanonicalName());

        logger.info("Deploying {} Verticle.", PingPongService.class.getSimpleName());
        vertx.deployVerticle(PingPongService.class.getCanonicalName());
    }

}
