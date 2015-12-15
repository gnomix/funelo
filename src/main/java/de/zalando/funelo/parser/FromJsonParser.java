package de.zalando.funelo.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.funelo.domain.KafkaRequestData;

import java.io.IOException;

public class FromJsonParser {
    public static KafkaRequestData parseKafkaRequestDataFromJson(String kafkaRequestDataJson) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(kafkaRequestDataJson, KafkaRequestData.class);
    }
}
