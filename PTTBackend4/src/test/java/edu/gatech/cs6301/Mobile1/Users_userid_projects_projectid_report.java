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

public class Users_userid_projects_projectid_report extends PTTBackendTests {

    @Test
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report when 
    //          user exists && list of projects exists && show number of pomodoros && show total hours worked
    public void pttTest1() throws Exception {
    	deleteAllUsers();
    	try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
    		
    		// get user id
            String userId = getIdFromResponse(response);
            response.close();
            
    		// create a project
    		response = createProject("CS6301HW", userId);
    		
    		// get project id
    		String projectId = getIdFromResponse(response);
    		response.close();
    		
            // create a session
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
            response.close();

            // get and test a report
            response = getReport(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", true, true);
            
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}], \"completedPomodoros\" : 2, \"totalHoursWorkedOnProject\" : 1}]";
    		System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report when 
    //          user exists && list of projects exists && show number of pomodoros && does not show total hours worked
    public void pttTest2() throws Exception {
    	deleteAllUsers();
    	try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
    		
    		// get user id
            String userId = getIdFromResponse(response);
            response.close();
            
    		// create a project
    		response = createProject("CS6301HW", userId);
    		
    		// get project id
    		String projectId = getIdFromResponse(response);
    		response.close();
    		
            // create a session
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
            response.close();

            // get and test a report
            response = getReport(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", true, false);
            
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}], \"completedPomodoros\" : 2}]";
    		System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report when 
    //          user exists && list of projects exists && does not show number of pomodoros && show total hours worked
    public void pttTest3() throws Exception {
    	deleteAllUsers();
    	try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
    		
    		// get user id
            String userId = getIdFromResponse(response);
            response.close();
            
    		// create a project
    		response = createProject("CS6301HW", userId);
    		
    		// get project id
    		String projectId = getIdFromResponse(response);
    		response.close();
    		
            // create a session
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
            response.close();

            // get and test a report
            response = getReport(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", false, true);
            
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1}], \"totalHoursWorkedOnProject\" : 1}]";
    		System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report when 
    //          user exists && list of projects exists && does not show number of pomodoros && does not show total hours worked
    public void pttTest4() throws Exception {
    	deleteAllUsers();
    	try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
    		
    		// get user id
            String userId = getIdFromResponse(response);
            response.close();
            
    		// create a project
    		response = createProject("CS6301HW", userId);
    		
    		// get project id
    		String projectId = getIdFromResponse(response);
    		response.close();
    		
            // create a session
            response = createSession(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
            response.close();

            // get and test a report
            response = getReport(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", false, false);
            
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

            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T21:00Z\",\"hoursWorked\":1.0}]}";
    		System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report when 
    //          user exists && list of projects does not exist
    public void pttTest5() throws Exception {
    	deleteAllUsers();
    	try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
    		
    		// get user id
            String userId = getIdFromResponse(response);
            response.close();
    		
    		// a fake project id
    		String projectId = "1";
    		response.close();

            // get and test a report
            response = getReport(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", true, true);
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
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report when 
    //          user does not exist
    public void pttTest6() throws Exception {
    	deleteAllUsers();
    	try {
    	
    		// fake user id and project id
    		String userId = "1";
    		String projectId = "1";

            // get and test a report
            CloseableHttpResponse response = getReport(userId, projectId, "2019-02-18T20:00Z", "2019-02-18T21:00Z", true, true);
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
    // Purpose: Tests GET /users/{userId}/projects/{projectId}/report with a bad request 
    public void pttTest7() throws Exception {
    	deleteAllUsers();
    	try {
    	
            CloseableHttpResponse response = getReport("123ab", "123dc", "123", "123", true, true);
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

