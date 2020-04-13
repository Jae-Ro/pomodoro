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
import java.util.ArrayList;

public class users_userId_projects_projectId_sessions_sessionId {
    // private String baseUrl = "http://localhost:8080";
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
            // HttpHost localhost = new HttpHost("locahost", 8080);
            // cm.setMaxPerRoute(new HttpRoute(localhost), 10);

            HttpHost remotehHost = new HttpHost("http://gazelle.cc.gatech.edu", 9008);
            cm.setMaxPerRoute(new HttpRoute(remotehHost), 10);

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
    public void updateSessionTest() throws Exception {
        deleteUsers();

        try {
            String fn = "Jae";
            String ln = "SessionTest";
            String emailAddress = "jae@swe.org";
            CloseableHttpResponse response = createUser(fn, ln, emailAddress);
            int userId = Integer.parseInt(getIdFromResponse(response));
            System.out.println("this is the userID: " + userId);
            response.close();

            response = createProject(userId, "TestProject");
            int projectId = Integer.parseInt(getIdFromResponse(response));
            System.out.println("this is the projectID: " + projectId);
            response.close();

            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-19T20:00Z", 1);
            int sessionId = Integer.parseInt(getIdFromResponse(response));
            System.out.println("this is the sessionID: " + sessionId);
            response.close();

            response = updateSession(userId, projectId, sessionId, "2019-02-18T20:00Z", "2019-02-19T20:00Z", 2);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + sessionId
                    + ",\"startTime\":\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-19T20:00Z\",\"counter\":" + 2 + "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," + "\"lastName\":\"" + lastName
                + "\"," + "\"email\":\"" + email + "\"}");
        System.out.println("this is the input " + "{\"firstName\":\"" + firstName + "\"," + "\"lastName\":\"" + lastName
                + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(int userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createSession(int userId, int projectId, String startTime, String endTime,
            int counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," + "\"endTime\":\"" + endTime
                + "\"," + "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateSession(int userId, int projectId, int sessionId, String startTime,
            String endTime, int counter) throws IOException {
        HttpPut httpRequest = new HttpPut(
                baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," + "\"endTime\":\"" + endTime
                + "\"," + "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

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
                    CloseableHttpResponse response1 = httpclient.execute(httpDelete);
                    entity = response1.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    System.out.println(responseString);
                    arrList.add(responseString);
                    // response1.close();
                }
            }
        }
        System.out.println("Deleted Users: " + arrList);
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

}