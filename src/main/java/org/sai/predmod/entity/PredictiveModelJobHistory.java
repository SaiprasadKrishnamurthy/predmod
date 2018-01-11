package org.sai.predmod.entity;

import javax.persistence.Column;
import javax.persistence.*;

@Entity
public class PredictiveModelJobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column
    private String predModelDefJson;

    @Lob
    @Column
    private byte[] normalizedValuesBlob;

    @Lob
    @Column
    private byte[] trainedModelBlob;

    @Column
    private long lastTrainedDateTime;

    @Column
    private long lastTrainingTimeTookInSeconds;

    @Column
    private long trainingDatasetSize;

    @Column
    @Enumerated(value = EnumType.STRING)
    private PredictiveModelJobStatusType status;

    @Column
    private String error;
}
