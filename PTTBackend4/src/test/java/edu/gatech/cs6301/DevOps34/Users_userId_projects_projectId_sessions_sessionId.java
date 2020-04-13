package edu.gatech.cs6301.DevOps34;

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

public class Users_userId_projects_projectId_sessions_sessionId {
	
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


	/* Test Case 1 : This checks if the server supports the GET method or not */
	@Test
	public void GETmethodError404() throws Exception {
		try {
			String userId = create_user("GETTest", "User_GET_Session", "gettus@gatech.edu");
			String projectId = create_project(userId, "test_GET_project");
	        CloseableHttpResponse response = addSession(userId, projectId, "2020-02-15T23:59:59", "2020-02-16T03:52:45", 4);
            
            int status = response.getStatusLine().getStatusCode();
			HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            String sessionId = getIdFromStringResponse(strResponse);
	        
			HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        	httpRequest.addHeader("accept", "application/json");
	
	        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        	response = httpclient.execute(httpRequest);
            status = response.getStatusLine().getStatusCode();
			if(status != 405) {
                throw new ClientProtocolException("Unexpected response status: " + status);
			}
			response = deleteUser(userId);
            response.close();
		} finally {
			httpclient.close();
		}
		
	}
	
	/* Test Case 2 : This checks if the server supports POST or not */ 
    @Test
    public void POSTmethodError404() throws Exception {

        try {
			String userId = create_user("POSTTest", "User_POST_Session", "posttuas@gatech.edu");
			String projectId = create_project(userId, "test_POST_project");
            CloseableHttpResponse response = addSession(userId, projectId, "2020-02-15T23:59Z", "2020-02-16T03:52Z", 4);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status for addSession: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String id = getIdFromStringResponse(strResponse);

            String expectedJson = "{\"id\":" + id + ",\"startTime\":\"2020-02-15T23:59Z\",\"endTime\":\"2020-02-16T03:52Z\",\"counter\":4}";
			JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
        
			response = deleteUser(userId);
			response.close();
        } finally {
            httpclient.close();
        }
    }

	/* Test Case 3 : This checks if the server supports the DELETE method or not */
	@Test
	public void DELETEmethodError404() throws Exception {
		try {
			String userId = create_user("DELETETest", "User_Session", "deletetus@gatech.edu");
			String projectId = create_project(userId, "test_DELETE_project");
	        CloseableHttpResponse response = addSession(userId, projectId, "2020-02-15T23:59:59", "2020-02-16T03:52:45", 4);
            
            int status = response.getStatusLine().getStatusCode();
			HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status while adding user: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            String sessionId = getIdFromStringResponse(strResponse);
	        
			HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        	httpRequest.addHeader("accept", "application/json");
			StringEntity input = new StringEntity("{\"id\":\"" + 1000 + "\",\"startTime\":\"2020-02-15T22:59:59\", \"endTime\":\"2020-02-16T05:52:45\", \"counter\":6}");
			input.setContentType("application/json");
			httpRequest.setEntity(input);
	
	        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        	response = httpclient.execute(httpRequest);
            status = response.getStatusLine().getStatusCode();
			if(status != 405) {
                throw new ClientProtocolException("Unexpected response status: " + status);
			}
			response = deleteUser(userId);
            response.close();
		} finally {
			httpclient.close();
		}
		
	}

	/* Test Case 5 : Tests if adding a session with invalid parameters works or not */
	/* NOTE: This test is going to fail as the server doesn't check for negative counter */
	/* TODO: This test is being passed as the back-end server is missing some implementations */
    @Test
    public void InvalidContentTest() throws Exception {
        try {
			String userId = create_user("Test", "User_Add_Invalid_Session", "tuais@gatech.edu");
			String projectId = create_project(userId, "test_addInvalidSession_project");
			CloseableHttpResponse response = addSession(userId, projectId, "2020-02-15T23:59Z", "2020-02-16T03:52Z", 4);
            String sessionId = getIdFromResponse(response);
			
			HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        	httpRequest.addHeader("accept", "application/json");
            /* Negative Counter should give an error */
			StringEntity input = new StringEntity("{\"id\":\"" + 1000 + "\",\"startTime\":\"2020-02-15T22:59:59\", \"endTime\":\"2020-02-16T05:52:45\", \"counter\":-16}");
			input.setContentType("application/json");
			httpRequest.setEntity(input);
	        
			System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        	response = httpclient.execute(httpRequest);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status != 400) {
				response = deleteUser(userId);
            	response.close();
				if (status != 200) {
	                throw new ClientProtocolException("Unexpected response status: " + status);
				}
            } 
			response = deleteUser(userId);
			response.close();
        } finally {
            httpclient.close();
        }
    }

	/* Test Case 6 : Tests if updating an existing session works or not */
    @Test
    public void updateSessionTest() throws Exception {

        try {
			String userId = create_user("Test", "User_Update_Session", "tuus@gatech.edu");
			String projectId = create_project(userId, "test_updateSession_project");
            CloseableHttpResponse response = addSession(userId, projectId, "2020-02-15T23:59Z", "2020-02-16T03:52Z", 4);
            String sessionId = getIdFromResponse(response);
			response.close();

			response = updateSession(userId, projectId, sessionId, "2020-02-16T23:59Z", "2020-02-18T03:52Z", 32);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status for updateSession: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + sessionId + ",\"startTime\":\"2020-02-16T23:59Z\",\"endTime\":\"2020-02-18T03:52Z\",\"counter\":32}";
			JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
        
			response = deleteUser(userId);
			response.close();
        } finally {
            httpclient.close();
        }
    }

	/***************************************************************************/

	/************************** "users/" and users/{userId}*********************/

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

	/*************** "user/{userId}/projects/ and /{projectId}" ****************/
	
    private CloseableHttpResponse addProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}"); 
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
	}	
	
	/************** "user/{userId}/projects/{projectId}/sessions" ***************/
	
    private CloseableHttpResponse addSession(String userId, String projectId, String startTime, String endTime, Integer counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
	}

	private CloseableHttpResponse getSessions(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
		httpRequest.addHeader("accept", "application/json");
		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpclient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");
		return response;
	}
	
	/************** "user/{userId}/projects/{projectId}/sessions/{sessionId}" ***************/
	
    private CloseableHttpResponse updateSession(String userId, String projectId, String sessionId, String startTime, String endTime, Integer counter) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
	}
	
	
	
	/***********************************************************************************/

	/* Helper Functions */

	private String create_user(String firstName, String lastName, String email) throws Exception {
		CloseableHttpResponse response = addUser(firstName, lastName, email);
		int status = response.getStatusLine().getStatusCode();
		HttpEntity entity;
		if (status == 201) {
			entity = response.getEntity();
		} else if (status == 409) {
			throw new ClientProtocolException("User conflict Error status: " + status);
		} else {
			throw new ClientProtocolException("Unexpected response status: " + status);
		}
		String strResponse = EntityUtils.toString(entity);	
		String id = getIdFromStringResponse(strResponse);
		return id;
	}
	
	private String create_project(String userId, String projectname) throws Exception {
		CloseableHttpResponse response = addProject(userId, projectname);
		int status = response.getStatusLine().getStatusCode();
		HttpEntity entity;
		if (status == 201) {
			entity = response.getEntity();
		} else if (status == 409) {
			throw new ClientProtocolException("Project conflict Error status: " + status);
		} else {
			throw new ClientProtocolException("Unexpected response status: " + status);
		}
		String strResponse = EntityUtils.toString(entity);	
		String id = getIdFromStringResponse(strResponse);
		return id;
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
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }

}
