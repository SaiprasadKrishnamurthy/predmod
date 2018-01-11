package org.mews.rulepipe.repository;

import org.mews.rulepipe.entity.RuleFlow;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by saipkri on 17/08/17.
 */
public interface RuleFlowRepository extends CrudRepository<RuleFlow, Long> {
    RuleFlow findByName(String name);
}
