package edu.gatech.cs6301.DevOps12;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

class TestUtils {
	private TestUtils() {
	}

	private static String sBaseUrl = null;
	private static Random random = new Random();

	public static void setBaseUrl(String baseUrl) {
		sBaseUrl = baseUrl;
	}

	public static CloseableHttpResponse createUser(CloseableHttpClient httpclient, String firstName, String lastName,
			String email) throws IOException {
		HttpPost httpRequest = new HttpPost(getUrlFromPath(Constants.USERS_PATH));
		httpRequest.addHeader("accept", "application/json");

		StringEntity input = new StringEntity(getUserObjectAsJsonString(firstName, lastName, email));
		input.setContentType("application/json");
		httpRequest.setEntity(input);

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpclient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse updateUser(CloseableHttpClient httpclient, String userId, String firstName,
			String lastName, String email) throws IOException {
		HttpPut httpRequest = new HttpPut(getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId)));
		httpRequest.addHeader("accept", "application/json");

		StringEntity input = new StringEntity(getUserObjectAsJsonString(userId, firstName, lastName, email));
		input.setContentType("application/json");
		httpRequest.setEntity(input);

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpclient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getAllUsers(CloseableHttpClient httpClient) throws IOException {
		HttpGet httpRequest = new HttpGet(getUrlFromPath(Constants.USERS_PATH));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getUserById(CloseableHttpClient httpClient, String id) throws IOException {
		HttpGet httpRequest = new HttpGet(getUrlFromPath(String.format(Constants.USERS_ID_PATH, id)));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse deleteUserById(CloseableHttpClient httpClient, String id) throws IOException {
		HttpDelete httpRequest = new HttpDelete(getUrlFromPath(String.format(Constants.USERS_ID_PATH, id)));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getProjectsForUserId(CloseableHttpClient httpClient, String userId)
			throws IOException {
		HttpGet httpRequest = new HttpGet(
				getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId) + Constants.PROJECTS_PATH));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse createProjectForUserId(CloseableHttpClient httpClient, String id,
			String projectName) throws IOException {
		HttpPost httpRequest = new HttpPost(
				getUrlFromPath(String.format(Constants.USERS_ID_PATH, id) + Constants.PROJECTS_PATH));
		httpRequest.addHeader("accept", "application/json");

		StringEntity input = new StringEntity(getProjectObjectAsJsonString(projectName));
		input.setContentType("application/json");
		httpRequest.setEntity(input);

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse updateProjectForUserId(CloseableHttpClient httpClient, String userId,
			String projectId, String projectName) throws IOException {
		HttpPut httpRequest = new HttpPut(getUrlFromPath(
				String.format(Constants.USERS_ID_PATH, userId) + String.format(Constants.PROJECTS_ID_PATH, projectId)));
		httpRequest.addHeader("accept", "application/json");

		StringEntity input = new StringEntity(getProjectObjectAsJsonString(projectId, projectName));
		input.setContentType("application/json");
		httpRequest.setEntity(input);

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse updateSessionForProjectId(CloseableHttpClient httpClient, String userId,
			String projectId, String sessionId, String startTime, String endTime, String counter) throws IOException {
		HttpPut httpRequest = new HttpPut(getUrlFromPath(
				String.format(Constants.USERS_ID_PATH, userId) + String.format(Constants.PROJECTS_ID_PATH, projectId)
						+ String.format(Constants.SESSIONS_ID_PATH, sessionId)));
		httpRequest.addHeader("accept", "application/json");

		StringEntity input = new StringEntity(getSessionObjectAsJsonString(sessionId, startTime, endTime, counter));
		input.setContentType("application/json");
		httpRequest.setEntity(input);

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getProjectByProjectId(CloseableHttpClient httpClient, String userId,
			String projectId) throws IOException {
		HttpGet httpRequest = new HttpGet(getUrlFromPath(
				String.format(Constants.USERS_ID_PATH, userId) + String.format(Constants.PROJECTS_ID_PATH, projectId)));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse deleteProjectByProjectId(CloseableHttpClient httpClient, String userId,
			String projectId) throws IOException {
		HttpDelete httpRequest = new HttpDelete(getUrlFromPath(
				String.format(Constants.USERS_ID_PATH, userId) + String.format(Constants.PROJECTS_ID_PATH, projectId)));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getProjectReport(CloseableHttpClient httpClient, String userId,
			String projectId, String startTime, String endTime) throws IOException {
		HttpGet httpRequest = new HttpGet(getUrlFromPath(
				String.format(Constants.USERS_ID_PATH, userId) + String.format(Constants.PROJECTS_ID_PATH, projectId)
						+ String.format(Constants.REPORT_PATH, startTime, endTime)));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getProjectReportWithOptionalParameters(CloseableHttpClient httpClient,
			String userId, String projectId, String startTime, String endTime, boolean includeCompletedPomodoros,
			boolean includeTotalHoursWorkedOnProject) throws IOException {
		HttpGet httpRequest = new HttpGet(getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId)
				+ String.format(Constants.PROJECTS_ID_PATH, projectId)
				+ String.format(Constants.REPORT_PATH, startTime, endTime)
				+ String.format(Constants.REPORT_PATH_POMODOROS, Boolean.toString(includeCompletedPomodoros))
				+ String.format(Constants.REPORT_PATH_HOURS, Boolean.toString(includeTotalHoursWorkedOnProject))));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse getSessionsForProject(CloseableHttpClient httpClient, String userId,
			String projectId) throws IOException {
		HttpGet httpRequest = new HttpGet(getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId)
				+ String.format(Constants.PROJECTS_ID_PATH, projectId) + Constants.SESSIONS_PATH));
		httpRequest.addHeader("accept", "application/json");

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static CloseableHttpResponse createSessionForProject(CloseableHttpClient httpClient, String userId,
			String projectId, String startTime, String endTime, String counter) throws IOException {
		HttpPost httpRequest = new HttpPost(getUrlFromPath(String.format(Constants.USERS_ID_PATH, userId)
				+ String.format(Constants.PROJECTS_ID_PATH, projectId) + Constants.SESSIONS_PATH));
		httpRequest.addHeader("accept", "application/json");

		StringEntity input = new StringEntity(getSessionObjectAsJsonString(startTime, endTime, counter));
		input.setContentType("application/json");
		httpRequest.setEntity(input);

		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpClient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");

		return response;
	}

	public static String getProjectObjectAsJsonString(String projectName) {
		return String.format(Constants.PROJECT_JSON, projectName);
	}

	public static String getProjectObjectAsJsonString(String id, String projectName) {
		return String.format(Constants.PROJECT_JSON_WITH_ID, id, projectName);
	}

	public static String getUserObjectAsJsonString(String firstName, String lastName, String email) {
		return String.format(Constants.USER_JSON, firstName, lastName, email).replace("\\", "");
	}

	public static String getUserObjectAsJsonString(String id, String firstName, String lastName, String email) {
		return String.format(Constants.USER_JSON_WITH_ID, id, firstName, lastName, email).replace("\\", "");
	}

	public static String getSessionObjectAsJsonString(String startTime, String endTime, String counter) {
		return String.format(Constants.SESSIONS_JSON, startTime, endTime, counter);
	}

	public static String getSessionObjectAsJsonString(String id, String startTime, String endTime, String counter) {
		return String.format(Constants.SESSIONS_JSON_WITH_ID, id, startTime, endTime, counter).replace("\\", "");
	}

	public static String getSessionObjectForReportAsJsonString(String startTime, String endTime, String hoursWorked) {
		return String.format(Constants.SESSIONS_REPORT_JSON, startTime, endTime, hoursWorked);
	}

	public static String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
		HttpEntity entity = response.getEntity();
		String strResponse = EntityUtils.toString(entity);
		String id = getIdFromStringResponse(strResponse);
		return id;
	}

	public static String getIdFromStringResponse(String strResponse) throws JSONException {
		JSONObject object = new JSONObject(strResponse);

		String id = null;
		@SuppressWarnings("unchecked")
		Iterator<String> keyList = object.keys();
		while (keyList.hasNext()) {
			String key = keyList.next();
			if (key.equals("id")) {
				id = object.get(key).toString();
			}
		}
		return id;
	}

	public static String getUrlFromPath(String path) {
		return sBaseUrl + Constants.BASE_PATH + path;
	}

	public static String generateEmailId(String suffix) {
		int randomInt = random.nextInt();
		while (randomInt <= 0) {
			randomInt = random.nextInt();
		}
		return Integer.toString(randomInt).concat(suffix);
	}
}
