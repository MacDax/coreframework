package com.springb.framework.core.connector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCoreConnectionFactory {

	private int connectionTimeout;
	private int socketTimeout;
	private String proxyHost;
	private int proxyPort;
	private SchemeRegistry schReg = null;
	private PoolingClientConnectionManager poolingClientConnectionManager = null;
	private boolean isConnectionStateEnabled = false;
	private static final UserTokenHandler NOOP_USER_HANDLER = new NoopUserTokenHdr();
	private final Logger logger = LoggerFactory.getLogger(HttpCoreConnectionFactory.class);
	private String userName;
	private String password;
	
	public HttpCoreConnectionFactory(int connectionTimeout, int socketTimeout, int maxTotalConnections, 
			String userName, String password) {
		this.connectionTimeout = connectionTimeout;
		this.socketTimeout = socketTimeout;
		Scheme plainScheme = new Scheme("http", 80, new PlainSocketFactory());
		if(this.schReg == null) {
			this.schReg = new SchemeRegistry();
		}
		if(this.schReg.get("http") == null) {
			//Scheme plainScheme = new Scheme("http", 80, new PlainSocketFactory());
			/*if(this.schReg == null) {
				this.schReg = new SchemeRegistry();
			}*/
			this.schReg.register(plainScheme);
		}else {
		    this.schReg.register(plainScheme);
		}
		//if(maxTotalConnections > 1 ) {
		
		this.poolingClientConnectionManager = new PoolingClientConnectionManager(this.schReg);
		this.poolingClientConnectionManager.setMaxTotal(maxTotalConnections);
		this.poolingClientConnectionManager.setDefaultMaxPerRoute(maxTotalConnections);
	  //}
	}
	
	public HttpClient getHttpCoreClient() {
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter("http.socket.timeout", Integer.valueOf(this.socketTimeout));
		httpParams.setParameter("http.connection.timeout", Integer.valueOf(this.connectionTimeout));
		if(this.proxyHost != null) {
			HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort);
			httpParams.setParameter("http.route.default-proxy", proxy);
		}
		DefaultHttpClient httpClient;
		if(this.poolingClientConnectionManager != null) {
			httpClient = new DefaultHttpClient(this.poolingClientConnectionManager, httpParams);
		}else {
			ClientConnectionManager ccm = new SingleClientConnManager(this.schReg);
			httpClient = new DefaultHttpClient(ccm, httpParams);
		}
		configureConnectionState(httpClient);
		return httpClient;
	}

	private void configureConnectionState(DefaultHttpClient httpClient) {
		if((!this.isConnectionStateEnabled) && (httpClient != null)) {
			disableConnectionState(httpClient);
		}
		
	}
	
	private void disableConnectionState(DefaultHttpClient httpClient) {
		httpClient.setUserTokenHandler(NOOP_USER_HANDLER);
	}
	
   
	private JSONObject getOAuthToken(HttpClient httpClient) throws IOException {
		String oAuthTokenEndPoint = "http://localhost:8070/oauth/token";
		HttpPost httpPost = new HttpPost(oAuthTokenEndPoint);
		String base64Credentials = Base64.getEncoder().encodeToString(("clientuser" + ":"  
		+ "clientsecret").getBytes());
		httpPost.addHeader("Authorization", "Basic " + base64Credentials);
		String grant_type = "client_credentials";
		String scope = "resource-server-read resource-server-write";
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		StringEntity input = null;
		try{
			input = new StringEntity("grant_type=" + grant_type + "&scope=" + scope);
			httpPost.setEntity(input);
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		}
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		}catch(IOException e) {
			e.printStackTrace();
			throw e;
		}
		String result = null;
		try {
			result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		}catch(IOException e) {
			e.printStackTrace();
		}
		JSONObject oauthToken = new JSONObject(result);
		return oauthToken;
	}
	
	public HttpClient getCustomHttpCoreClient() {
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter("http.socket.timeout", Integer.valueOf(this.socketTimeout));
		httpParams.setParameter("http.connection.timeout", Integer.valueOf(this.connectionTimeout));
		JRDefaultHttpClient httpClient = null;
		
		if(this.poolingClientConnectionManager != null) {
			httpClient = new JRDefaultHttpClient(this.poolingClientConnectionManager, httpParams);
		}else {
			ClientConnectionManager ccm = new SingleClientConnManager(this.schReg);
			httpClient = new JRDefaultHttpClient(ccm, httpParams);
		}
		JSONObject oauthToken;
		try {
			oauthToken = getOAuthToken(httpClient);
			httpParams.setParameter("authorization.bearer.token", oauthToken.get("access_token"));
			httpParams.setParameter("user", "clientuser");
			httpParams.setParameter("client_secret", "clientsecret");
		}catch(IOException e) {
			e.printStackTrace();
			logger.info("OAUth server threw an exception : " + e.getMessage());
		}
		configureConnectionState(httpClient);
		return httpClient;
	}
	
	private static class NoopUserTokenHdr implements UserTokenHandler {

		@Override
		public Object getUserToken(HttpContext arg0) {
			return null;
		}
		
	}
}
