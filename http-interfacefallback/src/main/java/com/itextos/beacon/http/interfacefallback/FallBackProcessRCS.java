package com.itextos.beacon.http.interfacefallback;

import com.itextos.beacon.commonlib.messageobject.IMessage;
import com.itextos.beacon.http.interfacefallback.inmem.FallbackQ;

public class FallBackProcessRCS
{

    private FallBackProcessRCS()
    {}

    public static boolean sendToFallBack(
            IMessage aIMessage)
    {
        boolean isDone = false;
        while (!isDone)
            try
            {
                FallbackQ.getInstance().addMessage(aIMessage);
                isDone = true;
            }
            catch (final Exception e)
            {
                isDone = false;
            }

        return isDone;
    }

}