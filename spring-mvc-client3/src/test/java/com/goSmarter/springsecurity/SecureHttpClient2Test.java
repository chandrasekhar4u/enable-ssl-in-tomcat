package com.goSmarter.springsecurity;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class SecureHttpClient2Test {

	private static Logger logger = Logger
			.getLogger(SecureHttpClient2Test.class);

	static {
		System.setProperty(
				"javax.net.ssl.trustStore",
				SecureHttpClient1Test.client2Jks);
		System.setProperty("javax.net.ssl.trustStorePassword", "password");
		System.setProperty(
				"javax.net.ssl.keyStore",
				SecureHttpClient1Test.client2Jks);
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
	}

	/**
	 * @param args
	 */
	@Test
	public void testMainPage() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		method.setURI(new URI("https://localhost:8443/spring-mvc-client4/secure/index.jsp", false));
		client.executeMethod(method);

		assertEquals(200, method.getStatusCode());
	}

	@Test
	public void testSecurePage() throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		method.setURI(new URI("https://localhost:8443/spring-mvc-client4/secure1/index.jsp",
				false));
		client.executeMethod(method);

		assertEquals(200, method.getStatusCode());
	}

	@Test
	public void testSecurePageNegativeCase() throws Exception {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream keystoreInput = new FileInputStream(
				SecureHttpClient1Test.client1Jks);
		keystore.load(keystoreInput, "password".toCharArray());
		System.out.println("Keystore has " + keystore.size() + " keys");

		// load the truststore, leave it null to rely on cacerts distributed
		// with the JVM
		KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream truststoreInput = new FileInputStream(
				SecureHttpClient1Test.client1Jks);
		truststore.load(truststoreInput, "password".toCharArray());

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		SSLSocketFactory lSchemeSocketFactory = new SSLSocketFactory(keystore,
				"password", truststore);
		schemeRegistry
				.register(new Scheme("https", 8443, lSchemeSocketFactory));

		final HttpParams httpParams = new BasicHttpParams();
		DefaultHttpClient lHttpClient = new DefaultHttpClient(
				new SingleClientConnManager(schemeRegistry), httpParams);
		HttpGet lMethod = new HttpGet(
				"https://localhost:8443/spring-mvc-client4/secure1/index.jsp");

		HttpResponse lHttpResponse = lHttpClient.execute(lMethod);
		logger.debug(lHttpResponse.getStatusLine().getStatusCode());
		Assert.assertEquals(403, lHttpResponse.getStatusLine().getStatusCode());
	}
}
