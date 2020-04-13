package edu.gatech.cs6301.Backend2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;

import org.apache.http.HttpHeaders;
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

import static org.junit.Assert.assertEquals;

public class UsersUserIdProjectsProjectId extends PTTBackendTestsBase{

    private static Long existingUserId;
    private static Long existingProjectId;

    @Before
    public void runBefore() throws Exception {
        try {
            existingUserId = createAndGetUserId();
            existingProjectId = createAndGetProjectId(existingUserId);
            System.out.println(MessageFormat.format("existingUserId - {0}, existingProjectId - {1}", existingUserId, existingProjectId));
        } catch (Exception e) {
            System.out.println("Found error : " + e.getMessage());
            e.printStackTrace();
        }

    }

    @After
    public void runAfter() throws Exception {
        // finally delete the user to prevent it from interfering with other tests
        deleteUser(existingUserId);
    }

    // Purpose: GET info from an valid projectId -- alphanumeric string
    @Test
    public void pttTest1() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String alphanumeric = "abcdef" + System.currentTimeMillis();
            HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + existingUserId + "/projects/" + alphanumeric + "");
            httpRequest.addHeader("accept", "application/json");

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: GET info from an invalid projectId -- number larger that max int64
    @Test
    public void pttTest2() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + existingUserId + "/projects/" + "9223372036854775807");
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");
            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);
        }
    }

    // Purpose: PUT a valid project to an existing user
    @Test
    public void pttTest3() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()){

            String beforeProjectName = "beforeProj"+ System.currentTimeMillis();
            String afterProjectName = "AfterProj"+ System.currentTimeMillis();
            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, beforeProjectName);
            Long ProjectId = getIdFromResponse(response);
            response.close();

            response = updateProject(httpclient, existingUserId, ProjectId, afterProjectName);

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

            String expectedJson = "{\"projectname\":\"" + afterProjectName + "\",\"id\":" + ProjectId + "}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }

    }

    // Purpose: PUT an invalid project to an existing user -- empty projectname
    @Test
    public void pttTest4() throws Exception {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()){

            String beforeProjectName = "beforeProj"+ System.currentTimeMillis();
            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, beforeProjectName);
            Long ProjectId = getIdFromResponse(response);
            response.close();

            response = updateProjectInvalidInput(httpclient, existingUserId, ProjectId);

            int status = response.getStatusLine().getStatusCode();
            // expect 400 because, we tried to PUT with an invalid request body which does not contain projectName
            assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: GET info from valid projectId and existing userId
    @Test
    public void pttTest5() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String projectName = "newProj5"+ System.currentTimeMillis();
            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, projectName);
            Long newProjectId = getIdFromResponse(response);
            response.close();

            response = getProjectById(httpclient, existingUserId, newProjectId);

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

            String expectedJson = "{\"projectname\":\"" + projectName + "\",\"id\":" + newProjectId + "}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }

    }

//    // Purpose: DELETE existing project from an existing user
    @Test
    public void pttTest6() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            String tempProjectName = "tempProj"+ System.currentTimeMillis();
            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, tempProjectName);
            Long tempProjectId = getIdFromResponse(response);
            response.close();

            response = deleteProjectById(httpclient, existingUserId, tempProjectId);

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

            String expectedJson = "{\"projectname\":\"" + tempProjectName + "\",\"id\":" + tempProjectId + "}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }

    }

    // Purpose: DELETE non-existing project from an existing user
    @Test
    public void pttTest7() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            Long dummyProjectId = System.currentTimeMillis();

            CloseableHttpResponse response = deleteProjectById(httpclient, existingUserId, dummyProjectId);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }

    }

    // Purpose: PUT a valid project to a non-existing user
    @Test
    public void pttTest8() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()){
            Long missingUserId = System.currentTimeMillis();

            String afterProjectName = "AfterProj"+ System.currentTimeMillis();


            CloseableHttpResponse response = updateProject(httpclient, missingUserId, existingProjectId, afterProjectName);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: GET a project from a non-existing user
    @Test
    public void pttTest9() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long missingUserId = System.currentTimeMillis(); // making sure the ID is not present

            CloseableHttpResponse response = getProjectById(httpclient, missingUserId, existingProjectId);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: DELETE a project from a non-existing user
    @Test
    public void pttTest10() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long missingUserId = System.currentTimeMillis(); // making sure the ID is not present

            CloseableHttpResponse response = deleteProjectById(httpclient, missingUserId, existingProjectId);

            int status = response.getStatusLine().getStatusCode();
            assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }

    }

    private Long getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return getIdFromStringResponse(strResponse);
    }

}
