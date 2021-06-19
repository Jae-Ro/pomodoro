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

public class Users_userId_projects_projectId_reportTest {
    private Util util = new Util();
    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private boolean setupdone;

    public Users_userId_projects_projectId_reportTest() {
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

    // TEST 1: test with invalid userid
    @Test
    public void pttTest1() throws Exception {

        Util.deleteUsers();
        String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
        CloseableHttpResponse response = createReport("-1", pid, "2019-02-18T20:00Z", "2019-02-18T21:00Z", true, true);
        int status = response.getStatusLine().getStatusCode();
        if (status == 404) {
            System.out.println("\n\n\t****BAD PROJ NAME!*******");
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

    }

    // TEST 2: test with invalid project id
    @Test
    public void pttTest2() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createReport(id, "-1", "2019-02-18T20:00Z", "2019-02-18T21:00Z", true, true);
            
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("\n\n\t****BAD PROJ NAME!*******");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }

        finally {
            httpclient.close();
        }
    }


    // TEST 3: test with invalid timeframe format
    @Test
    public void pttTest3() throws Exception {
        try {
            Util.deleteUsers();
            String id = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
            String pid = Util.getIdFromResponse(createProject(id, "Proj1"));
            CloseableHttpResponse response = createReport(id, pid, "20:00", "21:00", true, true);

            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("\n\n\t****BAD PROJ NAME!*******");
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }

        finally {
            httpclient.close();
        }
    }

    // TEST 4: test with timeframe exactly cover two sessions
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        Util.deleteUsers();
        String userId = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String projectid = Util.getIdFromResponse(createProject(userId, "projectname"));

        CloseableHttpResponse response = createSession(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T20:30Z", "1");
        response.close();
        response = createSession(userId, projectid, "2019-02-18T21:00Z", "2019-02-18T21:30Z", "1");
        response.close();

        try {           
            response = createReport(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T21:30Z", true, true);
            
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

            // String expectedJson = "{\"sessions\":\"[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:30Z\",\"hoursWorked\":\"0.50\"}, {\"startingTime\":\"2019-02-18T21:00Z\",\"endingTime\":\"2019-02-18T21:30Z\",\"hoursWorked\":\"0.50\"}]\",\"completedPomodoros\":\"0\",\"totalHoursWorkedOnProject\":\"0\"}"; 
            
            String newStartTime1 = "2019-02-18T20:00Z";
            String newEndTime1 = "2019-02-18T20:30Z";
            String newStartTime2 = "2019-02-18T21:00Z";
            String newEndTime2 = "2019-02-18T21:30Z";
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
                    "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]," +
                    "\"completedPomodoros\":2," +
                    "\"totalHoursWorkedOnProject\":1}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // TEST 5: test with timeframe is smaller than two sessions
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        Util.deleteUsers();
        String userId = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String projectid = Util.getIdFromResponse(createProject(userId, "projectname"));
        CloseableHttpResponse response = createSession(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T20:30Z", "1");
        response.close();
        response = createSession(userId, projectid, "2019-02-18T21:00Z", "2019-02-18T21:30Z", "1");
        response.close();

        try {           
            response = createReport(userId, projectid, "2019-02-18T20:10Z", "2019-02-18T21:20Z", true, true);
            
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

            // String expectedJson = "{\"sessions\":\"[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:30Z\",\"hoursWorked\":\"0.50\"}, {\"startingTime\":\"2019-02-18T21:00Z\",\"endingTime\":\"2019-02-18T21:30Z\",\"hoursWorked\":\"0.50\"}]\",\"completedPomodoros\":\"0\",\"totalHoursWorkedOnProject\":\"0\"}"; 
            
            String newStartTime1 = "2019-02-18T20:00Z";
            String newEndTime1 = "2019-02-18T20:30Z";
            String newStartTime2 = "2019-02-18T21:00Z";
            String newEndTime2 = "2019-02-18T21:30Z";
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
                    "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]," +
                    "\"completedPomodoros\":2," +
                    "\"totalHoursWorkedOnProject\":1}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }


    // TEST 6: test with timeframe is larger than two sessions, and true for include pomodoro, true for include hours
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        Util.deleteUsers();
        String userId = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String projectid = Util.getIdFromResponse(createProject(userId, "projectname"));

        CloseableHttpResponse response = createSession(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T20:30Z", "1");
        response.close();
        response = createSession(userId, projectid, "2019-02-18T21:00Z", "2019-02-18T21:30Z", "1");
        response.close();

        try {           
            response = createReport(userId, projectid, "2019-02-18T19:30Z", "2019-02-18T21:40Z", true, true);
            
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

            // String expectedJson = "{\"sessions\":\"[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:30Z\",\"hoursWorked\":\"0.50\"}, {\"startingTime\":\"2019-02-18T21:00Z\",\"endingTime\":\"2019-02-18T21:30Z\",\"hoursWorked\":\"0.50\"}]\",\"completedPomodoros\":\"0\",\"totalHoursWorkedOnProject\":\"0\"}"; 
            
            String newStartTime1 = "2019-02-18T20:00Z";
            String newEndTime1 = "2019-02-18T20:30Z";
            String newStartTime2 = "2019-02-18T21:00Z";
            String newEndTime2 = "2019-02-18T21:30Z";
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
                    "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]," +
                    "\"completedPomodoros\":2," +
                    "\"totalHoursWorkedOnProject\":1}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // TEST 7: test with timeframe is larger than two sessions, and true for include pomodoro, false for include hours
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();
        Util.deleteUsers();
        String userId = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String projectid = Util.getIdFromResponse(createProject(userId, "projectname"));

        CloseableHttpResponse response = createSession(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T20:30Z", "1");
        response.close();
        response = createSession(userId, projectid, "2019-02-18T21:00Z", "2019-02-18T21:30Z", "1");
        response.close();

        try {           
            response = createReport(userId, projectid, "2019-02-18T19:30Z", "2019-02-18T21:40Z", true, false);
            
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

            // String expectedJson = "{\"sessions\":\"[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:30Z\",\"hoursWorked\":\"0.50\"}, {\"startingTime\":\"2019-02-18T21:00Z\",\"endingTime\":\"2019-02-18T21:30Z\",\"hoursWorked\":\"0.50\"}]\",\"completedPomodoros\":\"0\",\"totalHoursWorkedOnProject\":\"0\"}"; 
            
            String newStartTime1 = "2019-02-18T20:00Z";
            String newEndTime1 = "2019-02-18T20:30Z";
            String newStartTime2 = "2019-02-18T21:00Z";
            String newEndTime2 = "2019-02-18T21:30Z";
            // String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
            //         "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]," +
            //         "\"completedPomodoros\":2," +
            //         "\"totalHoursWorkedOnProject\":1}";
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
                    "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]," +
                    "\"totalHoursWorkedOnProject\":1}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    // TEST 8: test with timeframe is larger than two sessions, and false for include pomodoro, true for include hours
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();
        Util.deleteUsers();
        String userId = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String projectid = Util.getIdFromResponse(createProject(userId, "projectname"));

        CloseableHttpResponse response = createSession(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T20:30Z", "1");
        response.close();
        response = createSession(userId, projectid, "2019-02-18T21:00Z", "2019-02-18T21:30Z", "1");
        response.close();

        try {           
            response = createReport(userId, projectid, "2019-02-18T19:30Z", "2019-02-18T21:40Z", false, true);
            
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

            // String expectedJson = "{\"sessions\":\"[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:30Z\",\"hoursWorked\":\"0.50\"}, {\"startingTime\":\"2019-02-18T21:00Z\",\"endingTime\":\"2019-02-18T21:30Z\",\"hoursWorked\":\"0.50\"}]\",\"completedPomodoros\":\"0\",\"totalHoursWorkedOnProject\":\"0\"}"; 
            
            String newStartTime1 = "2019-02-18T20:00Z";
            String newEndTime1 = "2019-02-18T20:30Z";
            String newStartTime2 = "2019-02-18T21:00Z";
            String newEndTime2 = "2019-02-18T21:30Z";
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
                    "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]," +
                    "\"completedPomodoros\":2}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }


    // TEST 9: test with timeframe is larger than two sessions, and false for include pomodoro, false for include hours
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();
        Util.deleteUsers();
        String userId = Util.getIdFromResponse(Util.createUser("John", "Doe", "john.doe@email.com"));
        String projectid = Util.getIdFromResponse(createProject(userId, "projectname"));

        CloseableHttpResponse response = createSession(userId, projectid, "2019-02-18T20:00Z", "2019-02-18T20:30Z", "1");
        response.close();
        response = createSession(userId, projectid, "2019-02-18T21:00Z", "2019-02-18T21:30Z", "1");
        response.close();

        try {           
            response = createReport(userId, projectid, "2019-02-18T19:30Z", "2019-02-18T21:40Z", false, false);
            
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

            // String expectedJson = "{\"sessions\":\"[{\"startingTime\":\"2019-02-18T20:00Z\",\"endingTime\":\"2019-02-18T20:30Z\",\"hoursWorked\":\"0.50\"}, {\"startingTime\":\"2019-02-18T21:00Z\",\"endingTime\":\"2019-02-18T21:30Z\",\"hoursWorked\":\"0.50\"}]\",\"completedPomodoros\":\"0\",\"totalHoursWorkedOnProject\":\"0\"}"; 
            
            String newStartTime1 = "2019-02-18T20:00Z";
            String newEndTime1 = "2019-02-18T20:30Z";
            String newStartTime2 = "2019-02-18T21:00Z";
            String newEndTime2 = "2019-02-18T21:30Z";
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"" + newStartTime1 + "\",\"endingTime\":\"" + newEndTime1 + "\",\"hoursWorked\":0.5}," +
                    "{\"startingTime\":\"" + newStartTime2 + "\",\"endingTime\":\"" + newEndTime2 + "\",\"hoursWorked\":0.5}]}";

            JSONAssert.assertEquals(expectedJson,strResponse, false);
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
        CloseableHttpResponse response = httpclient.execute(httpRequest);

        if (response.getStatusLine().getStatusCode() == 201) {
            System.out.println("*************************CREATED A PROJECT**********************");
        }

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

        return response;
    }

    public CloseableHttpResponse createReport(String userId, String projectId, String fromTime, String toTime,
            boolean includeP, boolean includeH) throws IOException {
        HttpGet httpRequest = null;
        if(!includeP && !includeH){
            httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report" + "?from=" + fromTime + "&to=" + toTime + "&includeCompletedPomodoros=false" + "&includeTotalHoursWorkedOnProject=false");
        }else if(!includeP && includeH){
            httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report" + "?from=" + fromTime + "&to=" + toTime + "&includeCompletedPomodoros=true" + "&includeTotalHoursWorkedOnProject=false");
        }else if(includeP && !includeH){
            httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report" + "?from=" + fromTime + "&to=" + toTime + "&includeCompletedPomodoros=false" + "&includeTotalHoursWorkedOnProject=true");
        }else{
            httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report" + "?from=" + fromTime + "&to=" + toTime + "&includeCompletedPomodoros=true" + "&includeTotalHoursWorkedOnProject=true");
        }
        httpRequest.addHeader("accept", "application/json");
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
}