package org.mews.rulepipe.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saipkri on 18/08/17.
 */
@Configuration
@Data
public class RulebaseConfig {
    public static final List<Method> LIB_METHODS = new ArrayList<>();

    @Value("${workerInstances ?: 8}")
    private int ruleExecutorInstances;

    @Value("${ruleLibraryBasePkgs}")
    private String ruleLibraryBasePkgs;

}
