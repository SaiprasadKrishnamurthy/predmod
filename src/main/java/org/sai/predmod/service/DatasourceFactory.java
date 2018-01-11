package org.sai.predmod.service;

import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.util.csv.CSVFormat;
import org.sai.predmod.entity.DatasourceType;
import org.sai.predmod.entity.PredictiveModelDef;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DatasourceFactory {

    public VersatileDataSource dataSource(final PredictiveModelDef predictiveModelDef) {
        if (predictiveModelDef.getDatasourceType() == DatasourceType.CSV) {
            return new CSVDataSource(new File(predictiveModelDef.getDatasourceValue()), false, CSVFormat.DECIMAL_POINT);
        } else {
            throw new UnsupportedOperationException("RDBMS Datasource Not implemented yet");
        }
    }
}
