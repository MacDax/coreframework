package com.springb.framework.common.core;

import java.util.Map;

public abstract interface ProtocolHandler {
	public abstract String getHandledProtocall();
	public abstract Map sendAndReceive(Map paramMap, MessageStatus paramMessageStatus) throws Exception;
	public static enum PROTOCOL_DATA_ELEMENTS {
		HEADER, PAYLOAD, RESPONSE, ATTACHMENTS;
		private PROTOCOL_DATA_ELEMENTS() {}
	}
}
