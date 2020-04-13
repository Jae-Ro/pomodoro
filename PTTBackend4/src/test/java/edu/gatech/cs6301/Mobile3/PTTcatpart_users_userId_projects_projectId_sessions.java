package edu.gatech.cs6301.Mobile3;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.text.DateFormat;
import java.util.*;
import java.text.SimpleDateFormat;
import org.skyscreamer.jsonassert.JSONAssert;


public class PTTcatpart_users_userId_projects_projectId_sessions extends Helper {

    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private boolean setupdone;
    protected String userId;
    protected String projectId;
    protected TimeZone tz = TimeZone.getTimeZone("UTC");
    protected DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset


    /* Overloading the below method from the Helper file. This is because the session tests need a user and project
     with which to create sessions. */
    @Before
    public void runBefore() {
        if (!setupdone) {
            System.out.println("\n*** SETTING UP SESSION TEST ***\n");

            //Time Setup
            df.setTimeZone(tz);

            // Increase max total connection to 100
            cm.setMaxTotal(100);
            // Increase default max connection per route to 20
            cm.setDefaultMaxPerRoute(10);

            /* Setup time */
            httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

            try {
                deleteAllUsers(); //This is done to clear out all projects
            } catch (Exception e) { //Exception handling needed to keep IntelliJ happy
                System.out.println("Unable to delete all users before running test case");
            }

            /* Now we create a base user and project on which all operations are performed */
            try {
                //First we create a user and get a response back.
                CloseableHttpResponse response = createUser("F1", "L1", "E1@gmail.com");

                //Set up a user ID for the remaining project test cases
                userId = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");

                //Second we create a project and get a response back.
                response = createProject(userId, "0", "P1");

                //Set up a project ID for the remaining project test cases
                projectId = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");

                //Close out the response. That is, the connection made from this HTTP call is closed off (?)
                response.close();

                //This clears out the entity stream. That is, it makes sure that "response" has been processed fully
                //and the HTTP client does not have anything left in its output buffer
                EntityUtils.consume(response.getEntity());

                //Now shut off the connection to the remote server completely
                httpclient.close();
            } catch (Exception e) {
                System.out.println("\nError in trying to add a user/project during test case setup.\n");
            }

            // Just to make sure the setup happens in the off chance something went wrong between tests
            setupdone = true;
        }
        System.out.println("\n*** STARTING NEXT SESSION TEST ***\n");
    }


    /*
    Test Case 1  		<single>
    Endpoint :  /users/{userId}/projects/{projectId}/sessions/GET
    */
    @Test
    public void pttTest1() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        String id = null;
        String expectedJson = "";
        String schema = null;

        try {
            expectedJson += "["; //Expect a list of Sessions

            // Create Session 1
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            schema = getValidSessionSchemaTypeString(id, startTime1, endTime1, counter1);
            expectedJson += schema;
            expectedJson += ",";

            // Create Session 2
            String startTime2 = df.format(new Date());
            String endTime2 = df.format(new Date());
            String counter2 = "2";
            response = createSession(userId, projectId, startTime2, endTime2, counter2);


            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nUnexpected response status: " + status + "\n");
            }

            strResponse = EntityUtils.toString(response.getEntity());
            /* Print out the response */
            id = extractFieldFromJSONObjectTypeString(strResponse, "id");
            schema = getValidSessionSchemaTypeString(id, startTime2, endTime2, counter2);
            expectedJson += schema;
            expectedJson += "]";
            //System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");


            //Get all Sessions
            response = getAllSessions(userId, projectId);

            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nUnexpected response status: " + status + "\n");
            }

            strResponse = EntityUtils.toString(response.getEntity());


            System.out.println(expectedJson);
            System.out.println(strResponse);
            /* Assert correctness */
            JSONAssert.assertEquals(expectedJson, strResponse, false);

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();

        } catch (Exception e) {
            System.out.println("\nFailed GET request test case.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 2  		<error>
    userID :  empty string
     */
    @Test
    public void pttTest2() throws Exception {
        emptyUserID();
    }


    protected void emptyUserID() throws Exception{
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String blankUserId = "";
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(blankUserId, projectId, startTime1, endTime1, counter1);
            response.close();

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status != 404) { //As a blank user cannot be found
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }

            System.out.println("\nA 404 error code was returned as expected.\n");

            // Nothing to clear in the buffer because a "User not Found" error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where userId is blank.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 3  		<error>
    userID :  invalid int64 value
    */
    @Test
    public void pttTest3() throws Exception {
        invalidUserID();
    }


    protected void invalidUserID() throws Exception{
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String invalidUserId = "invalid";
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(invalidUserId, projectId, startTime1, endTime1, counter1);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) { //As an invalid user means a bad request
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed test case where userID is invalid (not an int64).\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 4  		<error>  (follows [if])
    startTime :  empty string
    */
    @Test
    public void pttTest4() throws Exception {
        emptyStartTime();
    }


    protected void emptyStartTime() throws Exception{
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String startTime1 = "";
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { //As an invalid user means a bad request
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed test case where startTime is blank.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 5  		<error>  (follows [if])
    startTime :  invalid ISO-8601 format
     */
    @Test
    public void pttTest5() throws Exception {
        invalidStartTime();
    }


    protected void invalidStartTime() throws Exception{
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String startTime1 = "01:47:43ZT2020-02-24";
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { //As an invalid user means a bad request
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed test case where startTime is invalid.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 6  		<error>  (follows [if])
    endTime :  empty string
    */
    @Test
    public void pttTest6() throws Exception {
        emptyEndTime();
    }


    protected void emptyEndTime() throws Exception{
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String startTime1 = df.format(new Date());
            String endTime1 = "";
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { //As an invalid user means a bad request
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed test case where endTime is blank.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 7  		<error>  (follows [if])
    endTime :  invalid ISO-8601 format
    */
    @Test
    public void pttTest7() throws Exception {
        invalidEndTime();
    }


    protected void invalidEndTime() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String startTime1 = df.format(new Date());
            String endTime1 = "01:47:43ZT2020-02-24";
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { //As an invalid user means a bad request
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed test case where endTime is invalid.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 8  		<error>  (follows [if])
    counter :  negative
    */
    @Test
    public void pttTest8() throws Exception {
        negativeCounter();
    }


    protected void negativeCounter() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "-1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) { //As an invalid user means a bad request
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed test case where counter is invalid.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 9  		(Key = 2.3.1.1.0.0.0.)
    Endpoint      :  /users/{userId}/projects/{projectId}/sessions/POST
    userID        :  valid int64 value
    projectID     :  valid int64 value
    sessionSchema :  invalidSchema
    startTime     :  <n/a>
    endTime       :  <n/a>
    counter       :  <n/a>
    */
    @Test
    public void pttTest9() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Create a session so there's a conflict at the end of it all */
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            String schema = getInvalidSessionSchemaTypeString("0", startTime1, endTime1, counter1);
            response = createSession(userId, projectId, schema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpecting status code of 400 instead of " + status + "\n");
            }

//            /* We can still print responses */
//            strResponse = EntityUtils.toString(entity);
//            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed invalid session schema test case.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /*
    Test Case 10 		(Key = 2.3.1.2.3.3.2.)
    Endpoint      :  /users/{userId}/projects/{projectId}/sessions/POST
    userID        :  valid int64 value
    projectID     :  valid int64 value
    sessionSchema :  validSchema
    startTime     :  valid ISO-8601 format
    endTime       :  valid ISO-8601 format
    counter       :  nonnegative
    */
    @Test
    public void pttTest10() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;
        String id;

        try {
            /* Create a session so there's a conflict at the end of it all */
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            String schema = getValidSessionSchemaTypeString("0", startTime1, endTime1, counter1);
            response = createSession(userId, projectId, schema);

            /* Remember the POST call has effectively already been made. So now we just directly test "response" */
            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nUnexpected response status: " + status + "\n");
            }

            /* Now to print out the response */
            strResponse = EntityUtils.toString(entity);
            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Assert that everything worked */
            id = extractFieldFromJSONObjectTypeString(strResponse, "id");
            String proj1_schema = getValidSessionSchemaTypeString(id, startTime1, endTime1, counter1);
            System.out.println(proj1_schema);
            System.out.println(strResponse);


            /* Clean and close */
            JSONAssert.assertEquals(proj1_schema, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } catch (Exception e) {
            System.out.println("\nFailed invalid session schema test case.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
                throw e;
            }
        }
    }


    /* Overloading the below method from the Helper file. This is because the server should be clean after this file
    has run its tests. */
    @After
    public void runAfter() {

        try {
            deleteAllUsers(); //This is done to clear out all sessions
        } catch (Exception e) { //Exception handling needed to keep IntelliJ happy
            System.out.println("Unable to delete all users after test case completion");
        }

        System.out.println("*** ENDING TEST ***");
    }
}

