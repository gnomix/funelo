package de.zalando.funelo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class KafkaProducer extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {

        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(props);

        Producer<String, String> producer = new Producer<String, String>(config);

        KeyedMessage<String, String> data = new KeyedMessage<String, String>("test", "localhost:2181", "Test");
        producer.send(data);

        producer.close();

        future.complete();
    }
}
