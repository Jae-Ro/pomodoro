package edu.gatech.cs6301.Web2;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.Iterator;

public class Users_userId_projects {

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

    @Test
    // Test Case 1 <error>
    // Get_User_ID : Empty
    // Purpose: Test if the user id is empty
    // we determined this case is unnecessary as get will pull all users if empty
    public void pttTest1() throws Exception {
        deleteUsers();
        try {

            String id = "";

            CloseableHttpResponse response = getUser(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) { // changed by Phalguna from 404 to 200
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            // Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Test Case 2 <error>
    // Get_User_ID : Not empty but not in database
    // Purpose: Test if the user id is not empty but doesn't exist in database
    public void pttTest2() throws Exception {
        deleteUsers();

        try {
            String id = "123454321";

            CloseableHttpResponse response = getUser(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Test Case 3 <error>
    // Post_Project_name : Empty
    // Purpose: Test if project with empty name can be added to an existing user
    public void pttTest3() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("Cheng", "Zhang", "czhang494@gatech.edu");
            String userId = getIdFromResponse(response);

            response.close();

            String projectName = "";
            CloseableHttpResponse cpResponse = createProject(userId, projectName);

            int status = cpResponse.getStatusLine().getStatusCode();
            HttpEntity entity;

            if (status == 400) {
                entity = cpResponse.getEntity();
            } else {
                if (status == 201) {
                    entity = cpResponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + cpResponse.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(userId));
                    deleteUser.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(userId));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Test Case 4 (Key = 1.1.)
    // Get_User_ID : User_ID exists in database
    // Post_Project_name : Not empty
    // Purpose: Test if project with valid project name can be added to an existing
    // user
    public void pttTest4() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("Cheng", "Zhang", "czhang494@gatech.edu");
            String userId = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(userId, "Valid Project Name");

            int status = cpResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = cpResponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + cpResponse.getStatusLine().getStatusCode() + ") ***");

            String projectId = getIdFromStringResponse(strResponse);
            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"Valid Project Name\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(cpResponse.getEntity());

            CloseableHttpResponse deleterProjectResponse = deleteProject(Integer.parseInt(userId),
                    Integer.parseInt(projectId));
            deleterProjectResponse.close();

            CloseableHttpResponse deleterUserResponse = deleteUser(Integer.valueOf(userId));
            deleterUserResponse.close();

            cpResponse.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse deleteUser(Integer id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," + "\"lastName\":\"" + lastName
                + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getUser(String UserId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + UserId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String user_id, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + user_id + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(int userId, int projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

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
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

    public void getAllUsersId() throws Exception {
        CloseableHttpResponse response = getAllUsers();
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String strResponse = EntityUtils.toString(entity);
            System.out.println(strResponse);
            // parsing JSON
            JSONArray result = new JSONArray(strResponse);
            for (int i = 0; i < result.length(); i++) {
                JSONObject object = result.getJSONObject(i);
                String id = null;
                Iterator<String> keyList = object.keys();
                while (keyList.hasNext()) {
                    String key = keyList.next();
                    if (key.equals("id")) {
                        id = object.get(key).toString();
                        System.out.println(id);
                    }
                }
            }

        }
        response.close();
    }

    // private CloseableHttpResponse deleteUser(int id) throws IOException {
    // HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
    // httpDelete.addHeader("accept", "application/json");
    //
    // System.out.println("*** Executing request " + httpDelete.getRequestLine() +
    // "***");
    // CloseableHttpResponse response = httpclient.execute(httpDelete);
    // System.out.println("*** Raw response " + response + "***");
    // // EntityUtils.consume(response.getEntity());
    // // response.close();
    // return response;
    // }

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

    private void deleteUsers() throws IOException {
        try {
            CloseableHttpResponse response = getAllUsers();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String strResponse = EntityUtils.toString(entity);
                System.out.println(strResponse);
                // parsing JSON
                JSONArray result = new JSONArray(strResponse);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = result.getJSONObject(i);
                    String id = null;
                    Iterator<String> keyList = object.keys();
                    while (keyList.hasNext()) {
                        String key = keyList.next();
                        if (key.equals("id")) {
                            id = object.get(key).toString();
                            System.out.println(id);
                            deleteUser(id);
                        }
                    }
                }
            }
            response.close();
        } catch (Exception e) {
            System.out.print("Can not delete all users");
        }
    }

}