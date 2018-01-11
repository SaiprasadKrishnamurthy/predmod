package org.mews.rulepipe.repository;

import org.mews.rulepipe.entity.Rule;
import org.mews.rulepipe.model.RuleFamilyType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by saipkri on 17/08/17.
 */
public interface RuleRepository extends CrudRepository<Rule, Long> {
    Rule findByName(String name);
    List<Rule> findByFamily(RuleFamilyType family);
}
