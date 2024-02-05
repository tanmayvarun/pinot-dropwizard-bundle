package io.dropwizard.pinot.query.models.selection;

import com.google.common.base.Joiner;
import io.dropwizard.pinot.constants.GeneralConstants;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Getter
public class ColumnSubsetSelection extends ColumnSelection {

    private final List<String> columns;

    @Builder
    public ColumnSubsetSelection(List<String> columns, List<String> tableColumns) {
        super(SelectionType.SPECIFIC);
        validateColumns(columns, tableColumns);
        this.columns = columns;
    }

    private void validateColumns(List<String> columns, List<String> tableColumns) {
        boolean valid = !CollectionUtils.isEmpty(columns) &&
                columns.stream().noneMatch(column -> StringUtils.isBlank(column) || !tableColumns.contains(column));

        if (! valid) {
            throw PinotDaoException.error(ErrorCode.INVALID_PINOT_QUERY, Map.of("columns", columns));
        }
    }

    @Override
    public String stringify() {
        return String.format("%s", Joiner.on(GeneralConstants.COMMA_DELIMITER).join(columns));
    }
}
