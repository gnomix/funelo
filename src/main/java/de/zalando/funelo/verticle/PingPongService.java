package de.zalando.funelo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPongService extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(PingPongService.class);

    @Override
    public void start(Future<Void> future) {
        logger.info("Starting {} Verticle.", PingPongService.class.getSimpleName());

        EventBus eventBus = vertx.eventBus();

        eventBus.consumer("ping-address", message -> {
            // Now send back reply
            message.reply("pong!");
        });

        future.complete();
    }
}
