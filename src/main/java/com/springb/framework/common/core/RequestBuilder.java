package com.springb.framework.common.core;

import java.util.Map;

public abstract class RequestBuilder<R extends RequestTO,
S extends SystemHostContext, D extends RequestTO.ApplicationHostContext> {

	private final S hostContextStatic;
	
	public RequestBuilder(S hostContextStatic) {
		this.hostContextStatic = hostContextStatic;
	}
	
	public abstract Object buildRequest(R paramR, String paramString) throws Exception;
	public abstract Object buildRequestorContext(D paramD, String paramString) throws Exception;
	public abstract Map getAdditionalRequestDetails(R paramR, String paramString) throws Exception;
	
	protected final S getHostContextStatic() {
		return this.hostContextStatic;
	}
}
