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

public class Users_userId_projects_projectId {

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

    // Test Case 1 <error>
    // Get_User_ID : Empty
    // Purpose: Test if the user id is empty
    // we determined this case is unnecessary as get will pull all users if empty
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        try {
            String id = "";

            CloseableHttpResponse response = getUser(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) { // changed from 404 to 200
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(200, status); // phalguna

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 2 <error>
    // Get_User_ID : Not empty but not in database
    // Purpose: Test if the user id is not empty but doesn't exist in database
    @Test
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

    // Test Case 3 <error>
    // Get_Project_ID : Not empty but not in database
    // Purpose: Test if the project id is not empty but doesn't exist in database
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String user_id = getIdFromResponse(response);

            response.close();
            String project_id = "4354534534";

            response = getProject(user_id, project_id);

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

            CloseableHttpResponse deleteresponse = deleteUser(user_id);
            deleteresponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 4 <error>
    // Delete_Project_ID : Empty
    // purpose: Test deleting project with empty id
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john1@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project Name");
            cpResponse.close();

            String project_id = "";
            CloseableHttpResponse deleteProjectResponse = deleteProject(user_id, project_id);

            int status = deleteProjectResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 405) { // Phalguna
                entity = deleteProjectResponse.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(user_id);
                deleteresponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(405, status); // Phalguna 404,405

            CloseableHttpResponse deleteresponse = deleteUser(user_id);
            deleteresponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 5 <error>
    // Delete_Project_ID : Not empty but not in database
    // purpose: Test deleting project with not empty id but not in database
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john1@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project Name");
            cpResponse.close();

            String project_id = "123454321";
            CloseableHttpResponse deleteProjectResponse = deleteProject(user_id, project_id);

            int status = deleteProjectResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = deleteProjectResponse.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(user_id);
                deleteresponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            CloseableHttpResponse deleteresponse = deleteUser(user_id);
            deleteresponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test Case 6 (Key = 1.1.1.1.) Put_User_ID : User_ID exists in database
     * Put_Project_ID : Project_ID exists in database Put_Session_ID : Session_ID
     * exists in database Put_Start Time : Valid time format Put_End Time : Valid
     * time format Put_Counter : Valid positive integer
     */
    // Purpose: Test if session can be created (everything is valid)
    @Test
    public void pttTest6() throws Exception {
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john1@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String newProjectName = "New Project Name";
            response = putProject(user_id, project_id, newProjectName);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(user_id);
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(user_id, project_id);
                deleteprojectresponse.close();

                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(200, status);

            CloseableHttpResponse deleteresponse = deleteUser(user_id);
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(user_id, project_id);
            deleteprojectresponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test Case 7 (Key = 1.1.1.2.) Get_User_ID : User_ID exists in database
     * Get_Project_ID : Project_ID exists in database Delete_Project_ID : Project_ID
     * exists in database Put_Project name : Empty
     */
    // Purpose: Test if project name can be changed to empty string
    @Test
    public void pttTest7() throws Exception {
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john1@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String newProjectName = "";
            CloseableHttpResponse putResponse = putProject(user_id, project_id, newProjectName);
            System.out.println("1111111111111111111111111111111111");
            int status = putResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { // Phalguna 200 to 400
                entity = putResponse.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(user_id);
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(user_id, project_id);
                deleteprojectresponse.close();

                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + putResponse.getStatusLine().getStatusCode()
                    + ") ***");
            Assert.assertEquals(400, status); // Phalguna 200 to 400

            CloseableHttpResponse deleteresponse = deleteUser(user_id);
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(user_id, project_id);
            deleteprojectresponse.close();

            EntityUtils.consume(putResponse.getEntity());
            putResponse.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse deleteProject(String user_id, String project_id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
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

    private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProject(String user_id, String project_id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putProject(String user_id, String project_id, String project_name)
            throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpRequest.addHeader("content-type", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + project_name + "\"}");
        httpRequest.setEntity(input);
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
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

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
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
