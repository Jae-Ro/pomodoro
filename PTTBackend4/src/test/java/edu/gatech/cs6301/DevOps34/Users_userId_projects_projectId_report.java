package edu.gatech.cs6301.DevOps34;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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

public class Users_userId_projects_projectId_report {
	
	/* Adding the Backend testing server for DevOps34 Team */
    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;
    
    @Before
    public void runBefore() {
	if (!setupdone) {
	    System.out.println("*** SETTING UP TESTS ***");
	    // Increase max total connection to 100
	    cm.setMaxTotal(100);
	    // Increase default max connection per route to 20
	    cm.setDefaultMaxPerRoute(10);
	    // Increase max connections for localhost:80 to 50
	    HttpHost localhost = new HttpHost("locahost", 8080);
	    cm.setMaxPerRoute(new HttpRoute(localhost), 10);
	    httpclient = HttpClients.custom().setConnectionManager(cm).build();
	    setupdone = true;
	}
        System.out.println("*** STARTING TEST ***");
    }

    @After
    public void runAfter() {
        System.out.println("*** ENDING TEST ***");
    }

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***


    private CloseableHttpResponse addUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse addProject(String userid, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse addSession(String userid, String projectid, String startTime, String endTime, Integer counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects/" + projectid + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse getReport(String userID, String projectID, String from, String to, boolean includeCompletedPomodoros, boolean includeTotalHoursWorkedOnProject) throws IOException {
        String ifIncludeCompletedPomodoros = null;
        String ifIncludeTotalHoursWorkedOnProject = null;
        if (includeCompletedPomodoros) {
            ifIncludeCompletedPomodoros = "true";
        }
        else {
            ifIncludeCompletedPomodoros = "false";
        }
        if (includeTotalHoursWorkedOnProject) {
            ifIncludeTotalHoursWorkedOnProject = "true";
        } else {
            ifIncludeTotalHoursWorkedOnProject = "false";
        }
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userID + "/projects/" + projectID + "/report?from=" + from + "&to=" + to + "&includeCompletedPomodoros=" + includeCompletedPomodoros + "&includeTotalHoursWorkedOnProject=" + ifIncludeTotalHoursWorkedOnProject);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }
    private CloseableHttpResponse deleteProject(String userid, String projectid) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userid + "/projects/" + projectid);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }
    //Test Case1: test if it runs correctly for the bad request
    @Test
    public void getReportTest1() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        CloseableHttpResponse response;

        response = addUser("test10", "test10", "test10@email.org");
        id = getIdFromResponse(response);
        response.close();

        response =  addProject(id, "testproj10");
        projectid = getIdFromResponse(response);
        response.close();

        response = addSession(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z",1);
        response.close();
        try {
            HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id + "/projects/" + projectid+"/reports/");
            httpDelete.addHeader("accept", "application/json");

            System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
            response = httpclient.execute(httpDelete);
            System.out.println("*** Raw response " + response + "***");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }

    //Test Case2: test if it runs correctly if  User or project not found
    @Test
    public void getReportTest2() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        try {
            CloseableHttpResponse response;


            response = getReport("00002270", "150","2019-02-18T20:00Z","2019-02-18T21:00Z", true, true);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    //Test Case3: test if it runs correctly if  time is invalid
    @Test
    public void getReportTest3() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        CloseableHttpResponse response;

        response = addUser("test8", "test8", "test8@email.org");
	    id = getIdFromResponse(response);
	    response.close();

        response =  addProject(id, "testproj8");
	    projectid = getIdFromResponse(response);
	    response.close();
       
	    response = addSession(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z",1);
	    sessionid = getIdFromResponse(response);
	    response.close();
        try {
			response = getReport(id, projectid ,"2019-13-18T20:00Z","2019-02-18T21:00Z", true, true);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }

    //Test Case4: test if it runs correctly for the correct request and include completedPromodoro and hours worked
    @Test
    public void getReportTest4() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        CloseableHttpResponse response;

        response = addUser("test8", "test8", "test8@email.org");
        id = getIdFromResponse(response);
        response.close();

        response =  addProject(id, "testproj8");
        projectid = getIdFromResponse(response);
        response.close();

        response = addSession(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z",1);
        sessionid = getIdFromResponse(response);
        response.close();

        try {

            expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}], \"completedPomodoros\":1, \"totalHoursWorkedOnProject\":1}\"}";

            //Query
            response = getReport(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z", true, true);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println(expectedJson);
            System.out.println(strResponse);
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }
    //Test Case5: test if it runs correctly for the correct request and include completedPromodoro and do not include hours worked
    @Test
    public void getReportTest5() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        CloseableHttpResponse response;

        response = addUser("test8", "test8", "test8@email.org");
        id = getIdFromResponse(response);
        response.close();

        response =  addProject(id, "testproj8");
        projectid = getIdFromResponse(response);
        response.close();

        response = addSession(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z",1);
        sessionid = getIdFromResponse(response);
        response.close();

        try {

            expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}], \"completedPomodoros\":1}\"}";

            //Query
            response = getReport(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z", true, false);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println(expectedJson);
            System.out.println(strResponse);
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }
    //Test Case6: test if it runs correctly for the correct request and do not include completedPromodoro and include hours worked
    @Test
    public void getReportTest6() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        CloseableHttpResponse response;

        response = addUser("test8", "test8", "test8@email.org");
        id = getIdFromResponse(response);
        response.close();

        response =  addProject(id, "testproj8");
        projectid = getIdFromResponse(response);
        response.close();

        response = addSession(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z",1);
        sessionid = getIdFromResponse(response);
        response.close();

        try {

            expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}],  \"totalHoursWorkedOnProject\":1}\"}";

            //Query
            response = getReport(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z", false, true);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println(expectedJson);
            System.out.println(strResponse);
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }
    //Test Case7: test if it runs correctly for the correct request and include completedPromodoro and hours worked
    @Test
    public void getReportTest7() throws Exception {
        String strResponse, id, projectid, sessionid, expectedJson;
        HttpEntity entity;
        CloseableHttpResponse response;

        response = addUser("test8", "test8", "test8@email.org");
        id = getIdFromResponse(response);
        response.close();

        response =  addProject(id, "testproj8");
        projectid = getIdFromResponse(response);
        response.close();

        response = addSession(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z",1);
        sessionid = getIdFromResponse(response);
        response.close();

        try {

            expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}]}\"}";

            //Query
            response = getReport(id, projectid,"2019-02-18T20:00Z","2019-02-18T21:00Z", false, false);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println(expectedJson);
            System.out.println(strResponse);
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }


    /***********************************************************************************/

	/* Helper Functions */

    private String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
    }

    private String getIdFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        String id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

}
