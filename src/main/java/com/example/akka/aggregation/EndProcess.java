package com.example.akka.aggregation;

import akka.actor.UntypedActor;
import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.dispatch.OnComplete;
import com.example.akka.aggregation.messages.AggregationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


public class EndProcess extends UntypedActor {

    private static Logger log = Logger.getLogger(EndProcess.class.getName());
    private final String OUTPUT_FILE = "aggregation.txt";

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof AggregationResult) {
            final AggregationResult result = (AggregationResult) message;
            log.info("Aggregation took " + result.getDuration());

            // make non-blocking writing
            Future<String> f = Futures.future(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String res;
                    try (BufferedWriter write = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
                        for (Integer id : result.getFinalResult().keySet()) {
                            write.write(id + " : " + result.getFinalResult().get(id) + "\n");
                        }
                    } catch (IOException ex) {
                        res = "Result was not written to file.";
                    }
                    res = "Result was successfully written to file.";
                    return res;
                }
            }, getContext().dispatcher());

            f.onComplete(new OnComplete<String>() {
                @Override
                public void onComplete(Throwable throwable, String res) {
                    log.info(res);
                    getContext().system().shutdown();
                }
            });


        } else {
            unhandled(message);
        }
    }
}
