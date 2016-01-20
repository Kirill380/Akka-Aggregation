package com.example.akka.aggregation;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import com.example.akka.aggregation.messages.AggregationResult;
import com.example.akka.aggregation.messages.FilePiece;
import com.example.akka.aggregation.messages.PartialSum;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Master extends UntypedActor {

    private final ActorRef workerRouter;
    private final ActorRef endProcess;
    private final long start = System.currentTimeMillis();
    private int messageCount;
    private final int numOfMessages;
    private Map<Integer, Integer> result = new HashMap<>();

    public Master(int numOfWorkers, ActorRef endProcess, int numOfMessages) {
        this.endProcess = endProcess;
        this.workerRouter =  this.getContext()
                                 .actorOf(new Props(SumWorker.class)
                                         .withRouter(new RoundRobinRouter(numOfWorkers)), "workerRouter");
        this.numOfMessages = numOfMessages;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof FilePiece) {
            workerRouter.tell(message, getSelf());

        } else if(message instanceof PartialSum) {
            Map<Integer, Integer> part = ((PartialSum) message).getAggregatedResult();

            for (Integer id : part.keySet()) {
                Integer tempResult = part.get(id);
                result.put(id, tempResult + part.get(id));
            }
            messageCount++;

            if(messageCount == numOfMessages) {
                Duration duration =  Duration.create(System.currentTimeMillis() - start, TimeUnit.MICROSECONDS);
                endProcess.tell(new AggregationResult(duration, result), getSelf());
                // Stops this actor and all its supervised children
                getContext().stop(getSelf());
            }

        } else {
            unhandled(message);
        }
    }
}
