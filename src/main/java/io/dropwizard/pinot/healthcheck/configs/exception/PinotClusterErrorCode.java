package io.dropwizard.pinot.healthcheck.configs.exception;

import lombok.Getter;

@Getter
public enum PinotClusterErrorCode {

    NO_SERVERS_AVAILABLE_FOR_TAGGING("NO_SERVERS_AVAILABLE_FOR_TAGGING", false, 500),

    INSTANCE_TAG_UPDATE_FAILED("INSTANCE_TAG_UPDATE_FAILED", false, 500),

    UPDATE_TAGS_VALIDATION_FAILED("UPDATE_TAGS_VALIDATION_FAILED", false, 500),

    GET_INSTANCE_INFO_FAILED("GET_INSTANCES_LIST_FAILED", false, 500),

    GET_INSTANCES_LIST_FAILED("GET_INSTANCES_LIST_FAILED", false, 500),

    BAD_REQUEST("BAD_REQUEST", false, 400),

    INVALID_SCHEMA_ID("INVALID_SCHEMA_ID", false, 400),

    INVALID_TABLE_ID("INVALID_TABLE_ID", false, 400),

    TABLE_INGESTION_TOPIC_NOT_EXIST("TABLE_INGESTION_TOPIC_NOT_EXIST", false, 400),

    INGESTION_TOPIC_ALREADY_MAPPED("TABLE_INGESTION_TOPIC_NOT_EXIST", false, 400),

    SCHEMA_ALREADY_EXISTS("SCHEMA_ALREADY_EXISTS", false, 400),
    SCHEMA_CREATION_FAILED("SCHEMA_CREATION_FAILED", false, 500),

    TABLE_ALREADY_EXISTS("TABLE_ALREADY_EXISTS", false, 400),

    TABLE_CREATION_FAILED("TABLE_CREATION_FAILED", false, 500),

    DAO_ERROR("DAO_ERROR", false, 500)

    ;

    private final String code;

    private final boolean stackTrace;

    private final int httpResponseCode;

    PinotClusterErrorCode(String code, boolean stackTrace, int httpResponseCode) {
        this.code = code;
        this.stackTrace = stackTrace;
        this.httpResponseCode = httpResponseCode;
    }
}

