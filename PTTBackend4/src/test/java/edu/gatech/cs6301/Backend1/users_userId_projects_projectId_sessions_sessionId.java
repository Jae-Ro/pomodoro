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

public class users_userId_projects_projectId_sessions_sessionId {
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

    //Purpose: Test for invalid userId in a PUT request path. Should return 404
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = putSessionIdRequest("7", "7", "7", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            int status = response.getStatusLine().getStatusCode();
            //Invalid userid should return 404
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid projectId in a PUT request path. Should return 404
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, "7");
            CloseableHttpResponse response = putSessionIdRequest(createdUserId, "7", "7", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            //Invalid projectid should return 404
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid sessionId in a PUT request path. Should return 404
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse deleteProject = deleteSession(createdUserId, createdProjectId, "7");
            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdUserId, "7", "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            //Invalid sessionid should return 404
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for sessionId parameter not present in PUT request body. Should still succeed and return 200.
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "FAIL");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for startTime parameter not present in PUT request body
    //Missing starttime parameter should result in error
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "FAIL", "2019-02-18T20:00Z", "0", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid starttime parameter in PUT request body. Should return 400.
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2:2", "2019-02-18T20:00Z", "0", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for endTime parameter not present in PUT request body. Should result in 400
    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "FAIL", "0", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid endtime parameter in PUT request body. Should result in 400
    @Test
    public void pttTest8() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2:2", "0", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for counter parameter not present in PUT request body. Should return 400
    @Test
    public void pttTest9() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "FAIL", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for non integer counter parameter in PUT request body. Should return 400.
    @Test
    public void pttTest10() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "abc", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for negative counter parameter in PUT request body. Should return 400.
    @Test
    public void pttTest11() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "-100", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for valid PUT request body with non empty ID in body. Should return 200 with the response body containing the updated request
    @Test
    public void pttTest12() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "100", "1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for valid PUT request body with empty ID in body.Should return 200 with the response body containing the updated request
    @Test
    public void pttTest13() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestSessions", "TestSessions", "testsessions@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestSessions");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionIdRequest(createdUserId, createdProjectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "0", "1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = putSessionIdRequest(createdUserId, createdProjectId, createdSessionId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", "100", "");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    //Helper Functions
    protected CloseableHttpResponse createUser(String id, String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
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


    protected CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

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
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
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
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postProjectRequest(String userId, String projectId, String projectName) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if ("FAIL".equals(projectName)) {
            input = new StringEntity("{\"id\":\"" + "\"}");
        } else {
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

    private CloseableHttpResponse getProjectRequest(String userId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postSessionIdRequest(String userId, String projectId, String startTime, String endTime, String counter, String bodyId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                "\"counter\":\"" + counter + "\"," +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
//        System.out.println("*** Executing request " + EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessionIdRequest(String userId, String projectId, String sessionId, String startTime, String endTime, String counter, String bodyId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if ("FAIL".equals(bodyId)) {
            input = new StringEntity("{\"counter\":\"" + counter + "\"," +
                    "\"startTime\":\"" + startTime + "\"," +
                    "\"endTime\":\"" + endTime + "\"}");
        }else if ("FAIL".equals(startTime)) {
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"counter\":\"" + counter + "\"," +
                    "\"endTime\":\"" + endTime + "\"}");
        } else if ("FAIL".equals(endTime)) {
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"counter\":\"" + counter + "\"," +
                    "\"startTime\":\"" + startTime + "\"}");
        } else if ("FAIL".equals(counter)) {
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
//        System.out.println("*** Executing request " + EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse deleteSession(String userId, String projectId, String sessionId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

}
