package io.dropwizard.pinot.healthcheck.configs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExperimentConfig {
    private boolean experimental;
    private List<String> whitelistedMerchants = new ArrayList<>();
}
