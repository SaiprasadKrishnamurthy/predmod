package org.sai.predmod.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PredictiveModelDef implements Serializable {
    private String id;
    private String description;
    private int trainingIterations = 2;
    private DatasourceType datasourceType;
    private String datasourceValue;
    private List<Column> inputColumns;
    private String unknownValueRepresentedAs = "?";
    private Column predictedColumn;
    private ProblemType problemType;
    private ModelType modelType = ModelType.feedforward;
}
