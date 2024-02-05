package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class TableIndexConfig {
    private String loadMode;
    private List<String> invertedIndexColumns;
    private boolean createInvertedIndexDuringSegmentGeneration;
    private List<String> sortedColumn;
    private List<String> bloomFilterColumns;
    private Object bloomFilterConfigs;
    private List<String> noDictionaryColumns;
    private List<String> onHeapDictionaryColumns;
    private List<String> varLengthDictionaryColumns;
    private boolean enableDefaultStarTree;
    private List<Object> starTreeIndexConfigs;
    private boolean enableDynamicStarTreeCreation;
    private SegmentPartitionConfig segmentPartitionConfig;
    private Object columnMinMaxValueGeneratorMode;
    private boolean aggregateMetrics;
    private boolean nullHandlingEnabled;
    private Map<String, Object> streamConfigs;
}

