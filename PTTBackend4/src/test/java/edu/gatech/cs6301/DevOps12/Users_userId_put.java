package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_put extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test updating an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyId = "/";

			// Update the user with emptyId
			CloseableHttpResponse response = TestUtils.updateUser(httpclient, emptyId, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a non-existent user
	@Test
	public void pttTest2() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			String nonExistentId = id + "123";

			// Update user with nonExistentId
			CloseableHttpResponse response = TestUtils.updateUser(httpclient, nonExistentId, "John", "Doe", emailId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a user with malformed user object
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			HttpPut httpRequest = new HttpPut(TestUtils.getUrlFromPath(String.format(Constants.USERS_ID_PATH, id)));
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

	// Purpose: Update email to an existing user's email
	@Test
	public void pttTest4() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			// Create another user
			String anotherEmailId = TestUtils.generateEmailId("john-2@doe.org");
			CloseableHttpResponse createAnotherUserResponse = TestUtils.createUser(httpclient, "John", "Doe",
					anotherEmailId);
			Assert.assertEquals(201, createAnotherUserResponse.getStatusLine().getStatusCode());

			// Update user1's email ID to user2's email ID
			CloseableHttpResponse response = TestUtils.updateUser(httpclient, id, "John", "Doe", anotherEmailId);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode()); // TODO: This should ideally throw a 400

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Update user's email to an invalid email
	@Test
	public void pttTest5() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			Assert.assertEquals(201, createResponse.getStatusLine().getStatusCode());

			// Update user1's email ID to an invalid email ID
			String invalidEmailId = "";
			CloseableHttpResponse response = TestUtils.updateUser(httpclient, id, "John", "Doe", invalidEmailId);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode()); // TODO: This should ideally throw a 400

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Update user with missing firstname
	@Test
	public void pttTest6() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			Assert.assertEquals(201, createResponse.getStatusLine().getStatusCode());

			// Update user1 to have an empty firstname
			CloseableHttpResponse response = TestUtils.updateUser(httpclient, id, "", "Doe", emailId);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode()); // TODO: This should ideally throw a 400

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Update user with missing lastname
	@Test
	public void pttTest7() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);

			Assert.assertEquals(201, createResponse.getStatusLine().getStatusCode());

			// Update user1 to have an empty lastname
			CloseableHttpResponse response = TestUtils.updateUser(httpclient, id, "John", "", emailId);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode()); // TODO: This should ideally throw a 400

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a user successfully
	@Test
	public void pttTest8() throws Exception {
		try {
			// Create a user
			String id, emailId = TestUtils.generateEmailId("john@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe", emailId);
			id = TestUtils.getIdFromResponse(createResponse);
			Assert.assertEquals(201, createResponse.getStatusLine().getStatusCode());

			CloseableHttpResponse updateResponse = TestUtils.updateUser(httpclient, id, "John1", "Doe", emailId);
			id = TestUtils.getIdFromResponse(updateResponse);
			Assert.assertEquals(200, updateResponse.getStatusLine().getStatusCode());

			// Get user by id
			CloseableHttpResponse response = TestUtils.getUserById(httpclient, id);
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			// Verify if the updated user is returned
			HttpEntity entity = response.getEntity();
			String expectedJson = TestUtils.getUserObjectAsJsonString(id, "John1", "Doe", emailId);
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			EntityUtils.consume(entity);
			response.close();
		} finally {
			httpclient.close();
		}
	}
}
