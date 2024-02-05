package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenants{
    private String broker;
    private String server;
}
