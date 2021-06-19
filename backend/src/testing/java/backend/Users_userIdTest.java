package backend;

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
import static backend.Util.*;

public class Users_userIdTest {

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

    // Purpose: Tests getting on notpresent userid
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            String not_present_id = "" + (Integer.parseInt(id) + 1);// more than the one generated.
            response.close();
            response = getUser(not_present_id);
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct response for not present user");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests getting on invalid userid
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = getUser("aa");
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct response for not invalid userid");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests putting on notpresent userid
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            String not_present_id = "" + (Integer.parseInt(id) + 1);// more than the one generated.
            response.close();
            response = updateUser(not_present_id, "John", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct response for not present user");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests putting on invalid userid
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = updateUser("aa", "John", "Doe", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct response for not invalid userid");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests putting on notpresent userid
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            String not_present_id = "" + (Integer.parseInt(id) + 1);// more than the one generated.
            response.close();
            response = deleteUser(not_present_id);
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct response for not present user");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests putting on invalid userid
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse response = deleteUser("aa");
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct response for not invalid userid");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests put/update on valid userid with empty firstName
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            response.close();
            response = updateUser(id, "", "Goel", "john@doe.org");
            int status = response.getStatusLine().getStatusCode();
            response.close();
            if (status == 400) {
                System.out.println("Correct response for not blank firstName");
            } else {
                throw new ClientProtocolException(
                        "Unexpected response status: " + status + "\n Blank firstname should give an error");
            }

        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests put/update on valid userid with empty lastName
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            response.close();
            response = updateUser(id, "Rohan", "", "john@doe.org");

            int status = response.getStatusLine().getStatusCode();
            response.close();
            if (status == 400) {
                System.out.println("Correct response for not blank lastName");
            } else {
                throw new ClientProtocolException(
                        "Unexpected response status: " + status + "\n Blank lastName should give an error");
            }

        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests getting on valid userid
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        String expectedJson = "";
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            // EntityUtils.consume(response.getEntity());
            id = getIdFromResponse(response);
            expectedJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\",\"id\":" + id + "}";
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

            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: Tests put/update on valid userid with valid details
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        String expectedJson = "";
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            response.close();

            expectedJson = "{\"firstName\":\"Rohan\",\"lastName\":\"Goel\",\"email\":\"john@doe.org\",\"id\":" + id
                    + "}";
            response = updateUser(id, "Rohan", "Goel", "john@doe.org");

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

    // Purpose: Tests delete on valid userid
    @Test
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        String id = null;
        String expectedJson = "";
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            id = getIdFromResponse(response);
            response.close();

            expectedJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@doe.org\",\"id\":" + id + "}";
            response = deleteUser(id);

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
}
