package io.dropwizard.pinot.healthcheck.configs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicConfig {

    private String topicName;
    private String kafkaConsumerGroupId;

}
