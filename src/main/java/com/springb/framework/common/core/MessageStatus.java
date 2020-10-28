package com.springb.framework.common.core;

public class MessageStatus {

	private Integer statusCode;
	private String statusDescription;
	private String request;
	private String response;
	private Long responseTime;
	private boolean CacheEnableMessage;

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Long responseTime) {
		this.responseTime = responseTime;
	}

	public boolean isCacheEnableMessage() {
		return CacheEnableMessage;
	}

	public void setCacheEnableMessage(boolean cacheEnableMessage) {
		CacheEnableMessage = cacheEnableMessage;
	}

}
