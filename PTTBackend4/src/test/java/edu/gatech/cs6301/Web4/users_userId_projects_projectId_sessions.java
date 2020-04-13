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

public class users_userId_projects_projectId_sessions {

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

       // Purpose: test creating a session
    @Test
    public void createSession() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "johnj@doe.org");
            String userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "JohnDoe's Project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T20:00Z", 0);
            // int sessionId = Integer.parseInt(getIdFromResponse(response));
            // String sessionId = getIdFromResponse(response);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            //String id = getIdFromStringResponse(strResponse);
            //String cnt = getCounterFromStringResponse(strResponse);
            //int id = Integer.parseInt(getIdFromStringResponse(strResponse));
            //int cnt = Integer.parseInt(getCounterFromStringResponse(strResponse));

            String expectedJson = "{\"startTime\":\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-18T20:00Z\",\"counter\":0}";
            // String expectedJson = "{\"id\":\"" + sessionId + "\",\"startTime\":\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-18T20:00Z\",\"counter\":\"" + 0 + "\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);


            EntityUtils.consume(response.getEntity());
            response.close();
            //deleteUser(userId);
        } finally {

            httpclient.close();
        }
    }
   
    // Purpose: test getting all sessions from a given project
    @Test
    public void getSessions() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Johni", "Doe", "johni@doe.org");
            String userId = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            response = createProject(userId, "John's Project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = createSession(userId, projectId, "2020-02-18T20:00Z", "2020-02-18T20:00Z", 0);
            String sessionId = getIdFromResponse(response);
            response.close();

            response = getSessions(userId, projectId);

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

            // int id = Integer.parseInt(getIdFromResponse(response));
            // String id = "";
            // String id = getIdFromResponse(response);
            String newStartTime = "2020-02-18T20:00Z";
            String newEndTime = "2020-02-18T20:00Z";

            // String expectedJson = "{\"startTime\":\"" + newStartTime + "\",\"endTime\":\"" + newEndTime + "\",\"counter\":1}" ;

            String expectedJson = "[{\"startTime\":\"2020-02-18T20:00Z\", \"endTime\":\"2020-02-18T20:00Z\",\"counter\":0}]";
            // String expectedJson = "{\"startTime\":" + id + "\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-18T20:00Z\",\"counter\":0}";
            // String expectedJson = "{\"id\":\"" + id + "\",\"startTime\":\"2020-02-18T20:00Z\", \"endTime\":\"2020-02-18T20:00Z\",\"counter\":0}";
	        JSONAssert.assertEquals(expectedJson.toString(), strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            //deleteUser(userId);
        } finally {

            httpclient.close();
        }
    }

    // Purpose: test getting session from an empty project
    @Test
    public void getEmptySession() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userId = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();
            response = createProject(userId, "Johnii's Project");
            String projectId = getIdFromResponse(response);
            response.close();

            response = getSessions(userId, projectId);

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

            String expectedJson = "[]";
	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            //deleteUser(userId);
        } finally {

            httpclient.close();
        }
    }

 
    // Purpose: test getting sessions from an unexisting project
    @Test
    public void getSessionFromMissingProject() throws Exception {
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

            String missingId = "123" + projectId1 + projectId2;

            response = getSessions(userId1, missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
            // deleteUser(userId1);
            // deleteUser(userId2);
        } finally {

            httpclient.close();
        }
    }

    // Purpose: test getting sessions from an unexisting user
    @Test
    public void getSessionFromMissingUser() throws Exception {
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

            String missingId = "123" + userId1 + userId2;

            response = getSessions(missingId, projectId1);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
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

    private CloseableHttpResponse createSession(String userId, String projectId, String start, String end, int counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId+"/projects/" + projectId + "/sessions/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + start + "\"," +
                                        "\"endTime\":\"" + end + "\"," +
                                        "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getSessions(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/"+ userId + "/projects/" + projectId + "/sessions/");
        httpRequest.addHeader("accept", "application/json");

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

    private String getCounterFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);

        String id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("counter")) {
                id = object.get(key).toString();
            }
        }
        return id;
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
