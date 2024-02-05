package io.dropwizard.pinot.config;

import io.dropwizard.pinot.healthcheck.configs.Endpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class PinotDaoBundleConfig {

    @Builder.Default
    private Mode mode = Mode.EXECUTION;
    private KafkaProducerConfig kafkaProducerConfig;
    private String keytabPath;

    /**
     * For local testing, are not setting an LB, but in production an LB will front all the brokers.
     */
    private Endpoint brokerEndpoint;

    private Endpoint controllerEndpoint;

}
