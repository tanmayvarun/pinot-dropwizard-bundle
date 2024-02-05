package io.dropwizard.pinot.services.healthcheck.impl;


import io.dropwizard.pinot.healthcheck.configs.exception.QueryErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.QueryProcessingException;
import io.dropwizard.pinot.repository.pinot.PinotAdminDao;
import io.dropwizard.pinot.services.healthcheck.ClusterHealthCheckService;
import io.dropwizard.pinot.services.healthcheck.QueryProcessingCheckRequest;
import io.dropwizard.pinot.services.healthcheck.TableQueryabilityService;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.QueryTimeRange;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;
import io.dropwizard.pinot.utils.DateUtils;

import java.util.Objects;

public class TableQueryabilityServiceImpl implements TableQueryabilityService {

    private ClusterHealthCheckService clusterHealthCheckService;

    private PinotAdminDao pinotAdminDao;

    public TableQueryabilityServiceImpl(ClusterHealthCheckService clusterHealthCheckService,
                                        PinotAdminDao pinotAdminDao) {
        this.clusterHealthCheckService = clusterHealthCheckService;
        this.pinotAdminDao = pinotAdminDao;
    }

    /**
     * 1. check cluster health
     * 2. check table health
     * 3. check query time range within bounds
     * @param request
     * @return
     */
    @Override
    public void validateQueryProcessingAllowed(QueryProcessingCheckRequest request) {
        validateClusterIsHealthy();

        validateQueryNotSeekingUnavailableData(request.getTableConfig().getTableName() ,
                request.getTableConfig().getTableType(), request.getQuery().getTimeRange());
    }

    private void validateQueryNotSeekingUnavailableData(String tableId,
                                                        TableType tableType,
                                                        QueryTimeRange queryTimeRange) {
        TableConfig tableConfig = pinotAdminDao.getTableConfig(tableId, tableType);

        if (Objects.isNull(tableConfig)) {
            throw QueryProcessingException.error(QueryErrorCode.DAO_CONNECTION_ERROR,
                    "Unable to check table config for table serving query", true);
        }

        long retentionTimeDays = pinotAdminDao.getTableDataRetentionInDays(tableId, tableType);

        if (Objects.nonNull(queryTimeRange.getStart()) &&
                DateUtils.subtractDaysFromCurrentTime(retentionTimeDays).before(queryTimeRange.getStart())) {
            QueryProcessingException.error(QueryErrorCode.QUERY_TIMERANGE_NOT_SUPPORTED,
                    "Query start time is before table oldest data", false);
        }

        //todo: if query end time is greater than the latest ingestion timestamp, it should be blocked.
        if (Objects.nonNull(queryTimeRange.getEnd()) &&
                queryTimeRange.getEnd().after(DateUtils.currentTime())) {
            QueryProcessingException.error(QueryErrorCode.QUERY_TIMERANGE_NOT_SUPPORTED,
                    "Query end time is after table latest data", false);
        }
    }


    private void validateClusterIsHealthy() {
        if (! clusterHealthCheckService.goodConnectivity()) {
            throw QueryProcessingException.error(QueryErrorCode.CLUSTER_DEGRADED,
                    "Cluster is not healthy, please retry later", false);
        }
    }
}
