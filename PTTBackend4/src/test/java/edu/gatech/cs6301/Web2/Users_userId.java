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
import org.json.JSONArray;
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

public class Users_userId {
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

    // Test Case 1 <error>
    // Get_User_ID : Not empty but not in database
    // Purpose: Test if the user id is not empty but doesn't exist in database
    // which would return 404
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse user1_response = createUser("John", "Doe", "johndoe@doe.org");

            CloseableHttpResponse user2_response = createUser("James", "Doe", "Jamesdoe@doe.org");

            HttpEntity user1_entity;
            HttpEntity user2_entity;

            user1_entity = user1_response.getEntity();
            user2_entity = user2_response.getEntity();

            String user1_strResponse = EntityUtils.toString(user1_entity);
            String user2_strResponse = EntityUtils.toString(user2_entity);

            System.out.println("*** String response " + user1_strResponse + " ("
                    + user1_response.getStatusLine().getStatusCode() + ") ***");

            String id1 = getIdFromStringResponse(user1_strResponse);
            String id2 = getIdFromStringResponse(user2_strResponse);
            String missingId = id1 + id2; // making sure the ID is not present
            // String missingId = "xyz" + id1 + id2; // making sure the ID is not present

            CloseableHttpResponse response = getUser(missingId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            System.out.println("*** String response " + response + " (" + status + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();
            user1_response.close();
            user2_response.close();

        } finally {
            httpclient.close();
        }
    }

    // Test Case 2 <single>
    // Delete_User_ID : User_ID exists in database
    // Purpose: Test delete user is working correct
    @Test
    public void pttTest2() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteUsers();
        String expectedJson = null;

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            String deleteid = getIdFromResponse(response);
            response.close();

            HttpEntity entity;
            String strResponse;

            response = deleteUser(deleteid);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
                System.out.println(entity);
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "{\"id\":" + deleteid
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            // EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllUsers();
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "[]";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            // EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }

    }

    // Test Case 3 <error>
    // Delete_User_ID : Not empty but not in database
    // Purpose: Test if the user id is not empty but doesn't exist in database
    // which would return 404
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse user1_response = createUser("John", "Doe", "johndoe@doe.org");

            CloseableHttpResponse user2_response = createUser("James", "Doe", "Jamesdoe@doe.org");

            HttpEntity user1_entity;
            HttpEntity user2_entity;

            user1_entity = user1_response.getEntity();
            user2_entity = user2_response.getEntity();

            String user1_strResponse = EntityUtils.toString(user1_entity);
            String user2_strResponse = EntityUtils.toString(user2_entity);

            System.out.println("*** String response " + user1_strResponse + " ("
                    + user1_response.getStatusLine().getStatusCode() + ") ***");

            String id1 = getIdFromStringResponse(user1_strResponse);
            String id2 = getIdFromStringResponse(user2_strResponse);
            String missingId = id1 + id2; // making sure the ID is not present
            // String missingId = "xyz" + id1 + id2; // making sure the ID is not present

            CloseableHttpResponse response = deleteUser(missingId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            System.out.println("*** String response " + response + " (" + status + ") ***");

            // i dont know why the following line would cause the error
            // " Premature end of Content-Length delimited message body "
            // EntityUtils.consume(response.getEntity());

            response.close();
            user1_response.close();
            user2_response.close();

        } finally {
            httpclient.close();
        }
    }

    // Test Case 4 <error>
    // Delete_User_ID : Empty
    // purpose: Test deleting project with empty id
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {

            String emptyId = ""; // making sure the ID is not present

            CloseableHttpResponse response = deleteUser(emptyId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(405, status); // 404 to 405
            System.out.println("*** String response " + response + " (" + status + ") ***");

            // i dont know why the following line would cause the error
            // " Premature end of Content-Length delimited message body "
            // EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }
    }

    // Test Case 5 <error>
    // Put_First_name : Contains number & special characters
    // Purpose: Test if the modified first name containing special char or numbers
    // which we think is not accepted and should return 400
    // although the backend did not handle this case and return 200, resulting that
    // we failed this testcase
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john_create@doe.org");
            String id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();
            response = updateUser(id, "Tom5", "Doe", "tom@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 200) {
                    entity = response.getEntity();
                    strResponse = EntityUtils.toString(entity);
                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            strResponse = EntityUtils.toString(entity);
            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // Test Case 6 <error>
    // Put_Last_name : Contains numbers & special characters
    // Purpose: Test if the modified last name containing special char or numbers
    // which we think is not accepted and should return 400
    // although the backend did not handle this case and return 200, resulting that
    // we failed this testcase
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john_create@doe.org");
            String id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();
            response = updateUser(id, "Tom", "William#", "tom@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                if (status == 200) {
                    entity = response.getEntity();
                    strResponse = EntityUtils.toString(entity);
                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            strResponse = EntityUtils.toString(entity);
            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // Test Case 7 (Key = 1.0.1.1.1.)
    // Get_User_ID : User_ID exists in database
    // Delete_User_ID : <n/a>
    // Put_First_name : Valid name
    // Put_Last_name : Valid name
    // Put_Email : Empty
    // Purpose: Test if the modified first name and last name are acceptable and
    // the user did not modify the email
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();
            response = updateUser(id, "Tom", "William", "john@doe.org");

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
                    + ",\"firstName\":\"Tom\",\"lastName\":\"William\",\"email\":\"john@doe.org\"}";

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void getContactTest() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john_create@doe.org");
            String id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
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
                    + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john_create@doe.org\"}";

            System.out.println(expectedJson);

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            // CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
            // CloseableHttpResponse deleteresponse = deleteUser(id);
            // deleteresponse.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
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

    private CloseableHttpResponse updateUser(String id, String firstName, String lastName, String email)
            throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
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
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

    public void getAllUsersId() throws Exception {
        CloseableHttpResponse response = getAllUsers();
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String strResponse = EntityUtils.toString(entity);
            System.out.println(strResponse);
            // parsing JSON
            JSONArray result = new JSONArray(strResponse);
            for (int i = 0; i < result.length(); i++) {
                JSONObject object = result.getJSONObject(i);
                String id = null;
                Iterator<String> keyList = object.keys();
                while (keyList.hasNext()) {
                    String key = keyList.next();
                    if (key.equals("id")) {
                        id = object.get(key).toString();
                        System.out.println(id);
                    }
                }
            }

        }
        response.close();
    }

    // private CloseableHttpResponse deleteUser(int id) throws IOException {
    // HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
    // httpDelete.addHeader("accept", "application/json");
    //
    // System.out.println("*** Executing request " + httpDelete.getRequestLine() +
    // "***");
    // CloseableHttpResponse response = httpclient.execute(httpDelete);
    // System.out.println("*** Raw response " + response + "***");
    // // EntityUtils.consume(response.getEntity());
    // // response.close();
    // return response;
    // }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private void deleteUsers() throws IOException {
        try {
            CloseableHttpResponse response = getAllUsers();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String strResponse = EntityUtils.toString(entity);
                System.out.println(strResponse);
                // parsing JSON
                JSONArray result = new JSONArray(strResponse);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = result.getJSONObject(i);
                    String id = null;
                    Iterator<String> keyList = object.keys();
                    while (keyList.hasNext()) {
                        String key = keyList.next();
                        if (key.equals("id")) {
                            id = object.get(key).toString();
                            System.out.println(id);
                            response = deleteUser(id);
                            response.close();
                        }
                    }
                }
            }
            response.close();
        } catch (Exception e) {
            System.out.print("Can not delete all users");
        }
    }
}
