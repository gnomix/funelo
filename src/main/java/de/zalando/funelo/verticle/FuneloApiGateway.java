package de.zalando.funelo.verticle;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.funelo.ConfigurationOptions;
import de.zalando.funelo.FuneloException;
import de.zalando.funelo.domain.Endpoint;
import de.zalando.funelo.domain.KafkaRequestData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.zalando.funelo.domain.RequestData;
import de.zalando.funelo.parser.ToJsonParser;

import java.io.IOException;
import java.util.List;

public class FuneloApiGateway extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(FuneloApiGateway.class);

    @Override
    public void start(final Future<Void> future) {
        logger.info("Starting {} Verticle.", FuneloApiGateway.class.getSimpleName());

        final Router router = Router.router(vertx);

        createHealthEndpoint(router);
        createHelloEndpoint(router);
        createDynamicEndpoints(router);

        createHttpServer(router, future);
    }

    private List<Endpoint> parseEndpoints(final String endpoints) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(endpoints,
                    mapper.getTypeFactory().constructCollectionType(List.class, Endpoint.class));
        } catch (IOException e) {
            throw new FuneloException("Endpoints file does not contain a valid JSON object", e);
        }
    }

    private void createDynamicEndpoints(final Router router) {
        final JsonArray endpointsConfig = config().getJsonArray(ConfigurationOptions.HTTP_ENDPOINTS);
        if(endpointsConfig == null) {
            throw new FuneloException("Endpoints configuration is missing.");
        }
        final List<Endpoint> endpoints = parseEndpoints(endpointsConfig.encode());
        endpoints.forEach(endpoint -> createDynamicEndpoint(router, endpoint));
    }

    private void createDynamicEndpoint(final Router router, final Endpoint endpoint) {
        final Handler<RoutingContext> routingContextHandler = routingContext -> {
            final HttpServerRequest request = routingContext.request();
            final RequestData requestData = new RequestData(request.headers(), request.params(), request.uri(), request.path());

            try {
                final String requestJson = ToJsonParser.parseRequestDataToJson(requestData);
                logger.debug(requestJson);

                KafkaRequestData kafkaRequestData = new KafkaRequestData(requestJson, endpoint.getTopic());
                final String kafkaRequestJson = ToJsonParser.parseKafkaRequestDataToJson(kafkaRequestData);

                vertx.eventBus().send("send-to-kafka", kafkaRequestJson, reply -> {
                    if (reply.succeeded()) {
                        logger.debug(reply.result().body().toString());
                    } else {
                        logger.debug("Failed to save data to Kafka.");
                    }
                });

            } catch (JsonProcessingException e) {
                logger.warn("ERROR: cannot parse params and headers to json");
            }

            final HttpServerResponse response = routingContext.response();
            response.end();
        };

        logger.debug("Exposing http endpoint: {}", endpoint.getPath());
        router.route(endpoint.getMethod(), endpoint.getPath()).handler(routingContextHandler);
    }

    private void createHealthEndpoint(final Router router) {
        final Handler<RoutingContext> routingContextHandler = routingContext -> {
            final HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json").end("{\"status\":\"UP\"}");
        };

        router.route("/health").handler(routingContextHandler);
    }

    private void createHelloEndpoint(final Router router) {
        final Handler<RoutingContext> routingContextHandler = routingContext -> {
        	final HttpServerRequest request = routingContext.request();
            final HttpServerResponse response = routingContext.response();
            vertx.eventBus().send("ping-address", "ping!", reply -> {
                if (reply.succeeded()) {
                    reply.result().body();
                } else {
                    System.out.println("No reply");
                }
                response.putHeader("content-type", "text/html").end("Hello World from Java with Vert.x");
            });
        };

        router.route("/hello").handler(routingContextHandler);
    }

    private void createHttpServer(final Router router, final Future<Void> future) {
        final int port = config().getInteger(ConfigurationOptions.HTTP_PORT, 8080);
        final String host = config().getString(ConfigurationOptions.HTTP_HOST, "localhost");

        final HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept)
                .listen(port, host,
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
