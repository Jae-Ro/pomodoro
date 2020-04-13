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

public class users_userId_projects_projectId_sessions {
    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;
//    private users usersInstance = new users();

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
        while (keyList.hasNext()){
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
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId +"/projects/"+projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postProjectRequest(String userId, String projectId, String projectName) throws IOException {
        System.out.println(baseUrl + "/users/"+userId + "/projects");
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

    private CloseableHttpResponse getProjectRequest(String userId) throws IOException {
        System.out.println(baseUrl + "/users/"+userId + "/projects");
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/"+userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSessionsRequest(String userId, String projectId) throws IOException {
        System.out.println(baseUrl + "/users/"+userId + "/projects");
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse postSessionRequest(String userId, String projectId, String startTime, String endTime, String counter, String bodyId) throws IOException {
        System.out.println(baseUrl + "/users/" + userId + "/projects");
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
//        System.out.println("*** Executing request " + EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessionIdRequest(String userId, String projectId, String sessionId, String startTime, String endTime, String counter, String bodyId) throws IOException {
        System.out.println(baseUrl + "/users/"+userId + "/projects");
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+userId + "/projects/"+projectId+"/sessions/"+sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
//        if("FAIL".equals(projectName)){
//            input = new StringEntity("{\"id\":\"" + "\"}");
//        }
//        else {
            input = new StringEntity("{\"id\":\"" + bodyId + "\"," +
                    "\"counter\":\"" + counter + "\"," +
                    "\"startTime\":\"" + startTime + "\"," +
                    "\"endTime\":\"" + endTime + "\"}");
//        }
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
//        System.out.println("*** Executing request " + EntityUtils.toString(httpRequest.getEntity()) + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
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
//            usersInstance = new users();
            setupdone = true;
        }
        System.out.println("*** STARTING TEST ***");
    }

    @After
    public void runAfter() {
        System.out.println("*** ENDING TEST ***");
    }

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    //Purpose: Test for invalid userId in a GET request path. Should return 404
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = getSessionsRequest("7","7");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid userId in a POST request path. Should return 404
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse deleteUser = deleteUser("7");
            CloseableHttpResponse response = postSessionRequest("7","7","2019-02-18T20:00Z","2019-02-18T20:00Z","0", "1");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid projectId in a post request path. Should return 404
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId,"7");
            CloseableHttpResponse response = putSessionIdRequest(createdUserId,"7","7", "2019-02-18T20:00Z","2019-02-18T20:00Z","0","1");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for invalid projectId in a get request path. Should return 404
    @Test
    public void pttTest4() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId,"7");
            CloseableHttpResponse response = getSessionsRequest("7","7");
            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for startTime parameter not present in POST request body
    @Test
    public void pttTest5() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"FAIL","2019-02-18T20:00Z","0","1");

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

    //Purpose: Test for invalid startTime format parameter in POST request body
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"2019/22/22","2019-02-18T20:00Z","0","1");

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

    //Purpose: Test for endTime parameter not present in POST request body
    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId, "2019-02-18T20:00Z","FAIL","0","1");

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

    //Purpose: Test for invalid endTime format parameter in POST request body
    @Test
    public void pttTest8() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"2019-02-18T20:00Z", "2019/22/22","0","1");

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

    //Purpose: Test for counter not present in POST request body
    @Test
    public void pttTest9() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"2019-02-18T20:00Z", "2019-02-18T20:00Z","FAIL","1");

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

    //Purpose: Test for non-integer counter  in POST request body
    @Test
    public void pttTest10() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"2019-02-18T20:00Z", "2019-02-18T20:00Z","A","1");

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

    //Purpose: Test for negative counter in POST request body
    @Test
    public void pttTest11() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"2019-02-18T20:00Z", "2019-02-18T20:00Z","-1","1");

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

//Purpose: Test for /sessions get
    @Test
    public void pttTest12() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse createSessionResponse = postSessionRequest(createdUserId,createdProjectId,"2019-02-18T20:00Z","2019-02-18T20:00Z","0","1");
            String createdSessionId = getIdFromResponse(createSessionResponse);
            createSessionResponse.close();

            CloseableHttpResponse response = getSessionsRequest(createdUserId,createdProjectId);

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteSession = deleteSession(createdUserId, createdProjectId, createdSessionId);
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(200, status);
            String responseBody = EntityUtils.toString(response.getEntity());
            Assert.assertTrue(responseBody.contains(createdSessionId));

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //Purpose: Test for /sessions post
    @Test
    public void pttTest13() throws Exception {
        try {
            CloseableHttpResponse createUserResponse = createUser("7", "Test","Test","test@test.com");
            String createdUserId = getIdFromResponse(createUserResponse);
            createUserResponse.close();
            CloseableHttpResponse createProjectResponse = postProjectRequest(createdUserId,"", "Test");
            String createdProjectId = getIdFromResponse(createProjectResponse);
            createProjectResponse.close();
            CloseableHttpResponse response = postSessionRequest(createdUserId,createdProjectId,"2019-02-17T20:00Z", "2019-02-18T20:00Z","0","1");

            int status = response.getStatusLine().getStatusCode();
            CloseableHttpResponse deleteProject = deleteProject(createdUserId, createdProjectId);
            CloseableHttpResponse deleteUser = deleteUser(createdUserId);
            Assert.assertEquals(201, status);
            String responseBody = EntityUtils.toString(response.getEntity());
            Assert.assertTrue(responseBody.contains("2019-02-17T20:00Z"));

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    
}
