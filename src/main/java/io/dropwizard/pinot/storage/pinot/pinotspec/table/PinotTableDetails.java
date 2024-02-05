package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import io.dropwizard.pinot.models.kafka.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PinotTableDetails {
    private PinotSchemaConfig schemaConfig;
    private TableConfig tableConfig;
}
