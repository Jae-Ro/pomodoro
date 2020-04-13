package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId_sessions_post extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***\

	// Purpose: Test adding a session for a project for an invalid userId
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyUserId = "";

			String projectId = "11";
			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Create project for a user with emptyId
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, emptyUserId, projectId,
					startTime, endTime, counter);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project for a non-existent user
	@Test
	public void pttTest2() throws Exception {
		try {
			// Create a user
			String id;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			id = TestUtils.getIdFromResponse(createResponse);

			String nonExistentId = id + "123";

			String projectId = "11";
			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Create project for a user with nonExistentId
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, nonExistentId, projectId,
					startTime, endTime, counter);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project with a invalid project id.
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String invalidProjectId = "";

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Create project for a user with nonExistentId
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, invalidProjectId,
					startTime, endTime, counter);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project with a non-existent project id
	@Test
	public void pttTest4() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String id;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			id = TestUtils.getIdFromResponse(createProjectResponse);

			String nonExistentProjectId = id + "123";

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Create project for a user with nonExistentId
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, nonExistentProjectId,
					startTime, endTime, counter);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for invalid user mapping for existent userId
	// and existent projectId
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
					"Test Project " + userOneId);
			String projectIdOne = TestUtils.getIdFromResponse(createResponseProjectOne);

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Get project1 from user2
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userTwoId, projectIdOne,
					startTime, endTime, counter);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session with a malformed session object
	@Test
	public void pttTest6() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			// Create a project
			String projectId;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			projectId = TestUtils.getIdFromResponse(createProjectResponse);

			HttpPost httpRequest = new HttpPost(TestUtils.getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId)
					+ String.format(Constants.PROJECTS_ID_PATH, projectId) + Constants.SESSIONS_PATH));
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

	// Purpose: Test adding a session for a project with an invalid start time.
	@Test
	public void pttTest7() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String projectId;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			projectId = TestUtils.getIdFromResponse(createProjectResponse);

			String invalidStartTime = "";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Create project for a user with invalid start time.
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, projectId,
					invalidStartTime, endTime, counter);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project with an invalid end time.
	@Test
	public void pttTest8() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String projectId;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			projectId = TestUtils.getIdFromResponse(createProjectResponse);

			String startTime = "2019-02-18T20:00Z";
			String invalidEndTime = "";
			String counter = "2";

			// Create project for a user with invalid start time.
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, projectId, startTime,
					invalidEndTime, counter);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project with an endTime that is less
	// than the start time.
	@Test
	public void pttTest9() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String projectId;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			projectId = TestUtils.getIdFromResponse(createProjectResponse);

			String startTime = "2019-02-18T20:00Z";
			String smallerEndTime = "2019-02-18T18:00Z";
			String counter = "2";

			// Create project for a user with invalid start time.
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, projectId, startTime,
					smallerEndTime, counter);

			Assert.assertEquals(400, response.getStatusLine().getStatusCode()); // TODO: This should ideally throw a 400

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project with an invalid counter.
	@Test
	public void pttTest10() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String projectId;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			projectId = TestUtils.getIdFromResponse(createProjectResponse);

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String invalidCounter = "";

			// Create project for a user with invalid start time.
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, projectId, startTime,
					endTime, invalidCounter);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode()); // TODO: This should ideally throw a 400

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test adding a session for a project successfully.
	@Test
	public void pttTest11() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String projectId;
			CloseableHttpResponse createProjectResponse = TestUtils.createProjectForUserId(httpclient, userId,
					"Test Project " + userId);
			projectId = TestUtils.getIdFromResponse(createProjectResponse);

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";
			String counter = "2";

			// Create project for a user with invalid start time.
			CloseableHttpResponse response = TestUtils.createSessionForProject(httpclient, userId, projectId, startTime,
					endTime, counter);
			Assert.assertEquals(201, response.getStatusLine().getStatusCode());

			// Verify if the session created is returned correctly.
			HttpEntity entity = response.getEntity();
			String strResponse = EntityUtils.toString(entity);
			String sessionId = TestUtils.getIdFromStringResponse(strResponse);

			String expectedJson = TestUtils.getSessionObjectAsJsonString(sessionId, startTime, endTime, counter);
			JSONAssert.assertEquals(expectedJson, strResponse, false);

			response.close();
		} finally {
			httpclient.close();
		}
	}
}
