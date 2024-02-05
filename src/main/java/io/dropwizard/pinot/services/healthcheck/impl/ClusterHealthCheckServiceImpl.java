package io.dropwizard.pinot.services.healthcheck.impl;

import io.dropwizard.pinot.TopicService;
import io.dropwizard.pinot.healthcheck.Health;
import io.dropwizard.pinot.healthcheck.IngestionLag;
import io.dropwizard.pinot.repository.pinot.PinotAdminDao;
import io.dropwizard.pinot.services.healthcheck.ClusterHealthCheckService;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ClusterHealthCheckServiceImpl implements ClusterHealthCheckService {

    private final PinotAdminDao pinotAdminDao;

    private final TopicService topicService;

    @Override
    public boolean goodConnectivity() {
        Health health = pinotAdminDao.ping();
        if (! health.equals(Health.OK)) {
            return false;
        }
        return true;
    }

    //todo: implement
    @Override
    public IngestionLag checkLag(String topic) {
        return IngestionLag.NONE;
    }
}
