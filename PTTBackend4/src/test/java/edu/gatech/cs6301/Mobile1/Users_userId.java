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

public class Users_userId extends PTTBackendTests {

    @Test
    // Purpose: Tests GET /users/{userId} when user with userId exists
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String id = getIdFromResponse(response);
            response.close();

            response = getUser(id);

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
    // Purpose: Tests GET /users/{usersId} when user with userId does not exist
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getUser("1");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();

        } finally {
            httpclient.close();
        }

    }

    // Comment from Phalguna: They are putting without an email, should be invalid
    @Test
    // Purpose: Tests PUT /users/{usersId} when user with userId exists
    public void pttTest3() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser(id, "Pinhead", "Larry");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { // changed from 200 to 400 by Phalguna
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            // String expectedJson = "{\"id\":" + id
            // +
            // ",\"firstName\":\"Pinhead\",\"lastName\":\"Larry\",\"email\":\"john@doe.org\"}";
            // JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests PUT /users/{userId} when user with userId does not exist
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateUser("1", "Pinhead", "Larry");
            int status = response.getStatusLine().getStatusCode();
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();

        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests PUT /users/{userId} with a bad request
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try {

            // CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // String id = getIdFromResponse(response);
            // response.close();
            String id = "sdf";

            HttpPut httpRequest = new HttpPut(baseUrl + "/users/sd" + id);
            httpRequest.addHeader("accept", "application/json");

            CloseableHttpResponse response = httpclient.execute(httpRequest);

            int status = response.getStatusLine().getStatusCode();
            if (status != 404) { // changed by Phalguna from 400 to 404
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests DELETE /users/{userId} when user with userId exists and has
    // projects
    // Since what happens when a user with projects is deleted depends entirely on
    // the implementation,
    // this test and the following test are the same.
    public void pttTest6() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String id = getIdFromResponse(response);
            response.close();

            response = deleteUser(id);

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

            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getUser(id);

            status = response.getStatusLine().getStatusCode();
            if (status != 404) {
                throw new ClientProtocolException("User not deleted");
            }
        } finally {
            httpclient.close();
        }

    }

    @Test
    // Purpose: Tests DELETE /users/{userId} when user with userId exists and does
    // not have projects
    public void pttTest7() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String id = getIdFromResponse(response);
            response.close();

            response = deleteUser(id);

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

            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getUser(id);

            status = response.getStatusLine().getStatusCode();
            if (status != 404) {
                throw new ClientProtocolException("User not deleted");
            }
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests DELETE /users/{userId} when user with userId does not exist
    public void pttTest8() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = deleteUser("1");

            int status = response.getStatusLine().getStatusCode();
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            response.close();
        } finally {
            httpclient.close();
        }

    }
}
