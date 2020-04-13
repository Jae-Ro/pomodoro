package edu.gatech.cs6301.DevOps12;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_delete extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test deleting an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyId = "/";

			// Get the user with emptyId
			CloseableHttpResponse response = TestUtils.deleteUserById(httpclient, emptyId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test deleting a non-existent user
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
			CloseableHttpResponse response = TestUtils.deleteUserById(httpclient, nonExistentId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test deleting a user successfully
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			// Get all users
			CloseableHttpResponse response = TestUtils.deleteUserById(httpclient, id);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			// Verify if both the users are returned
			HttpEntity entity = response.getEntity();
			String expectedJson = TestUtils.getUserObjectAsJsonString(id, "John", "Doe", emailId);
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			CloseableHttpResponse getResponse = TestUtils.getUserById(httpclient, id);
			Assert.assertEquals(404, getResponse.getStatusLine().getStatusCode());

			EntityUtils.consume(response.getEntity());
			response.close();
		} finally {
			httpclient.close();
		}
	}
}
