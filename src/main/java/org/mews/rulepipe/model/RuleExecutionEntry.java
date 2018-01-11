package org.mews.rulepipe.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by saipkri on 23/08/17.
 */
@Data
@AllArgsConstructor
public class RuleExecutionEntry {
    private final String transactionId;
    private final String ruleName;
    private final Long timestamp;
}
