package org.sai.predmod.rest;

import io.swagger.annotations.Api;
import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.entity.PredictiveModelJobStatusType;
import org.sai.predmod.repository.PredictiveModelRepository;
import org.sai.predmod.vertx.Bootstrap;
import org.sai.predmod.vertx.TrainingVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;

@Api("Training API")
@RestController
@RefreshScope
public class TrainingApi {

    @Autowired
    private Bootstrap bootstrap;

    @Autowired
    private PredictiveModelRepository predictiveModelRepository;

    @PostMapping("/train/{modelId}")
    public DeferredResult<?> train(@PathVariable("modelId") final String modelId, final HttpServletResponse httpServletResponse) throws Exception {
        DeferredResult<Object> response = new DeferredResult<>();
        PredictiveModel model = predictiveModelRepository.findByPredModelDefId(modelId.trim());
        if (model.getStatus() == PredictiveModelJobStatusType.InProgress) {
            response.setErrorResult(modelId + " training is currently in progress. You cannot submit another training job whilst one is in progress.");
            httpServletResponse.setStatus(429);
        } else {
            bootstrap.getVertx().eventBus().send(TrainingVerticle.class.getName(), modelId);
            response.setResult("Training Job Submitted");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        }
        return response;
    }
}
