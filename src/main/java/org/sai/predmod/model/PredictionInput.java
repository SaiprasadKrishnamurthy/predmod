package org.sai.predmod.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
public class PredictionInput implements Serializable {
    private String transactionId = UUID.randomUUID().toString();
    private String modelId;
    private Map<String, Object> inputs;
}
