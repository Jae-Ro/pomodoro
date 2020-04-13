package edu.gatech.cs6301.Backend4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import static edu.gatech.cs6301.Backend4.Util.*;

public class Users_userId_projectsTest {

    private String baseUrl = " http://gazelle.cc.gatech.edu:9305/";
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

    // *** Get util set up***
    private Util util = new Util();

    // Purpose: Test getting all projects of a user
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        String userId = null;
        String projectId = null;
        String expectedJson = "";

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            // System.out.println(response.getEntity());
            userId = getIdFromResponse(response);

            response.close();

            // Create Project 1
            response = createProject(userId, "testProject1");
            projectId = getIdFromResponse(response);
            expectedJson += "[{\"id\":" + projectId + ",\"projectname\":\"testProject1\"}";
            response.close();

            // Create Project 2
            response = createProject(userId, "testProject2");
            projectId = getIdFromResponse(response);
            expectedJson += ",{\"id\":" + projectId + ",\"projectname\":\"testProject2\"}]";
            response.close();

            response = getAllProjects(userId);

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

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test getting all projects of an invalid user, should get 404 status
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        String userId = null;

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            userId = getIdFromResponse(response);
            response.close();

            response = createProject(userId, "testProject1");
            response.close();

            response = getAllProjects(userId + "0");

            int status = response.getStatusLine().getStatusCode();

            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Test creating a project for a user,
    // should return 201 status and the project json
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        String userId = null;
        String projectId = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            userId = getIdFromResponse(response);
            response.close();

            // Create Project
            response = createProject(userId, "testProject1");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            projectId = getIdFromStringResponse(strResponse);
            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            String expectedJson = "{\"id\":" + projectId + ",\"projectname\":\"testProject1\"}";

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: Test creating a project for a user with an empty projectname,
    // should return 400 status
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        String userId = null;
        String projectId = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            userId = getIdFromResponse(response);
            response.close();

            // Create Project
            response = createProject(userId, "");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(400, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: Test creating a project for a user with a duplicate projectname,
    // should return 409 status
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        String userId = null;
        String projectId = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            userId = getIdFromResponse(response);
            response.close();

            // Create Project
            response = createProject(userId, "testProject");
            response.close();
            response = createProject(userId, "testProject");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(409, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Purpose: Test creating a project for an invalid user,
    // should return 404 status
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        String userId = "1";
        String projectId = null;
        try {
            // Create Project
            CloseableHttpResponse response = createProject(userId, "testProject");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
}
