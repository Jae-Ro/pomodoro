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

public class Users_userid_projects_projectid_sessions_sessionId extends PTTBackendTests {

    @Test
    public void pttTest1() throws Exception {
        // test for put method: update the session
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

            // update the session
            response = updateSession(userId, projectId, sessionId, "2019-02-18T20:00Z", "2019-02-18T22:00Z", 4);

            // check whether the response meets expectation
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
            String expectedJson = "{\"counter\":4,\"startTime\":\"2019-02-18T20:00Z\",\"endTime\":\"2019-02-18T22:00Z\"" 
                + ",\"id\":" + sessionId + "}";
			System.out.println(expectedJson);
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }


    @Test
    // test for put method: bad request
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = updateSession("adcd", "sd", "23", "2019-02-18T20:00Z", "2019-02-18T21:00Z", 2);
            // check whether the response meets expectation
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
    public void pttTest3() throws Exception {
        // test for put: session does not exist
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

            String sessionId = "1";

            // update the session
            response = updateSession(userId, projectId, sessionId, "2019-02-18T20:00Z", "2019-02-18T22:00Z", 4);

            // check whether the response meets expectation
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
    public void pttTest4() throws Exception {
        // test for put: project does not exist
        deleteAllUsers();
        try {
            // create a user
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            
            // get the user id from the response
            String userId = getIdFromResponse(response);
            response.close();

            String projectId = "1";

            String sessionId = "1";

            // update the session
            response = updateSession(userId, projectId, sessionId, "2019-02-18T20:00Z", "2019-02-18T22:00Z", 4);

            // check whether the response meets expectation
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
    public void pttTest5() throws Exception {
        // test for put: user does not exist
        deleteAllUsers();
        try {
            String userId = "1";
            String projectId = "1";
            String sessionId = "1";

            // update the session
            CloseableHttpResponse response = updateSession(userId, projectId, sessionId, "2019-02-18T20:00Z", "2019-02-18T22:00Z", 4);

            // check whether the response meets expectation
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
