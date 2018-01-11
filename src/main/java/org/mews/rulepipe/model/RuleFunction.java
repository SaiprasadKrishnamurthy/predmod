package org.mews.rulepipe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RuleFunction {
    private String name;
    private String documentation;
    private List<String> argTypes;
}