package com.example.akka.aggregation.messages;


import akka.util.Duration;

import java.util.Map;

public class AggregationResult {

    private final Duration duration;
    private final Map<Integer, Integer> finalResult;

    public AggregationResult(Duration duration, Map<Integer, Integer> finalResult) {
        this.duration = duration;
        this.finalResult = finalResult;
    }

    public Duration getDuration() {
        return duration;
    }

    public Map<Integer, Integer> getFinalResult() {
        return finalResult;
    }
}
