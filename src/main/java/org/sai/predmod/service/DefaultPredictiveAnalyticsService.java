package org.sai.predmod.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.encog.ConsoleStatusReportable;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.model.EncogModel;
import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.entity.PredictiveModelDef;
import org.sai.predmod.entity.PredictiveModelJobStatusType;
import org.sai.predmod.model.PredictiveAnalyticsService;
import org.sai.predmod.repository.PredictiveModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

@Service
public class DefaultPredictiveAnalyticsService implements PredictiveAnalyticsService {

    private final PredictiveModelRepository predictiveModelRepository;
    private final DatasourceFactory datasourceFactory;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    public DefaultPredictiveAnalyticsService(final PredictiveModelRepository predictiveModelRepository,
                                             final DatasourceFactory datasourceFactory) {
        this.predictiveModelRepository = predictiveModelRepository;
        this.datasourceFactory = datasourceFactory;
    }

    @Override
    public boolean trainingInProgress(final PredictiveModel predictiveModelJobConfig) {
        return predictiveModelRepository.findByPredModelDefId(predictiveModelJobConfig.getPredModelDefId()).getStatus() == PredictiveModelJobStatusType.InProgress;
    }

    @Override
    public void train(final PredictiveModel predictiveModel) {
        try {
            PredictiveModelDef predictiveModelDef = OBJECT_MAPPER.readValue(new String(predictiveModel.getPredModelDefJson()), PredictiveModelDef.class);
            VersatileDataSource dataSource = datasourceFactory.dataSource(predictiveModelDef);
            VersatileMLDataSet data = new VersatileMLDataSet(dataSource);

            // Set up input variables.
            final MutableInt index = new MutableInt(0);
            predictiveModelDef.getInputColumns().forEach(inputColumn -> {
                ColumnDefinition columnDefinition = data.defineSourceColumn(inputColumn.getName(), index.intValue(), inputColumn.getKind());
                // enumerations if any
                if (inputColumn.getKind() == ColumnType.ordinal) {
                    columnDefinition.defineClass(inputColumn.getEnumerations());
                }
                data.getNormHelper().defineMissingHandler(columnDefinition, MissingHandlers.missingHandlerFor(predictiveModelDef.getMissingValue()));
                index.increment();
            });

            data.getNormHelper().defineUnknownValue(predictiveModelDef.getUnknownValueRepresentedAs());

            // Define response variable.
            ColumnDefinition outputColumn = data.defineSourceColumn(predictiveModelDef.getPredictedColumn().getName(), index.intValue(), ColumnType.nominal);
            data.analyze();
            data.defineSingleOutputOthersInput(outputColumn);

            EncogModel model = new EncogModel(data);
            model.selectMethod(data, predictiveModelDef.getModelType().name());

            // Send any output to the console.
            model.setReport(new ConsoleStatusReportable());

            // Now normalize the data.  Encog will automatically determine the correct normalization
            // type based on the model you chose in the last step.
            data.normalize();

            // Hold back some data for a final validation.
            // Shuffle the data into a random ordering.
            // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
            model.holdBackValidation(0.3, true, 1001);

            // Choose whatever is the default training type for this model.
            model.selectTrainingType(data);

            // Use a n-fold cross-validated train.  Return the best method found.
            MLRegression bestMethod = (MLRegression) model.crossvalidate(predictiveModelDef.getTrainingIterations(), true);

            // Display our normalization parameters.
            NormalizationHelper helper = data.getNormHelper();

            byte[] bestMethodSer = SerializationUtils.serialize((Serializable) bestMethod);
            byte[] helperSer = SerializationUtils.serialize(helper);

            predictiveModel.setTrainedModelBlob(bestMethodSer);
            predictiveModel.setNormalizedValuesBlob(helperSer);

            // update the model.
            predictiveModelRepository.save(predictiveModel);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean snapshotHistory(final PredictiveModel predictiveModelJobConfig) {
        return false;
    }

    @Override
    public Object predict(final PredictiveModel predictiveModel, final Map<String, Object> inputVariables) {
        try {
            PredictiveModelDef predictiveModelDef = OBJECT_MAPPER.readValue(predictiveModel.getPredModelDefJson(), PredictiveModelDef.class);
            NormalizationHelper helper = (NormalizationHelper) SerializationUtils.deserialize(predictiveModel.getNormalizedValuesBlob());
            MLRegression bestMethod = (MLRegression) SerializationUtils.deserialize(predictiveModel.getTrainedModelBlob());

            MLData input = helper.allocateInputVector();
            String[] line = new String[inputVariables.size()];
            final MutableInt index = new MutableInt(0);

            predictiveModelDef.getInputColumns()
                    .forEach(inputColumn -> {
                        line[index.intValue()] = inputVariables.get(inputColumn.getName()).toString();
                        index.increment();
                    });

            helper.normalizeInputVector(line, input.getData(), false);

            MLData output = bestMethod.compute(input);
            return helper.denormalizeOutputVectorToString(output)[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
