package io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RawQuery {
    private final String sql;
}
