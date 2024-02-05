package io.dropwizard.pinot.healthcheck.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {
    private String host;
    private String port;
    private String uriPrefix;
}
