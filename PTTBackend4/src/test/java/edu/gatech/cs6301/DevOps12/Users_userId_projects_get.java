package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_get extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test getting all projects for an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyUserId = "";

			// Get the projects with emptyUserId
			CloseableHttpResponse response = TestUtils.getProjectsForUserId(httpclient, emptyUserId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting projects for a non-existent user
	@Test
	public void pttTest2() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			String nonExistentId = id + "123";

			// Get projects with nonExistentId
			CloseableHttpResponse response = TestUtils.getProjectsForUserId(httpclient, nonExistentId);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting all projects for a user successfully
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createUserResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createUserResponse);
			Assert.assertEquals(201, createUserResponse.getStatusLine().getStatusCode());

			// Create two projects for the user
			String projectId1, projectId2;
			CloseableHttpResponse createProjectResponse;

			String projectName1 = "Test Project 1 " + id; // since the service seems to look for unique project names
			createProjectResponse = TestUtils.createProjectForUserId(httpclient, id, projectName1);
			Assert.assertEquals(201, createProjectResponse.getStatusLine().getStatusCode());
			projectId1 = TestUtils.getIdFromResponse(createProjectResponse);

			String projectName2 = "Test Project 2 " + id; // since the service seems to look for unique project names
			createProjectResponse = TestUtils.createProjectForUserId(httpclient, id, projectName2);
			Assert.assertEquals(201, createProjectResponse.getStatusLine().getStatusCode());
			projectId2 = TestUtils.getIdFromResponse(createProjectResponse);

			// Get all projects by userId
			CloseableHttpResponse response = TestUtils.getProjectsForUserId(httpclient, id);
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			// Verify if both the projects are returned
			HttpEntity entity = response.getEntity();
			String expectedJson = "[" + TestUtils.getProjectObjectAsJsonString(projectId1, projectName1) + ","
					+ TestUtils.getProjectObjectAsJsonString(projectId2, projectName2) + "]";
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			EntityUtils.consume(response.getEntity());
			response.close();
		} finally {
			httpclient.close();
		}
	}
}
