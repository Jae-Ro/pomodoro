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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class users_userId_projects_projectId {

    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;
    // private users usersInstance = new users();

    protected CloseableHttpResponse createUser(String id, String firstname, String lastname, String email)
            throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if (id.equals("1") == true) // ID Field absent
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," + "\"lastName\":\"" + lastname + "\","
                    + "\"email\":\"" + email + "\"}");
        else if (id.equals("2") == true) // ID Field empty
            input = new StringEntity("{\"id\":\"" + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\""
                    + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (firstname.equals("Fail") == true) // Field absent
            input = new StringEntity(
                    "{\"id\":\"" + id + "\"," + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (firstname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + "\"," + "\"lastName\":\""
                    + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (lastname.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"email\":\""
                    + email + "\"}");
        else if (lastname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + "\"," + "\"email\":\"" + email + "\"}");
        else if (email.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"}");
        else if (email.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + "\"}");
        else
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
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

    private CloseableHttpResponse putProjectRequest(String userId, String projectId, String projectName)
            throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects/" + projectId);
        HttpPut httpPut = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpPut.addHeader("accept", "application/json");
        StringEntity input;
        if ("FAIL".equals(projectName)) {
            input = new StringEntity("{\"id\":\"" + "\"}");
        } else if ("NOID".equals(projectId)) {
            input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        } else {
            input = new StringEntity("{\"id\":\"" + projectId + "\"," + "\"projectname\":\"" + projectName + "\"}");
        }
        input.setContentType("application/json");
        httpPut.setEntity(input);

        System.out.println("*** Executing request " + httpPut.getRequestLine() + "***");
        // System.out.println("*** Executing request " +
        // EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpPut);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putProjectRequest1(String userId, String projectId, String id_in_body,
            String projectName) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects/" + projectId);
        HttpPut httpPut = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpPut.addHeader("accept", "application/json");
        StringEntity input;
        if ("FAIL".equals(projectName)) {
            input = new StringEntity("{\"id\":\"" + "\"}");
        } else if ("NOID".equals(id_in_body)) {
            input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        } else if ("Empty".equals(id_in_body)) {
            input = new StringEntity("{\"id\":\"" + "\"," + "\"projectname\":\"" + projectName + "\"}");
        } else {
            input = new StringEntity("{\"id\":\"" + projectId + "\"," + "\"projectname\":\"" + projectName + "\"}");
        }
        System.out.println(input.toString());
        input.setContentType("application/json");
        httpPut.setEntity(input);

        System.out.println("*** Executing request " + httpPut.getRequestLine() + "***");
        // System.out.println("*** Executing request " +
        // EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpPut);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProjectRequest(String userId, String projectId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects/" + projectId);
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProjectidRequest(String userId, String projectId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects/" + projectId);
        HttpGet httpGet = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpGet.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpGet.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

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

    // Purpose: Test for invalid userId in a PUT request path. Should return 404
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = putProjectRequest("7", "8", "testProject");
            int status = response.getStatusLine().getStatusCode();
            // HttpEntity entity;
            // String strResponse;

            // if (status == 404) {
            // entity = response.getEntity();
            // } else {
            // throw new ClientProtocolException("Unexpected response status: " + status);
            // }
            // strResponse = EntityUtils.toString(entity);

            // System.out.println("*** String response " + strResponse + " (" +
            // response.getStatusLine().getStatusCode() + ") ***");

            // JSONAssert.assertEquals(expectedJson,strResponse, false);

            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for invalid userId in a Delete request path. Should return 404
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = deleteProjectRequest("7", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for invalid userId in a Get request path. Should return 404
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = getProjectidRequest("7", "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for invalid projectId in a Put request path. Should return 404
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            System.out.println("UserID:" + createdUserId);

            CloseableHttpResponse response = putProjectRequest(createdUserId, "123", "testProject");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for invalid projectId in a Delete request path. Should return
    // 404
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            System.out.println("UserID:" + createdUserId);

            CloseableHttpResponse response = deleteProjectRequest(createdUserId, "");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(405, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for invalid projectId in a Get request path. Should return 404
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            System.out.println("UserID:" + createdUserId);
            CloseableHttpResponse response = getProjectidRequest(createdUserId, "1224");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for PUT /projectId request without id present in body return
    // 200
    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse putProjectResponse = putProjectRequest1(createdUserId, createdProjectId, "NOID",
                    "testPutProjectName");

            System.out.println("UserID:" + createdUserId);
            int status = putProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(putProjectResponse.getEntity());

            EntityUtils.consume(putProjectResponse.getEntity());
            putProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for no projectName in a Put request body. Should return 400
    @Test
    public void pttTest8() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            System.out.println("UserID:" + createdUserId);
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = putProjectRequest(createdUserId, createdProjectId, "FAIL");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for an empty projectName in a Put request body. Should return
    // 400
    @Test
    public void pttTest9() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            System.out.println("UserID:" + createdUserId);
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();

            CloseableHttpResponse response = putProjectRequest(createdUserId, createdProjectId, "");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for /projectId in a Get request path. Should return 200
    @Test
    public void pttTest10() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse getProjectResponse = getProjectidRequest(createdUserId, createdProjectId);

            System.out.println("UserID:" + createdUserId);
            int status = getProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(getProjectResponse.getEntity());
            Assert.assertTrue(responseBody.contains(createdProjectId));
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for valid PUT /projectId request with an empty id return 200
    @Test
    public void pttTest11() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse putProjectResponse = putProjectRequest1(createdUserId, createdProjectId, "Empty",
                    "testPutProjectName");

            System.out.println("UserID:" + createdUserId);
            int status = putProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(putProjectResponse.getEntity());

            Assert.assertTrue(responseBody.contains(createdProjectId));

            EntityUtils.consume(putProjectResponse.getEntity());
            putProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for valid PUT /projectId request return 200
    @Test
    public void pttTest12() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse putProjectResponse = putProjectRequest(createdUserId, createdProjectId,
                    "testPutProjectName");

            System.out.println("UserID:" + createdUserId);
            int status = putProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(putProjectResponse.getEntity());
            Assert.assertTrue(responseBody.contains("testPutProjectName"));

            EntityUtils.consume(putProjectResponse.getEntity());
            putProjectResponse.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test for /projectId in a Delete request path. Should return 200
    @Test
    public void pttTest13() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test", "Test", "test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId, "", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse deleteProjectResponse = deleteProjectRequest(createdUserId, createdProjectId);

            System.out.println("UserID:" + createdUserId);
            int status = deleteProjectResponse.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(deleteProjectResponse.getEntity());
            Assert.assertTrue(responseBody.contains(createdProjectId));
        } finally {
            httpclient.close();
        }
    }

}