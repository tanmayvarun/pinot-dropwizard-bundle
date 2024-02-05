package io.dropwizard.pinot.storage.pinot.pinotspec.query.processed.response;

import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.pinot.client.ExecutionStats;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
public class SelectionQueryResponse<T> {
    private final ExecutionStats executionStats;
    private final List<T> results;
}
