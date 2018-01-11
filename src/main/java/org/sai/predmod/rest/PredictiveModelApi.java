package org.sai.predmod.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.sai.predmod.model.PredictionInput;
import org.sai.predmod.vertx.Bootstrap;
import org.sai.predmod.vertx.PredictionVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

@Api("Prediction API")
@RestController
@RefreshScope
public class PredictiveModelApi {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private Bootstrap bootstrap;

    @PostMapping("/predict/{modelId}")
    public DeferredResult<?> predict(@PathVariable("modelId") final String modelId, final @RequestBody Map<String, Object> input) throws Exception {
        DeferredResult<Object> response = new DeferredResult<>();
        PredictionInput predictionInput = new PredictionInput();
        predictionInput.setModelId(modelId.trim());
        predictionInput.setInputs(input);
        bootstrap.getVertx().eventBus().send(PredictionVerticle.class.getName(), OBJECT_MAPPER.writeValueAsString(predictionInput));

        // React to the response.
        bootstrap.getVertx().eventBus().consumer(predictionInput.getTransactionId(), msg -> {
            response.setResult(msg.body());
            bootstrap.getVertx().eventBus().consumer(predictionInput.getTransactionId()).unregister();
        });
        return response;
    }
}
