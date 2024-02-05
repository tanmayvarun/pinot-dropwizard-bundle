package io.dropwizard.pinot.healthcheck.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoLucyConfig {

    private String host;
    private int port;
    private String apiPath;
    private String authToken;
    private String principal;
    private String keytabPath;
}
