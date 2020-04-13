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

public class UsersTest {

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

    // *** Get util set up***
    private Util util = new Util();

    // Purpose: Tests creating a user with empty first name
    @Test
    public void pttTest1() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("", "Doe", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct response for no firstName");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests creating a user with empty last name
    @Test
    public void pttTest2() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct response for no lastname");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests creating a user with invalid email
    @Test
    public void pttTest3() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "johndoeorg");
            // passing as this needs to be checked in frontEnd
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct response for invalid email");
            } else {
                throw new ClientProtocolException(
                        "Unexpected response status: " + status + "\nIncorrect email should be rejected");
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests creating a user with invalid email
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct response for empty email");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests creating a user with duplicate email
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response0 = createUser("John1", "Doe1", "repeat@mail.org");
            response0.close();

            CloseableHttpResponse response = createUser("John2", "Doe2", "repeat@mail.org");

            int status = response.getStatusLine().getStatusCode();
            if (status == 409) {
                System.out.println("Correct response for empty email");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests the getting all the users
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        String expectedJson = "";

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            id = getIdFromResponse(response);
            expectedJson += "[{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\",\"id\":" + id
                    + "}";
            response.close();

            response = createUser("Jane", "Wall", "jane@wall.com");
            // EntityUtils.consume(response.getEntity());
            id = getIdFromResponse(response);
            expectedJson += ",{\"firstName\":\"Jane\",\"lastName\":\"Wall\",\"email\":\"jane@wall.com\",\"id\":" + id
                    + "}]";
            response.close();

            response = getAllUsers();

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

    // Purpose: pttTest: Tests creating a user with all valid parameters
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

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
            String expectedJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\",\"id\":" + id
                    + "}";

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

}
