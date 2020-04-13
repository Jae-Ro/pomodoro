package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId_report_get extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test getting a report for an Invalid User Id.
	@Test
	public void pttTest1() throws Exception {
		try {
			String emptyUserId = "";
			String projectId = "11";
			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			// Get report with emptyUserId
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, emptyUserId, projectId, startTime,
					endTime);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report for a project for a non-existent user
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
			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			// Get projects with nonExistentId
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, nonExistentUserId, projectId,
					startTime, endTime);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report for an invalid projectId
	@Test
	public void pttTest3() throws Exception {
		try {
			// Create a user
			String userId;
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John", "Doe",
					TestUtils.generateEmailId("john@doe.org"));
			userId = TestUtils.getIdFromResponse(createResponse);

			String emptyProjectId = "";
			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			// Get a project with emptyProjectId
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, userId, emptyProjectId, startTime,
					endTime);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report for a non-existent projectId
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

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			// Get a project with non-existent ProjectId
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, userId, invalidProjectId, startTime,
					endTime);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report for invalid user mapping for existent userId
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
					"testProject 1 " + userOneId);
			String projectIdOne = TestUtils.getIdFromResponse(createResponseProjectOne);

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			// Get project1 from user2
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, userTwoId, projectIdOne, startTime,
					endTime);
			Assert.assertEquals(404, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report with a invalid start time
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

			String invalidStartTime = "01234";
			String endTime = "2019-02-18T21:00Z";

			// Get a project with invalid start time.
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, userId, projectId, invalidStartTime,
					endTime);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report with a invalid end time.
	@Test
	public void pttTest7() throws Exception {
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

			String startTime = "2019-02-18T20:00Z";
			String invalidEndTime = "01234";

			// Get a project with a invalid end time.
			CloseableHttpResponse response = TestUtils.getProjectReport(httpclient, userId, projectId, startTime,
					invalidEndTime);
			Assert.assertEquals(400, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report with all valid parameters and true values for
	// the optional boolean parameters,
	// includeCompletedPomodoros, and includeTotalHoursWorkedOnProject.
	@Test
	public void pttTest8() throws Exception {
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

			String sessionStartTime = "2019-02-18T20:00Z";
			String sessionEndtime = "2019-02-18T20:30Z";
			String counter = "1";
			String hoursWorked = "0.5";

			// Create a session for the project
			CloseableHttpResponse createSessionResponse = TestUtils.createSessionForProject(httpclient, userId,
					projectId, sessionStartTime, sessionEndtime, counter);

			Assert.assertEquals(201, createSessionResponse.getStatusLine().getStatusCode());

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			boolean includeCompltedPomodoros = true;
			boolean includeTotalHoursWorkedOnProject = true;

			// Get a report for project with optional parameters set to true.
			CloseableHttpResponse response = TestUtils.getProjectReportWithOptionalParameters(httpclient, userId,
					projectId, startTime, endTime, includeCompltedPomodoros, includeTotalHoursWorkedOnProject);

			// Verify if the correct report was returned.
			HttpEntity entity = response.getEntity();
			String expectedSessionJson = TestUtils.getSessionObjectForReportAsJsonString(sessionStartTime,
					sessionEndtime, hoursWorked);
			String expectedJson = Constants.REPORT_START_JSON + expectedSessionJson
					+ String.format(Constants.REPORT_END_WITH_OPTIONALS_JSON, '1', hoursWorked);
			String strResponse = EntityUtils.toString(entity);

			JSONAssert.assertEquals(expectedJson, strResponse, false);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report with all valid parameters optional parameter
	// values as follows.
	// completed pomodoros = true
	// total hours = false
	@Test
	public void pttTest9() throws Exception {
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

			String sessionStartTime = "2019-02-18T20:00Z";
			String sessionEndtime = "2019-02-18T20:30Z";
			String counter = "1";
			String hoursWorked = "0.5";

			// Create a session for the project
			CloseableHttpResponse createSessionResponse = TestUtils.createSessionForProject(httpclient, userId,
					projectId, sessionStartTime, sessionEndtime, counter);
			Assert.assertEquals(201, createSessionResponse.getStatusLine().getStatusCode());

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			boolean includeCompltedPomodoros = true;
			boolean includeTotalHoursWorkedOnProject = false;

			// Get a report for the project.
			CloseableHttpResponse response = TestUtils.getProjectReportWithOptionalParameters(httpclient, userId,
					projectId, startTime, endTime, includeCompltedPomodoros, includeTotalHoursWorkedOnProject);

			// Verify if the correct report was returned.
			HttpEntity entity = response.getEntity();
			String expectedSessionJson = TestUtils.getSessionObjectForReportAsJsonString(sessionStartTime,
					sessionEndtime, hoursWorked);
			String expectedJson = Constants.REPORT_START_JSON + expectedSessionJson
					+ String.format(Constants.REPORT_END_WITH_POMODOROS_JSON, '1');
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report with all valid parameters optional parameter
	// values as follows.
	// completed pomodoros = false
	// total hours = true
	@Test
	public void pttTest10() throws Exception {
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

			String sessionStartTime = "2019-02-18T20:00Z";
			String sessionEndtime = "2019-02-18T20:30Z";
			String counter = "1";
			String hoursWorked = "0.5";

			// Create a session for the project
			CloseableHttpResponse createSessionResponse = TestUtils.createSessionForProject(httpclient, userId,
					projectId, sessionStartTime, sessionEndtime, counter);
			Assert.assertEquals(201, createSessionResponse.getStatusLine().getStatusCode());

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			boolean includeCompltedPomodoros = false;
			boolean includeTotalHoursWorkedOnProject = true;

			// Get a report for project.
			CloseableHttpResponse response = TestUtils.getProjectReportWithOptionalParameters(httpclient, userId,
					projectId, startTime, endTime, includeCompltedPomodoros, includeTotalHoursWorkedOnProject);

			// Verify if the correct report was returned.
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			HttpEntity entity = response.getEntity();
			String expectedSessionJson = TestUtils.getSessionObjectForReportAsJsonString(sessionStartTime,
					sessionEndtime, hoursWorked);
			String expectedJson = Constants.REPORT_START_JSON + expectedSessionJson
					+ String.format(Constants.REPORT_END_WITH_HOURS_JSON, hoursWorked);
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			response.close();
		} finally {
			httpclient.close();
		}
	}

	// Purpose: Test getting a report with all valid parameters optional parameter
	// values as follows.
	// completed pomodoros = false
	// total hours = false
	@Test
	public void pttTest11() throws Exception {
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

			String sessionStartTime = "2019-02-18T20:00Z";
			String sessionEndtime = "2019-02-18T20:30Z";
			String counter = "1";
			String hoursWorked = "0.5";

			// Create a session for the project
			CloseableHttpResponse createSessionResponse = TestUtils.createSessionForProject(httpclient, userId,
					projectId, sessionStartTime, sessionEndtime, counter);
			Assert.assertEquals(201, createSessionResponse.getStatusLine().getStatusCode());

			String startTime = "2019-02-18T20:00Z";
			String endTime = "2019-02-18T21:00Z";

			boolean includeCompltedPomodoros = false;
			boolean includeTotalHoursWorkedOnProject = false;

			// Get a report for project.
			CloseableHttpResponse response = TestUtils.getProjectReportWithOptionalParameters(httpclient, userId,
					projectId, startTime, endTime, includeCompltedPomodoros, includeTotalHoursWorkedOnProject);

			// Verify if the correct report was returned.
			HttpEntity entity = response.getEntity();
			String expectedSessionJson = TestUtils.getSessionObjectForReportAsJsonString(sessionStartTime,
					sessionEndtime, hoursWorked);
			String expectedJson = Constants.REPORT_START_JSON + expectedSessionJson + Constants.REPORT_END_JSON;
			JSONAssert.assertEquals(expectedJson, EntityUtils.toString(entity), false);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			response.close();
		} finally {
			httpclient.close();
		}
	}
}
