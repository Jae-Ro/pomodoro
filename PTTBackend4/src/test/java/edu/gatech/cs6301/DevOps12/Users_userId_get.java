package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_get extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test getting an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyId = "/";

			// Get the user with emptyId
			CloseableHttpResponse response = TestUtils.getUserById(httpclient, emptyId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a non-existent user
	@Test
	public void pttTest2() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			String nonExistentId = id + "123";

			// Get user with nonExistentId
			CloseableHttpResponse response = TestUtils.getUserById(httpclient, nonExistentId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a user successfully
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			// Get all users
			CloseableHttpResponse response = TestUtils.getUserById(httpclient, id);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			// Verify if both the users are returned
			HttpEntity entity = response.getEntity();
			String expectedJson = TestUtils.getUserObjectAsJsonString(id, "John", "Doe", emailId);
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			EntityUtils.consume(response.getEntity());
			response.close();
		} finally {
			httpclient.close();
		}
	}
}
