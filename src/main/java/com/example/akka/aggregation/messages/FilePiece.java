package com.example.akka.aggregation.messages;

import java.util.Collections;
import java.util.List;


public class FilePiece {

    private final List<String> records;


    public FilePiece(List<String> records) {
        this.records = Collections.unmodifiableList(records);
    }


    public List<String> getRecords() {
        return records;
    }
}
