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

public class Users_userid_projects_projectid_sessions extends PTTBackendTests {

    @Test
     // test for get method: Return all sessions for a given project
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            
            // get the user id from the response
            String userId = getIdFromResponse(response);
            response.close();

            // create a project for the user
            response = createProject("CS6301HW", userId);

            // get the project id from the response
            String projectId = getIdFromResponse(response);
            response.close();

            // create a session for the user
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
            String sessionId = getIdFromResponse(response);
            response.close();


            // check whether the response meets expectation
            response = getAllSessions(userId, projectId);
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
            String expectedJson = "[{\"id\":" + sessionId + 
                ",\"startTime\":\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-18T21:00Z\",\"counter\":2}]";
			System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }
	
    @Test
    // test for get method &  the project does not exist
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            
            // get the user id from the response
            String userId = getIdFromResponse(response);
            response.close();

            String projectId = "1"; // this project does not exist

            // check whether the response meets expectation
            response = getAllSessions(userId, projectId);
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
    // test for get method: the user does not exist
    public void pttTest3() throws Exception {

        deleteAllUsers();
        try {
            String userId = "1"; // this user does not exist
            String projectId = "1"; 

            // check whether the response meets expectation
            CloseableHttpResponse response = getAllSessions(userId, projectId);
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
    // test for get: bad request
    public void pttTest4() throws Exception {
        
        deleteAllUsers();
        try {
            CloseableHttpResponse response = getAllSessions("ds", "1sd"); // bad request
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
    // test for post method: create a new session
    public void pttTest5() throws Exception {

        deleteAllUsers();
        try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            
            // get the user id from the response
            String userId = getIdFromResponse(response);
            response.close();

            // create a project for the user
            response = createProject("CS6301HW", userId);

            // get the project id from the response
            String projectId = getIdFromResponse(response);
            response.close();

            // create a session for the user
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
           
            // check whether the post response meets expectation
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
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            String expectedJson = "{\"counter\":2,\"startTime\":\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-18T21:00Z\","
                + "\"id\":" + sessionId + "}";
			// System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }

    }

    @Test
     // test for post: project does not exist
    public void pttTest6() throws Exception {
       
        deleteAllUsers();
        try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            
            // get the user id from the response
            String userId = getIdFromResponse(response);
            response.close();

            String projectId = "1";

            // create a session for the user
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);

            // check whether the post response meets expectation
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
    // test for post: user does not exist
    public void pttTest7() throws Exception {
        
        deleteAllUsers();
        try {
            String userId = "1";
            String projectId = "1";

            // create a session for the user
            CloseableHttpResponse response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);

            // check whether the post response meets expectation
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
    // test for post: bad request
    public void pttTest8() throws Exception {
        
        deleteAllUsers();
        try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            
            // get the user id from the response
            String userId = getIdFromResponse(response);
            response.close();

            // create a project for the user
            response = createProject("CS6301HW", userId);

            // get the project id from the response
            String projectId = getIdFromResponse(response);
            response.close();

            // create a bad post request for session
            HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
            httpRequest.addHeader("accept", "application/json");
            StringEntity input = new StringEntity("{\"start\":\"2019-02-18T20:00Z\"," +
                    "\"end\":\"2019-02-18T21:00Z\"," +
                    "\"counter\":2}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);
            response = httpclient.execute(httpRequest);

            // check whether the post response meets expectation
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
