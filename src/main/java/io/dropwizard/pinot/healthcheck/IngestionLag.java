package io.dropwizard.pinot.healthcheck;

public enum IngestionLag {

    NONE(0, 0),
    IGNORABLE(0, 1000),
    LOW(1000, 100000),
    HIGH(100001, 1000000),
    VERY_HIGH(1000001, Long.MAX_VALUE)
    ;

    private long lowerLimit;
    private long upperLimit;

    IngestionLag(long lowerLimit, long upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }
}
