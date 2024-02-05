package io.dropwizard.pinot.healthcheck.configs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StreamProcessingConfig {
    private boolean enabled;
    private ExperimentConfig experimentConfig;

}
