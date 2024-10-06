package com.itextos.beacon.smpp.interfaces.inmemdrainer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextos.beacon.commonlib.constants.TimerIntervalConstant;
import com.itextos.beacon.commonlib.utility.timer.ITimedProcess;
import com.itextos.beacon.commonlib.utility.tp.ScheduledTimedProcessor;
import com.itextos.beacon.platform.smpputil.ISmppInfo;
import com.itextos.beacon.smpp.objects.SmppObjectType;
import com.itextos.beacon.smpp.objects.inmem.InfoCollection;

abstract class AbstractInmemDrainer
        implements
        ITimedProcess,
        IInmemDrainer
{

    private static final Log       log         = LogFactory.getLog(AbstractInmemDrainer.class);

    protected final SmppObjectType mSmppObjectType;
 //   private final ScheduledTimedProcessorForSpleepOfEachExecution   mTimedProcessor;
    private final int              mBatchSize;
    private boolean                canContinue = true;

    AbstractInmemDrainer(
            SmppObjectType aSmppObjectType,
            int aBatchSize,
            TimerIntervalConstant aIntervalConstant)
    {
        mSmppObjectType = aSmppObjectType;
        mBatchSize      = aBatchSize;
        /*
        mTimedProcessor = new ScheduledTimedProcessorForSpleepOfEachExecution(aSmppObjectType + "-InMemDrainer", this, aIntervalConstant);
      //  mTimedProcessor.start();
        Thread virtualThreadInstance = Thread.ofVirtual().start(mTimedProcessor);
		*/
        ScheduledTimedProcessor.getInstance().start(aSmppObjectType + "-InMemDrainer", this, aIntervalConstant);
    }

    @Override
    public boolean canContinue()
    {
        return canContinue;
    }

    @Override
    public boolean processNow()
    {
        final List<ISmppInfo> objectList = InfoCollection.getInstance().getObjects(mSmppObjectType, mBatchSize);
        if (objectList.isEmpty())
            return false;

        try
        {
            processInMemObjects(objectList);
        }
        catch (final Exception e)
        {
            log.error("Exception while processing the inmemory object for the type '" + mSmppObjectType + "'", e);
            InfoCollection.getInstance().addInfoObject(mSmppObjectType, objectList);
        }
        return true;
    }

    @Override
    public void stopMe()
    {
        canContinue = false;
    }

}