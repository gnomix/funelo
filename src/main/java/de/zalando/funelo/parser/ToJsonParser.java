package de.zalando.funelo.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.zalando.funelo.domain.RequestData;

public class ToJsonParser {
    public static String parseRequestDataToJson(final RequestData requestData) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(requestData);
    }
}
