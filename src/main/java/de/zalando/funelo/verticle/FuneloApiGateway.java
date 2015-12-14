package de.zalando.funelo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.zalando.funelo.domain.RequestData;
import de.zalando.funelo.parser.ToJsonParser;

import java.time.LocalDateTime;

public class FuneloApiGateway extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(FuneloApiGateway.class);
    private final String healthEndpointResponse = "{\"status\":\"UP\"}";

    @Override
    public void start(Future<Void> future) {
        logger.info("Starting {} Verticle.", FuneloApiGateway.class.getSimpleName());

        Router router = Router.router(vertx);

        createHealthEndpoint(router);
        createHelloEndpoint(router);
        createHttpServer(router, future);

    }

    private void createHealthEndpoint(Router router) {
        Handler<RoutingContext> routingContextHandler = routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json").end(healthEndpointResponse);
        };

        router.route("/health").handler(routingContextHandler);
    }

    private void createHelloEndpoint(Router router) {
        Handler<RoutingContext> routingContextHandler = routingContext -> {
        	final HttpServerRequest request = routingContext.request();
            HttpServerResponse response = routingContext.response();
            vertx.eventBus().send("ping-address", "ping!", reply -> {
                if (reply.succeeded()) {
                    reply.result().body();
                } else {
                    System.out.println("No reply");
                }
                response.putHeader("content-type", "text/html").end("Hello World from Java with Vert.x");
            });
            
        	final RequestData requestData = new RequestData(request.headers(), request.params(), request.uri(), request.path());
        	try {
        		final String requestJson = ToJsonParser.parseRequestDataToJson(requestData);
        		logger.info(requestJson.toString());
        		//TODO send requestJson to kafka
			} catch (JsonProcessingException e) {
				logger.warn("ERROR: cannot parse params and headers to json");
			}
        };

        router.route("/hello").handler(routingContextHandler);
    }

    private void createHttpServer(Router router, Future<Void> future) {
        final int port = config().getInteger("http.port", 8080);
        final String host = config().getString("http.host", "localhost");

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept)
                .listen(port,
                        result -> {
                            if (result.succeeded()) {
                                logger.info("Successfully Started HttpServer on port: {}", port);
                                future.complete();
                            } else {
                                logger.error("Failed to start HttpServer", result.cause());
                                future.fail(result.cause());
                            }
                        }
                );
    }
}
