package com.springb.framework.core.connector;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

public class JRDefaultHttpClient extends DefaultHttpClient {

	protected BasicHttpProcessor createHttpProcessor() {
		BasicHttpProcessor processor = super.createHttpProcessor();
		processor.addInterceptor(new RemoveSoapHeadersInterceptor(), 0);
		processor.addInterceptor(new TimeLoggingInterceptor(), 1);
		return processor;
	}
	
	public JRDefaultHttpClient(ClientConnectionManager conman, HttpParams httpParams) {
		super(conman, httpParams);
	}
	
	private static class RemoveSoapHeadersInterceptor implements HttpRequestInterceptor {

		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			if(request instanceof HttpEntityEnclosingRequest) {
				if(request.containsHeader("Transfer-Encoding")) {
					request.removeHeaders("Transfer-Encoding");
				}
				if(request.containsHeader("Content-Length")) {
					request.removeHeaders("Content-Length");
				}
			}
			context.setAttribute("startTime", Long.valueOf(System.currentTimeMillis()));
			
		}
		
	}
	
	private static class TimeLoggingInterceptor implements HttpResponseInterceptor {

		@Override
		public void process(HttpResponse hr, HttpContext hc) throws HttpException, IOException {
			try{
				Long startTime = (Long) hc.getAttribute("startTime");
				System.out.println("Time tacke for backend communictation is : " + ( System.currentTimeMillis() - startTime.longValue()) );
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
}
