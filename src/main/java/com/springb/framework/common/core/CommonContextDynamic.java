package com.springb.framework.common.core;

import java.util.GregorianCalendar;

public class CommonContextDynamic implements RequestTO.ApplicationHostContext {

	private String sessionId;
	private Integer activitySequenceNbr;
	private String requestId;
	private String requestSequenceId;
	private GregorianCalendar creationTimeStamp;
	private String invokerId;
	private String hostName;
	private String messageId;
	private String initiatoIdType;
	private String originatorId;
	private String originatorIdType;
	private String sessionSequanceNumber;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getActivitySequenceNbr() {
		return activitySequenceNbr;
	}

	public void setActivitySequenceNbr(Integer activitySequenceNbr) {
		this.activitySequenceNbr = activitySequenceNbr;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestSequenceId() {
		return requestSequenceId;
	}

	public void setRequestSequenceId(String requestSequenceId) {
		this.requestSequenceId = requestSequenceId;
	}

	public GregorianCalendar getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public void setCreationTimeStamp(GregorianCalendar creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}

	public String getInvokerId() {
		return invokerId;
	}

	public void setInvokerId(String invokerId) {
		this.invokerId = invokerId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getInitiatoIdType() {
		return initiatoIdType;
	}

	public void setInitiatoIdType(String initiatoIdType) {
		this.initiatoIdType = initiatoIdType;
	}

	public String getOriginatorId() {
		return originatorId;
	}

	public void setOriginatorId(String originatorId) {
		this.originatorId = originatorId;
	}

	public String getOriginatorIdType() {
		return originatorIdType;
	}

	public void setOriginatorIdType(String originatorIdType) {
		this.originatorIdType = originatorIdType;
	}

	public String getSessionSequanceNumber() {
		return sessionSequanceNumber;
	}

	public void setSessionSequanceNumber(String sessionSequanceNumber) {
		this.sessionSequanceNumber = sessionSequanceNumber;
	}

}
