package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SegmentPartitionConfig {
    private Map<String, PartitionFunction> columnPartitionMap;
}

class PartitionFunction {
    private PartitionFunctionType functionName;
    private int numPartitions;
}

enum PartitionFunctionType {
    Murmur,
    Modulo,
    ByteArray,
    HashCode
    ;
}