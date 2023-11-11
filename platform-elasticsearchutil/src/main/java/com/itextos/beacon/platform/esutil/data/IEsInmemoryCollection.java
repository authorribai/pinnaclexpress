package com.itextos.beacon.platform.esutil.data;

import java.util.List;

import com.itextos.beacon.commonlib.message.IMessage;

public interface IEsInmemoryCollection
{

    boolean add(
            IMessage aMessage);

    boolean add(
            List<IMessage> aMessageList);

    void processRemainingData();

}