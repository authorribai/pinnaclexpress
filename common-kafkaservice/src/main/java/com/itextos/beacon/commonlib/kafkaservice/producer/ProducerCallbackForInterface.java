package com.itextos.beacon.commonlib.kafkaservice.producer;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.itextos.beacon.commonlib.kafkaservice.common.KafkaRedisHandler;
import com.itextos.beacon.commonlib.messageobject.IMessage;

public class ProducerCallbackForInterface
        implements
        Callback
{

    private static final Log              log = LogFactory.getLog(ProducerCallbackForInterface.class);

    private final Producer                mProducer;
    private final String                  mTopicName;
    private final IMessage                mMessage;
    private final BlockingQueue<IMessage> mFallBackHolder;

    public ProducerCallbackForInterface(
            Producer aProducer,
            String aTopicName,
            IMessage aMessage,
            BlockingQueue<IMessage> aFallBackHolder)
    {
        mProducer       = aProducer;
        mTopicName      = aTopicName;
        mMessage        = aMessage;
        mFallBackHolder = aFallBackHolder;
    }

    @Override
    public void onCompletion(
            RecordMetadata aMetadata,
            Exception aException)
    {

        if ((aException != null) || (aMetadata == null) || !aMetadata.hasOffset())
        {
            log.error("Exception while sending to kafka. Doing the backup process. IMessage :'" + mMessage + "'", aException);

            try
            {

                if (mFallBackHolder != null) {
                	
                	log.debug("ProducerCallbackInterface got error put the message to fallbackHolder");
                	 mFallBackHolder.put(mMessage);
                }
                else
                {
                    log.error("Exception while adding the message to InMemqueue while the Kafka is not available. " + "But the fallback inmemory queue is null. "
                            + "This data will be available in the Redis. " + "Please check in Redis.");
                    KafkaRedisHandler.addToProducerRedis(mProducer.getComponent(), mTopicName, mMessage);
                }
            }
            catch (final Exception e)
            {
                log.error("Exception while adding the message to InMemqueue while the Kafka is not available. This data will be available in the Redis. Please check in Redis.", e);
                KafkaRedisHandler.addToProducerRedis(mProducer.getComponent(), mTopicName, mMessage);
            }
        }

        mProducer.removeFromInMemory(mMessage);
        mProducer.removeCompletedCallback(this);
    }

}