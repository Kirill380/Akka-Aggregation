package com.example.akka.aggregation.utils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataGenerator {
    private static Logger log = Logger.getLogger(DataGenerator.class.getName());
    private final int RANGE = 10000;

    private Random rnd = new Random();
    private List<String> records = new ArrayList<>();

    public void createFile() {
        createData();
        try (BufferedWriter write = new BufferedWriter(new FileWriter(Constants.FILE_PATH))) {
            for (String record : records) {
                write.write(record + "\n");
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "I/O Exception: ", ex);
        }
    }


    private void createData() {
        for (int i = 0; i < Constants.NUMBER_OF_RECORDS; i++) {
            records.add(i % Constants.NUMBER_OF_UNIQUE + ";" + rnd.nextInt(RANGE));
        }
        shuffle(records);
    }


    private void shuffle(List<String> records) {
        for (int i = 0; i < records.size(); i++) {
            int swap = new Random().nextInt(i + 1);
            String temp = records.get(swap);
            records.set(swap, records.get(i));
            records.set(i, temp);
        }
    }
}
