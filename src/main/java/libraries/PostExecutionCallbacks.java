package libraries;

import org.mews.rulepipe.model.RuleExecutionContext;
import org.mews.rulepipe.model.RuleLibrary;

import java.util.Map;

/**
 * Created by saipkri on 23/08/17.
 */
@RuleLibrary(documentation = "Post execution callbacks")
public class PostExecutionCallbacks {

    @RuleLibrary(documentation = "Construct the response")
    public static Map buildResponse(final RuleExecutionContext<Map> context) {
        System.out.println(" POST EXECUTION ---- ------ " + context);
        return context.getStateVariables();
    }
}
