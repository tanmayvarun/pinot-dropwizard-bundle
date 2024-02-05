package io.dropwizard.pinot.healthcheck.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReportDatastoreConfig {
    /**
     * For local testing, are not setting an LB, but in production an LB will front all the brokers.
     */
    private Endpoint brokerEndpoint;

    private Endpoint controllerEndpoint;

}
