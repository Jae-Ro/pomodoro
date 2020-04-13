package edu.gatech.cs6301.Backend1;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

import org.skyscreamer.jsonassert.JSONAssert;
import java.net.URLEncoder;

public class users_userId_projects_projectId_report {
    
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

    /**
     * The following are helper methods and constants used
     */

    protected CloseableHttpResponse createUser(String id, String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if(id.equals("1")==true) //ID Field absent
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if(id.equals("2")==true) //ID Field empty
            input = new StringEntity("{\"id\":\"" + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(firstname.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(firstname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(lastname.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(lastname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + "\"," +
                    "\"email\":\"" + email + "\"}");
        else if(email.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"}");
        else if(email.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + "\"}");
        else
            input = new StringEntity("{\"id\":\"" + id + "\"," +
                    "\"firstName\":\"" + firstname + "\"," +
                    "\"lastName\":\"" + lastname + "\"," +
                    "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postProjectRequest(String userId, String projectId, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if("FAIL".equals(projectName)){
            input = new StringEntity("{\"id\":\"" + "\"}");
        }else {
            input = new StringEntity("{\"id\":\"" + projectId + "\"," +
                    "\"projectname\":\"" + projectName + "\"}");
        }
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println("*** Executing request " + EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postSessionIdRequest(String userId, String projectId, String startTime, String endTime, String counter, String bodyId) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if("FAIL".equals(startTime)){
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"counter\":\"" + counter + "\"," +
                    "\"endTime\":\"" + endTime + "\"}");
        }else if("FAIL".equals(endTime)){
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"counter\":\"" + counter + "\"," +
                    "\"startTime\":\"" + startTime + "\"}");
        }else if("FAIL".equals(counter)){
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"startTime\":\"" + startTime + "\"," +
                    "\"endTime\":\"" + endTime + "\"}");
        } else {
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"counter\":\"" + counter + "\"," +
                    "\"startTime\":\"" + startTime + "\"," +
                    "\"endTime\":\"" + endTime + "\"}");
        }
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
    }

    protected String getIdFromStringResponse(String strResponse) throws JSONException {
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

    private CloseableHttpResponse getProjectReport(String from, String to, boolean includeCompletedPomodoros, Boolean includeTotalHoursWorkedOnProject, String userId, String projectId) throws IOException {

        String endpointUrl = "/users/" + userId + "/projects/" + projectId + "/report?";
        if (!from.equals(MISSING_STRING)){
            endpointUrl += "from=" + from + "&";
        }
        if (!to.equals(MISSING_STRING)) {
            endpointUrl += "to=" + to + "&";
        }
        if (includeTotalHoursWorkedOnProject != null) {
            endpointUrl += "includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject + "&";
        }
        endpointUrl += "includeCompletedPomodoros=" + includeCompletedPomodoros;
//        endpointUrl = URLEncoder.encode(endpointUrl);

        HttpGet httpRequest = new HttpGet(baseUrl + endpointUrl);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId +"/projects/"+projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse deleteSession(String userId, String projectId, String sessionId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId +"/projects/"+projectId+"/sessions/"+sessionId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    public String[] createReportContent() throws Exception {
        CloseableHttpResponse createUserResponse = createUser("7", "TestQwE","TestQWe","testqwe@test.com");
        String createdUserId = getIdFromResponse(createUserResponse);
        createUserResponse.close();
        CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "TestQWe");
        String createdProjectId = getIdFromResponse(createProjectResponse);
        createProjectResponse.close();
        CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId,createdProjectId,"2019-02-18T20:00Z","2019-02-18T20:00Z","0","1");
        String createdSessionId = getIdFromResponse(createSessionResponse);
        createSessionResponse.close();

        return new String[] {createdUserId, createdProjectId, createdSessionId};
    }

    private static final String MISSING_STRING = "324lksdjflbq4kjspdfjlaks";
    private static final String INVALID_TIME = "THIS_IS_OBVIOUSLY_NOT_A_REAL_TIME";
    private static final String VALID_TIME_1 = "2019-02-18T20:00Z";
    private static final String VALID_TIME_2 = "2019-02-18T20:00Z";
    private static final String INVALID_USER_ID = "12321";
    private static final String INVALID_PROJECT_ID = "12312";

    // Purpose: Tests for missing "from" parameter (i.e. not in time format)
    @Test
    public void pttTest1() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(MISSING_STRING, VALID_TIME_2, true, true, userId, projectId);
            int status = response.getStatusLine().getStatusCode();

            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests for invalid "from" parameter (i.e. an invalid time)
    @Test
    public void pttTest2() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(INVALID_TIME, VALID_TIME_2, true, true, userId, projectId);
            int status = response.getStatusLine().getStatusCode();

            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests for missing "to" parameter (i.e. )
    @Test
    public void pttTest3() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, MISSING_STRING, true, true, userId, projectId);
            int status = response.getStatusLine().getStatusCode();

            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests for invalid "to" parameter (i.e. an invalid time)
    @Test
    public void pttTest4() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, INVALID_TIME, true, true, userId, projectId);
            int status = response.getStatusLine().getStatusCode();

            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests for invalid userId (i.e. a userId that doesn't exist)
    @Test
    public void pttTest5() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, true, true, INVALID_USER_ID, sessionId);
            int status = response.getStatusLine().getStatusCode();

            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
    
    // Purpose: Tests for invalid projectId (i.e. a projectId that doesn't exist)
    @Test
    public void pttTest6() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, true, true, userId, INVALID_PROJECT_ID);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Valid request; includeCompletedPomodoros = true, includeTotalHoursWorkedOnProject = true    
    @Test
    public void pttTest7() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, true, true, userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);

            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                Assert.assertEquals(200, status);
                return;
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:00Z\",\"hoursWorked\":0}],\"completedPomodoros\":0,\"totalHoursWorkedOnProject\":0}";//"{ \"sessions\": [ { \"startingTime\": \"2019-02-18T20:00Z\", \"endingTime\": \"2019-02-18T20:00Z\", \"hoursWorked\": \"0\" } ], \"completedPomodoros\": \"0\", \"totalHoursWorkedOnProject\": 0}";
	        System.out.println(expectedJson+" "+strResponse);
            JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Valid request; includeCompletedPomodoros = true, includeTotalHoursWorkedOnProject = false    
    @Test
    public void pttTest8() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, true, false, userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                Assert.assertEquals(200, status);
                return;
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{ \"sessions\": [ { \"startingTime\": \"2019-02-18T20:00Z\", \"endingTime\": \"2019-02-18T20:00Z\", \"hoursWorked\": 0 } ], \"completedPomodoros\": 0}";
	        JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Valid request; includeCompletedPomodoros = true, includeTotalHoursWorkedOnProject = null    
    @Test
    public void pttTest9() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, true, null, userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                Assert.assertEquals(200, status);
                return;
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{ \"sessions\": [ { \"startingTime\": \"2019-02-18T20:00Z\", \"endingTime\": \"2019-02-18T20:00Z\", \"hoursWorked\": 0 } ], \"completedPomodoros\": 0}";
	        JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Valid request; includeCompletedPomodoros = false, includeTotalHoursWorkedOnProject = true    
    @Test
    public void pttTest10() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, false, true, userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
            
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                Assert.assertEquals(200, status);
                return;
            }
            String strResponse = EntityUtils.toString(entity);
            System.out.println(strResponse);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{ \"sessions\": [ { \"startingTime\": \"2019-02-18T20:00Z\", \"endingTime\": \"2019-02-18T20:00Z\", \"hoursWorked\": 0 } ], \"totalHoursWorkedOnProject\": 0}";
	        JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Valid request; includeCompletedPomodoros = false, includeTotalHoursWorkedOnProject = false    
    @Test
    public void pttTest11() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, false, false, userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
             
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                Assert.assertEquals(200, status);
                return;
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{ \"sessions\": [ { \"startingTime\": \"2019-02-18T20:00Z\", \"endingTime\": \"2019-02-18T20:00Z\", \"hoursWorked\": 0 } ]}";
	        JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Valid request; includeCompletedPomodoros = false, includeTotalHoursWorkedOnProject = null
    @Test
    public void pttTest12() throws Exception {
        try {
            String[] output = createReportContent();
            String userId = output[0];
            String projectId = output[1];
            String sessionId = output[2];

            CloseableHttpResponse response = getProjectReport(VALID_TIME_1, VALID_TIME_2, false, null, userId, projectId);
            int status = response.getStatusLine().getStatusCode();
            
            deleteSession(userId, projectId, sessionId);
            deleteProject(userId, projectId);
            deleteUser(userId);
             
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                Assert.assertEquals(200, status);
                return;
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{ \"sessions\": [ { \"startingTime\": \"2019-02-18T20:00Z\", \"endingTime\": \"2019-02-18T20:00Z\", \"hoursWorked\": 0 } ]}";
	        JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

}
