package io.dropwizard.pinot.models.kafka;

import lombok.Getter;

@Getter
public class Topic {

    public static final int DEFAULT_REPLICATION_FACTOR = 3;
    private String name;
    private int partitions;
    private int replicationFactor = 3;


    public String toString() {
        return "Topic(name=" + this.getName() + ", partitions=" + this.getPartitions() + ", replicationFactor=" + this.getReplicationFactor() + ")";
    }

    public Topic(String name, int partitions, int replicationFactor) {
        this.name = name;
        this.partitions = partitions;
        this.replicationFactor = replicationFactor;
    }

    public Topic() {
    }

}