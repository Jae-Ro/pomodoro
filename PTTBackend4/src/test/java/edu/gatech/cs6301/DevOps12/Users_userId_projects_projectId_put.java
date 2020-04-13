package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId_put extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test updating a project for an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyUserId = "";
			String projectId = "11";
			String projectName = "Test Project";

			// Update a project with emptyUserId
			CloseableHttpResponse response = TestUtils.updateProjectForUserId(httpclient, emptyUserId, projectId,
					projectName);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();

		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a project for a non-existent user
	@Test
	public void pttTest2() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			String nonExistentUserId = id + "123";
			String projectId = "11";
			String projectName = "Test Project";

			// Update project with nonExistentId
			CloseableHttpResponse response = TestUtils.updateProjectForUserId(httpclient, nonExistentUserId, projectId,
					projectName);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a project for an invalid projectId
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String emptyProjectId = "";
			String projectName = "Test Project 0 " + userId;

			// Get a project with emptyProjectId
			CloseableHttpResponse response = TestUtils.updateProjectForUserId(httpclient, userId, emptyProjectId,
					projectName);
			Assert.assertEquals(405, response.getStatusLine().getStatusCode());

			response.close();

		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a project for a non-existent projectId
	@Test
	public void pttTest4() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			// Create a project for the user created
			CloseableHttpResponse createResponseProject = TestUtils.createProjectForUserId(httpclient, userId,
					"testProject " + userId);
			String projectId = TestUtils.getIdFromResponse(createResponseProject);

			String invalidProjectId = projectId + "11";
			String projectName = "Test Project 0 " + userId;

			// Get a project with non-existent ProjectId
			CloseableHttpResponse response = TestUtils.updateProjectForUserId(httpclient, userId, invalidProjectId,
					projectName);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();

		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test invalid user mapping for existing userId and existing
	// projectId
	@Test
	public void pttTest5() throws Exception {
		try {
			// Create user 1
			String userOneId;
			CloseableHttpResponse createResponseOne = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userOneId = TestUtils.getIdFromResponse(createResponseOne);

			// Create user 2
			String userTwoId;
			CloseableHttpResponse createResponseTwo = TestUtils.createUser(httpclient, "A", "B",
					TestUtils.generateEmailId("a@b.org"));
			userTwoId = TestUtils.getIdFromResponse(createResponseTwo);

			// Create a project1 for the user 1
			CloseableHttpResponse createResponseProjectOne = TestUtils.createProjectForUserId(httpclient, userOneId,
					"testProject 1 " + userOneId);
			String projectIdOne = TestUtils.getIdFromResponse(createResponseProjectOne);
			String projectName = "Test Project 0 " + userTwoId;

			// Update project1 from user2
			CloseableHttpResponse response = TestUtils.updateProjectForUserId(httpclient, userTwoId, projectIdOne,
					projectName);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();

		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a project with a malformed project object
	@Test
	public void pttTest6() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			// Create a project for the user created
			CloseableHttpResponse createResponseProject = TestUtils.createProjectForUserId(httpclient, userId,
					"testProject " + userId);
			String projectId = TestUtils.getIdFromResponse(createResponseProject);

			// Update the original project with a malformed project object
			HttpPut httpRequest = new HttpPut(TestUtils.getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId)
					+ String.format(Constants.PROJECTS_ID_PATH, projectId)));
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

	// Purpose: Test updating a project with a duplicated project name for the same
	// user
	@Test
	public void pttTest7() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createUserResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createUserResponse);

			// Create project for the user
			CloseableHttpResponse createProjectResponse1, createProjectResponse2;
			createProjectResponse1 = TestUtils.createProjectForUserId(httpclient, userId, "Test Project " + userId);
			Assert.assertEquals(201, createProjectResponse1.getStatusLine().getStatusCode());

			createProjectResponse2 = TestUtils.createProjectForUserId(httpclient, userId, "Test Project 2 " + userId);
			Assert.assertEquals(201, createProjectResponse2.getStatusLine().getStatusCode());

			String projectId = TestUtils.getIdFromResponse(createProjectResponse1);

			// Update the project with the same name
			createProjectResponse1 = TestUtils.updateProjectForUserId(httpclient, userId, projectId,
					"Test Project 2 " + userId);
			Assert.assertEquals(409, createProjectResponse1.getStatusLine().getStatusCode());

			createUserResponse.close();
			createProjectResponse1.close();
			createProjectResponse2.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test updating a project successfully
	@Test
	public void pttTest8() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createUserResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			Assert.assertEquals(201, createUserResponse.getStatusLine().getStatusCode());
			userId = TestUtils.getIdFromResponse(createUserResponse);

			// Create a project for the user created
			CloseableHttpResponse createProjectResponse;
			createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId, "Test Project " + userId);
			Assert.assertEquals(201, createProjectResponse.getStatusLine().getStatusCode());
			String projectId = TestUtils.getIdFromResponse(createProjectResponse);

			String updatedProjectName = "Update Project Name " + userId;

			// Update the project
			createProjectResponse = TestUtils.updateProjectForUserId(httpclient, userId, projectId, updatedProjectName);
			Assert.assertEquals(200, createProjectResponse.getStatusLine().getStatusCode());

			// Get a project from a user
			CloseableHttpResponse response = TestUtils.getProjectByProjectId(httpclient, userId, projectId);
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			// Verify if the right project is returned
			HttpEntity entity = response.getEntity();
			String expectedJson = TestUtils.getProjectObjectAsJsonString(projectId, updatedProjectName);
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			EntityUtils.consume(response.getEntity());
			response.close();
		} finally {
			httpclient.close();
		}
	}

}
