package com.itextos.beacon.httpclienthandover.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextos.beacon.commonlib.componentconsumer.processor.AbstractKafkaComponentProcessor;
import com.itextos.beacon.commonlib.constants.ClusterType;
import com.itextos.beacon.commonlib.constants.Component;
import com.itextos.beacon.commonlib.kafka.service.consumer.ConsumerInMemCollection;
import com.itextos.beacon.commonlib.messageobject.BaseMessage;
import com.itextos.beacon.commonlib.messageobject.DeliveryObject;
import com.itextos.beacon.commonlib.messageobject.IMessage;

public class DataProcessor
        extends
        AbstractKafkaComponentProcessor
{

    private static final Log log = LogFactory.getLog(DataProcessor.class);

    public DataProcessor(
            String aThreadName,
            Component aComponent,
            ClusterType aPlatformCluster,
            String aTopicName,
            ConsumerInMemCollection aConsumerInMemCollection,
            int aSleepInMillis)
    {
        super(aThreadName, aComponent, aPlatformCluster, aTopicName, aConsumerInMemCollection, aSleepInMillis);
    }

    @Override
    public void doCleanup()
    {}

    @Override
    public void doProcess(
            BaseMessage aArg0)
            throws Exception
    {
        Inmemorydata.getInstance().add((DeliveryObject) aArg0);
    }

    @Override
    protected void updateBeforeSendBack(
            IMessage aArg0)
    {}

}
