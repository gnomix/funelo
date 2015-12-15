package de.zalando.funelo.domain;

public class KafkaRequestData {
    private String message;
    private String topicName;

    public KafkaRequestData() {
    }

    public KafkaRequestData(String message, String topicName) {
        this.message = message;
        this.topicName = topicName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
