package com.itextos.beacon.interfaces.generichttpapi.processor.async;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextos.beacon.commonlib.constants.TimerIntervalConstant;
import com.itextos.beacon.commonlib.message.AsyncRequestObject;
import com.itextos.beacon.commonlib.message.IMessage;
import com.itextos.beacon.commonlib.utility.timer.ITimedProcess;
import com.itextos.beacon.commonlib.utility.tp.ScheduledTimedProcessor;
import com.itextos.beacon.http.generichttpapi.common.data.QueueObject;
import com.itextos.beacon.http.generichttpapi.common.utils.FileGenUtil;

public class AsyncFallbackQReaper
        implements
        ITimedProcess,Runnable
{

    private static final Log     log         = LogFactory.getLog(AsyncFallbackQReaper.class);
 //   private final ScheduledTimedProcessorForSpleepOfEachExecution mTimedProcessor;
    private boolean              canContinue = true;

    private static class SingletonHolder
    {

        @SuppressWarnings("synthetic-access")
        static final AsyncFallbackQReaper INSTANCE = new AsyncFallbackQReaper();

    }
    
  public void run() {
    	
    	long startTime=System.currentTimeMillis();
    	int loopcount=0;
    	while(true) {
    		loopcount++;
    
    		boolean status=processNow();
    		
    		if(status) {
    			
    			if((System.currentTimeMillis()-startTime)>500||loopcount>10) {
    				
    				break;
    			}
    			
    		}else {
    			
    			break;
    			
    		}
    	}
    }

    public static AsyncFallbackQReaper getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private AsyncFallbackQReaper()
    {
    	
    	/*
        mTimedProcessor = new ScheduledTimedProcessorForSpleepOfEachExecution("AsyncFallbackInserter", this, TimerIntervalConstant.INTERFACE_FALLBACK_TABLE_INSERTER);
      //  mTimedProcessor.start();
        Thread virtualThreadInstance = Thread.ofVirtual().start(mTimedProcessor);

		*/
    	
    	ScheduledTimedProcessor.getInstance().start("AsyncFallbackInserter", this, TimerIntervalConstant.INTERFACE_FALLBACK_TABLE_INSERTER);
    	
    }

    private static boolean process()
    {
        boolean hasRecord = false;

        try
        {
            final List<IMessage> lRecords      = AsyncFallbackQ.getInstance().getMessage();
            final int            lTotalDrained = lRecords.size();
            hasRecord = lTotalDrained > 0;

            if (log.isInfoEnabled())
                log.info("Total Messages Drained - " + lTotalDrained);

            if (hasRecord)
            {
                boolean done = false;

                for (final IMessage lMessage : lRecords)
                {
                    if (log.isDebugEnabled())
                        log.debug("Stroing in file..");

                    final QueueObject lQueueObject = getQueueObject(lMessage);

                    while (!done)
                        done = FileGenUtil.storeInFile(lQueueObject);
                }
            }
        }
        catch (final Exception e)
        {
            log.error("ERROR >>>> Exception in FallbackQReaper Reaper thread \n", e);
        }
        return hasRecord;
    }

    private static QueueObject getQueueObject(
            IMessage aMessage)
    {
        final AsyncRequestObject lAsyncRequestObject = (AsyncRequestObject) aMessage;

        final QueueObject        lQueueObject        = new QueueObject(lAsyncRequestObject.getMessageId(), lAsyncRequestObject.getCustomerIp(), lAsyncRequestObject.getMessageContent(),
                lAsyncRequestObject.getMessageSource(), lAsyncRequestObject.getRequestedTime(), lAsyncRequestObject.getClientId(), lAsyncRequestObject.getClusterType().getKey(),
                lAsyncRequestObject.getMessageType().getKey());

        if (log.isDebugEnabled())
            log.debug("Message Queue Object : " + lQueueObject.toString());

        return lQueueObject;
    }

    @Override
    public boolean canContinue()
    {
        return canContinue;
    }

    @Override
    public boolean processNow()
    {
        return process();
    }

    @Override
    public void stopMe()
    {
        canContinue = false;
    }

}
