package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;

public class BaseTestCase {
    protected String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
	protected PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	protected CloseableHttpClient httpclient;
	protected boolean setupdone;

	@Before
	public void runBefore() {
		if (!setupdone) {
			System.out.println("*** SETTING UP TESTS ***");
			// Increase max total connection to 100
			cm.setMaxTotal(100);
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(10);
			// Increase max connections for localhost:80 to 50
			HttpHost localhost = new HttpHost("localhost", 8080);
			cm.setMaxPerRoute(new HttpRoute(localhost), 10);
			httpclient = HttpClients.custom().setConnectionManager(cm).build();

			TestUtils.setBaseUrl(baseUrl);
			setupdone = true;
		}
		System.out.println("*** STARTING TEST ***");
	}

	@After
	public void runAfter() {
		System.out.println("*** ENDING TEST ***");
	}
}
