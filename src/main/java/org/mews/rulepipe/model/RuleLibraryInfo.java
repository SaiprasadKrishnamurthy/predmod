package org.mews.rulepipe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RuleLibraryInfo {
    private String clazz;
    private String clazzDocumentation;
    private List<RuleFunction> functions;
}