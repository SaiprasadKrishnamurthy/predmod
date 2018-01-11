package org.mews.rulepipe.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by saipkri on 16/08/17.
 */
@Entity
@Table(name = "RULEFLOW")
@Data
public class RuleFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @JoinColumn(name="EDGES")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RuleFlowEdge> edges;

    @Column
    private String postExecutionCallback;
}
