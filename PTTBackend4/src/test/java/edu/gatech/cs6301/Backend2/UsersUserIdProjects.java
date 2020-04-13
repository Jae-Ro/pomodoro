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

public class UsersUserIdProjects extends PTTBackendTestsBase {

    private static Long existingUserId;

    @Before
    public void runBefore() throws Exception {
        try {
            existingUserId = createAndGetUserId();
            System.out.println(MessageFormat.format("existingUserId - {0}", existingUserId));
        } catch (Exception e) {
            System.out.println("FOund error : " + e.getMessage());
            e.printStackTrace();
        }

    }

    @After
    public void runAfter() throws Exception {
        // finally delete the user to prevent it from interfering with other tests
        deleteUser(existingUserId);
    }

    // Purpose: GET all projects of an existing user
    @Test
    public void pttTest1() throws Exception {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String projectName = "dummyProj"+ System.currentTimeMillis();
            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, projectName);
            Long projectId = getIdFromResponse(response);
            response.close();

            response = getUserProjects(httpclient, existingUserId);

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

            String expectedJson = "[{\"projectname\":\"" + projectName + "\",\"id\":" + projectId + "}]";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: GET projects of a non-existing user
    @Test
    public void pttTest2() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long missingUserId = System.currentTimeMillis(); // making sure the ID is not present

            CloseableHttpResponse response = getUserProjects(httpclient, missingUserId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: POST a valid project to an existing user
    @Test
    public void pttTest3() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String projectName = "validProj"+ System.currentTimeMillis();
            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, projectName);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Long projectId = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"projectname\":\"" + projectName + "\",\"id\":" + projectId + "}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: POST a invalid project to an existing user
    @Test
    public void pttTest4() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            CloseableHttpResponse response = createUserProject(httpclient, existingUserId, "");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    // Purpose: POST a valid project to a non-existing user
    @Test
    public void pttTest5() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Long missingUserId = System.currentTimeMillis(); // making sure the ID is not present
            String projectName = "coolProj"+ System.currentTimeMillis();

            CloseableHttpResponse response = createUserProject(httpclient, missingUserId, projectName);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

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