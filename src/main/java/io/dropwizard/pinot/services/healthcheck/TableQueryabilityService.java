package io.dropwizard.pinot.services.healthcheck;

public interface
TableQueryabilityService {

    void validateQueryProcessingAllowed(QueryProcessingCheckRequest request);
}
