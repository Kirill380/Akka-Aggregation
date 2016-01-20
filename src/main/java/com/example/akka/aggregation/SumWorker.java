package com.example.akka.aggregation;

import akka.actor.UntypedActor;
import com.example.akka.aggregation.messages.FilePiece;
import com.example.akka.aggregation.messages.PartialSum;

import java.util.HashMap;
import java.util.Map;


public class SumWorker extends UntypedActor {
    

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof FilePiece) {
            FilePiece piece = (FilePiece) message;
            Map<Integer, Integer> res = new HashMap<>();
            for (String record : piece.getRecords()) {
                String[] keyValue = record.split(";");
                int id = Integer.parseInt(keyValue[0]);
                int amount = Integer.parseInt(keyValue[1]);
                if(res.containsKey(id)) {
                    res.put(id, res.get(id) + amount);
                } else  {
                    res.put(id, amount);
                }
            }
            getSender().tell(new PartialSum(res), getSelf());
        } else  {
            unhandled(message);
        }
    }
}
