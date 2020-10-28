package com.springb.framework.common.core;

public class RequestTO {
	private boolean synchronousMode = true;
	private String sessionId;
	private String cacheKey;
	private String cacheF5Cookie;
	private boolean bypassCache;
	private String encryptionKey;
	private String referenceId;
	private String asynchronousThreadPoolName;
	private String middlewareName;
	private String middlerwareMessageName;
	private ApplicationHostContext applicationHostContext;

	public ApplicationHostContext getApplicationHostContext() {
		return applicationHostContext;
	}

	public void setApplicationHostContext(ApplicationHostContext applicationHostContext) {
		this.applicationHostContext = applicationHostContext;
	}

	public boolean isSynchronousMode() {
		return synchronousMode;
	}

	public void setSynchronousMode(boolean synchronousMode) {
		this.synchronousMode = synchronousMode;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getCacheF5Cookie() {
		return cacheF5Cookie;
	}

	public void setCacheF5Cookie(String cacheF5Cookie) {
		this.cacheF5Cookie = cacheF5Cookie;
	}

	public boolean isBypassCache() {
		return bypassCache;
	}

	public void setBypassCache(boolean bypassCache) {
		this.bypassCache = bypassCache;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getAsynchronousThreadPoolName() {
		return asynchronousThreadPoolName;
	}

	public void setAsynchronousThreadPoolName(String asynchronousThreadPoolName) {
		this.asynchronousThreadPoolName = asynchronousThreadPoolName;
	}

	public String getMiddlewareName() {
		return middlewareName;
	}

	public void setMiddlewareName(String middlewareName) {
		this.middlewareName = middlewareName;
	}

	public String getMiddlerwareMessageName() {
		return middlerwareMessageName;
	}

	public void setMiddlerwareMessageName(String middlerwareMessageName) {
		this.middlerwareMessageName = middlerwareMessageName;
	}

	public static abstract interface ApplicationHostContext {
	}
}
