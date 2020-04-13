package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_post extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test adding a project for an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyUserId = "";

			// Create project for a user with emptyId
			CloseableHttpResponse response = TestUtils.createProjectForUserId(httpclient, emptyUserId, "Test Project");
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a project for a non-existent user
	@Test
	public void pttTest2() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			String nonExistentId = id + "123";

			// Create project for a user with nonExistentId
			CloseableHttpResponse response = TestUtils.createProjectForUserId(httpclient, nonExistentId,
					"Test Project " + id);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a project with a malformed project object
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			HttpPost httpRequest = new HttpPost(
					TestUtils.getUrlFromPath(String.format(Constants.USERS_ID_PATH, id) + Constants.PROJECTS_PATH));
			httpRequest.addHeader("accept", "application/json");

			StringEntity input = new StringEntity("{ \"MALFORMED_OBJECT_PROP\": \"MALFORMED_VALUE\"");
			input.setContentType("application/json");
			httpRequest.setEntity(input);

			System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
			CloseableHttpResponse response = httpclient.execute(httpRequest);
			System.out.println("*** Raw response " + response + "***");

			Assert.assertEquals(400, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a project with a duplicated project name for the same
	// user
	@Test
	public void pttTest4() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createUserResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createUserResponse);

			// Create project for the user
			CloseableHttpResponse createProjectResponse;
			createProjectResponse = TestUtils.createProjectForUserId(httpclient, id, "Test Project " + id);
			Assert.assertEquals(201, createProjectResponse.getStatusLine().getStatusCode());

			// Create another project with the same name
			createProjectResponse = TestUtils.createProjectForUserId(httpclient, id, "Test Project " + id);
			Assert.assertEquals(409, createProjectResponse.getStatusLine().getStatusCode());

			createUserResponse.close();
			createProjectResponse.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a project for a user successfully
	@Test
	public void pttTest5() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			// Create project for the user
			CloseableHttpResponse createProjectResponse;
			createProjectResponse = TestUtils.createProjectForUserId(httpclient, id, "Test Project " + id);
			Assert.assertEquals(201, createProjectResponse.getStatusLine().getStatusCode());

			// Verify if created project is returned
			HttpEntity entity = createProjectResponse.getEntity();
			String entityStr = EntityUtils.toString(entity);
			String projectId = TestUtils.getIdFromStringResponse(entityStr);
			String expectedJson = TestUtils.getProjectObjectAsJsonString(projectId, "Test Project " + id);
			JSONAssert.assertEquals(expectedJson, entityStr, false);

			EntityUtils.consume(createProjectResponse.getEntity());
			createProjectResponse.close();
			createResponse.close();
		} finally {
			httpclient.close();
		}
	}
}
