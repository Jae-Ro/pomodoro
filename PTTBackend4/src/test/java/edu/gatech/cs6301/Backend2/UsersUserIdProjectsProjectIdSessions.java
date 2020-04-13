package edu.gatech.cs6301.Backend2;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.*;

import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.text.MessageFormat;

import static org.junit.Assert.*;

public class UsersUserIdProjectsProjectIdSessions extends PTTBackendTestsBase {

    private static Long existingUserId;
    private static Long existingProjectId;

    @Before
    public void runBefore() throws Exception {
        try {
            existingUserId = createAndGetUserId();
            existingProjectId = createAndGetProjectId(existingUserId);

            System.out.println(MessageFormat.format("existingUserId - {0}, existingProjectId - {1}", existingUserId, existingProjectId));
        } catch (Exception e) {
            System.out.println("FOund error : " + e.getMessage());
            e.printStackTrace();
        }

    }

    @After
    public void runAfter() throws Exception {
        // finally delete the user and project as a cleanup
        deleteUser(existingUserId);
        deleteProjectById(existingUserId, existingProjectId);
    }

    // Purpose: POST - Add a new session and return the newly created object
    @Test
    public void pttTest1() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            CloseableHttpResponse response = createSession(httpclient, existingUserId, existingProjectId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");


            Long id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\n" +
                "\"counter\": 2,\n" +
                "\"startTime\": \"2019-02-18T20:00Z\",\n" +
                "\"endTime\": \"2019-02-18T21:00Z\",\n" +
                "\"id\": "+ id +"\n" +
                "}";
            System.out.println("*** expectedJson " + expectedJson + "***");
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: POST - Try adding a new session with invalid Request Body (endTime < startTime)
    @Test
    public void pttTest2() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "wrongTimeFormat";
            String endTime = "2019-02-18T20:00Z";
            Long counter = 2L;
            CloseableHttpResponse response = createSession(httpclient, existingUserId, existingProjectId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(status, 400);
        }

    }

    // Purpose: GET - Return all sessions for a given project
    // IMPORTANT : This test case fails for given backend because, Sessions GET endpoint is not implemented last time.
    @Test
    public void pttTest3() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long newProjectId = createAndGetProjectId(existingUserId, "proj" + System.currentTimeMillis());

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            CloseableHttpResponse response = createSession(httpclient, existingUserId, newProjectId, startTime, endTime, counter);
            Long id1 = getIdFromResponse(response);
            response.close();

            String startTime2 = "2020-01-18T20:00Z";
            String endTime2 = "2020-01-18T21:00Z";
            Long counter2 = 2L;
            response = createSession(httpclient, existingUserId, newProjectId, startTime, endTime, counter);
            Long id2 = getIdFromResponse(response);
            response.close();

            response = getAllSessions(httpclient, existingUserId, newProjectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            assertTrue(strResponse.contains(id1.toString()) && strResponse.contains(id2.toString()));
            EntityUtils.consume(response.getEntity());
            response.close();
            deleteProjectById(existingUserId, newProjectId);
        }
    }

    // Purpose: POST - Try adding a new session for a non-existing Project Id
    @Test
    public void pttTest4() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {


            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            Long nonExistingProjectId = System.currentTimeMillis();
            CloseableHttpResponse response = createSession(httpclient, existingUserId, nonExistingProjectId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: GET - Try getting all sessions for a non-existing Project Id
    @Test
    public void pttTest5() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long nonExistingProjectId = System.currentTimeMillis();

            CloseableHttpResponse response = getAllSessions(httpclient, existingUserId, nonExistingProjectId);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    private CloseableHttpResponse getAllSessions(CloseableHttpClient httpclient, Long userId, Long projectId) throws IOException {

        String methodEndPoint = MessageFormat.format("/users/{0}/projects/{1}/sessions", userId, projectId);
        HttpGet httpRequest = new HttpGet(baseUrl + methodEndPoint);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;


    }

    private Long getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return getIdFromStringResponse(strResponse);
    }

}
