package com.example.akka.aggregation;


import akka.actor.*;
import com.example.akka.aggregation.messages.FilePiece;
import com.example.akka.aggregation.utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
* Time of aggregation depend on number of workers and size of messages that
* calculated using numOfMessages.
* */
public class Aggregation {
    private static Logger log = Logger.getLogger(Aggregation.class.getName());
    private int numOfWorkers = 4;
    private int numOfMessages = 1000;
    private String filePath = Constants.FILE_PATH;


    public static void main(String[] args) {
        Aggregation agg = new Aggregation();
        agg.parseInput(args);
        agg.aggregate();
    }

    private void aggregate() {
        ActorSystem system = ActorSystem.create("aggregation");

        // create the end process actor, which will save the result to file and shutdown the system
        final ActorRef end = system.actorOf(new Props(EndProcess.class), "endProcess");


        ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new Master(numOfWorkers, end, numOfMessages);
            }
        }), "master");


        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<String> piece = new ArrayList<>();
            int sizeOfMessage = Constants.NUMBER_OF_RECORDS / numOfMessages;

            while ((line = reader.readLine()) != null) {
                piece.add(line);
                if (piece.size() == sizeOfMessage) {
                    master.tell(new FilePiece(piece));
                    piece = new ArrayList<>();
                }
            }

            if (!piece.isEmpty()) {
                master.tell(new FilePiece(piece));
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Exception: ", e);
        }
    }


    private void parseInput(String[] args) {
        for (int i = 0; i < args.length; i++) {
            // detect option if no options were detected then ignore program arguments
            if (args[i].contains("-")) {
                if (args.length <= i + 1)
                    throw new IllegalArgumentException("Option may not be without value");

                switch (args[i]) {
                    case "-w":
                        numOfWorkers = parseIntValue(args[i + 1]);
                        break;
                    case "-m":
                        numOfMessages = parseIntValue(args[i + 1]);
                        break;
                    case "-f":
                        filePath = args[i + 1];
                        break;
                    default:
                        throw new IllegalArgumentException("No such option");
                }
            }
        }

    }


    private int parseIntValue(String value) {
        int num = Integer.parseInt(value);
        if (num <= 0)
            throw new IllegalArgumentException("Value may not be less or equal zero");
        return num;
    }

}
