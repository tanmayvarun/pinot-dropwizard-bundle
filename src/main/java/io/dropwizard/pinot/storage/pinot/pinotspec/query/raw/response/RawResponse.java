package io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.response;

import java.util.List;

public abstract class RawResponse {

    private ResultTable resultTable;

    private List<String> exceptions;

    private long minConsumingFreshnessTimeMs;

    private long numConsumingSegmentsQueried;

    private long numDocsScanned;

    private long numEntriesScannedInFilter;

    private long numEntriesScannedPostFilter;

    private boolean numGroupsLimitReached;

    private long numSegmentsMatched;

    private long numSegmentsProcessed;

    private long numSegmentsQueried;

    private long numServersQueried;

    private long numServersResponded;

    private long timeUsedMs;

    private long totalDocs;

    private List<Object> traceInfo;

    private List<Object> segmentStatistics;

}
