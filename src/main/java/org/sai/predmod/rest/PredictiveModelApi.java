package org.sai.predmod.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.sai.predmod.entity.PredictiveModel;
import org.sai.predmod.entity.PredictiveModelDef;
import org.sai.predmod.entity.PredictiveModelHistory;
import org.sai.predmod.model.PredictionInput;
import org.sai.predmod.repository.PredictiveModelHistoryRepository;
import org.sai.predmod.repository.PredictiveModelRepository;
import org.sai.predmod.vertx.Bootstrap;
import org.sai.predmod.vertx.PredictionVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api("Prediction API")
@RestController
@RefreshScope
public class PredictiveModelApi {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private Bootstrap bootstrap;

    @Autowired
    private PredictiveModelRepository predictiveModelRepository;

    @Autowired
    private PredictiveModelHistoryRepository predictiveModelHistoryRepository;

    @PostMapping("/predict/{modelId}")
    public DeferredResult<?> predict(@PathVariable("modelId") final String modelId, final @RequestBody Map<String, Object> input, final HttpServletResponse httpServletResponse) throws Exception {
        DeferredResult<Object> response = new DeferredResult<>();
        PredictionInput predictionInput = new PredictionInput();
        predictionInput.setModelId(modelId.trim());
        predictionInput.setInputs(input);
        bootstrap.getVertx().eventBus().send(PredictionVerticle.class.getName(), OBJECT_MAPPER.writeValueAsString(predictionInput));

        // React to the success response.
        bootstrap.getVertx().eventBus().consumer(predictionInput.getTransactionId(), msg -> {
            response.setResult(msg.body());
            bootstrap.getVertx().eventBus().consumer(predictionInput.getTransactionId()).unregister();
        });
        // React to the error response.
        bootstrap.getVertx().eventBus().consumer(predictionInput.getTransactionId() + "|ERROR", msg -> {
            response.setErrorResult(msg.body());
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            bootstrap.getVertx().eventBus().consumer(predictionInput.getTransactionId() + "|ERROR").unregister();
        });
        return response;
    }

    @PutMapping("/model")
    public ResponseEntity<?> saveOrUpdateModel(final @RequestBody PredictiveModelDef modelDef) throws Exception {
        PredictiveModel predictiveModel = new PredictiveModel();
        predictiveModel.setPredModelDefId(modelDef.getId());
        predictiveModel.setPredModelDefJson(OBJECT_MAPPER.writeValueAsString(modelDef).getBytes());
        predictiveModelRepository.save(predictiveModel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/models")
    public ResponseEntity<Iterable<PredictiveModel>> findAll() throws Exception {
        return new ResponseEntity<>(predictiveModelRepository.findAllByOrderByLastTrainedDateTimeDesc(), HttpStatus.OK);
    }

    @GetMapping("/model-history")
    public ResponseEntity<Iterable<PredictiveModelHistory>> findAllHistory() throws Exception {
        return new ResponseEntity<>(predictiveModelHistoryRepository.findAllByOrderByLastTrainedDateTimeDesc(), HttpStatus.OK);
    }
}
