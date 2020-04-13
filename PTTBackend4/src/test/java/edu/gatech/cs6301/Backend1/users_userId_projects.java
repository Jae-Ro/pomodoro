package edu.gatech.cs6301.Backend1;

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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.skyscreamer.jsonassert.JSONAssert;

public class users_userId_projects {

    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;
    // private users usersInstance = new users();

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
            // usersInstance = new users();
            setupdone = true;
        }
        System.out.println("*** STARTING TEST ***");
    }

    @After
    public void runAfter() {
        System.out.println("*** ENDING TEST ***");
    }

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    // Purpose: Test for invalid userId in a POST request path. Should return 404
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = postProjectRequest("7", "", "testProject");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for invalid userId in a GET request path. Should return 404
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = getProjectRequest("7");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for missing projectId parameter in POST request body. Should
    // pass with 201.
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestProjects", "TestProjects",
                    "testprojects@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            System.out.println("UserID:" + createdUserId);
            CloseableHttpResponse response = postProjectRequest(createdUserId, "FAIL", "testproject");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for missing projectname parameter in POST request body
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestProjects", "TestProjects",
                    "testprojects@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            System.out.println("UserID:" + createdUserId);
            CloseableHttpResponse response = postProjectRequest(createdUserId, "", "FAIL");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for empty projectname in POST request body
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestProjects", "TestProjects",
                    "testprojects@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            System.out.println("UserID:" + createdUserId);
            CloseableHttpResponse response = postProjectRequest(createdUserId, "", "");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for valid GET /projects request
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestProjects", "TestProjects",
                    "testprojects@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestProjects");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);

            System.out.println("UserID:" + createdUserId);
            int status = getProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(getProjectResponse.getEntity());
            Assert.assertTrue(responseBody.contains(createdProjectId));

            EntityUtils.consume(getProjectResponse.getEntity());
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for valid POST /projects request with an empty project id
    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestProjects", "TestProjects",
                    "testprojects@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "TestProjects");
            HttpEntity entity = createProjectResponse.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String createdProjectId = getIdFromStringResponse(strResponse);
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);

            System.out.println("UserID:" + createdUserId);
            int status = createProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(201, status);
            String expectedJson = "{\"id\":" + createdProjectId + "," + "\"projectname\":\"TestProjects\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);

            EntityUtils.consume(getProjectResponse.getEntity());
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for valid POST /projects request with a non empty project id
    @Test
    public void pttTest8() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "TestProjects", "TestProjects",
                    "testprojects@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "123", "TestProjects");
            HttpEntity entity = createProjectResponse.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String createdProjectId = getIdFromStringResponse(strResponse);
            // createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectRequest(createdUserId);

            System.out.println("UserID:" + createdUserId);
            int status = createProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(201, status);

            String expectedJson = "{\"id\":" + createdProjectId + "," + "\"projectname\":\"TestProjects\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            getProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    // Helper methods
    protected CloseableHttpResponse createUser(String id, String firstname, String lastname, String email)
            throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\""
                + lastname + "\"," + "\"email\":\"" + email + "\"}");
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

    private CloseableHttpResponse postProjectRequest(String userId, String projectId, String projectName)
            throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if ("FAIL".equals(projectName)) {
            input = new StringEntity("{\"id\":\"" + "\"}");
        } else if ("FAIL".equals(projectId)) {
            input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        } else {
            input = new StringEntity("{\"id\":\"" + projectId + "\"," + "\"projectname\":\"" + projectName + "\"}");
        }
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        // System.out.println("*** Executing request " +
        // EntityUtils.toString(httpRequest.getEntity()) + "***");
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

}