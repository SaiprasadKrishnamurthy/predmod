package org.mews.rulepipe.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Created by saipkri on 16/08/17.
 */
@Entity
@Data
public class RuleAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String transactionId;

    @Column
    private long timestamp;

    @Column
    private String flowName;

    @Column
    private String flowDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RULEEXECTIME")
    @MapKeyColumn(name = "KEY")
    @Column(name = "VALUE")
    private Map<String, Long> ruleExecTime = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    @Lob
    private Map<String, String> ruleErrors = new HashMap<>();

    @Column
    private long totalTimeTakenInMillis;

    @JoinColumn(name = "EDGES")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RuleFlowEdgeSnapshot> edges;

    @Transient
    private String pipeline;

    @Transient
    private String displayId;

    @Transient
    private String labelType;

    @JoinColumn(name = "RULES")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RuleSnapshot> rules;

    @Data
    private static class RuleInfo {
        private String ruleName;
        private Long execTime;
        private String status;
        private String errorMsg;
    }

    @Transient
    private List<RuleInfo> ruleInfos = new ArrayList<>();

    public String getPipeline() {
        Set<String> allRules = new LinkedHashSet<>();
        StringBuilder out = new StringBuilder();
        out.append("graph LR").append("\n");
        edges.forEach(ruleFlowEdgeSnapshot -> {
            if (!ruleErrors.containsKey(ruleFlowEdgeSnapshot.getRuleNameFrom())) {
                out.append(ruleFlowEdgeSnapshot.getRuleNameFrom()).append("[").append(ruleFlowEdgeSnapshot.getRuleNameFrom()).append("]");
            } else {
                out.append(ruleFlowEdgeSnapshot.getRuleNameFrom()).append("[").append(ruleFlowEdgeSnapshot.getRuleNameFrom()).append(" <br /><br />").append("ERROR").append("]");
            }
            out.append("  ==>  ");
            if (!ruleErrors.containsKey(ruleFlowEdgeSnapshot.getRuleNameTo())) {
                out.append(ruleFlowEdgeSnapshot.getRuleNameTo()).append("[").append(ruleFlowEdgeSnapshot.getRuleNameTo() == null ? ruleFlowEdgeSnapshot.getRuleNameFrom() : ruleFlowEdgeSnapshot.getRuleNameTo()).append("]");
            } else {
                out.append(ruleFlowEdgeSnapshot.getRuleNameTo()).append("[").append(ruleFlowEdgeSnapshot.getRuleNameTo() == null ? ruleFlowEdgeSnapshot.getRuleNameFrom() : ruleFlowEdgeSnapshot.getRuleNameTo()).append(" <br /><br />").append("ERROR").append("]");
            }
            out.append("\n");
            allRules.add(ruleFlowEdgeSnapshot.getRuleNameFrom());
            allRules.add(ruleFlowEdgeSnapshot.getRuleNameTo());

        });
        Set<String> notExecuted = allRules.stream()
                .filter(Objects::nonNull)
                .filter(name -> rules.stream().noneMatch(r -> name.equals(r.getName())))
                .collect(Collectors.toSet());

        out.append("\n");
        out.append("classDef green fill:#9f6,stroke:#333,stroke-width:2px;").append("\n");
        String successRules = allRules.stream().filter(name -> !notExecuted.contains(name)).collect(joining(","));
        String errorRules = ruleErrors.keySet().stream().collect(joining(","));
        out.append("class ").append(successRules).append(" green ").append("\n");
        return out.toString();
    }

    public String getDisplayId() {
        return "a" + transactionId;
    }

    public String getLabelType() {
        return !ruleErrors.isEmpty() ? "danger" : "info";
    }

    public List<RuleInfo> getRuleInfos() {
        ruleExecTime.entrySet().forEach(entry -> {
            RuleInfo ruleInfo = new RuleInfo();
            ruleInfo.setRuleName(entry.getKey());
            ruleInfo.setExecTime(entry.getValue());
            ruleInfo.setStatus(ruleErrors.containsKey(entry.getKey()) ? "ERROR" : "SUCCESS");
            ruleInfo.setErrorMsg(ruleErrors.getOrDefault(entry.getKey(), ""));
            ruleInfos.add(ruleInfo);
        });
        return ruleInfos;
    }
}
