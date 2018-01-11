package org.sai.predmod.model;

import org.sai.predmod.entity.PredictiveModel;

import java.util.Map;

public interface PredictiveAnalyticsService {
    boolean trainingInProgress(PredictiveModel predictiveModelJobConfig);
    void train(PredictiveModel predictiveModelJobConfig);
    void snapshotHistory(PredictiveModel predictiveModelJobConfig);
    Object predict(PredictiveModel predictiveModelJobConfig, Map<String, Object> inputVariables);
}
