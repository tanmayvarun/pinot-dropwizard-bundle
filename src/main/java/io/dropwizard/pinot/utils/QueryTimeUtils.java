package io.dropwizard.pinot.utils;

import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.QueryTimeRange;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.Objects;

@UtilityClass
public class QueryTimeUtils {

    public QueryTimeRange getTransactionDateTimerange(Long startTime, Long endTime) {

        if (Objects.isNull(startTime) && Objects.isNull(endTime)) {
            return null;
        }

        QueryTimeRange timeRange = QueryTimeRange.builder().build();

        if (Objects.isNull(startTime)) {
            timeRange.setStart(null);
        } else {
            timeRange.setStart(new Date(endTime));
        }

        if (Objects.isNull(endTime)) {
            timeRange.setEnd(null);
        } else {
            timeRange.setEnd(new Date(endTime));
        }

        return timeRange;
    }
}
