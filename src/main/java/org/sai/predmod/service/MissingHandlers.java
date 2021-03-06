package org.sai.predmod.service;

import org.encog.EncogError;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.missing.MeanMissingHandler;
import org.encog.ml.data.versatile.missing.MissingHandler;
import org.sai.predmod.entity.MissingValueFunctionType;

public final class MissingHandlers {

    private MissingHandlers() {
    }

    public static MissingHandler missingHandlerFor(MissingValueFunctionType missingValueFunctionType) {
        if (missingValueFunctionType == MissingValueFunctionType.Mean) {
            return new MeanMissingHandler();
        } else if (missingValueFunctionType == MissingValueFunctionType.Max) {
            return new MeanMissingHandler() {
                @Override
                public double processDouble(final ColumnDefinition columnDefinition) {
                    return columnDefinition.getHigh();
                }
            };
        } else if (missingValueFunctionType == MissingValueFunctionType.Min) {
            return new MeanMissingHandler() {
                @Override
                public double processDouble(final ColumnDefinition columnDefinition) {
                    return columnDefinition.getLow();
                }
            };
        } else if (missingValueFunctionType == MissingValueFunctionType.StandardDeviation) {
            return new MeanMissingHandler() {
                @Override
                public double processDouble(final ColumnDefinition columnDefinition) {
                    return columnDefinition.getSd();
                }
            };
        } else if (missingValueFunctionType == MissingValueFunctionType.QuestionMarkString) {
            return new MissingHandler() {
                @Override
                public void init(NormalizationHelper normalizationHelper) {

                }

                @Override
                public String processString(ColumnDefinition columnDefinition) {
                    return "?";
                }

                @Override
                public double processDouble(final ColumnDefinition columnDefinition) {
                    throw new EncogError("This doesn't accept a numeric value.");
                }
            };
        } else {
            return new MeanMissingHandler() {
                @Override
                public double processDouble(final ColumnDefinition columnDefinition) {
                    return 0D;
                }
            };
        }
    }
}
