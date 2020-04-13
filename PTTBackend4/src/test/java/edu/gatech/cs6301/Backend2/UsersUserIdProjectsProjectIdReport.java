package edu.gatech.cs6301.Backend2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
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

public class UsersUserIdProjectsProjectIdReport extends PTTBackendTestsBase{
    //Purpose: test GET with a project id that doesn't exist
    @Test
    public void pttTest1() throws Exception {
        deleteAllUsers();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        CloseableHttpResponse response = getReport("2019-02-18T20:00Z", nowAsISO, 400L, 100L, "true", "true");
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    //Purpose: test GET with invalid date - greater than 'to' value
    @Test
    public void pttTest2() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        CloseableHttpResponse response = getReport("2020-02-18T20:00Z", "2019-02-18T20:00Z", 400L, 100L, "true", "true");
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    //Purpose: test GET with invalid date - less than 'from' value
    @Test
    public void pttTest3() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        CloseableHttpResponse response = getReport(nowAsISO, "2019-02-18T20:00Z", 400L, 100L, "true", "true");
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    //Purpose: test the error of includeCompletedPomodoros as value type of non boolean
    @Test
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String firstName = "A" + System.currentTimeMillis();
            String lastName = "B" + System.currentTimeMillis();
            String email = "AB" + System.currentTimeMillis() + "@gmail.com";
            Long userId = createAndGetUserId(firstName, lastName, email);
            Long projectId = createAndGetProjectId(userId);

            Long completed = 5L;
            String startTime = "2019-02-18T10:00Z";
            String endTime = "2019-02-18T20:00Z";
            Long hoursWorked = 10L;

            CloseableHttpResponse response = createSession(httpclient, userId, projectId, startTime, endTime, completed);

            try {
                response = getReport("2019-02-18T20:00Z", "2019-02-19T20:00Z", userId, projectId, "nonboolean", "true");
            } catch (HttpResponseException e) {
                Assert.assertEquals(400, e.getStatusCode());
            }
        }
    }

    //Purpose: test the error of includeTotalHoursWorkedOnProject as value type of non boolean
    @Test
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            Long userId = createAndGetUserId();
            Long projectId = createAndGetProjectId(userId);

            HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/report");
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String from = "2020-02-18T20:00Z";

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            String to = df.format(new Date());

            URIBuilder builder = new URIBuilder("http://example.com/");
            builder.setParameter("from", from).setParameter("to", to).setParameter("includeCompletedPomodoros", String.valueOf(true)).setParameter("includeTotalHoursWorkedOnProject", "nonboolean");

            HttpPost post = new HttpPost(builder.build());

            System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
            CloseableHttpResponse response = httpclient.execute(httpRequest);
            System.out.println("*** Raw response " + response + "***");

            try {
                response = getReport("2019-02-18T20:00Z", "2019-02-19T20:00Z", userId, projectId, "true", "nonboolean");
            } catch (HttpResponseException e) {
                Assert.assertEquals(400, e.getStatusCode());
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //Purpose: Test a valid GET request with all parameters
    @Test
    public void pttTest6() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String firstName = "A" + System.currentTimeMillis();
            String lastName = "B" + System.currentTimeMillis();
            String email = "AB" + System.currentTimeMillis() + "@gmail.com";
            Long userId = createAndGetUserId(firstName, lastName, email);
            Long projectId = createAndGetProjectId(userId);

            Long completed = 5L;
            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T21:00Z";
            Long hoursWorked = 1L;

            CloseableHttpResponse response = createSession(httpclient, userId, projectId, startTime, endTime, completed);

            response = getReport(httpclient, "2019-02-18T20:00Z", "2019-02-19T20:00Z", userId, projectId, "true", "true");

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\n" +
                    "\"sessions\": [{\"startingTime\":\"" + startTime + "\",\"endingTime\":\"" + endTime + "\",\"hoursWorked\":" + hoursWorked + "}],\n" +
                    "\"completedPomodoros\":" + completed + ",\n" +
                    "\"totalHoursWorkedOnProject\":" +  hoursWorked + "\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
        }

    }

    //Purpose: Test a valid GET request with all parameters and includeTotalHoursWorkedOnProject as false
    @Test
    public void pttTest7() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String firstName = "A" + System.currentTimeMillis();
            String lastName = "B" + System.currentTimeMillis();
            String email = "AB" + System.currentTimeMillis() + "@gmail.com";
            Long userId = createAndGetUserId(firstName, lastName, email);
            Long projectId = createAndGetProjectId(userId);

            Long completed = 5L;
            String startTime = "2019-02-18T10:00Z";
            String endTime = "2019-02-18T20:00Z";
            Long hoursWorked = 10L;

            CloseableHttpResponse response = createSession(httpclient, userId, projectId, startTime, endTime, completed);

            response = getReport(httpclient, "2019-02-18T20:00Z", "2019-02-19T20:00Z", userId, projectId, "true", "false");

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\n" +
                    "\"sessions\": [{\"startingTime\":\"" + startTime + "\",\"endingTime\":\"" + endTime + "\",\"hoursWorked\":" + hoursWorked + "}],\n" +
                    "\"completedPomodoros\":" + completed + "\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
        }
    }

    //Purpose: Test a valid GET request with all parameters and includeCompletedPomodoros as false
    @Test
    public void pttTest8() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String firstName = "A" + System.currentTimeMillis();
            String lastName = "B" + System.currentTimeMillis();
            String email = "AB" + System.currentTimeMillis() + "@gmail.com";
            Long userId = createAndGetUserId(firstName, lastName, email);
            Long projectId = createAndGetProjectId(userId);

            Long completed = 5L;
            String startTime = "2019-02-18T10:00Z";
            String endTime = "2019-02-18T20:00Z";
            Long hoursWorked = 10L;

            CloseableHttpResponse response = createSession(httpclient, userId, projectId, startTime, endTime, completed);

            response = getReport(httpclient, "2019-02-18T20:00Z", "2019-02-19T20:00Z", userId, projectId, "false", "true");

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\n" +
                    "\"sessions\": [{\"startingTime\":\"" + startTime + "\",\"endingTime\":\"" + endTime + "\",\"hoursWorked\":" + hoursWorked + "}],\n" +
                    "\"totalHoursWorkedOnProject\":" +  hoursWorked + "\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
        }
    }

    //Purpose: Test a valid GET request with all parameters and includeCompletedPomodoros and includeTotalHoursWorkedOnProject as false
    @Test
    public void pttTest9() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String firstName = "A" + System.currentTimeMillis();
            String lastName = "B" + System.currentTimeMillis();
            String email = "AB" + System.currentTimeMillis() + "@gmail.com";
            Long userId = createAndGetUserId(firstName, lastName, email);
            Long projectId = createAndGetProjectId(userId);

            Long completed = 5L;
            String startTime = "2019-02-18T10:00Z";
            String endTime = "2019-02-18T20:00Z";
            Long hoursWorked = 10L;

            createSession(httpclient, userId, projectId, startTime, endTime, completed);

            CloseableHttpResponse response = getReport(httpclient, "2019-02-18T20:00Z", "2019-02-19T20:00Z", userId, projectId, "true", "true");

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\n" +
                    "\"sessions\": [{\"startingTime\":\"" + startTime + "\",\"endingTime\":\"" + endTime + "\",\"hoursWorked\":" + hoursWorked + "}]\n" +
                    "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
        }
    }



}
