package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

class FieldConfigList {
    private String name;
    private String encodingType;
    private List<String> indexTypes;
    private ObjectNode timestampConfig;
}

class IngestionConfig {
    private Object filterConfig;
    private Object transformConfigs;
}

class MerchantId {
    private String functionName;
    private int numPartitions;
}

class Query {
    private Object timeoutMs;
}

class Quota {
    private Object storage;
    private String maxQueriesPerSecond;
}

@Getter
public class TableConfig {
    private String tableName;
    private TableType tableType;
    private Tenants tenants;
    private SegmentsConfig segmentsConfig;
    private TableIndexConfig tableIndexConfig;
    private IngestionConfig ingestionConfig;
    private Quota quota;
    private Object task;
    private Metadata metadata;
    private Routing routing;
    private Query query;
    private List<FieldConfigList> fieldConfigList;
    private UpsertConfig upsertConfig;
    private List<TierConfig> tierConfigs;
}

class Metadata {
    private Map<String, String> customConfigs;
}
class Routing{
    private List<String> segmentPrunerTypes;
    private String instanceSelectorType;
}

class TagOverrideConfig implements Serializable {
}

class TimestampConfig{
    private List<String> granularities;
}

class UpsertConfig{
    private String mode;
    private String defaultPartialUpsertStrategy;
    private Map<String, String> partialUpsertStrategies;
}

