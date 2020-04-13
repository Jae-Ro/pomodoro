package edu.gatech.cs6301.DevOps34;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
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

public class Users_userId {
	
	/* Adding the Backend testing server for DevOps34 Team */
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
  
	/****************************** /users ************************************/

    //Get user by ID test
    @Test
    public void getUserbyIDTest() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response = addUser("Get", "User", "gu@gatech.edu");
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

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"Get\",\"lastName\":\"User\",\"email\":\"gu@gatech.edu\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());

            response = deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void getUserByIdWithInvalidIdTest() throws Exception {
        try {
            deleteUsers();
            CloseableHttpResponse response =
                    addUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            // Perform GET /users/{userId} and ensure that the user is returned correctly.
            response = getUser(id + id + "12345");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);

            deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }
    @Test
    public void updateUserWithInvalidUserIdTest() throws Exception {
        try {
            deleteUsers();

            // Adding a user to the PTT

            CloseableHttpResponse response =
                    addUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            // Perform PUT /users/{userId} with Invalid UserId and ensure that the request is cleanly handled.

            response = updateUser(id + id + "12345", "Tom", "James", "tom@james.org");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);

            deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void updateUserWithBadUserIdTest() throws Exception {
        try {
            deleteUsers();

            // Adding a user to the PTT

            CloseableHttpResponse response =
                    addUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            // Perform PUT /users/{userId} with Invalid UserId and ensure that the request is cleanly handled.

            response = updateUser("BAD_USER_ID", "Tom", "James", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);

            deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void deleteUserWithInvalidIdTest() throws Exception {
        try {

            // Adding a user to the PTT
            deleteUsers();

            CloseableHttpResponse response =
                    addUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            /*
             * Perform DELETE /users/{userId} with Invalid UserId and ensure that the server handles it cleanly and
             * returns back a 404.
             */

            response = deleteUser(id + id + "12345");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);

            response.close();
        } finally {
            httpclient.close();
        }
    }
    @Test
    public void deleteUserWithBadUserIdTest() throws Exception {
        try {
            deleteUsers();

            // Adding a user to the PTT

            CloseableHttpResponse response =
                    addUser("John", "Doe", "john@doe.org");
            String id = getIdFromResponse(response);
            response.close();

            /*
             * Perform DELETE /users/{userId} with an Empty UserId ("") to ensure that the server handles it cleanly and
             * returns back a 400.
             */

            response = deleteUser("BAD_USER_ID");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(status, 404);

            response.close();
        } finally {
            httpclient.close();
        }
    }



	/**************************************************************************/
	
	/****************************** /users/{userId} ***************************/
    
	@Test
    public void updateUserTest() throws Exception {

        try {
            CloseableHttpResponse response = addUser("User", "XYZ", "uxyz@gatech.edu");
            String id = getIdFromResponse(response);
            response.close();

            response = updateUser(id, "UpdatedUser", "ABC", "uxyz@gatech.edu");

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

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"UpdatedUser\",\"lastName\":\"ABC\",\"email\":\"uxyz@gatech.edu\"}";
	    	JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());

			response = deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void getUserTest() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response = addUser("Get", "User", "gu@gatech.edu");
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

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"Get\",\"lastName\":\"User\",\"email\":\"gu@gatech.edu\"}";
	    JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
			
			response = deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }


    @Test
    public void deleteUserTest() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = null;

        try {
            CloseableHttpResponse response = addUser("Delete", "User", "du@gatech.edu");
            // EntityUtils.consume(response.getEntity());
            String deleteid = getIdFromResponse(response);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteUser(deleteid);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "{\"id\":" + deleteid + ",\"firstName\":\"Delete\",\"lastName\":\"User\",\"email\":\"du@gatech.edu\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

	/**************************************************************************/

	/************************* "user/" *************************************************/

    private CloseableHttpResponse addUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    
	private CloseableHttpResponse getUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
	
	// This method is not there. 
	/*
    private CloseableHttpResponse deleteUsers() throws IOException {
	HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }
	*/
	
	/***********************************************************************************/
	
	/************************* "user/{userId}" *****************************************/
    // Gets Id from a JSON Object
    private long getIdFromJSONObject(JSONObject object) throws JSONException {
        String id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return Long.parseLong(id);
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

    private CloseableHttpResponse updateUser(String id, String firstName, String lastName, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

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
        // Get all users
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
        EntityUtils.consume(response.getEntity());
        response.close();

        try {
            // Get Ids from the response
            JSONArray jsonUsers = new JSONArray(strResponse);

            // For each item in array, parse the id and issue delete
            for (int i = 0; i < jsonUsers.length(); i++) {
                long deleteID = getIdFromJSONObject(jsonUsers.getJSONObject(i));
                response = deleteUser(Long.toString(deleteID));
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } catch (JSONException je){
            throw new ClientProtocolException("JSON PROBLEM: " + je);
        }

    }


    /***********************************************************************************/

	/* Helper Functions */

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
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

}
