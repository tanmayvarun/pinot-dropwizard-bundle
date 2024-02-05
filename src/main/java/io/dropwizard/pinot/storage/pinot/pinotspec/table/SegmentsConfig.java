package io.dropwizard.pinot.storage.pinot.pinotspec.table;


import lombok.Getter;

@Getter
public class SegmentsConfig {
    private String timeColumnName;
    private String timeType;
    private String retentionTimeUnit;
    private String retentionTimeValue;
    private String segmentPushType;
    private String segmentAssignmentStrategy;
    private String schemaName;
    private String replication;
    private String replicasPerPartition;
}

