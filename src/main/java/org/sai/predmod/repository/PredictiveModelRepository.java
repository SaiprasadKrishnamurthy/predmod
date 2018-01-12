package org.sai.predmod.repository;

import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.entity.PredictiveModelJobStatusType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PredictiveModelRepository extends CrudRepository<PredictiveModel, Long> {
    PredictiveModel findByPredModelDefId(String predModelDefId);

    List<PredictiveModel> findByStatusOrderByLastTrainedDateTime(PredictiveModelJobStatusType statusType);

    List<PredictiveModel> findAllByOrderByLastTrainedDateTimeDesc();
}
