package org.mews.rulepipe.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.mews.rulepipe.entity.Rule;
import org.mews.rulepipe.entity.RuleFlow;
import org.mews.rulepipe.entity.RuleFlowEdge;
import org.mews.rulepipe.model.RuleExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TransactionalDataRepository {
    // Auto expire-able LRU cache

    private Cache<String, List<RuleFlowEdge>> ruleFlowEdgesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();
    private Cache<String, RuleExecutionContext<?>> contextsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();
    private Cache<String, RuleFlow> flowsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();
    private Cache<String, Object> responsesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();
    private Cache<String, AtomicInteger> predecessorsCountTrackCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();
    private Cache<String, Long> startTimesMillis = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    private final RuleRepository ruleRepository;
    private final RuleFlowRepository ruleFlowRepository;

    @Autowired
    public TransactionalDataRepository(final RuleFlowRepository ruleFlowRepository, final RuleRepository ruleRepository) {
        this.ruleFlowRepository = ruleFlowRepository;
        this.ruleRepository = ruleRepository;
    }

    public void setup(final RuleExecutionContext<?> ruleExecutionContext, final String ruleFlowName) {
        RuleFlow ruleFlow = ruleFlowRepository.findByName(ruleFlowName);
        ruleFlowEdgesCache.put(ruleExecutionContext.getId(), ruleFlow.getEdges());
        _setup(ruleExecutionContext, ruleFlow);

    }

    public void setup(final RuleExecutionContext<?> ruleExecutionContext, final RuleFlow ruleFlow) {
        ruleFlowEdgesCache.put(ruleExecutionContext.getId(), ruleFlow.getEdges());
        _setup(ruleExecutionContext, ruleFlow);
    }

    private void _setup(final RuleExecutionContext<?> ruleExecutionContext, final RuleFlow ruleFlow) {
        contextsCache.put(ruleExecutionContext.getId(), ruleExecutionContext);
        ruleExecutionContext.setRuleFlow(ruleFlow);
        flowsCache.put(ruleExecutionContext.getId(), ruleFlow);
        ruleFlow.getEdges().stream()
                .flatMap(edge -> Stream.of(edge.getRuleNameFrom(), edge.getRuleNameTo()))
                .forEach(ruleName ->
                        predecessorsCountTrackCache.put(ruleExecutionContext.getId() + "|" + ruleName,
                                new AtomicInteger(prevRulesFor(ruleExecutionContext.getId(), ruleName).size())));
    }

    public Rule firstRule(final RuleExecutionContext<?> ruleExecutionContext) {
        List<RuleFlowEdge> edges = ruleFlowEdgesCache.getIfPresent(ruleExecutionContext.getId());
        return ruleRepository.findByName(edges.get(0).getRuleNameFrom());
    }

    public RuleExecutionContext<?> contextFor(final String transactionId) {
        return contextsCache.getIfPresent(transactionId);
    }

    public RuleFlow flowFor(final String transactionId) {
        return flowsCache.getIfPresent(transactionId);
    }

    public void saveResponse(final RuleExecutionContext<?> ruleExecutionContext, final Object response) {
        responsesCache.put(ruleExecutionContext.getId(), response);
    }

    public Object responseFor(final RuleExecutionContext<?> ruleExecutionContext) {
        return responsesCache.getIfPresent(ruleExecutionContext.getId());
    }

    public void startExec(final RuleExecutionContext<?> ruleExecutionContext, final Rule rule) {
        startTimesMillis.put(ruleExecutionContext.getId() + "|" + rule.getName(), System.currentTimeMillis());
    }

    public void endExec(final RuleExecutionContext<?> ruleExecutionContext, final Rule rule) {
        Long totalExecTime = System.currentTimeMillis() - startTimesMillis.getIfPresent(ruleExecutionContext.getId() + "|" + rule.getName());
        ruleExecutionContext.getRuleExecutionTimingsInMillis().put(rule.getName(), totalExecTime);
    }


    public List<Rule> nextRulesFor(final String transactionId, final String ruleName) {
        AtomicInteger counter = predecessorsCountTrackCache.getIfPresent(transactionId + "|" + ruleName);
        if (counter != null) {
            counter.decrementAndGet();
        }

        List<RuleFlowEdge> edges = ruleFlowEdgesCache.getIfPresent(transactionId);
        if (edges == null) {
            return Collections.emptyList();
        }
        return edges.stream()
                .filter(edge -> edge.getRuleNameFrom().equals(ruleName))
                .filter(edge -> edge.getRuleNameTo() != null)
                .map(RuleFlowEdge::getRuleNameTo)
                .map(ruleRepository::findByName)
                .collect(Collectors.toList());
    }

    private List<Rule> prevRulesFor(final String transactionId, final String ruleName) {
        List<RuleFlowEdge> edges = ruleFlowEdgesCache.getIfPresent(transactionId);
        if (edges == null) {
            return Collections.emptyList();
        }
        return edges.stream()
                .filter(edge -> edge.getRuleNameTo() != null)
                .filter(edge -> edge.getRuleNameTo().equals(ruleName))
                .map(RuleFlowEdge::getRuleNameFrom)
                .map(ruleRepository::findByName)
                .collect(Collectors.toList());
    }

    public Rule ruleFor(final String ruleName) {
        return ruleRepository.findByName(ruleName);
    }

    public boolean hasAllPredecessorsFinishedExecution(final String transactionId, final String ruleName) {
        return predecessorsCountTrackCache.getIfPresent(transactionId + "|" + ruleName).get() <= 0;
    }
}
