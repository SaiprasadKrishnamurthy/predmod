package org.mews.rulepipe.entity;

import org.mews.rulepipe.model.RuleFamilyType;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by saipkri on 16/08/17.
 */
@Entity
@Data
public class RuleSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "EVALUATIONCONDITION")
    private String evaluationCondition;

    @Column(name = "EXECUTIONACTION")
    private String executionAction;

    @Column
    private int priority;

    @Column(name = "SHORTCIRCUIT")
    @Enumerated(value = EnumType.STRING)
    private YesNoType shortCircuit;

    @Column
    @Enumerated(value = EnumType.STRING)
    private YesNoType active;

    @Column
    @Enumerated(value = EnumType.STRING)
    private RuleFamilyType family;

    @Column(name = "ABORTONERROR")
    @Enumerated(value = EnumType.STRING)
    private YesNoType abortOnError = YesNoType.Y;
}
