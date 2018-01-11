package libraries;

import org.mews.rulepipe.model.RuleExecutionContext;
import org.mews.rulepipe.model.RuleFamilyType;
import org.mews.rulepipe.model.RuleLibrary;

import java.util.List;

/**
 * Created by saipkri on 02/08/17.
 */
@RuleLibrary(documentation = "General set of utilities")
public class ActionUtil {

    @RuleLibrary(documentation = "Simple Print to the console")
    public static void print(final Object obj) {
        // This prints something.
        //throw new NullPointerException("I'm thrown here");
    }

    @RuleLibrary(documentation = "Function that initiates the Risk Assessment by calling various sources", ruleFamily = RuleFamilyType.RISK_RULE)
    public static void initiateRiskAssessment(final RuleExecutionContext ruleExecutionContext, final List<String> watchlists, final List<String> profiles) throws InterruptedException {
        // Some random function
        if (System.currentTimeMillis() % 3 == 0) {
            Thread.sleep(15_000);
        }
        if (System.currentTimeMillis() % 4 == 0) {
            throw new NullPointerException("Thrown intentionally");
        }
        ruleExecutionContext.getStateVariables().put("WatchlistResponse", "<WatchlistResponse />");
        ruleExecutionContext.getStateVariables().put("ProfilerResponse", "<ProfilerResponse />");
    }
}
