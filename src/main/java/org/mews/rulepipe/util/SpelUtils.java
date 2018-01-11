package org.mews.rulepipe.util;

import org.mews.rulepipe.config.RulebaseConfig;
import org.mews.rulepipe.model.RuleExecutionContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Created by saipkri on 02/08/17.
 */
public class SpelUtils {

    private SpelUtils() {
    }

    public static boolean eval(final RuleExecutionContext ruleExecutionContext, final String spelExpression) {
        // TODO do not construct every time.
        StandardEvaluationContext simpleContext = new StandardEvaluationContext(ruleExecutionContext);
        simpleContext.setVariable("ctx", ruleExecutionContext);
        RulebaseConfig.LIB_METHODS.forEach(m -> {
            simpleContext.registerFunction(m.getName(), m);
        });
        ExpressionParser parser = new SpelExpressionParser();
        return (Boolean) parser.parseExpression(spelExpression).getValue(simpleContext);
    }

    public static void execute(final RuleExecutionContext ruleExecutionContext, final String spelExpression) {
        // TODO do not construct every time.
        StandardEvaluationContext simpleContext = new StandardEvaluationContext(ruleExecutionContext);
        simpleContext.setVariable("ctx", ruleExecutionContext);
        RulebaseConfig.LIB_METHODS.forEach(m -> simpleContext.registerFunction(m.getName(), m));
        ExpressionParser parser = new SpelExpressionParser();
        parser.parseExpression(spelExpression).getValue(simpleContext);
    }

    public static Object invoke(final RuleExecutionContext ruleExecutionContext, final String spelExpression) {
        // TODO do not construct every time.
        StandardEvaluationContext simpleContext = new StandardEvaluationContext(ruleExecutionContext);
        simpleContext.setVariable("ctx", ruleExecutionContext);
        RulebaseConfig.LIB_METHODS.forEach(m -> simpleContext.registerFunction(m.getName(), m));
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(spelExpression).getValue(simpleContext);
    }
}
