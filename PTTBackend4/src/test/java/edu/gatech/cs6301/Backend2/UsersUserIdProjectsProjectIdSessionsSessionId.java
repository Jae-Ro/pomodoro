package edu.gatech.cs6301.Backend2;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.MessageFormat;
import static org.junit.Assert.*;

public class UsersUserIdProjectsProjectIdSessionsSessionId extends PTTBackendTestsBase {

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

    // Purpose: PUT - try with session ID greater than int64
    @Test
    public void pttTest1() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            String sessionIdGreaterThanInt64 = "123456789123456789123456";
            CloseableHttpResponse response = updateSession(httpclient, existingUserId, existingProjectId, sessionIdGreaterThanInt64, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(500, status);
        }
    }

    // Purpose: PUT - try with session ID given as an alphanumeric string
    @Test
    public void pttTest2() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            String sessionIdAlphaNumeric = "1234asd156h";
            CloseableHttpResponse response = updateSession(httpclient, existingUserId, existingProjectId, sessionIdAlphaNumeric, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);
        }
    }

    // Purpose: PUT - Update session and return the updated object
    @Test
    public void pttTest3() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            // First create the session so we can try updating this session after
            CloseableHttpResponse response = createSession(httpclient, existingUserId, existingProjectId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(201, status);

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            Long id = getIdFromStringResponse(strResponse);
            String expectedJson = "{\n" +
                    "\"counter\": 2,\n" +
                    "\"startTime\": \"2019-02-18T20:00Z\",\n" +
                    "\"endTime\": \"2019-02-18T21:00Z\",\n" +
                    "\"id\": "+ id +"\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            startTime = "2020-02-18T20:00Z";
            endTime = "2020-02-18T21:00Z";
            counter = 3L;
            response = updateSession(httpclient, existingUserId, existingProjectId, id, startTime, endTime, counter);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            expectedJson = "{\n" +
                    "\"counter\": 3,\n" +
                    "\"startTime\": \"2020-02-18T20:00Z\",\n" +
                    "\"endTime\": \"2020-02-18T21:00Z\",\n" +
                    "\"id\": "+ id +"\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: PUT error check - Try updating a session with invalid session object.
    @Test
    public void pttTest4() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;

            // First create the session so we can try updating this session after
            CloseableHttpResponse response = createSession(httpclient, existingUserId, existingProjectId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(201, status);

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            Long id = getIdFromStringResponse(strResponse);
            String expectedJson = "{\n" +
                    "\"counter\": 2,\n" +
                    "\"startTime\": \"2019-02-18T20:00Z\",\n" +
                    "\"endTime\": \"2019-02-18T21:00Z\",\n" +
                    "\"id\": "+ id +"\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            startTime = "invalidSessionObjectStartDateValue";
            endTime = "2020-02-18T21:00Z";
            counter = 3L;
            response = updateSession(httpclient, existingUserId, existingProjectId, id, startTime, endTime, counter);
            response.close();

            status = response.getStatusLine().getStatusCode();
            assertEquals(400, status);
        }
    }

    // Purpose: PUT error check - Update session on sessionId which exists but not owned by projectId
    @Test
    public void pttTest5() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long newProjectId = createAndGetProjectId(existingUserId);
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            // First create the session so we can try updating this session after
            CloseableHttpResponse response = createSession(httpclient, existingUserId, newProjectId, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(201, status);
            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            Long sessionIdOwnedByNewProjectId = getIdFromStringResponse(strResponse);

            // Now try updating this sessionId object with existingProjectId which does not own it
            startTime = "2020-02-18T20:00Z";
            endTime = "2020-02-18T21:00Z";
            counter = 3L;
            response = updateSession(httpclient, existingUserId, existingProjectId, sessionIdOwnedByNewProjectId, startTime, endTime, counter);

            status = response.getStatusLine().getStatusCode();
            // expect 404 because, sessionIdOwnedByNewProjectId is not found in the list of sessions for existingProjectId
            assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        }

    }



    // Purpose: PUT error check - Update session on sessionId which does not exist
    @Test
    public void pttTest6() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long counter = 2L;
            Long sessionIdDoesNotExist = System.currentTimeMillis();
            CloseableHttpResponse response = updateSession(httpclient, existingUserId, existingProjectId, sessionIdDoesNotExist, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);
        }
    }
}
