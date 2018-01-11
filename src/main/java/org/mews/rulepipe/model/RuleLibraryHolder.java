package org.mews.rulepipe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RuleLibraryHolder {
    private Class<?> clazz;
    private String name;
    private Object instance;
    private List<Method> methods;
}