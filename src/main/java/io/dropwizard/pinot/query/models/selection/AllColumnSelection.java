package io.dropwizard.pinot.query.models.selection;

import lombok.Getter;

@Getter
public class AllColumnSelection extends ColumnSelection {

    public AllColumnSelection() {
        super(SelectionType.ALL);
    }

    @Override
    public String stringify() {
        return "*";
    }
}
