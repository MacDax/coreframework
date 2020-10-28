package com.springb.framework.common.core;

public class ResponseContextTO {

	private String host;
	private String messageName;
	private String middleWareServiceName;
	private String requestId;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getMiddleWareServiceName() {
		return middleWareServiceName;
	}

	public void setMiddleWareServiceName(String middleWareServiceName) {
		this.middleWareServiceName = middleWareServiceName;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
