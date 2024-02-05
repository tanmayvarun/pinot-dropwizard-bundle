package io.dropwizard.pinot.services.healthcheck;

import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.Query;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryProcessingCheckRequest {
    private TableConfig tableConfig;
    private Query query;
}
