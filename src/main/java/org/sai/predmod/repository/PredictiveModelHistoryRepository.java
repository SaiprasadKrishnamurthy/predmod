package org.sai.predmod.repository;

import org.sai.predmod.entity.PredictiveModelHistory;
import org.sai.predmod.entity.PredictiveModelJobStatusType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PredictiveModelHistoryRepository extends CrudRepository<PredictiveModelHistory, Long> {
    PredictiveModelHistory findByPredModelDefId(String predModelDefId);
    List<PredictiveModelHistory> findByStatusOrderByLastTrainedDateTime(PredictiveModelJobStatusType statusType);
    List<PredictiveModelHistory> findAllByOrderByLastTrainedDateTimeDesc();
}
