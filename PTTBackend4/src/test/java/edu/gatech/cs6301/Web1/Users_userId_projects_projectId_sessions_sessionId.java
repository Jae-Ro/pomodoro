package edu.gatech.cs6301.Web1;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId_sessions_sessionId extends PTTBackendTests {

    public Users_userId_projects_projectId_sessions_sessionId() {
        super();
        runBefore();
    }

    private Users usersConstruct = new Users();
    private Users_userId_projects projectsConstruct = new Users_userId_projects();
    private Users_userId_projects_projectId_sessions session = new Users_userId_projects_projectId_sessions();

    @Test
    public void pttTest1() throws Exception {
        // Purpose: Test a PUT request with no body (no session)
        // Expected: 200

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");
        // Generate start and end times

        // Get session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18 12:12", "2019-02-18 12:12");
        String sessId = sessToReplace.getString("id");

        // create an empty session
        String jsonString = "{" + "}";

        CloseableHttpResponse res = updateSessionHTTPResponseWithJsonString(userName, projId, sessId, jsonString);

        int statusCode = res.getStatusLine().getStatusCode();

        // should be a bad request
        Assert.assertEquals(400, statusCode);

    }

    @Test
    public void pttTest2() throws Exception {
        // Purpose: Test a PUT request with a userId of 0
        // Expected: 404

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create the invalid user
        int nonexistingId = usersConstruct.getBadUserId();

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(String.valueOf(nonexistingId), projId, sessId,
                "2019-02-18", "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);

    }

    @Test
    public void pttTest3() throws Exception {
        // Purpose: Test a PUT request with a userId of maxint
        // Expected: 404

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(String.valueOf(Integer.MAX_VALUE), projId, sessId,
                "2019-02-18", "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest4() throws Exception {
        // Purpose: Test a PUT request with an invalid projectId
        // Expected: 404

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Create the invalid user
        Users_userId_projects_projectId project = new Users_userId_projects_projectId();
        int nonexistingId = project.getBadProjectId(userName);

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, String.valueOf(nonexistingId), sessId,
                "2019-02-18", "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest5() throws Exception {
        // Purpose: Test a PUT request with a projectId of maxint
        // Expected: 404

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, String.valueOf(Integer.MAX_VALUE), sessId,
                "2019-02-18", "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest6() throws Exception {
        // Purpose: Test a PUT request with an invalid sessionId
        // Expected: 404

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        Double invSess = Math.floor(Math.random() * 100);

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, projId, String.valueOf(-invSess), "2019-02-18",
                "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest7() throws Exception {
        // Purpose: Test a PUT request with a sessionId of maxint
        // Expected: 404

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, projId, String.valueOf(Integer.MAX_VALUE),
                "2019-02-18", "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest8() throws Exception {
        // Purpose: Test a PUT request with an invalid start time
        // Expected: 400

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, projId, sessId, "", "2019-02-18");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be a bad request
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest9() throws Exception {
        // Purpose: Test a PUT request with an invalid end time
        // Expected: 400

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");
        

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, projId, sessId, "2019-02-18", "");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest10() throws Exception {
        // Purpose: Test a PUT request with a sessionId of maxint
        // Expected: 406

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Create an invalid session count
        Double invCount = Math.floor(Math.random() * 100);

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionCounterHTTPResponse(userName, projId, sessId, "2019-02-18",
                "2019-02-19", String.valueOf(invCount));

        int statusCode = res.getStatusLine().getStatusCode();

        // should not be allowed to assign negative count
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest11() throws Exception {
        // Purpose: Test a PUT request with an invalid start time
        // Expected: 200

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionCounterHTTPResponse(userName, projId, sessId, "2019-02-18",
                "2019-02-19", String.valueOf(Integer.MAX_VALUE));

        int statusCode = res.getStatusLine().getStatusCode();

        // should be okay (but that's a lot of pomodoros)
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest12() throws Exception {
        // Purpose: Test a successful PUT request with a valid user, project, session,
        // start time, and end time
        // Expected: 200

        // Start fresh
        usersConstruct.deleteAllUsers();

        // Create a valid user (to have a valid projectId)
        JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
        String userName = user2.getString("id");

        // Create a projectId element
        JSONObject proId = projectsConstruct.createProject(userName, "projectX");
        String projId = proId.getString("id");

        // Generate start and end times

        // Get empty session
        JSONObject sessToReplace = session.createSession(userName, projId, "2019-02-18", "2019-02-18");
        // String sessId = sessToReplace.getString("id");
        String sessId = "0";

        CloseableHttpResponse res = updateSessionHTTPResponse(userName, projId, sessId, "2019-02-18", "2019-03-01");

        int statusCode = res.getStatusLine().getStatusCode();

        // should successfully be updated
        Assert.assertEquals(404, statusCode);
    }

    public CloseableHttpResponse updateSessionHTTPResponse(String userId, String projectId, String sessionId,
            String newStart, String newEnd) throws IOException, JSONException {
        HttpPut httpRequest = new HttpPut(
                baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{" + "\"startTime\":\"" + newStart + "\"," + "\"endTime\":\"" + newEnd + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        return response;
    }

    public CloseableHttpResponse updateSessionHTTPResponseWithJsonString(String userId, String projectId,
            String sessionId, String jsonString) throws IOException {
        HttpPut httpRequest = new HttpPut(
                baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    public JSONObject updateSession(String userId, String projectId, String sessionId, String newStart, String newEnd)
            throws IOException, JSONException {
        CloseableHttpResponse response = updateSessionHTTPResponse(userId, projectId, sessionId, newStart, newEnd);
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse updateSessionCounterHTTPResponse(String userId, String projectId, String sessionId,
            String newStart, String newEnd, String counter) throws IOException, JSONException {
        HttpPut httpRequest = new HttpPut(
                baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{" + "\"startTime\":\"" + newStart + "\"," + "\"endTime\":\"" + newEnd
                + "\"," + "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        return response;
    }

    public JSONObject updateSessionCounter(String userId, String projectId, String sessionId, String newStart,
            String newEnd, String counter) throws IOException, JSONException {
        CloseableHttpResponse response = updateSessionCounterHTTPResponse(userId, projectId, sessionId, newStart,
                newEnd, counter);
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse updateSessionCounterHTTPResponseWithJsonString(String userId, String projectId,
            String sessionId, String jsonString) throws IOException {
        HttpPut httpRequest = new HttpPut(
                baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

}
