package com.springb.framework.common.core;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class RESTHttpProtocolHandler implements ProtocolHandler {
	
	private final HttpClient client;
	private String baseEndPointUrl;
	private HttpParams httpParams;
	
	public RESTHttpProtocolHandler(String baseEndPointUrl, HttpClient httpClient) {
		this.client = httpClient;
		this.baseEndPointUrl = baseEndPointUrl;
	}

	public RESTHttpProtocolHandler(String baseEndPointUrl, HttpClient httpClient, HttpParams httpParams) {
		this.client = httpClient;
		this.baseEndPointUrl = baseEndPointUrl;
		this.httpParams = httpParams;
	}
	public String getHandledProtocall() {
		
		return "REST/JSON";
	}

	public Map sendAndReceive(Map requestMap, MessageStatus responseStatus) throws Exception {
		HashMap payloadMap = null;
		String requestUrl = this.baseEndPointUrl + (String)requestMap.get(REST_PROTOCOL_DATA_ELEMENTS.URI);
		StringBuilder requestParams = (new StringBuilder("Received request with URI :")).append(this.baseEndPointUrl)
				.append(", request params : ").append(requestMap.toString());
		Object payloadObj = requestMap.get(PROTOCOL_DATA_ELEMENTS.PAYLOAD);
		HTTP_ENTITY_TYPE contentType = null == requestMap.get(REST_PROTOCOL_DATA_ELEMENTS.CONTENT_BODY) ?
				(HTTP_ENTITY_TYPE.CONTENT_BODY) : HTTP_ENTITY_TYPE.URL_FORM_HTTP_ENTITY;
		Object connectionMethod;
		String strGetResponseBody;
		if(requestMap.get(PROTOCOL_DATA_ELEMENTS.HEADER) == HTTP_METHOD_TYPE.GET) {
			if(payloadObj instanceof Map) {
				payloadMap = (HashMap)payloadObj;
			}
			if(payloadMap != null && !payloadMap.isEmpty()) {
				strGetResponseBody = this.buildQueryString(payloadMap);
				if(strGetResponseBody != null) {
					requestUrl = requestUrl + "?" + strGetResponseBody;
				}
			}
			responseStatus.setRequest(requestParams.toString());
			connectionMethod = new HttpGet(requestUrl);
		}else {
			if(requestMap.get(PROTOCOL_DATA_ELEMENTS.HEADER) != HTTP_METHOD_TYPE.POST) {
				strGetResponseBody = "RESTProtocolHandlerV2: Received request of type " + requestMap.get(PROTOCOL_DATA_ELEMENTS.HEADER) +
						" which is not yet supported.";
			}
			responseStatus.setRequest(requestParams.toString());
			HttpPost arg30 = new HttpPost(requestUrl);
			if(null != contentType) {
				switch(contentType.ordinal()+1) {
				case 1:
					if(payloadObj instanceof Map) {
						payloadMap = (HashMap)payloadObj;
					}
					this.buildPOSTRequestBodyForFormEncodedContent(arg30, payloadMap);
					break;
				case 2:
					Object contentHeaderType = requestMap.get(REST_PROTOCOL_DATA_ELEMENTS.CONTENT_HEADER_TYPE);
					this.buildPOSTRequestBodyForJsonContent(arg30, payloadObj.toString(), (HTTP_HEADER_CONTENT_TYPE)requestMap.get(REST_PROTOCOL_DATA_ELEMENTS.CONTENT_HEADER_TYPE));
					break;
				case 3:
					if(payloadObj instanceof Map) {
						payloadMap = (HashMap)payloadObj;
					}
					//this.buildPOSTRequestForMultipartContent(arg30, payloadMap);
				}
			}
			connectionMethod = arg30;
		}
		this.buildHeaders((HttpRequestBase)connectionMethod, requestMap);
		strGetResponseBody = null;
		HashMap responseMap = new HashMap();
		try {
			String errorMessage;
			try{
				HttpResponse arg31 = this.client.execute((HttpUriRequest)connectionMethod);
				HttpEntity arg33 = arg31.getEntity();
				ContentType responseContentType = ContentType.get(arg33);
				byte[] responseContent = EntityUtils.toByteArray(arg33);
				if(responseContentType != null) {
					System.out.println("Received response ");
				}
				strGetResponseBody = new String(responseContent);
				responseStatus.setResponse(strGetResponseBody);
				HashMap responseHeader = new HashMap();
				Header[] arg19 = arg31.getAllHeaders();
				int arg20 = arg19.length;
				int arg21 = 0;
				while(true) {
					if(arg21 >= arg20) {
						responseMap.put(PROTOCOL_DATA_ELEMENTS.RESPONSE, strGetResponseBody);
						responseMap.put(PROTOCOL_DATA_ELEMENTS.HEADER, responseHeader);
						this.setDelegateCallStatusCodeAndDescription(responseStatus, arg31);
						break;
					}
					Header header = arg19[arg21];
					responseHeader.put(header.getName(), header.getValue());
					++arg21;
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				throw ex;
			}
		}finally {
			if(connectionMethod != null) {
				((HttpRequestBase)connectionMethod).releaseConnection();
			}
		}
		return responseMap;
	}

	
	private void setDelegateCallStatusCodeAndDescription(MessageStatus messageStatus, HttpResponse response) {
		Integer statusCode = null;
		String statusDescription = null;
		StatusLine statusLine = response.getStatusLine();
		if(statusLine != null) {
			statusCode = Integer.valueOf(statusLine.getStatusCode());
			statusDescription = statusLine.getReasonPhrase();
		}
		System.out.println("delegate response status code :: " + statusCode);
		messageStatus.setStatusCode(statusCode);
		messageStatus.setStatusDescription(statusDescription);
	}

	private void buildHeaders(HttpRequestBase req, Map requestMap) {
		if(requestMap != null && !requestMap.isEmpty()
				&& requestMap.get(REST_PROTOCOL_DATA_ELEMENTS.HEADER) != null) {
			Map tempheaderMap = (Map) requestMap.get(REST_PROTOCOL_DATA_ELEMENTS.HEADER);
			if(tempheaderMap != null && !tempheaderMap.isEmpty()) {
				Iterator arg3 = tempheaderMap.entrySet().iterator();
				while(arg3.hasNext()) {
					Entry entry = (Entry)arg3.next();
					req.addHeader((String)entry.getKey(), (String)entry.getValue());
				}
			}
			Map tempAdditionalHeadersMap = new HashMap<String, String>();
			HttpParams httpParams = this.httpParams;
			if(null != httpParams) {
				Object bearerToken = httpParams.getParameter("authorization.bearer.token");
				System.out.println("beareertoken : " + bearerToken);
				//Object user = httpParams.getParameter("user");
				//req.addHeader("user", user.toString());
				//Object secret = httpParams.getParameter("client-secret");
				//req.addHeader("client-secret", secret.toString());
				req.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken.toString());
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private void buildPOSTRequestBodyForFormEncodedContent(HttpPost postMethod, Map payloadMap) {
		if(null != payloadMap) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			payloadMap.forEach((key, value) -> {
				if(value instanceof byte[]) {
					builder.addBinaryBody((String)key, (byte[])((byte[])value));
				}else{
					builder.addTextBody((String)key, (String)value);
				}
			});
			HttpEntity multipart = builder.build();
			postMethod.setEntity(multipart);
		}
	}
	
	private void buildPOSTRequestBodyForJsonContent(HttpPost postMethod, Object payloadMapObj, HTTP_HEADER_CONTENT_TYPE contentType) {
		if(null != payloadMapObj && payloadMapObj.getClass().equals(String.class)) {
			try{
				System.out.println("Payload obj req ::" + new StringEntity((String)payloadMapObj));
			}catch(UnsupportedEncodingException e) {
				System.out.println("Payload obj req UnsupportedEncodingException");
				e.printStackTrace();
			}
			if(null == contentType) {
				postMethod.setEntity(new StringEntity((String)payloadMapObj, ContentType.APPLICATION_JSON));
			}else {
				postMethod.setEntity(new StringEntity((String) payloadMapObj, contentType.getContentType()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String buildQueryString(Map requestProperties) {
		String EQUALS = "=";
		String QUERYSTRING_DELIMITER = "&";
		StringBuilder queryString = new StringBuilder();
		if(requestProperties != null && !requestProperties.isEmpty()) {
			requestProperties.forEach((key, value) -> {
				queryString.append((String)key).append("=").append((String)value).append("&");
			});
			return queryString.substring(0, queryString.length()-1);
		}else {
		return null;
		}
	}


	public static enum REST_PROTOCOL_DATA_ELEMENTS {
		HTTP_ENTITY_TYPE, CONTENT_BODY, HEADER, CONTENT_HEADER_TYPE, URI;
		private REST_PROTOCOL_DATA_ELEMENTS() {};		
	}
	
	public static enum HTTP_ENTITY_TYPE {
		CONTENT_BODY, URL_FORM_HTTP_ENTITY, STRING_HTTP_ENTITY;
		private HTTP_ENTITY_TYPE() {};
	}
	
	public static enum HTTP_METHOD_TYPE {
		GET, POST, PUT, DELETE;
		private HTTP_METHOD_TYPE() {};
	}
	
	public static enum HTTP_HEADER_CONTENT_TYPE {
		APPLICATION_JSON;
		private HTTP_HEADER_CONTENT_TYPE() {}
		public ContentType getContentType() {
			return ContentType.APPLICATION_JSON;
		}
	}
}
