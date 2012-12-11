package com.goSmarter.springsecurity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.apache.http.impl.conn.SingleClientConnManager;

public class SecureHttpClient1Test {

	public static final String path = "D:/apache-tomcat-6.0.36/conf/";
	public static final String client1Jks = path + "client1.jks";
	public static final String client2Jks = path + "client2.jks";
	
	private static Logger logger = Logger.getLogger(SecureHttpClient1Test.class);

	private SchemeRegistry getSchemeRegistry(String keystoreString,
			String truststoreString) throws Exception {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

		InputStream keystoreInput = new FileInputStream(keystoreString);
		keystore.load(keystoreInput, "password".toCharArray());
		System.out.println("Keystore has " + keystore.size() + " keys");

		// load the truststore, leave it null to rely on cacerts distributed
		// with the JVM
		KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream truststoreInput = new FileInputStream(truststoreString);
		truststore.load(truststoreInput, "password".toCharArray());

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		SSLSocketFactory lSchemeSocketFactory = new SSLSocketFactory(keystore,
				"password", truststore);
		schemeRegistry
				.register(new Scheme("https", 8443, lSchemeSocketFactory));
		return schemeRegistry;
	}

	@Test
	public void testMainPage() throws Exception {

		SchemeRegistry schemeRegistry = getSchemeRegistry(
				client1Jks,
				client1Jks);

		final HttpParams httpParams = new BasicHttpParams();
		DefaultHttpClient lHttpClient = new DefaultHttpClient(
				new SingleClientConnManager(schemeRegistry), httpParams);
		HttpGet lMethod = new HttpGet("https://localhost:8443/spring-mvc-client3/secure/index.jsp");

		HttpResponse lHttpResponse = lHttpClient.execute(lMethod);
		logger.debug(lHttpResponse.getStatusLine().getStatusCode());
		
				
		Assert.assertEquals(200, lHttpResponse.getStatusLine().getStatusCode());
		
		readData(lHttpResponse);
		
	}

	private void readData(HttpResponse lHttpResponse) throws IOException {
		InputStream is = lHttpResponse.getEntity().getContent();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String str = null;

		while((str = reader.readLine()) != null){
			logger.debug(str);
		}
	}

	@Test
	public void testSecurePage() throws Exception {
		SchemeRegistry schemeRegistry = getSchemeRegistry(
				client1Jks,
				client1Jks);

		final HttpParams httpParams = new BasicHttpParams();
		DefaultHttpClient lHttpClient = new DefaultHttpClient(
				new SingleClientConnManager(schemeRegistry), httpParams);
		HttpGet lMethod = new HttpGet(
				"https://localhost:8443/spring-mvc-client3/secure1/index.jsp");

		HttpResponse lHttpResponse = lHttpClient.execute(lMethod);
		logger.debug(lHttpResponse.getStatusLine().getStatusCode());
		Assert.assertEquals(200, lHttpResponse.getStatusLine().getStatusCode());
		readData(lHttpResponse);
	}

	@Test
	public void testSecurePageNegativeCase() throws Exception {
		SchemeRegistry schemeRegistry = getSchemeRegistry(
				client2Jks,
				client2Jks);

		final HttpParams httpParams = new BasicHttpParams();
		DefaultHttpClient lHttpClient = new DefaultHttpClient(
				new SingleClientConnManager(schemeRegistry), httpParams);
		HttpGet lMethod = new HttpGet(
				"https://localhost:8443/spring-mvc-client3/secure1/index.jsp");

		HttpResponse lHttpResponse = lHttpClient.execute(lMethod);
		logger.debug(lHttpResponse.getStatusLine().getStatusCode());
		Assert.assertEquals(403, lHttpResponse.getStatusLine().getStatusCode());
		readData(lHttpResponse);
	}
}
