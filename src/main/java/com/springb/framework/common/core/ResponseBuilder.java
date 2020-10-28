package com.springb.framework.common.core;

public abstract class ResponseBuilder<S extends ResponseTO> {

	public abstract S processResponse(Object paramObject) throws Exception;
	public void processResponseHeaders(Object rawResponseHeader, S responseTo) throws Exception {};
	protected S getResponseTO() {
		return null;
	}
	public void processAttachements(Object attachement, S responseTo) throws Exception {};
}
