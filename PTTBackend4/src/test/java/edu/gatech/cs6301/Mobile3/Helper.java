package edu.gatech.cs6301.Mobile3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class Helper {

    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    protected CloseableHttpClient httpclient;
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

    //Functions related to extracting fields from JSON object or list of JSON objects
    protected String extractFieldFromJSONObjectTypeString(String json_object, String field) throws JSONException {
        JSONObject object = new JSONObject(json_object);
        String field_value = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals(field)) {
                field_value = object.get(key).toString();
            }
        }
        return field_value;
    }

    protected ArrayList<String> extractFieldFromJSONArrayTypeString(String json_list, String field) throws JSONException {
        JSONArray json_arr = new JSONArray(json_list);
        ArrayList<String> fields = new ArrayList<String>();
        for (int i = 0; i < json_arr.length(); i++)
        {
            JSONObject object = json_arr.getJSONObject(i);
            String field_value = null;
            Iterator<String> keyList = object.keys();
            while (keyList.hasNext()){
                String key = keyList.next();
                if (key.equals(field)) {
                    field_value = object.get(key).toString();
                    fields.add(field_value);
                }
            }
        }
        return fields;
    }

    //Functions related to Schemas
    protected String getValidUserSchemaTypeString(String id, String firstName, String lastName, String email)
    {
        String schema = "{" +
                "\"id\":" + id + "," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"" +
                "}";
        return schema;
    }


    protected String getInvalidUserSchemaTypeString(String id, String firstName, String lastName, String email)
    {
        String schema = "{" +
                "\"id\":" + id + "," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "}";
        return schema;
    }

    protected String getValidProjectSchemaTypeString(String id, String projectname)
    {
        String schema = "{" +
                "\"id\":" + id + "," +
                "\"projectname\":\"" + projectname + "\"" +
                "}";
        return schema;
    }


    protected String getInvalidProjectSchemaTypeString(String id, String projectname)
    {
        String schema = "{" +
                "\"id\":" + id + "," +
                "}";
        return schema;
    }

    protected String getValidSessionSchemaTypeString(String id, String startTime, String endTime, String counter)
    {
        String schema = "{" +
                "\"id\":" + id + "," +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + Integer.parseInt(counter) +
                "}";
        return schema;
    }

    protected String getInvalidSessionSchemaTypeString(String id, String startTime, String endTime, String counter)
    {
        String schema = "{" +
                "\"id\":" + id + "," +
                "\"startTime\":\"" + startTime + "\"," +
                "\"counter\":" + Integer.parseInt(counter) +
                "}";
        return schema;
    }

    //Functions that implement REST APIs

    //Endpoint: /users (GET)
    protected CloseableHttpResponse getAllUsers() throws IOException {
        String url = baseUrl + "/users";
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/ (POST)
    protected CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        String url = baseUrl + "/users";
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("accept", "application/json");
        String userSchema = null;
        userSchema = getValidUserSchemaTypeString("0", firstName, lastName, email);
        StringEntity input = new StringEntity(userSchema);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println("Input: " + userSchema);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createUser(String userSchema) throws IOException {
        String url = baseUrl + "/users";
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(userSchema);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println("Input: " + userSchema);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //it will delete all users but there is no end point for this.
    protected void deleteAllUsers() throws IOException, JSONException {
        CloseableHttpResponse response_GET = getAllUsers();
        HttpEntity entity = response_GET.getEntity();
        String strResponse = EntityUtils.toString(entity);
        System.out.println("***" + strResponse + "***");
        ArrayList<String> fields = extractFieldFromJSONArrayTypeString(strResponse,"id");
        for (int i = 0; i < fields.size(); i++) {
            String id = fields.get(i);
            System.out.println("Deleting user id: " + id);
            deleteUser(id);
            //Wait for some time, otherwise server become unresponsive
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e){
                System.err.format("IOException: %s%n", e);
            }

        }
        response_GET.close();
        return;
    }

    //Endpoint: /users/{userId} (GET)
    public CloseableHttpResponse getUser(String userId) throws IOException {
        String url = baseUrl + "/users/" + userId;
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId} (PUT)
    protected CloseableHttpResponse updateUser(String userId, String firstName, String lastName, String email) throws IOException {
        String url = baseUrl + "/users/" + userId;
        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader("accept", "application/json");
        String userSchema = null;
        userSchema = getValidUserSchemaTypeString(userId, firstName, lastName, email);
        StringEntity input = new StringEntity(userSchema);

        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse updateUser(String userId, String userSchema) throws IOException {
        String url = baseUrl + "/users/" + userId;
        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(userSchema);

        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId} (DELETE)
    protected CloseableHttpResponse deleteUser(String userId) throws IOException {
        String url = baseUrl + "/users/" + userId;
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects (GET)
    protected CloseableHttpResponse getAllProjects(String userId) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects";
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects (POST)
    protected CloseableHttpResponse createProject(String userId, String id, String projectname) throws IOException {
        String url = null;

        if (userId.length() == 0) { //Adjust URL slightly for the case of a blank userID
            url = baseUrl + "/users" + userId + "/projects";
        }
        else {
            url = baseUrl + "/users/" + userId + "/projects";
        }

        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("accept", "application/json");
        String projectSchema = null;
        projectSchema = getValidProjectSchemaTypeString("0", projectname);
        StringEntity input = new StringEntity(projectSchema);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println("Input: " + projectSchema);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createProject(String userId, String projectSchema) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects";
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(projectSchema);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println("Input: " + projectSchema);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId} (GET)
    protected CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId;
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId} (PUT)
    /*
    This is commented bcz signature of both the "updateUser" would be same then. Keeping the more generic one

    protected CloseableHttpResponse updateProject(String userId, String projectId, String projectname) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId;
        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader("accept", "application/json");
        String projectSchema = null;
        projectSchema = getValidProjectSchemaTypeString(projectId, projectname);
        StringEntity input = new StringEntity(projectSchema);

        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
     */

    protected CloseableHttpResponse updateProject(String userId, String projectId, String projectSchema) throws IOException {
        String url = null;

        if (userId.length() == 0) { //Adjust URL slightly for the case of a blank userID
            url = baseUrl + "/users" + userId + "/projects/" + projectId;
        }
        else {
            url = baseUrl + "/users/" + userId + "/projects/" + projectId;
        }
        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(projectSchema);

        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId} (DELETE)
    protected CloseableHttpResponse deleteProject(String userId, String projectId) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId;
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId}/sessions (GET)
    protected CloseableHttpResponse getAllSessions(String userId, String projectId) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions";
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId}/sessions (POST)
    protected CloseableHttpResponse createSession(String userId, String projectId, String startTime, String endTime, String counter) throws IOException {
        String url = null;

        if (userId.length() == 0) { //Adjust URL slightly for the case of a blank userID
            url = baseUrl + "/users" + userId + "/projects/" + projectId + "/sessions";
        }
        else {
            url = baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions";
        }

        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("accept", "application/json");

        String sessionSchema = null;
        sessionSchema = getValidSessionSchemaTypeString("0", startTime, endTime, counter);
        StringEntity input = new StringEntity(sessionSchema);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println(url);
        System.out.println("Input: " + sessionSchema);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    protected CloseableHttpResponse createSession(String userId, String projectId, String sessionSchema) throws IOException {
        System.out.println("hello");
        String url = null;

        if (userId.length() == 0) { //Adjust URL slightly for the case of a blank userID
            url = baseUrl + "/users" + userId + "/projects/" + projectId + "/sessions";
        }
        else {
            url = baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions";
        }

        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(sessionSchema);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        System.out.println(url);
        System.out.println("Input: " + sessionSchema);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId}/sessions/{sessionId} (PUT)
    protected CloseableHttpResponse updateSession(String userId, String projectId, String sessionId, String startTime, String endTime, String counter) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId;
        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader("accept", "application/json");
        String sessionSchema = null;
        sessionSchema = getValidSessionSchemaTypeString(sessionId, startTime, endTime, counter);
        StringEntity input = new StringEntity(sessionSchema);

        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse updateSession(String userId, String projectId, String sessionId, String sessionSchema) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId;
        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(sessionSchema);

        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //Endpoint: /users/{userId}/projects/{projectId}/report (GET)
    protected CloseableHttpResponse getReport(String userId, String projectId, String from, String to, String includeCompletedPomodoros, String includeTotalHoursWorkedOnProject) throws IOException {
        String url = baseUrl + "/users/" + userId + "/projects/" + projectId + "/report";
        String query = "?from=" + from + "&" +
                       "to=" + to + "&" +
                       "includeCompletedPomodoros=" + includeCompletedPomodoros + "&" +
                       "includeTotalHoursWorkedOnProject=" + includeTotalHoursWorkedOnProject;

        System.out.println(url + query);
        System.out.println();

        HttpGet httpRequest = new HttpGet(url + query);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

/*
    private CloseableHttpResponse createContact(String firstname, String familyname, String phonenumber, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/api/contacts");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                "\"familyname\":\"" + familyname + "\"," +
                "\"phonenumber\":\"" + phonenumber + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateContact(String id, String firstname, String familyname, String phonenumber, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/api/contacts/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstname\":\"" + firstname + "\"," +
                "\"familyname\":\"" + familyname + "\"," +
                "\"phonenumber\":\"" + phonenumber + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getContact(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/api/contacts/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllContacts() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/api/contacts");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteContact(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/api/contacts/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse deleteContacts() throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/api/contacts");
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
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }
*/
}
