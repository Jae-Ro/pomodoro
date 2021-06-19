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

public class Users_userId_projects_projectId_sessions_sessionIdTest {
    private Util util = new Util();
    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;

    public Users_userId_projects_projectId_sessions_sessionIdTest() {
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

    // Purpose: Test the case where the user id is invalid
    @Test
    public void pttTest1() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession("-1", pid, sid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            int status = response.getStatusLine().getStatusCode();

            if (status == 404) {
                System.out.println("Correct Status Code");
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

    // Purpose: Invalid Project Id, expect a 404
    @Test
    public void pttTest2() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession(id, "-1", sid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            int status = response.getStatusLine().getStatusCode();

            if (status == 404) {
                System.out.println("Correct Status Code");
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

    // Purpose: invalid sessionid test, expect a 404
    @Test
    public void pttTest3() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession(id, pid, "-1", "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            int status = response.getStatusLine().getStatusCode();

            if (status == 404) {
                System.out.println("Correct Status Code");
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

    // Purpose: Empty Start and End Times, epxect a 400
    @Test
    public void pttTest4() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession(id, pid, sid, "", "", "1");
            int status = response.getStatusLine().getStatusCode();

            if (status == 400) {
                System.out.println("Correct Status Code");
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

    // Purpose: Start time is after the end time, should expect a 400
    @Test
    public void pttTest5() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession(id, pid, sid, "2002-01-01T12:11Z", "2002-01-01T11:11Z", "1");
            int status = response.getStatusLine().getStatusCode();

            if (status == 400) {
                System.out.println("Correct Status Code");
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

    // Purpose: Empty Counter in body, expect a 400
    @Test
    public void pttTest6() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-01T21:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);

            HttpPut httpRequest = new HttpPut(
                    baseUrl + "/users/" + id + "/" + "projects/" + pid + "/" + "sessions/" + sid);
            httpRequest.addHeader("accept", "application/json");
            StringEntity input = new StringEntity(
                    "{\"startTime\":\"" + "2002-01-01T20:00Z" + "\"," + "\"endTime\":\"" + "2002-01-01T21:00Z" + "}");
            input.setContentType("application/json");

            httpRequest.setEntity(input);

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);

            int status = response.getStatusLine().getStatusCode();

            if (status == 400) {
                System.out.println("Correct Status Code");
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

    // Purpose: Invalid counter meaning -1, should expect a 400
    @Test
    public void pttTest7() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession(id, pid, sid, "2002-01-01T12:11Z", "2002-01-01T13:11Z",
                    "-1");
            int status = response.getStatusLine().getStatusCode();

            if (status == 400) {
                System.out.println("Correct Status Code");
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

    // A correct PUT implementation, expect a 200
    @Test
    public void pttTest8() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse session_response = createSession(id, pid, "2002-01-01T20:00Z", "2002-01-02T20:00Z",
                    "1");
            String sid = Util.getIdFromResponse(session_response);
            CloseableHttpResponse response = updateSession(id, pid, sid, "2002-01-01T12:11Z", "2002-01-01T13:11Z", "2");
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                System.out.println("Correct Status Code");
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

    public CloseableHttpResponse updateSession(String userid, String projectid, String sessionId, String start,
            String end, String counter) throws Exception {

        HttpPut httpRequest = new HttpPut(
                baseUrl + "/users/" + userid + "/" + "projects/" + projectid + "/" + "sessions/" + sessionId);
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
}