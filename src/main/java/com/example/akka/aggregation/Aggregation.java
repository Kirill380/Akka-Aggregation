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

public class Aggregation {
    private static Logger log = Logger.getLogger(Aggregation.class.getName());

    public static void main(String[] args) {
        Aggregation agg = new Aggregation();
        agg.aggregate(4, 1000);
    }

    private void aggregate(final int numOfWorkers, final int numOfMessages) {
        ActorSystem system = ActorSystem.create("aggregation");

        // create the end process actor, which will save the result to file and shutdown the system
        final ActorRef end = system.actorOf(new Props(EndProcess.class), "endProcess");


        ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new Master(numOfWorkers, end, numOfMessages);
            }
        }), "master");


        try(BufferedReader reader = new BufferedReader(new FileReader(Constants.FILE_PATH))) {
            String line;
            List<String> piece = new ArrayList<>();
            int sizeOfMessage = Constants.NUMBER_OF_RECORDS / numOfMessages;
            while ((line = reader.readLine()) != null) {
                piece.add(line);
                if(piece.size() == sizeOfMessage) {
                    master.tell(new FilePiece(piece));
                    piece = new ArrayList<>();
                }
            }

            if(!piece.isEmpty()) {
                master.tell(new FilePiece(piece));
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Exception: ", e);
        }
    }
}
