package edu.gatech.cs6301.Web2;

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

public class Users {

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
            HttpHost localhost = new HttpHost("locahost", 8080);
            cm.setMaxPerRoute(new HttpRoute(localhost), 10);
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
    // Test Case 1 <error>
    // Post_First name : Contains numbers or special characters
    // Purpose : To test if the user can have a number/special character in their
    // first name
    public void pttTest1() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John5", "Doe", "john1@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                if (status == 201) {
                    entity = response.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    response.close();
                    deleteresponse.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(201, status);

            EntityUtils.consume(response.getEntity());

            response.close();
        } finally {

            httpclient.close();
        }
    }

    @Test
    // Test Case 2 <error>
    // Post_First name : Empty
    // Purpose : To test if the user can have an empty first name
    public void pttTest2() throws Exception {

        try {
            CloseableHttpResponse response = createUser("", "Doe", "john2475@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 201) {
                    entity = response.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    response.close();
                    deleteresponse.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Test Case 3 <error>
    // Post_Last name : Contains numbers or special characters
    // Purpose : To test if the user can have a number/special character in their
    // last name
    public void pttTest3() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe%", "john3@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 201) {
                    entity = response.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    response.close();
                    deleteresponse.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {

            httpclient.close();
        }
    }

    @Test
    // Test Case 4 <error>
    // Post_Last name : Empty
    // Purpose : To test if the user can have an empty last name
    public void pttTest4() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "", "john4@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 201) {
                    entity = response.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    response.close();
                    deleteresponse.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {

            httpclient.close();
        }
    }

    @Test
    // Test Case 5 <error>
    // Post_Email : Invalid email address
    // Purpose : To test if the user can have a incorrectly formatted email address
    public void pttTest5() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john5@doeorg");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 201) {
                    entity = response.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    response.close();
                    deleteresponse.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Test Case 6 <error>
    // Post_Email : Is not unique
    // Purpose : To test if the user can be inputted into the database with an email
    // that already exists in the database
    public void pttTest6() throws Exception {

        try {
            CloseableHttpResponse initialUser = createUser("John", "Doe", "john6@doe.org");

            CloseableHttpResponse response = createUser("James", "Doe", "john6@doe.org");

            int status = response.getStatusLine().getStatusCode();
            int initialStatus = response.getStatusLine().getStatusCode();

            HttpEntity entity;
            HttpEntity initialEntity;

            if (status == 409) {
                entity = response.getEntity();
                initialEntity = initialUser.getEntity();
            } else {
                if (status == 201 || initialStatus == 201) {
                    entity = response.getEntity();
                    initialEntity = initialUser.getEntity();

                    String strResponse = EntityUtils.toString(entity);
                    String initResponse = EntityUtils.toString(initialEntity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    String Deleteinitid = getIdFromStringResponse(initResponse);

                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    CloseableHttpResponse deleteInitresponse = deleteUser(Integer.valueOf(Deleteinitid));
                    response.close();
                    deleteresponse.close();
                    deleteInitresponse.close();

                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            String initResponse = EntityUtils.toString(initialEntity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(409, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Test Case 7 <error>
    // Post_Email : Empty
    // Purpose : To test if the user can have an empty email address
    public void pttTest7() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 201) {
                    entity = response.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    String Deleteid = getIdFromStringResponse(strResponse);
                    CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                    response.close();
                    deleteresponse.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /*
     * Test Case 8 (Key = 1.1.1.) Post_First name : Valid name Post_Last name :
     * Valid name Post_Email : Valid email address
     */
    // Purpose : To test if the user can be created if all fields are valid
    @Test
    public void pttTest8() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john90@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);
            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john90@doe.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            if (status == 201) {
                String Deleteid = getIdFromStringResponse(strResponse);
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                response.close();
                deleteresponse.close();
            }
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse deleteUser(int id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," + "\"lastName\":\"" + lastName
                + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
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
