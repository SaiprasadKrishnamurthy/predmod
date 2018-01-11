package org.mews.rulepipe.repository;

import org.mews.rulepipe.entity.RuleAudit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by saipkri on 17/08/17.
 */
public interface RuleAuditRepository extends CrudRepository<RuleAudit, Long> {
    List<RuleAudit> findAllByOrderByTimestampDesc();

    List<RulePerfStats> findTop10ByOrderByTimestampDesc();
}
