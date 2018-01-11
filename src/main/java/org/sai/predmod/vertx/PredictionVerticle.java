package org.sai.predmod.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.model.PredictionInput;
import org.sai.predmod.model.PredictiveAnalyticsService;
import org.sai.predmod.repository.PredictiveModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PredictionVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(PredictionVerticle.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PredictiveAnalyticsService predictiveAnalyticsService;
    private final PredictiveModelRepository predictiveModelRepository;

    @Autowired
    public PredictionVerticle(final PredictiveAnalyticsService predictiveAnalyticsService,
                              final PredictiveModelRepository predictiveModelRepository) {
        this.predictiveAnalyticsService = predictiveAnalyticsService;
        this.predictiveModelRepository = predictiveModelRepository;
    }

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        getVertx().eventBus().consumer(PredictionVerticle.class.getName(), this::exec);
    }

    private void exec(final Message<String> msg) {
        try {
            String predictionInputJson = msg.body();
            PredictionInput predictionInput = OBJECT_MAPPER.readValue(predictionInputJson, PredictionInput.class);
            LOG.info(" \t Prediction: {}", predictionInput);
            PredictiveModel model = predictiveModelRepository.findByPredModelDefId(predictionInput.getModelId());
            Object predictedValue = predictiveAnalyticsService.predict(model, predictionInput.getInputs());
            getVertx().eventBus().send(predictionInput.getTransactionId(), predictedValue);
        } catch (Exception ex) {
            LOG.error("Error", ex);
        }
    }
}
