package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierConfig {

    private String name;

    private String segmentSelectorType;

    private String segmentAge;

    private String storageType;

    private String serverTag;

    private String tierBackend;

    private Map<String, String> tierBackendProperties;
}
