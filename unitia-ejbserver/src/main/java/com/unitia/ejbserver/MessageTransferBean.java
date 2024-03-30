package com.unitia.ejbserver;

import javax.ejb.Stateless;

@Stateless
public class MessageTransferBean implements MessageTransfer {


	@Override
	public String send(TransferBean bean) {

		System.out.println(bean.getMessageGson());
		
		return"ok";
	}
}