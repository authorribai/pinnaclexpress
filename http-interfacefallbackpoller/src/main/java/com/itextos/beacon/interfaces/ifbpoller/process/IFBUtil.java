package com.itextos.beacon.interfaces.ifbpoller.process;

import com.itextos.beacon.commonlib.constants.ConfigParamConstants;
import com.itextos.beacon.inmemory.appconfigparams.ApplicationConfiguration;
import com.itextos.beacon.inmemory.loader.InmemoryLoaderCollection;
import com.itextos.beacon.inmemory.loader.process.InmemoryId;

public class IFBUtil
{

    private IFBUtil()
    {}

    public static String getConfigParamsValueAsString(
            ConfigParamConstants aKey)
    {
        final ApplicationConfiguration lAppConfigValues = (ApplicationConfiguration) InmemoryLoaderCollection.getInstance().getInmemoryCollection(InmemoryId.APPLICATION_CONFIG);
        return lAppConfigValues.getConfigValue(aKey.getKey());
    }

}
