package de.zalando.funelo.verticle;

import de.zalando.funelo.domain.KafkaRequestData;
import de.zalando.funelo.parser.FromJsonParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class KafkaVerticle extends AbstractVerticle {


    private final Logger logger = LoggerFactory.getLogger(KafkaVerticle.class);

    @Override
    public void start(Future<Void> future) {

        String ip = "localhost:2181";

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        EventBus eventBus = vertx.eventBus();

        ZkClient zkClient = new ZkClient(ip, 10000, 10000, ZKStringSerializer$.MODULE$);

        eventBus.consumer("send-to-kafka", message -> {
            try {
                KafkaRequestData requestData = FromJsonParser.parseKafkaRequestDataFromJson(message.body().toString());

                if (!AdminUtils.topicExists(zkClient, requestData.getTopicName())) {
                    logger.debug("Create kafka topic: " + requestData.getTopicName());
                    AdminUtils.createTopic(zkClient, requestData.getTopicName(), 1, 1, new Properties());
                }

                producer.send(new ProducerRecord<String, String>(requestData.getTopicName(), ip, requestData.getMessage()));

            } catch (IOException e) {
                logger.error("Exception while parsing Kafka request JSON.", e);
            }
        });

        future.complete();
    }
}
