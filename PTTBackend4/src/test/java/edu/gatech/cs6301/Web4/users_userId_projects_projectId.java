package edu.gatech.cs6301.Web4;

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
import org.json.JSONArray;
import java.util.*;

public class users_userId_projects_projectId {

    //private String baseUrl = "http://localhost:8080";
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
	    //HttpHost localhost = new HttpHost("locahost", 8080);
        HttpHost remotehost = new HttpHost("http://gazelle.cc.gatech.edu", 9008);
        cm.setMaxPerRoute(new HttpRoute(remotehost), 10);
	    //cm.setMaxPerRoute(new HttpRoute(localhost), 10);
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

    // Purpose: test getting project by ID for a given user
    @Test
    public void getProjectByID() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();
            response = createProject(userId, "John's Project");
            String projectId = getIdFromResponse(response);
            String expectedJson = "{\"projectname\":\"John's Project\"}";
            //int Id = Integer.parseInt(projectId);
            response.close();

            response = getProject(userId, projectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            //int id = Integer.parseInt(getIdFromStringResponse(strResponse));

	        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            //deleteUser(userId);
        } finally {

            httpclient.close();
        }
    }

    // Purpose: test updating an existing project by ID
    @Test
    public void updateProject() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Tom99", "Doe", "tom99@doe.org");
            String userId = getIdFromResponse(response);
            response.close();
            response = createProject(userId, "Tom99's Project");
            String projectId = getIdFromResponse(response);
            //int Id = Integer.parseInt(projectId);
            response.close();

            response = updateProject(userId, projectId, "Tom's New Project");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;

            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            //int id = Integer.parseInt(getIdFromStringResponse(strResponse));

            String expectedJson = "{\"projectname\":\"Tom's New Project\"}";
	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());

            response.close();
            //deleteUser(userId);
        } finally {

            httpclient.close();
        }
    }

    // Purpose: test deleting an existing project by ID
    @Test
    public void DeleteProjectById() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String expectedJson = null;

        try {
            CloseableHttpResponse response = createUser("Jane", "Doe", "jane@doe.org");
            String userId = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();
            response = createProject(userId, "Jane's Project");
            String projectId = getIdFromResponse(response);
            //int Id = Integer.parseInt(projectId);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteProject(userId, projectId);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            int id = Integer.parseInt(getIdFromStringResponse(strResponse));

            expectedJson = "{\"projectname\":\"Jane's Project\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllProjects(userId);
            status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "[]";
	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            //deleteUser(userId);
        } finally {
            httpclient.close();
        }
    }

    // Purpose: test getting a project with an unexisting id
    @Test
    public void getMissingProject() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId1 = getIdFromResponse(response);
            response.close();
            response = createProject(userId1, "John's Project");
            String projectId1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Doe", "jane@doe.org");
            String userId2 = getIdFromResponse(response);
            response.close();
            response = createProject(userId2, "Jane's Project");
            String projectId2 = getIdFromResponse(response);
            response.close();

            String missingId = "123" + projectId1 + projectId2; // making sure the ID is not present

            response = getProject(userId1, missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
            // deleteProject(userId1, projectId1);
            // deleteProject(userId2, projectId2);
            // deleteUser(userId1);
            // deleteUser(userId2);

        } finally {
            httpclient.close();
        }
    }

    // Purpose: test deleting a project with an unexisting id
    @Test
    public void deleteMissingProject() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId1 = getIdFromResponse(response);
            response.close();
            response = createProject(userId1, "John's Project");
            String projectId1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Doe", "jane9@doe.org");
            String userId2 = getIdFromResponse(response);
            response.close();
            response = createProject(userId2, "Jane's Project");
            String projectId2 = getIdFromResponse(response);
            response.close();

            String missingId = "123" + projectId1 + projectId2; // making sure the ID is not present

            response = deleteProject(userId1, missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
            // deleteProject(userId1, projectId1);
            // deleteProject(userId2, projectId2);
            // deleteUser(userId1);
            // deleteUser(userId2);

        } finally {
            httpclient.close();
        }
    }

    // Purpose: test deleting a project from an unexisting user
    @Test
    public void deleteProjectFromMissingUser() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId1 = getIdFromResponse(response);
            response.close();
            response = createProject(userId1, "John's Project");
            String projectId1 = getIdFromResponse(response);
            response.close();

            response = createUser("Jane", "Doe", "jane@doe.org");
            String userId2 = getIdFromResponse(response);
            response.close();
            response = createProject(userId2, "Jane's Project");
            String projectId2 = getIdFromResponse(response);
            response.close();

            String missingId = "123" + userId1 + userId2; // making sure the ID is not present

            response = deleteProject(missingId, projectId1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
            // deleteProject(userId1, projectId1);
            // deleteProject(userId2, projectId2);
            // deleteUser(userId1);
            // deleteUser(userId2);

        } finally {
            httpclient.close();
        }
    }


    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"lastName\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId+"/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateProject(String userId, String projectId, String projectname) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+userId+"/projects/"+projectId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/"+ userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllProjects(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/"+ userId + "/projects" );
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/"+ userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse deleteUser(String userId) throws IOException {
	HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    // private CloseableHttpResponse deleteUsers() throws IOException {
	// HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/");
    //     httpDelete.addHeader("accept", "application/json");
    //
    //     System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
    //     CloseableHttpResponse response = httpclient.execute(httpDelete);
    //     System.out.println("*** Raw response " + response + "***");
    //     // EntityUtils.consume(response.getEntity());
    //     // response.close();
    //     return response;
    // }

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

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private void deleteUsers() throws IOException, JSONException {
        CloseableHttpResponse response = getAllUsers();
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        if (status == 200) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        strResponse = EntityUtils.toString(entity);
        JSONArray jsonArray = new JSONArray(strResponse);
        response.close();

        ArrayList<String> arrList = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.equals("id")) {
                    int id = Integer.parseInt(json.get(key).toString());
                    HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
                    httpDelete.addHeader("accept", "application/json");
                    // System.out.println("*** Executing request " + httpDelete.getRequestLine() +
                    // "***");
                    CloseableHttpResponse response1 = httpclient.execute(httpDelete);
                    entity = response1.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    System.out.println(responseString);
                    arrList.add(responseString);
                    // response1.close();
                    // System.out.println("*** Raw response " + response + "***");
                }
            }
        }
        System.out.println("Deleted Users: " + arrList);
    }

}
