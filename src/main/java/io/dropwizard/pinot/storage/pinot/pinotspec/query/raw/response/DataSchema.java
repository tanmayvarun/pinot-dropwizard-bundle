package io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.response;

import java.util.List;

public class DataSchema {
    private List<String> columnNames;
    private List<String> columnDataTypes;
    private List<List<?>> rows;
}
