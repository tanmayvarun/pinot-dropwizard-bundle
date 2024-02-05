package io.dropwizard.pinot.healthcheck.configs.exception;

public enum QueryErrorCode {

    QUERY_TIMERANGE_NOT_SUPPORTED("QUERY_TIMERANGE_NOT_SUPPORTED", false),


    RESPONSE_SIZE_EXCEEDS_CONFIGURED_LIMIT("RESPONSE_SIZE_EXCEEDS_CONFIGURED_LIMIT", false),

    DAO_READ_ERROR("DAO_READ_ERROR", false),

    COLUMN_DATATYPE_NOT_COMPATIBLE("DATA_TYPE_COMPATIBILITY_ERROR", false),

    DAO_CONNECTION_ERROR("DAO_CONNECTION_ERROR", false),

    CLUSTER_CONNECTIVITY_LOST("CLUSTER_CONNECTIVITY_LOST", false),

    CLUSTER_DEGRADED("CLUSTER_DEGRADED", false),

    UNKNOWN_ERROR("UNKNOWN_ERROR", false),

    UNSUPPORTED_OPERATION_ERROR("UNSUPPORTED_OPERATION_ERROR", false)


    ;





    private final String code;

    private final boolean stackTrace;

    QueryErrorCode(String code, boolean stackTrace) {
        this.code = code;
        this.stackTrace = stackTrace;
    }
}
