package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_post extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test adding a user with malformed user object
	@Test
	public void pttTest1() throws Exception {
		try {
			HttpPost httpRequest = new HttpPost(baseUrl + Constants.BASE_PATH + Constants.USERS_PATH);
			httpRequest.addHeader("accept", "application/json");

			StringEntity input = new StringEntity("{ \"MALFORMED_OBJECT_PROP\": \"MALFORMED_VALUE\"");
			input.setContentType("application/json");
			httpRequest.setEntity(input);

			System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
			CloseableHttpResponse response = httpclient.execute(httpRequest);
			System.out.println("*** Raw response " + response + "***");

			int status = response.getStatusLine().getStatusCode();
			Assert.assertEquals(400, status);

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a user with invalid and empty email
	@Test
	public void pttTest2() throws Exception {
		try {
			String emptyEmail = "";
			CloseableHttpResponse response = TestUtils.createUser(httpclient, "John", "Doe", emptyEmail);

			int status = response.getStatusLine().getStatusCode();
			Assert.assertEquals(status, 400);
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a user with existing email
	@Test
	public void pttTest3() throws Exception {
		try {
			String sameEmail = TestUtils.generateEmailId("john@doe.org");

			// Create a user
			CloseableHttpResponse response = TestUtils.createUser(httpclient, "John", "Doe", sameEmail);

			int status = response.getStatusLine().getStatusCode();
			Assert.assertEquals(201, status);
			response.close();

			// Create another user with the same email
			response = TestUtils.createUser(httpclient, "John", "Doe", sameEmail);

			status = response.getStatusLine().getStatusCode();
			Assert.assertEquals(409, status);
			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a user with invalid firstname
	@Test
	public void pttTest4() throws Exception {
		try {
			String invalidFirstname = "";
			CloseableHttpResponse response = TestUtils.createUser(httpclient, invalidFirstname, "Doe", "john@doe.org");

			int status = response.getStatusLine().getStatusCode();
			Assert.assertEquals(status, 400);

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a user with invalid lastname
	@Test
	public void pttTest5() throws Exception {
		try {
			String invalidLastname = "";
			CloseableHttpResponse response = TestUtils.createUser(httpclient, "John", invalidLastname, "john@doe.org");

			int status = response.getStatusLine().getStatusCode();
			Assert.assertEquals(status, 400);

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a user successfully
	@Test
	public void pttTest6() throws Exception {
		try {
			String email = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse response = TestUtils.createUser(httpclient, "John", "Doe", email);

			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity;
			if (status == 201) {
				entity = response.getEntity();
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
			String strResponse = EntityUtils.toString(entity);

			System.out.println(
					"*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

			String id = TestUtils.getIdFromStringResponse(strResponse);
			String expectedJson = TestUtils.getUserObjectAsJsonString(id, "John", "Doe", email);

			JSONAssert.assertEquals(expectedJson, strResponse, false);

			EntityUtils.consume(response.getEntity());
			response.close();
		} finally {
			httpclient.close();
		}
	}
}
