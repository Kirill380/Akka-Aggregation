package com.example.akka.aggregation.messages;


import java.util.Collections;
import java.util.Map;

public class PartialSum {

    private final Map<Integer, Integer> aggregatedResult;

    public PartialSum(Map<Integer, Integer> aggregatedResult) {
        this.aggregatedResult = Collections.unmodifiableMap(aggregatedResult);
    }

    public Map<Integer, Integer> getAggregatedResult() {
        return aggregatedResult;
    }
}
