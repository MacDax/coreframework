package com.springb.framework.common.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.springb.framework.common.core.ProtocolHandler.PROTOCOL_DATA_ELEMENTS;

public class Delegate<R extends RequestTO, S extends ResponseTO> {
	private final ProtocolHandler protocolHandler;
	private final RequestBuilder requestBuilder;
	private final ResponseBuilder<S> responseBuilder;
	private static ExecutorService globalExecutor;
	private Map<String, ExecutorService> localExecutors;
	//private CacheHandler cacheHandler;
	private static final Logger log = LoggerFactory.getLogger(Delegate.class);
	static {
		int delegateAsynchronousThreadCount = 2;
		globalExecutor = Executors.newFixedThreadPool(delegateAsynchronousThreadCount);
	}
	
	public Delegate(RequestBuilder requestBuilder, ResponseBuilder responseBuilder, ProtocolHandler protocolHandler) {
		this.requestBuilder = requestBuilder;
		this.responseBuilder = responseBuilder;
		this.protocolHandler = protocolHandler;
	}
	
	/*private class SynchrnousCall implements Callable<S> {
		private Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		
	}*/
	
	public S execute(R request) throws Exception {
		if(request.isSynchronousMode()) {
			log.info("Delegate Received a SYNCHRONOUS request");
			return executeDelegateCall(request);
		}
		log.info("Delegate: Recieved ASYNCHRONOUS request");
		S response = this.responseBuilder.getResponseTO();
		//FutureTask<S> future = new FutureTask(new AynchrnousCall(request,this));
		return response;
	}

	private S executeDelegateCall(R request) throws Exception {
		log.info("Request in executeDelegatePostCall :: " + request);
		if(request.getMiddlewareName() != null) {
			MDC.put("middlewareName", request.getMiddlewareName());
		}
		if(request.getMiddlerwareMessageName() != null) {
			MDC.put("middlewareMessageName", request.getMiddlerwareMessageName());
		}else{
			MDC.put("middlewareMessageName", request.getClass().getName());
		}
		MessageStatus messageStatus = new MessageStatus();
		HashMap requestMap = new HashMap(2);
		Map rawResponseMap = null;
		long delegateStartTime = 0L; long delegateEndTime = 0L; long requestBuilderStartTime =0L;
		long requestBuilderEndTime = 0L;
		boolean isRetrievedFromCache = false;
		//boolean isCacheEnabled = isCacheEnabled(request);
		delegateStartTime = System.currentTimeMillis();
		Object requestObj;
		try {
			requestBuilderStartTime = System.currentTimeMillis();
			requestObj = this.requestBuilder.buildRequest(request, this.protocolHandler.getHandledProtocall());
			requestMap.put(PROTOCOL_DATA_ELEMENTS.PAYLOAD, requestObj);
			Object requestorContextObj = this.requestBuilder.buildRequestorContext(request.getApplicationHostContext(), this.protocolHandler.getHandledProtocall());
			if(requestorContextObj != null) {
				requestMap.put(PROTOCOL_DATA_ELEMENTS.HEADER, requestorContextObj);
			}
			Map f5CoockieValue1 = this.requestBuilder.getAdditionalRequestDetails(request, this.protocolHandler.getHandledProtocall());
			if(f5CoockieValue1 != null) {
				requestMap.putAll(f5CoockieValue1);
			}
			requestBuilderEndTime = System.currentTimeMillis();
			log.info("Request build time : " + (requestBuilderEndTime - requestBuilderStartTime));
		}catch(Exception e1) {
			String errorMessage = "Delegate: Received Builder Excption from " + this.requestBuilder.getClass().getSimpleName() + e1.getMessage();
			log.info(errorMessage);
			MDC.remove("middlewareName");
			MDC.remove("middlewareMessageName");
			throw new Exception(errorMessage);
		}
		
		try {
			long protocolHandlerStarttime = System.currentTimeMillis();
			//messageStatus.setCacheEnableMessage(isCacheEnabled);
			rawResponseMap = this.protocolHandler.sendAndReceive(requestMap, messageStatus);
			long protocolHandlerEndtime = System.currentTimeMillis();
			log.info("Delegate: Response received from " + this.protocolHandler.getClass().getSimpleName());
		}catch(Exception ex) {
			log.info("Delegate received ex " + ex.getMessage());
			MDC.remove("middlewareName");
			MDC.remove("middlewareMessageName");
			throw ex;
		}
		
		S response = null;
		long responseBuilderStartTime = System.currentTimeMillis();
		if(rawResponseMap != null) {
			if(rawResponseMap.get(PROTOCOL_DATA_ELEMENTS.HEADER) != null) {
				this.responseBuilder.processResponseHeaders(rawResponseMap.get(PROTOCOL_DATA_ELEMENTS.HEADER), response);
			}
			if(rawResponseMap.get(PROTOCOL_DATA_ELEMENTS.RESPONSE) != null) {
				response = this.responseBuilder.processResponse(rawResponseMap.get(PROTOCOL_DATA_ELEMENTS.RESPONSE));
			}
		}else{
			response = this.responseBuilder.processResponse(messageStatus.getStatusDescription());
		}
		long responseBuilderEndTime = System.currentTimeMillis();
		return response;
	}
	
	
}
