package org.sai.predmod.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.repository.PredictiveModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author saikris
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class TrainingFinishedVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingFinishedVerticle.class);

    private final PredictiveModelRepository predictiveModelRepository;

    @Autowired
    public TrainingFinishedVerticle(final PredictiveModelRepository predictiveModelRepository) {
        this.predictiveModelRepository = predictiveModelRepository;
    }

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        getVertx().eventBus().consumer(TrainingFinishedVerticle.class.getName(), this::exec);
    }

    private void exec(final Message<String> msg) {
        String predDefId = msg.body();
        LOG.info(" \t Training Finished : {}, Snapshotting: {}", predDefId, predDefId);
        PredictiveModel model = predictiveModelRepository.findByPredModelDefId(predDefId);
        LOG.info("TRaining {} took: {} seconds (Training set size: {}) ", predDefId, model.getLastTrainingTimeTookInSeconds(), model.getTrainingDatasetSize());
    }
}
