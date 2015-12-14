package de.zalando.funelo;

import de.zalando.funelo.verticle.FuneloApiGateway;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FuneloApiGatewayTest {

    private final String healthEndpointResponse = "{\"status\":\"UP\"}";
    private final String ENDPOINTS = "[ { \"path\": \"/v1/myfeed/:eventtype\", \"method\": \"GET\", \"format\": \"JSON\" }, " +
            "{ \"path\": \"/v1/myads/:eventtype\", \"method\": \"GET\", \"format\": \"JSON\" } ]";
    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        JsonObject config = new JsonObject();
        config.put(ConfigurationOptions.HTTP_ENDPOINTS, new JsonArray(ENDPOINTS));
        deploymentOptions.setConfig(config);
        vertx.deployVerticle(FuneloApiGateway.class.getName(), deploymentOptions, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/hello",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("Hello"));
                        async.complete();
                    });
                });
    }

    @Test
    public void testHealthUrl(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/health",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().equals(healthEndpointResponse));
                        async.complete();
                    });
                });
    }
}
