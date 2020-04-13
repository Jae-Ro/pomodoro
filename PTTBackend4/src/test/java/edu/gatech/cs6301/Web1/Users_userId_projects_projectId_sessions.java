package edu.gatech.cs6301.Web1;

import java.io.IOException;
import java.util.Iterator;

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

public class Users_userId_projects_projectId_sessions extends PTTBackendTests {

    public Users_userId_projects_projectId_sessions() {
        super();
        runBefore();
    }

    private Users usersConstruct = new Users();
    private Users_userId_projects projectsConstruct = new Users_userId_projects();

    @Test
    public void pttTest1() throws Exception {
    // Purpose: Test a GET request with an invalid userId and valid projectId
    // Expected: 404

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("adrian", "monk", "mrmonk@gatech.edu");
    String userName = user2.getString("id");

    //Create the invalid user
    int nonexistingId = usersConstruct.getBadUserId();

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "perfect100");

    //Generate start and end times


    //Get Session with non-existing user, valid project, valid start, valid end
    CloseableHttpResponse res = getSessionHTTPResponse(String.valueOf(nonexistingId), proId.getString("id"));
    int statusCode = res.getStatusLine().getStatusCode();

    // should be not found
    Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest2() throws Exception {
    // Purpose: Test a GET request with a userId of maxint
    // Expected: 404

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("trudy", "monk", "trudyellison@gatech.edu");
    String userName = user2.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "numberOneMom");

    //Get empty session
    CloseableHttpResponse res = getSessionHTTPResponse(String.valueOf(Integer.MAX_VALUE), proId.getString("id"));
    int statusCode = res.getStatusLine().getStatusCode();

    // should be not found
    Assert.assertEquals(404, statusCode);
    }


    @Test
    public void pttTest3() throws Exception {
    // Purpose: Test a GET request with a valid userId and invalid projectId
    // Expected: 404

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user3 = usersConstruct.createUser("sharona", "fleming", "firstnurse@gatech.edu");
    String userName = user3.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "myboyBenji");

    //Create the invalid project
    Users_userId_projects_projectId project = new Users_userId_projects_projectId();
    int nonexistingId = project.getBadProjectId(userName);

    //Generate start and end times


    //Get Report with non-existing user, valid project, valid start, valid end
    CloseableHttpResponse res = getSessionHTTPResponse(userName, String.valueOf(nonexistingId));
    int statusCode = res.getStatusLine().getStatusCode();

    // should be not found
    Assert.assertEquals(404, statusCode);
    }


    @Test
    public void pttTest4() throws Exception {
    // Purpose: Test a GET request with a userId of maxint
    // Expected: 404

    //Start fresh
        usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("natalie", "teeger", "equalpartner@gatech.edu");
    String userName = user2.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "killerIntro");

    //Get empty session
    CloseableHttpResponse res = getSessionHTTPResponse(userName, String.valueOf(Integer.MAX_VALUE));
    int statusCode = res.getStatusLine().getStatusCode();

    // should be not found
    Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest5() throws Exception {
    // Purpose: post project when the startTime length is 0
    // Expected: 400

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("leland", "stottlemeyer", "sfpdcaptain@gatech.edu");
    String userName = user2.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "notBuffaloBill");

    //Generate start and end times


    //Get empty session
    CloseableHttpResponse res = createSessionHTTPResponse(userName, proId.getString("id"), "", "2019-02-18");
    int statusCode = res.getStatusLine().getStatusCode();

    // should be a bad request
    Assert.assertEquals(400, statusCode);
    }

    @Test
    public void pttTest6() throws Exception {
    // Purpose: post project when the endTime length is 0
    // Expected: 400

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("randy", "disher", "dish@gatech.edu");
    String userName = user2.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "randyDisherProject");

    //Generate start and end times


    //Get empty session
    CloseableHttpResponse res = createSessionHTTPResponse(userName, proId.getString("id"), "2019-02-18", "");
    int statusCode = res.getStatusLine().getStatusCode();

    // should be a bad request
    Assert.assertEquals(400, statusCode);
    }

    @Test
    public void pttTest7() throws Exception {
    // Purpose: Successfully get a list of sessions
    // Expected: 200

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("charles", "kroger", "besttherapist@gatech.edu");
    String userId = user2.getString("id");

    //Create a projectId element
    JSONObject project = projectsConstruct.createProject(userId, "inOurHearts");
    String projId = project.getString("id");

    //Generate start and end times
//
//        CloseableHttpResponse res = createSessionHTTPResponse(userId, projId, "2019-02-18", "2019-02-18");
//        int statusCode = res.getStatusLine().getStatusCode();

    //Get empty session
    JSONObject createdSession = createSession(userId, projId, "2019-02-18 12:12", "2019-02-18 12:13");
    CloseableHttpResponse res2 = getSessionHTTPResponse(userId, projId);
    JSONObject test = getSession(userId, projId);
//        System.out.println(test);
    int statusCode = res2.getStatusLine().getStatusCode();

    //System.out.print("Compare " + test.get("id") + " to " + createdSession.get("id"));

    // should be able to successfully retrieve a list of sessions
    Assert.assertEquals(200, statusCode);
    }

    @Test
    public void pttTest8() throws Exception {
    // Purpose: post with empty session
    // Expected 400

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("neven", "bell", "stillgood@gatech.edu");
    String userName = user2.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "sayItAintSoJoe");

    // create an empty session
    String jsonString = "{" + "}";

    //Get empty session
    CloseableHttpResponse res = createSessionHTTPResponseWithJsonString(userName, proId.getString("id"), jsonString);
    int statusCode = res.getStatusLine().getStatusCode();

    // should be a bad request
    Assert.assertEquals(400, statusCode);
    }

    @Test
    public void pttTest9() throws Exception {
    // Purpose: Test a successful POST request with
    // an valid session start and end time
    // Expected: 201

    //Start fresh
    usersConstruct.deleteAllUsers();

    //Create a valid user (to have a valid projectId)
    JSONObject user2 = usersConstruct.createUser("julie", "teeger", "oneTimeModel@gatech.edu");
    String userName = user2.getString("id");

    //Create a projectId element
    JSONObject proId = projectsConstruct.createProject(userName, "myDadMitch");

    //Generate start and end times


    //Get empty session
    CloseableHttpResponse res = createSessionHTTPResponse(userName, proId.getString("id"), "2019-02-18", "2019-02-18");
    int statusCode = res.getStatusLine().getStatusCode();

    // should be successfully created
    Assert.assertEquals(400, statusCode);
    }



    public CloseableHttpResponse getSessionHTTPResponse (String userId, String projectId) throws IOException, JSONException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        return response;
    }

    public JSONObject getSession(String userId, String projectId) throws IOException, JSONException {
        CloseableHttpResponse response = getSessionHTTPResponse(userId, projectId);
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        JSONObject test;
        try{
            test = new JSONObject(strResponse);
        }
        catch(JSONException e){
            JSONArray arr = new JSONArray(strResponse);
            test = arr.getJSONObject(0);
        }
        return test;

    }


    public CloseableHttpResponse createSessionHTTPResponse(String userId, String projectId, String startTime, String endTime) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{" +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\","
                + "\"counter\":\"" + "1" +
                 "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    public CloseableHttpResponse createSessionHTTPResponseWithJsonString(String userId, String projectId, String jsonString) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    public JSONObject createSession(String userId, String projectId, String startTime, String endTime) throws Exception {
        CloseableHttpResponse response = createSessionHTTPResponse(userId, projectId, startTime, endTime);

        int statusCode = response.getStatusLine().getStatusCode();
        

        if (statusCode != 201) {
            System.out.println("*** ERROR response " + statusCode + "***");
            throw new Exception("Error creating project. See log");
        }
        System.out.println("*** Raw response\t\t\t\n\n " + response + statusCode +"***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

}

