package edu.gatech.cs6301.Mobile1;

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

public class Users extends PTTBackendTests {

    @Test
    // Purpose: Tests GET /users when users exist
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String id = getIdFromResponse(response);
            response.close();

            response = getAllUsers();

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

            String expectedJson = "[{\"id\":" + id
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}]";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests GET /users when no users exist
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
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

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            String expectedJson = "[]";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests POST /users when user already exists
    public void pttTest3() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            response.close();

            response = createUser("John", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            if (status != 409) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests POST /users when user does not already exist
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            String id = getIdFromStringResponse(strResponse);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests POST with a bad request
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try {
            HttpPost httpRequest = new HttpPost(baseUrl + "/users");
            httpRequest.addHeader("accept", "application/json");

            CloseableHttpResponse response = httpclient.execute(httpRequest);

            int status = response.getStatusLine().getStatusCode();
            if (status != 400) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }

    }

}
