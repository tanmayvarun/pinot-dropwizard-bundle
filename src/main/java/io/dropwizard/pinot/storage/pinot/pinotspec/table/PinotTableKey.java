package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PinotTableKey {
    private String tableName;
    private TableType tableType;
}
