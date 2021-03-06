package org.sai.predmod.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.*;

@Entity
@Data
public class PredictiveModelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String predModelDefId;

    @Lob
    @Column(length = 10 * 1024 * 1024) // 10 megabytes
    private byte[] predModelDefJson;

    @Lob
    @Column(length = 10 * 1024 * 1024) // 10 megabytes
    private byte[] normalizedValuesBlob;

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
    private PredictiveModelJobStatusType status;

    @Column(length = 10 * 1024 * 1024)
    private String error;

    @Transient
    private String definitionJson;

    public String getDefinitionJson() {
        return new String(predModelDefJson);
    }
}
