package org.mews.rulepipe.model;

import org.mews.rulepipe.entity.Rule;
import org.mews.rulepipe.entity.YesNoType;
import org.mews.rulepipe.repository.TransactionalDataRepository;
import org.mews.rulepipe.executor.TimeLimitedTaskExecutor;
import org.mews.rulepipe.util.SpelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by saipkri on 18/08/17.
 */
@Component
public class DefaultRuleExecutor implements RuleExecutor {

    private final TimeLimitedTaskExecutor timeLimitedTaskExecutor;
    private final TransactionalDataRepository transactionalDataRepository;

    @Autowired
    public DefaultRuleExecutor(final TimeLimitedTaskExecutor timeLimitedTaskExecutor, final TransactionalDataRepository transactionalDataRepository) {
        this.timeLimitedTaskExecutor = timeLimitedTaskExecutor;
        this.transactionalDataRepository = transactionalDataRepository;
    }

    @Override
    public boolean evaluate(final Rule rule, final RuleExecutionContext<?> ruleExecutionContext) {
        transactionalDataRepository.startExec(ruleExecutionContext, rule);
        return !ruleExecutionContext.isShortCircuited() && SpelUtils.eval(ruleExecutionContext, rule.getEvaluationCondition());
    }

    @Override
    public void execute(final Rule rule, final RuleExecutionContext<?> ruleExecutionContext) throws Exception {
        Callable<Void> callable = () ->
        {
            ruleExecutionContext.getRulesExecutedChain().add(rule);

            // Async handling the result.
            try {
                SpelUtils.execute(ruleExecutionContext, rule.getExecutionAction());
            } catch (Exception ex) {
                ruleExecutionContext.getErroredRules().put(rule.getName(), ex.getCause().toString());
            }
            ruleExecutionContext.setShortCircuited(rule.getShortCircuit().equals(YesNoType.Y));
            transactionalDataRepository.endExec(ruleExecutionContext, rule);
            return null;
        };
        timeLimitedTaskExecutor.run(callable, rule.getTimeoutSecs(), TimeUnit.SECONDS);
    }
}
