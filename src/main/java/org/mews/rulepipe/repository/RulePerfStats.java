package org.mews.rulepipe.repository;

public interface RulePerfStats {

    String getTransactionId();

    long getTotalTimeTakenInMillis();
}