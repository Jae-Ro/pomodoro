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

public class Users_userId_projects_projectId_sessionsTest {
    private Util util = new Util();
    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;

    public Users_userId_projects_projectId_sessionsTest() {
        runBefore();
    }

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
            System.out.println("setup done");
            setupdone = true;
        }
        System.out.println("*** STARTING TEST ***");
    }

    @Before
    public void before() {
        System.out.println("BEFORE TEST");
    }

    @After
    public void runAfter() {
        System.out.println("ENDING TEST");
    }

    // Purpose: test with invalid userid
    @Test
    public void pttTest1() throws Exception {

        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createSession("-1", pid, "2019-02-18T20:00Z", "2019-02-18T21:00Z", "1");
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct Response status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
        // System.out.println("*** Raw response " + response + "***");

    }

    // Purpose: test with invalid project id
    @Test
    public void pttTest2() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createSession(id, "-1", "2019-02-18T20:00Z", "2019-02-18T21:00Z", "1");

            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("Correct Response status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        // System.out.println("*** Raw response " + response + "***");

        finally {
            httpclient.close();
        }
    }

    // Purpose: test POST with empty times
    @Test
    public void pttTest3() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createSession(id, pid, "", "", "1");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correst Response Status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        // System.out.println("*** Raw response " + response + "***");

        finally {
            httpclient.close();

        }
    }

    // Purpose: test POST with invalid timedate format
    @Test
    public void pttTest4() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createSession(id, pid, "20:00", "21:00", "1");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct Response status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        // System.out.println("*** Raw response " + response + "***");

        finally {
            httpclient.close();
        }
    }

    // Purpose: test POST with empty counter
    @Test
    public void pttTest5() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createSession(id, pid, "20:00", "21:00", "");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct Response status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        // System.out.println("*** Raw response " + response + "***");

        finally {
            httpclient.close();
        }
    }

    // Purpose: test POST with invalid counter
    @Test
    public void pttTest6() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createSession(id, pid, "20:00", "21:00", "-1");

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("Correct Response status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        // System.out.println("*** Raw response " + response + "***");

        finally {
            httpclient.close();
        }
    }

    // Purpose: A correct POST request
    @Test
    public void pttTest7() throws Exception {

        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("Jason", "ruler", "test@email.com"));
            String pid = Util.getIdFromResponse(Util.createProject(id, "test"));
            String start = "2019-02-18T20:00Z";
            String end = "2019-02-18T21:00Z";
            String counter = "1";
            CloseableHttpResponse resp = createSession(id, pid, start, end, counter);

            String stresp = EntityUtils.toString(resp.getEntity());
            System.out.println(stresp);
            String sid = Util.getIdFromStringResponse(stresp);
            int status = resp.getStatusLine().getStatusCode();

            if (status == 201) {
                System.out.println("Correct Response status");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            String correctJson = "{" + "\"id\":" + sid + "," + "\"startTime\":\"" + start + "\"," + "\"endTime\":\""
                    + end + "\"," + "\"counter\":" + counter + "}";

            System.out.println(correctJson);
            JSONAssert.assertEquals(correctJson, stresp, false);
            EntityUtils.consume(resp.getEntity());
            resp.close();
        } finally {
            httpclient.close();
        }
    }

    // A correct GET Request
    @Test
    public void pttTest8() throws Exception {
        String start = "2019-02-18T20:00Z";
        String end = "2019-02-18T21:00Z";
        String counter = "2";
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(Util.createProject(id, "test"));
            CloseableHttpResponse response = createSession(id, pid, start, end, counter);
            String sid = getIdFromResponse(response);
            response.close();

            response = getSessions(id, pid);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                System.out.println("Successfully Executed GET Sessions");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String stresp = EntityUtils.toString(response.getEntity());
            // String sid = Util.getIdFromStringResponse(stresp);
            String correctJson = "[{" + "\"id\":" + sid + "," + "\"startTime\":\"" + start + "\"," + "\"endTime\":\""
                    + end + "\"," + "\"counter\":" + counter + "}]";

            JSONAssert.assertEquals(correctJson, stresp, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    public CloseableHttpResponse createProject(String userid, String projectName) throws Exception {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");

        httpRequest.setEntity(input);

        // System.out.println("*** Executing request " + httpRequest.getRequestLine() +
        // "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);

        return response;
    }

    public CloseableHttpResponse createSession(String userid, String projectid, String start, String end,
            String counter) throws Exception {

        HttpPost httpRequest = new HttpPost(
                baseUrl + "/users/" + userid + "/" + "projects/" + projectid + "/" + "sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + start + "\"," + "\"endTime\":\"" + end + "\","
                + "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");

        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        int status = response.getStatusLine().getStatusCode();
        System.out.println(status);
        return response;
    }

    public CloseableHttpResponse getSessions(String userid, String projectid) throws Exception {

        HttpGet httpRequest = new HttpGet(
                baseUrl + "/users/" + userid + "/" + "projects/" + projectid + "/" + "sessions");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response from get sessions" + response + "***");

        return response;
    }

}