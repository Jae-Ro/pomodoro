package edu.gatech.cs6301.DevOps34;

import java.io.IOException;
import java.util.Iterator;
import java.util.*;

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

public class Users {
	
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

    @Test
    public void getUsersTest200() throws Exception {
        httpclient = HttpClients.createDefault();
        String id = null;
        String expectedJson = "[]";

        try {
            CloseableHttpResponse response = addUser("Some", "User", "su@gatech.edu");
            //id = getIdFromResponse(response);
            response.close();

            response = getUsers();

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

	    	JSONAssert.assertNotEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }




    @Test
    public void addUserTest() throws Exception {
	/* NOTE: This test will generally fail as the user should already exist, it should give an user exists conflict reponse otherwise */	

        try {
            CloseableHttpResponse response = addUser("Test", "User", "tu@gatech.edu");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else if (status == 409) {
                throw new ClientProtocolException("Resource Conflict. (Expected Behavior) status: " + status);
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\"tu@gatech.edu\"}";
			System.out.println(expectedJson);
			System.out.println(strResponse);
			JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
        
			response = deleteUser(id);
			response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void addUserTest400() throws Exception {
        /* NOTE: This test will generally fail as the user should already exist, it should give an user exists conflict reponse otherwise */

        try {
            CloseableHttpResponse response = addUser("Test", "User", "tu@gatech.edu");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else if (status == 400) {
                throw new ClientProtocolException("Bad request. (Expected Behavior) status: " + status);
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + ",\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\"tu@gatech.edu\"}";
            System.out.println(expectedJson);
            System.out.println(strResponse);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());

            response = deleteUser(id);
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




	/************** Additional test cases, need to update these ***************/

    @Test
    public void getMissingUserTest() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = addUser("Johnn", "Doed", "john50@doe.org");
            // EntityUtils.consume(response.getEntity());
            String id1 = getIdFromResponse(response);
            response.close();

            response = addUser("Jane", "Wall", "jane60@wall.com");
            // EntityUtils.consume(response.getEntity());
            String id2 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz" + id1 + id2; // making sure the ID is not present

            response = getUser(missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void deleteMissingUserTest() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = addUser("John", "Doe", "john60@doe.org");
            // EntityUtils.consume(response.getEntity());
            String id1 = getIdFromResponse(response);
            response.close();

            response = addUser("Jane", "Wall", "jane78@wall.com");
            // EntityUtils.consume(response.getEntity());
            String id2 = getIdFromResponse(response);
            response.close();

            String missingId = "xyz" + id1 + id2; // making sure the ID is not present

            response = deleteUser(missingId);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

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

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

	private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
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

    private CloseableHttpResponse updateUserIncorrect(String id, String firstname, String lastname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(
                "{\"firstname\":\"" + firstname + "\"," +
                        "\"noname\":\"" + lastname + "\"," +
                        "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
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
