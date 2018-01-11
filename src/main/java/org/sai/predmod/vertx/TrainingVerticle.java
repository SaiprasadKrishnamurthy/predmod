package org.sai.predmod.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.model.PredictiveAnalyticsService;
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
public class TrainingVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingVerticle.class);

    private final PredictiveAnalyticsService predictiveAnalyticsService;
    private final PredictiveModelRepository predictiveModelRepository;

    @Autowired
    public TrainingVerticle(final PredictiveAnalyticsService predictiveAnalyticsService,
                            final PredictiveModelRepository predictiveModelRepository) {
        this.predictiveAnalyticsService = predictiveAnalyticsService;
        this.predictiveModelRepository = predictiveModelRepository;
    }

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        getVertx().eventBus().consumer(TrainingVerticle.class.getName(), this::exec);
    }

    private void async(final Message<String> msg) {
        getVertx().executeBlocking(future -> {
            exec(msg);
            future.complete();
        }, res -> System.out.println("RESULT"));
    }

    private void exec(final Message<String> msg) {
        String predDefId = msg.body();
        LOG.info(" \t Training: {}", predDefId);
        PredictiveModel predictiveModelDef = predictiveModelRepository.findByPredModelDefId(predDefId);
        try {
            predictiveAnalyticsService.train(predictiveModelDef);
            getVertx().eventBus().send(TrainingFinishedVerticle.class.getName(), predDefId);
        } catch (Exception ex) {
            LOG.error("Error", ex);
        }
    }
}
