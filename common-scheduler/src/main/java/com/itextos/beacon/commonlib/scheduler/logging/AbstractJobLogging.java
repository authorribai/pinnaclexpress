package com.itextos.beacon.commonlib.scheduler.logging;

import java.util.List;

import com.itextos.beacon.commonlib.constants.TimerIntervalConstant;
import com.itextos.beacon.commonlib.utility.timer.ITimedProcess;
import com.itextos.beacon.commonlib.utility.timer.TimedProcessor;

public abstract class AbstractJobLogging
        implements
        IJobLogging,
        ITimedProcess
{

    private boolean              mCanContinue = true;
    private final TimedProcessor mTimedProcessor;

    public AbstractJobLogging()
    {
        mTimedProcessor = new TimedProcessor("ScheduleJobLogging", this, TimerIntervalConstant.DATA_REFRESHER_RELOAD_INTERVAL);
    //    mTimedProcessor.start();
        Thread virtualThreadInstance = Thread.ofVirtual().start(mTimedProcessor);

    }

    @Override
    public void storeJobTobeExecuted()
    {
        final List<JobData> lJobTobeExecuted = LoggingFactory.getInstance().getJobTobeExecuted();
        storeTobeExecuteInDb(lJobTobeExecuted);
    }

    protected abstract void storeTobeExecuteInDb(
            List<JobData> aJobTobeExecuted);

    @Override
    public void storeJobWasExecuted()
    {
        final List<JobExecutedData> lJobTobeExecuted = LoggingFactory.getInstance().getJobWasExecuted();
        storeExecutedInDb(lJobTobeExecuted);
    }

    protected abstract void storeExecutedInDb(
            List<JobExecutedData> aJobTobeExecuted);

    @Override
    public boolean canContinue()
    {
        return mCanContinue;
    }

    @Override
    public boolean processNow()
    {
        storeJobTobeExecuted();
        storeJobWasExecuted();
        return false;
    }

    @Override
    public void stopMe()
    {
        mCanContinue = false;
    }

}