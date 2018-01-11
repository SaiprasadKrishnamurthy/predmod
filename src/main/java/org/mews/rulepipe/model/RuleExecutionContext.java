package org.mews.rulepipe.model;

import org.mews.rulepipe.entity.Rule;
import org.mews.rulepipe.entity.RuleFlow;
import lombok.Data;
import lombok.ToString;

import java.util.*;

/**
 * Created by saipkri on 02/08/17.
 */
@Data
@ToString(exclude = {"stateVariables", "rulesExecutedChain"})
public class RuleExecutionContext<T> {
    private RuleFamilyType ruleFamilyType;
    private String id = UUID.randomUUID().toString();
    private T payload;
    private boolean shortCircuited;
    private Map<String, Object> stateVariables = new HashMap<>();
    private List<Rule> rulesExecutedChain = new ArrayList<>();
    private Map<String, Long> ruleExecutionTimingsInMillis = new LinkedHashMap<>();
    private Map<String, String> erroredRules = new HashMap<>();
    private RuleFlow ruleFlow;

    public static <T> RuleExecutionContext<T> newContext(final T payload) {
        RuleExecutionContext<T> tRuleExecutionContext = new RuleExecutionContext<>();
        tRuleExecutionContext.setPayload(payload);
        return tRuleExecutionContext;
    }

}
