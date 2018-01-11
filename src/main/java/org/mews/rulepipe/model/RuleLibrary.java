package org.mews.rulepipe.model;

import java.lang.annotation.*;

/**
 * Created by saipkri on 03/08/17.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface RuleLibrary {
    String documentation();

    RuleFamilyType ruleFamily() default RuleFamilyType.NONE;
}
