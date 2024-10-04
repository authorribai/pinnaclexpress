package com.itextos.beacon.platform.customkafkaprocessor.main;

import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextos.beacon.commonlib.commondbpool.DBDataSourceFactory;
import com.itextos.beacon.commonlib.commondbpool.JndiInfo;
import com.itextos.beacon.commonlib.utility.CoreExecutorPoolSingleton;
import com.itextos.beacon.platform.customkafkaprocessor.CustomKafkaConsumer;
import com.itextos.beacon.platform.customkafkaprocessor.process.FullMessageTableInserter;
import com.itextos.beacon.platform.customkafkaprocessor.util.CustomKafkaProperties;

public class StartApplication
{

    private static final Log log = LogFactory.getLog(StartApplication.class);

    public static void main(
            String[] args)
    {

/*
        try (
                Connection con = DBDataSourceFactory.getConnectionFromThin(JndiInfo.CONFIGURARION_DB);)
        {}
        catch (final Exception e)
        {}
*/
        final CustomKafkaProperties lCustomKafkaProperties = CustomKafkaProperties.getInstance();

        final boolean               lStartConsumers        = startConsumers(lCustomKafkaProperties);
        if (lStartConsumers)
            startThreads(lCustomKafkaProperties);
        else
            log.fatal("No consumer started. So process thread are not started.");
    }

    private static boolean startConsumers(
            CustomKafkaProperties aCustomKafkaProperties)
    {
        final Map<String, Integer> lTopicList        = aCustomKafkaProperties.getTopicList();
        boolean                    isConsumerCreated = false;

        for (final Entry<String, Integer> entry : lTopicList.entrySet())
        {
            final String topicName     = entry.getKey();
            final int    consumerCount = entry.getValue();

            for (int i = 1; i <= consumerCount; i++)
                try
                {
                    final CustomKafkaConsumer lConsumer = new CustomKafkaConsumer(topicName, i);
                    isConsumerCreated = isConsumerCreated || lConsumer.isConsumerCreated();

                    final String threadName = topicName + "-consumer-" + i;
                    /*
                    final Thread thread     = new Thread(lConsumer, threadName);
                    thread.start();
                    */
                    
                    /*
                    Thread virtualThread = Thread.ofVirtual().start(lConsumer);

                    virtualThread.setName( threadName);
                    */
                    CoreExecutorPoolSingleton.getInstance().submitTask(lConsumer, threadName);
                    
                    log.debug("Process Thread " + threadName + " started");
                }
                catch (final Exception e)
                {
                    log.error("Exception in starting the consumer", e);
                }
        }
        return isConsumerCreated;
    }

    private static void startThreads(
            CustomKafkaProperties aCustomKafkaProperties)
    {
        final int lProcessThreadsCount = aCustomKafkaProperties.getProcessThreadsCount();

        for (int i = 1; i <= lProcessThreadsCount; i++)
        {
            final FullMessageTableInserter lFullMessageTableInserter = new FullMessageTableInserter(3000);
            final String                   threadName                = "FullMsgInserter-" + i;
           /*
            final Thread                   thread                    = new Thread(lFullMessageTableInserter, threadName);
            thread.start();
            */
            
            /*
            Thread virtualThread = Thread.ofVirtual().start(lFullMessageTableInserter);

            virtualThread.setName( threadName);
            */
            CoreExecutorPoolSingleton.getInstance().submitTask(lFullMessageTableInserter, threadName);
            log.debug("Process Thread " + threadName + " started");
        }
    }

}