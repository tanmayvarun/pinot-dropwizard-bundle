package io.dropwizard.pinot.query.models;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.Objects;

@Data
public class Limit implements SqlQueryParam {

    @Nullable
    private final Integer limit;

    @Builder
    public Limit(@Nullable Integer limit) {
        if (Objects.nonNull(limit)) {
            Preconditions.checkArgument(limit > 0, "Limit needs to be a positive integer");
        }
        this.limit = limit;
    }

    @Override
    public SqlQueryParamType getType() {
        return SqlQueryParamType.LIMIT;
    }

    @Override
    public String stringify() {
        if (Objects.isNull(limit)) {
            return "";
        }

        return String.format("limit %d", limit);
    }
}
