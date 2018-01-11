package org.mews.rulepipe.model;

import org.mews.rulepipe.entity.Rule;

/**
 * Created by saipkri on 18/08/17.
 */
public interface RuleExecutor {
    boolean evaluate(Rule rule, RuleExecutionContext<?> ruleExecutionContext);

    void execute(Rule rule, RuleExecutionContext<?> ruleExecutionContext) throws Exception;
}
