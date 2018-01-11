package org.mews.rulepipe.vertx;

import org.mews.rulepipe.entity.RuleFlow;
import org.mews.rulepipe.repository.TransactionalDataRepository;
import org.mews.rulepipe.model.RuleExecutionContext;
import org.mews.rulepipe.util.SpelUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by saipkri on 18/08/17.
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class ResponseBuilderVerticle extends AbstractVerticle {

    private final TransactionalDataRepository transactionalDataRepository;

    @Autowired
    public ResponseBuilderVerticle(final TransactionalDataRepository transactionalDataRepository) {
        this.transactionalDataRepository = transactionalDataRepository;
    }

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        getVertx().eventBus().consumer(ResponseBuilderVerticle.class.getName(), this::exec);
    }

    private void exec(final Message<Object> msg) {
        String payload = msg.body().toString();
        String transactionId = payload.split("\\|")[0];

        RuleExecutionContext<?> ruleExecutionContext = transactionalDataRepository.contextFor(transactionId);
        RuleFlow ruleFlow = transactionalDataRepository.flowFor(transactionId);

        cleanupFalseTimeouts(ruleExecutionContext);

        if (StringUtils.isNotBlank(ruleFlow.getPostExecutionCallback())) {
            Object response = SpelUtils.invoke(ruleExecutionContext, ruleFlow.getPostExecutionCallback());
            transactionalDataRepository.saveResponse(ruleExecutionContext, response);
        } else {
            transactionalDataRepository.saveResponse(ruleExecutionContext, ruleExecutionContext);
        }
        getVertx().eventBus().send("DONE|" + ruleExecutionContext.getId(), ""); // Don't need to pass anything around.
    }

    private void cleanupFalseTimeouts(final RuleExecutionContext<?> ruleExecutionContext) {
        Set<String> timedoutKeys = ruleExecutionContext.getErroredRules().entrySet().stream()
                .filter(entry -> entry.getValue().toLowerCase().contains("timeout"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        timedoutKeys.forEach(ruleName -> {
            if (ruleExecutionContext.getRuleExecutionTimingsInMillis()
                    .get(ruleName) <= transactionalDataRepository.ruleFor(ruleName).getTimeoutSecs() * 1000) {
                ruleExecutionContext.getErroredRules().remove(ruleName);
            }
        });
    }
}
