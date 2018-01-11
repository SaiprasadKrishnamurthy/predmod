package org.mews.rulepipe.vertx;

import org.mews.rulepipe.entity.Rule;
import org.mews.rulepipe.model.RuleExecutor;
import org.mews.rulepipe.repository.TransactionalDataRepository;
import org.mews.rulepipe.model.RuleExecutionContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by saipkri on 18/08/17.
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class RuleExecutorVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(RuleExecutorVerticle.class);

    private final TransactionalDataRepository transactionalDataRepository;
    private final RuleExecutor ruleExecutor;

    @Autowired
    public RuleExecutorVerticle(final TransactionalDataRepository transactionalDataRepository, final RuleExecutor ruleExecutor) {
        this.transactionalDataRepository = transactionalDataRepository;
        this.ruleExecutor = ruleExecutor;
    }

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        getVertx().eventBus().consumer(RuleExecutorVerticle.class.getName(), this::exec);
    }

    private void exec(final Message<Object> msg) {
        String payload = msg.body().toString();
        String transactionId = payload.split("\\|")[0];
        String ruleName = payload.split("\\|")[1];

        LOG.info(" \t Rule: {}", ruleName);

        List<Rule> nextRules = transactionalDataRepository.nextRulesFor(transactionId, ruleName);

        boolean hasAllPredecessorsFinishedExecution = transactionalDataRepository.hasAllPredecessorsFinishedExecution(transactionId, ruleName);

        RuleExecutionContext<?> ruleExecutionContext = transactionalDataRepository.contextFor(transactionId);
        Rule rule = transactionalDataRepository.ruleFor(ruleName);

        if (ruleExecutor.evaluate(rule, ruleExecutionContext)) {
            try {
                ruleExecutor.execute(rule, ruleExecutionContext);
            } catch (Exception exception) {
                LOG.error("Error {} | {} | {}", ruleName, transactionId, exception);
                ruleExecutionContext.getErroredRules().put(ruleName, exception.toString());
            }
        }
        // Fire the next rules in the pipeline.
        if (nextRules.isEmpty()) {
            getVertx().eventBus().send(ResponseBuilderVerticle.class.getName(), transactionId + "|" + ""); // Don't need to pass anything around.
        } else if (hasAllPredecessorsFinishedExecution) {
            nextRules
                    .forEach(next -> callNextRule(transactionId, next));
        } else {
            LOG.info("\t\t ---  {} I'm still waiting, have more job to do ---- ", ruleName);
        }
    }

    private EventBus callNextRule(final String transactionId, final Rule next) {
        return vertx.eventBus().send(RuleExecutorVerticle.class.getName(), transactionId + "|" + next.getName());
    }
}
