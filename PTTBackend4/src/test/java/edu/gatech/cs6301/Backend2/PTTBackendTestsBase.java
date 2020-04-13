package edu.gatech.cs6301.Backend2;

import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;

import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class PTTBackendTestsBase {

    protected String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    //protected CloseableHttpClient httpclient;
    private boolean setupdone;

    @Before
    public void runBefore() throws Exception {
        if (!setupdone) {
            System.out.println("*** SETTING UP TESTS ***");
            // Increase max total connection to 100
            cm.setMaxTotal(100);
            // Increase default max connection per route to 20
            cm.setDefaultMaxPerRoute(10);
            // Increase max connections for localhost:80 to 50
            HttpHost localhost = new HttpHost("locahost", 8080);
            cm.setMaxPerRoute(new HttpRoute(localhost), 10);
            //httpclient = HttpClients.custom().setConnectionManager(cm).build();
            setupdone = true;
        }
        System.out.println("*** STARTING TEST ***");
    }

    @After
    public void runAfter() throws Exception {
        System.out.println("*** ENDING TEST ***");
    }

    // *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

    // Create StringEntity for a user POST request and set its content type as a json
    public StringEntity createUserPOST(String firstName, String lastName, String email){
        StringEntity user = null;
        try {
            user = new StringEntity("{" +
                    "\"id\":\"\"," +
                    "\"firstName\":\"" + firstName + "\"," +
                    "\"lastName\":\"" + lastName + "\"," +
                    "\"email\":\"" + email + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Update a user
    protected CloseableHttpResponse updateUser(CloseableHttpClient httpclient, Long id, String firstName, String lastName, String email)throws IOException  {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id + "");
        httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        StringEntity input = new StringEntity("{" +
                "\"id\":\"\"," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");

        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;

    }

    // Create StringEntity for a project POST request and set its content type as a json
    public StringEntity createProjectPOST(String projectname){
        StringEntity project = null;
        try {
            project = new StringEntity("{" +
                    "\"id\":\"\"," +
                    "\"projectname\":\"" + projectname + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

    protected Long createAndGetUserId(CloseableHttpClient httpclient, String firstName, String lastName, String email) throws Exception {
        HttpPost httpRequest = new HttpPost(baseUrl+ "/users");
        httpRequest.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        StringEntity user = createUserPOST(firstName, lastName, email);
        httpRequest.setEntity(user);

        CloseableHttpResponse response = httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);

        Long id = getIdFromStringResponse(strResponse);

        EntityUtils.consume(response.getEntity());
        response.close();
        return id;
    }

    protected Long createAndGetUserId(String firstName, String lastName, String email) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            return createAndGetUserId(httpclient, firstName, lastName, email);
        }
    }

    protected Long createAndGetUserId() throws Exception {
        String email = "user"+System.currentTimeMillis()+"@doe.org";
        String firstName = "John" + System.currentTimeMillis();
        String lastName = "Doe" + System.currentTimeMillis();
        return createAndGetUserId(firstName, lastName, email);
    }

    protected Long createAndGetProjectId(Long userId) throws Exception {

        return createAndGetProjectId(userId, "dummy_proj_name2" + System.currentTimeMillis());
    }

    protected Long createAndGetProjectId(Long userId, String projectName) throws Exception {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
            httpRequest.addHeader("accept", "application/json");
            StringEntity input = createProjectPOST(projectName);
            input.setContentType("application/json");
            httpRequest.setEntity(input);

            CloseableHttpResponse response = httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);

            Long id = getIdFromStringResponse(strResponse);

            EntityUtils.consume(response.getEntity());
            response.close();
            return id;

        }
    }

    // Get a specific user by their id
    protected CloseableHttpResponse getUserById(Long id) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }
    }

    // Retrieve all users stored in the backend
    protected CloseableHttpResponse getAllUsers() throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpRequest = new HttpGet(baseUrl + "/users");
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }

    }

    // Delete a specific user
    protected CloseableHttpResponse deleteUser(Long id) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id + "");
            httpDelete.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpDelete);
            System.out.println("*** Raw response " + response + "***");
            return response;
        }

    }

    // Delete all users
    protected void deleteAllUsers() throws IOException {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()){

            HttpGet httpRequest = new HttpGet(baseUrl + "/users");
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);

            ArrayList<Long> ids = getIdsFromStringResponse(EntityUtils.toString(response.getEntity()));
            for (Long id: ids) {
                HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id + "");
                httpDelete.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
                httpclient.execute(httpDelete);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // GET all projects of a specific user
    protected CloseableHttpResponse getUserProjects(CloseableHttpClient httpclient, Long userId) throws IOException {

        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;

    }

    // GET project by projectId
    protected CloseableHttpResponse getProjectById(CloseableHttpClient httpclient, Long userId, Long projectId) throws IOException {

        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;

    }

    // POST a project to a specific user
    protected CloseableHttpResponse createUserProject(Long userId, String projectName) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            return createUserProject(httpclient, userId, projectName);
        }
    }

    protected CloseableHttpResponse createUserProject(CloseableHttpClient httpclient, Long userId, String projectName)  throws Exception {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects", userId);
        HttpPost httpRequest = new HttpPost(baseUrl + methodEndPoint);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    // Delete a specific project
    protected CloseableHttpResponse deleteProjectById(CloseableHttpClient httpclient, Long userId, Long projectId) throws Exception {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects/{1}", userId, projectId);
        HttpDelete httpDelete = new HttpDelete(baseUrl + methodEndPoint);
        httpDelete.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        return response;

    }

    protected CloseableHttpResponse deleteProjectById(Long userId, Long projectId) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            return deleteProjectById(httpclient, userId, projectId);
        }
    }

    // Update a project
    protected CloseableHttpResponse updateProject(CloseableHttpClient httpclient, Long userId, Long projectId, String projectName)  throws Exception {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects/{1}", userId, projectId);
        HttpPut httpRequest = new HttpPut(baseUrl + methodEndPoint);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse updateProjectInvalidInput(CloseableHttpClient httpclient, Long userId, Long projectId)  throws Exception {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects/{1}", userId, projectId);
        HttpPut httpRequest = new HttpPut(baseUrl + methodEndPoint);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse createSession(CloseableHttpClient httpclient, Long userId, Long projectId, String startTime, String endTime, Long counter)  throws Exception {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects/{1}/sessions", userId, projectId);
        HttpPost httpRequest = new HttpPost(baseUrl + methodEndPoint);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{" +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse updateSession(CloseableHttpClient httpclient, Long userId, Long projectId, Long sessionId, String startTime, String endTime, Long counter)  throws Exception {

        return updateSession(httpclient, userId, projectId, sessionId.toString(), startTime, endTime, counter);
    }

    protected CloseableHttpResponse updateSession(CloseableHttpClient httpclient, Long userId, Long projectId, String sessionId, String startTime, String endTime, Long counter)  throws Exception {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects/{1}/sessions/{2}", userId, projectId, sessionId);
        HttpPut httpRequest = new HttpPut(baseUrl + methodEndPoint);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{" +
                "\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    // Extract the long id from JSON
    protected Long getIdFromStringResponse(String strResponse) throws JSONException {
        return getIdFromStringResponse(strResponse, "id");
    }

    protected Long getIdFromStringResponse(String strResponse, String idName) throws JSONException {
        System.out.println("strResponse------------------" + strResponse);
        JSONObject object = new JSONObject(strResponse);
        Long id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals(idName)) {
                id = Long.valueOf(object.get(key).toString());
            }
        }
        return id;
    }

    protected ArrayList<Long> getIdsFromStringResponse(String strResponse) throws JSONException {
        System.out.println("strResponse------------------" + strResponse);
        JSONObject object = new JSONObject(strResponse);
        Long id = null;
        ArrayList<Long> ids = new ArrayList<>();
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                ids.add(Long.valueOf(object.get(key).toString()));
            }
        }
        return ids;
    }

    //Retrieve a Report
    protected CloseableHttpResponse getReport(CloseableHttpClient httpclient, String from, String to, Long userId, Long projectId, String includeCompletedPomodoros, String includeTotalHoursWorkedOnProject) throws IOException, URISyntaxException {

        String uri = baseUrl + "/users/" + userId + "/projects/" + projectId + "/report";

        URIBuilder builder = new URIBuilder(uri);
        builder.setParameter("from", from).setParameter("to", to)
                .setParameter("includeCompletedPomodoros", includeCompletedPomodoros)
                .setParameter("includeTotalHoursWorkedOnProject", (includeTotalHoursWorkedOnProject));


        System.out.println("*** uri built-------- " + builder.build() + "***");
        HttpGet httpRequest = new HttpGet(builder.build());
        httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    protected CloseableHttpResponse getReport(String from, String to, Long userId, Long projectId, String includeCompletedPomodoros, String includeTotalHoursWorkedOnProject) throws IOException {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            if (!includeCompletedPomodoros.equals("true") && !includeCompletedPomodoros.equals("false"))
                throw new HttpResponseException(400, "includeCompletedPomodoros must be a boolean!");

            if (!includeTotalHoursWorkedOnProject.equals("true") && !includeTotalHoursWorkedOnProject.equals("false"))
                throw new HttpResponseException(400, "includeTotalHoursWorkedOnProject must be a boolean!");

            return getReport(httpclient, from, to, userId, projectId, includeCompletedPomodoros, includeTotalHoursWorkedOnProject);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}