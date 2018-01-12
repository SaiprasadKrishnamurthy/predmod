package org.sai.predmod.entity;

import lombok.Data;
import org.encog.ml.data.versatile.columns.ColumnType;

@Data
public class Column {
    private String name;
    private ColumnType kind;
    private String[] enumerations;
    private MissingValueFunctionType missingValue;
}
