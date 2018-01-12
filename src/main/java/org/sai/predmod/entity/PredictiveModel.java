package org.sai.predmod.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.*;

@Data
@Entity
public class PredictiveModel {

    @Id
    @Column(nullable = false)
    private String predModelDefId;

    @JsonIgnore
    @Lob
    @Column(length = 10 * 1024 * 1024) // 10 megabytes
    private byte[] predModelDefJson;

    @JsonIgnore
    @Lob
    @Column(length = 10 * 1024 * 1024) // 10 megabytes
    private byte[] normalizedValuesBlob;

    @JsonIgnore
    @Lob
    @Column(length = 10 * 1024 * 1024) // 10 megabytes
    private byte[] trainedModelBlob;

    @Column
    private long lastTrainedDateTime;

    @Column
    private double lastTrainingTimeTookInSeconds;

    @Column
    private long trainingDatasetSize;

    @Column
    @Enumerated(value = EnumType.STRING)
    private PredictiveModelJobStatusType status = PredictiveModelJobStatusType.Created;

    @Column(length = 10 * 1024 * 1024)
    @Lob
    private String error;

    @Transient
    private String definitionJson;

    @Transient
    private PredictiveModelDef definition;

    public String getDefinitionJson() {
        return new String(predModelDefJson);
    }

    public PredictiveModelDef getDefinition() {
        try {
            return new ObjectMapper().readValue(getDefinitionJson(), PredictiveModelDef.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public void setDefinition(PredictiveModelDef definition) {
        this.definition = definition;
    }
}
