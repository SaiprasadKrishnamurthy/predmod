package org.mews.rulepipe.repository;

import org.mews.rulepipe.config.RulebaseConfig;
import org.mews.rulepipe.model.RuleLibraryHolder;
import org.mews.rulepipe.model.RuleLibrary;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class RuleFunctionsRepository {


    private final RulebaseConfig rulebaseConfig;
    private List<RuleLibraryHolder> ruleLibraryHolders;


    @Autowired
    public RuleFunctionsRepository(final RulebaseConfig rulebaseConfiguration) {
        this.rulebaseConfig = rulebaseConfiguration;
        loadRuleLibraries();
    }

    private void loadRuleLibraries() {
        String[] rulebaseBasePkg = rulebaseConfig.getRuleLibraryBasePkgs().split(",");
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RuleLibrary.class));
        ruleLibraryHolders = Stream.of(rulebaseBasePkg)
                .flatMap(pkg -> scanner.findCandidateComponents(pkg).stream())
                .map(bd -> Unchecked.function(dontCare -> {
                    Class<?> clazz = Class.forName(bd.getBeanClassName());
                    return new RuleLibraryHolder(clazz, clazz.getSimpleName(), clazz.newInstance(), Arrays.asList(clazz.getDeclaredMethods()));
                }))
                .map(f -> f.apply(null))
                .map(ruleLibraryHolder -> {
                    RulebaseConfig.LIB_METHODS.addAll(ruleLibraryHolder.getMethods());
                    return ruleLibraryHolder;
                })
                .collect(Collectors.toList());
    }


    public List<RuleLibraryHolder> getRuleLibraryHolders() {
        return this.ruleLibraryHolders;
    }

}