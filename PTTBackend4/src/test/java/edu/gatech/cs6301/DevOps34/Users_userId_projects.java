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

public class Users_userId_projects {
	
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

    // /* Test Case 1 : This checks if the servers supports the PUT method or not */
    // @Test
    // public void PUTmethodError() throws Exception {
    //     httpclient = HttpClients.createDefault();

    //     try {
    //         String userId = createuser("PUTTest", "UserSession", "puttus@gatech.edu");
	//         CloseableHttpResponse response = addProject(userId, "testPUTproject");
            
    //         int status = response.getStatusLine().getStatusCode();
	// 		HttpEntity entity;
    //         if (status == 201) {
    //             entity = response.getEntity();
    //         } else if (status == 409) {
    //             throw new ClientProtocolException("Project conflict error status: " + status);
    //         } else {
    //             throw new ClientProtocolException("Unexpected response status: " + status);
    //         }
    //         String strResponse = EntityUtils.toString(entity);
    //         String projectId = getIdFromStringResponse(strResponse);
	        
	// 		HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects");
    //     	httpRequest.addHeader("accept", "application/json");
	//         StringEntity input = new StringEntity("{\"id\":\"" + projectId + "\",\"projectname\":\"" + "testPUTprojects");
	//         input.setContentType("application/json");
	//         httpRequest.setEntity(input);
	
	//         System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
    //     	response = httpclient.execute(httpRequest);
	//         System.out.println("*** Raw response " + response + "***");
    //         status = response.getStatusLine().getStatusCode();
	// 		if(status != 400) {
    //             throw new ClientProtocolException("Unexpected response status: " + status);
	// 		}
    //         response.close();
	// 		deleteUser(userId);

    //     } finally {
    //         httpclient.close();
    //     }
    // }

	// /* Test Case 2 : This checks if the server supports the DELETE method or not */
	// @Test
	// public void DELETEmethodError() throws Exception {
	// 	try {
	// 		String userId = createuser("DELETETest", "UserSession", "deletetus@gatech.edu");
	//         CloseableHttpResponse response = addProject(userId, "testDELETEproject");
            
    //         int status = response.getStatusLine().getStatusCode();
	// 		HttpEntity entity;
    //         String strResponse;
    //         if (status == 201) {
    //             entity = response.getEntity();
    //         } else {
    //             throw new ClientProtocolException("Unexpected response status while adding user: " + status);
    //         }
    //         strResponse = EntityUtils.toString(entity);
    //         String sessionId = getIdFromStringResponse(strResponse);
	        
	// 		HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId + "/projects");
    //     	httpRequest.addHeader("accept", "application/json");
	
	//         System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
    //     	response = httpclient.execute(httpRequest);
    //         status = response.getStatusLine().getStatusCode();
	// 		if(status != 404) {
    //             throw new ClientProtocolException("Unexpected response status: " + status);
	// 		}
	// 		response = deleteUser(userId);
    //         response.close();
	// 	} finally {
	// 		httpclient.close();
	// 	}
		
	// }

    /* Test Case 3 : Tests for an empty projects list when adding user without adding
     * projects */
    @Test
    public void getEmptyProjectsTest() throws Exception {
    
		httpclient = HttpClients.createDefault();
        String expectedJson = "[]";

        try {
			String userId = createuser("Test", "UserGetSession", "tugs@gatech.edu");
            CloseableHttpResponse response = getProjects(userId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
			} else {
				response = deleteUser(userId);
            	response.close();
                throw new ClientProtocolException("Unexpected response status for getProjects: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

	    	JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
			response = deleteUser(userId);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /* Test Case 4 : Tests for an invalid project
     * projects */
    @Test
    public void getInvalidProjectError() throws Exception {

        httpclient = HttpClients.createDefault();
        try {
            String userId = createuser("Test", "UserGetSession", "tugs@gatech.edu");
            String proj1 = createproject(userId, "proj1");
            String proj2 = createproject(userId, "proj2");
            CloseableHttpResponse response = getProject(userId, "xyz"+proj1+proj2);

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
            deleteUser(userId);

        } finally {
            httpclient.close();
        }
    }

    /* Test Case 5 : Tests adding multiple projects and getting a non-empty project list
     * projects */
    @Test
    public void getProjectsTest() throws Exception {

        httpclient = HttpClients.createDefault();
        String expectedJson = "[]";

        try {
            String userId = createuser("Test", "UserGetSession", "tugs@gatech.edu");
            createproject(userId, "proj1");
            createproject(userId, "proj2");
            CloseableHttpResponse response = getProjects(userId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                response = deleteUser(userId);
                response.close();
                throw new ClientProtocolException("Unexpected response status for getProjects: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            JSONAssert.assertNotEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response = deleteUser(userId);
            response.close();
        } finally {
            httpclient.close();
        }
    }


    /* Test Case 6 : Tests adding multiple projects and getting a non-empty project list
     * projects */
    @Test
    public void getProjectTest() throws Exception {

        httpclient = HttpClients.createDefault();
        String expectedJson = "[]";

        try {
            String userId = createuser("Test", "UserGetSession", "tugs@gatech.edu");
            String projectId = createproject(userId, "proj1");
            CloseableHttpResponse response = getProject(userId,projectId);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                response = deleteUser(userId);
                response.close();
                throw new ClientProtocolException("Unexpected response status for getProject: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            JSONAssert.assertNotEquals(expectedJson,strResponse, false);
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
	
	private CloseableHttpResponse getProjects(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
		httpRequest.addHeader("accept", "application/json");
		System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
		CloseableHttpResponse response = httpclient.execute(httpRequest);
		System.out.println("*** Raw response " + response + "***");
		return response;
	}

    private CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

	/***********************************************************************************/

	/* Helper Functions */

	private String createuser(String firstName, String lastName, String email) throws Exception {
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

    private String createproject(String userId, String projectname) throws Exception {
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
