package de.zalando.funelo.verticle;

import de.zalando.funelo.domain.KafkaRequestData;
import de.zalando.funelo.parser.FromJsonParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import kafka.admin.AdminUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class KafkaProducer extends AbstractVerticle {


    private final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Override
    public void start(Future<Void> future) {

        String ip = "localhost:2181";

        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(props);

        Producer<String, String> producer = new Producer<String, String>(config);

        EventBus eventBus = vertx.eventBus();

        ZkClient zkClient = new ZkClient(ip, 10000, 10000, ZKStringSerializer$.MODULE$);

        eventBus.consumer("send-to-kafka", message -> {
            try {
                KafkaRequestData requestData = FromJsonParser.parseKafkaRequestDataFromJson(message.body().toString());

                if (!AdminUtils.topicExists(zkClient, requestData.getTopicName())) {
                    AdminUtils.createTopic(zkClient, requestData.getTopicName(), 10, 1, new Properties());
                }

                KeyedMessage<String, String> data = new KeyedMessage<>(requestData.getTopicName(), ip, requestData.getMessage());
                producer.send(data);

            } catch (IOException e) {
                logger.error("Exception while parsing Kafka request JSON.", e);

                return;
            }
        });

        future.complete();
    }
}
