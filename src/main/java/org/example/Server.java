package org.example;

import org.example.entities.FDProperties;
import org.example.entities.Member;
import org.example.service.FailureDetector.Dissemination;
import org.example.service.FailureDetector.FDServer;
import org.example.service.Log.LogServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains Server related logic
 */
public class Server{

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public void startServer(){
        // Start the Log Server
        LogServer logServer = new LogServer();
        logServer.start();

        //Below code will start the Dissemination Service
        Dissemination dissemination = new Dissemination();
        dissemination.startDisseminatorService();

        //TODO code to introduce itself
        if(!((Boolean) FDProperties.getFDProperties().get("isIntroducer"))) {
            dissemination.sendAliveMessageToIntroducer();
        }else {
            FDProperties.getFDProperties().put("versionNo", Member.getLocalDateTime());
        }

        /* WE can keep pinging the nodes in a loop so that we will get the responses from all healthy nodes quickly and then wait
        for faulty nodes to reply through swim mechanism. Doing this we can achieve time bounded completeness.
        We can also do a thing like in one loop ping all members within 5 seconds and after completion of 5 secs then randomize the
        list and start the pinging process again.
        */
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //start the Failure detector scheduler
//        if(((Boolean) FDProperties.getFDProperties().get("isIntroducer"))) {
//            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            FDServer task = new FDServer(dissemination);
//            executor.scheduleAtFixedRate(task.send(), 0, 5, TimeUnit.SECONDS);
            task.start();
//        executor.scheduleAtFixedRate(task.send(), 0, (int) FDProperties.getFDProperties().get("protocolPeriod"), TimeUnit.SECONDS);
//        }
    }
}
