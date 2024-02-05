package io.dropwizard.pinot.services.healthcheck;

import io.dropwizard.pinot.healthcheck.IngestionLag;

/**
 * Realtime check on ingestion health of pinot cluster.
 */
public interface ClusterHealthCheckService {

    boolean goodConnectivity();

    IngestionLag checkLag(String topic);

}
