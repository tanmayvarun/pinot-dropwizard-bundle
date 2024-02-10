package io.dropwizard.pinot.query.models.filters;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class InFilter extends Filter {

    private final List<String> operands;

    @Builder
    protected InFilter(String columnName, List<String> operands) {
        super(FilterType.IN, columnName);
        this.operands = operands;
    }

    @Override
    public String toString() {
        StringBuilder commaSeparatedListBuilder = new StringBuilder();
        for (int i = 0; i < operands.size(); i++) {
            if (i > 0) {
                commaSeparatedListBuilder.append(",");
            }
            commaSeparatedListBuilder.append("\'").append(operands.get(i)).append("\'");
        }

        return String.format("%s in (%s)", columnName, commaSeparatedListBuilder.toString());
    }
}
