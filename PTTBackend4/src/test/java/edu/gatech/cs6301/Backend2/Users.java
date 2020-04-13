package edu.gatech.cs6301.Backend2;

import java.io.IOException;
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

public class Users extends PTTBackendTestsBase{

    //Purpose: Test that a GET request fetches all existing users.
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        Long id1 = createAndGetUserId("sx", "string2", "asdf");
        Long id2 = createAndGetUserId("sy", "string2", "asdf2");

        String expectedJson = "[\n" +
                "    {\n" +
                "        \"id\":" + id1 + ",\n" +
                "        \"firstName\": \"string\",\n" +
                "        \"lastName\": \"string\",\n" +
                "        \"email\": \"adsf\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":" + id2 + ",\n" +
                "        \"firstName\": \"string2\",\n" +
                "        \"lastName\": \"string2\",\n" +
                "        \"email\": \"adsf2\"\n" +
                "    },\n" +
                "]";

        CloseableHttpResponse response = getAllUsers();

        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        if (status == 200) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        // strResponse = EntityUtils.toString(entity);
        Assert.assertEquals(status, 200);

        // System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
        // JSONAssert.assertEquals(expectedJson,strResponse, false);
        // EntityUtils.consume(response.getEntity());
        // response.close();


    }

    //Purpose: Test the POST method with a valid user object.
    @Test
    public void pttTest2() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpRequest = new HttpPost(baseUrl+ "/users");
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
            String firstName = "newuser";
            String lastName = "newuser";
            String email = "newuser@gmail.com";
            StringEntity user = createUserPOST(firstName, lastName, email);
            httpRequest.setEntity(user);

            CloseableHttpResponse response = httpclient.execute(httpRequest);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Long id = getIdFromStringResponse(strResponse);
            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"email\":\"" + email + "\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);

            // finally delete the user to prevent it from interfering with other tests
            deleteUser(id);

            EntityUtils.consume(response.getEntity());
            response.close();

        }
    }

    //Purpose: Test a POST method with an invalid User object.
    @Test
    public void pttTest3() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpRequest = new HttpPost(baseUrl+ "/users");
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
            String firstName = "dummy";
            String lastName = "dummy";
            String email = "dummy@gmail.com";

            StringEntity user = null;
            try {
                user = new StringEntity("{" +
                        "\"id\":\"\"," +
                        "\"firstName\":\"" + firstName + "\"," +
                        "\"lastName\":\"" + lastName + "\"," +
                        "\"INVALID FIELD\":\"" + email + "\"}");

            } catch (Exception e) {
                e.printStackTrace();
            }

            httpRequest.setEntity(user);
            CloseableHttpResponse response = httpclient.execute(httpRequest);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 400);

            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }
}
