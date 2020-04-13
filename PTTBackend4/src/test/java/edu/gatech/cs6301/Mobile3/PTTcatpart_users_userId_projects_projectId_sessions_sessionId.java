package edu.gatech.cs6301.Mobile3;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Date;


public class PTTcatpart_users_userId_projects_projectId_sessions_sessionId extends PTTcatpart_users_userId_projects_projectId_sessions {


    /*
    Test Case 1  		<error>
    userID :  empty string
    */
    @Test
    public void pttTest1() throws Exception {
        emptyUserID();
    }


    /*
    Test Case 2  		<error>
    userID :  invalid int64 value
     */
    @Test
    public void pttTest2() throws Exception {
        invalidUserID();
    }


    /*
    Test Case 3  		<error>
    projectID :  empty string
    */
    @Test
    public void pttTest3() throws Exception {
        emptyProjectID();
    }


    protected void emptyProjectID() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String blankProjectId = "";
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, blankProjectId, startTime1, endTime1, counter1);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status != 404) { //As a blank project cannot be found
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }

            System.out.println("\nA 404 error code was returned as expected.\n");

            // Nothing to clear in the buffer because a "Project not Found" error means an empty output buffer
            response.close(); //Close out the response

        } catch (Exception e) {
            System.out.println("\nFailed test case where projectID is blank.\n");
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
    Test Case 4  		<error>
    projectID :  invalid int64 value
    */
    @Test
    public void pttTest4() throws Exception {
        invalidProjectID();
    }


    protected void invalidProjectID() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        try {
            /* Once we get a response, we just need the status code */
            String invalidProjectId = "invalid";
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, invalidProjectId, startTime1, endTime1, counter1);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status != 404) { //As a blank project cannot be found
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }

            System.out.println("\nA 400 error code was returned as expected.\n");

            // Nothing to clear in the buffer because a "Project not Found" error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where projectID is invalid.\n");
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
    Test Case 5  		<error>
    sessionID :  empty string
     */
    @Test
    public void pttTest5() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        String id = null;
        String old_schema = null;
        String new_schema = null;

        try {
            // 1 Create Session
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            old_schema = getValidSessionSchemaTypeString(id, startTime1, endTime1, counter1);

            // 2 Update Session
            String emptySessionId = "";
            String startTime2 = df.format(new Date());
            String endTime2 = df.format(new Date());
            String counter2 = "2";
            new_schema = getValidSessionSchemaTypeString(emptySessionId, startTime2, endTime2, counter2);
            response = updateSession(userId, projectId, emptySessionId, new_schema);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 405) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we tried to update the info with empty sessionId!!");
            }

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();

        } catch (Exception e) {
            System.out.println("\nFailed empty Project Id test case.\n");
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
    Test Case 6  		<error>
    sessionID :  invalid int64 value
    */
    @Test
    public void pttTest6() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        String id = null;
        String old_schema = null;
        String new_schema = null;

        try {
            // 1 Create Session
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            old_schema = getValidSessionSchemaTypeString(id, startTime1, endTime1, counter1);

            // 2 Update Session
            String invalidSessionId = "invalid";
            String startTime2 = df.format(new Date());
            String endTime2 = df.format(new Date());
            String counter2 = "2";
            new_schema = getValidSessionSchemaTypeString(invalidSessionId, startTime2, endTime2, counter2);
            response = updateSession(userId, projectId, invalidSessionId, new_schema);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we tried to update the info with invalid sessionId!!");
            }

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();

        } catch (Exception e) {
            System.out.println("\nFailed Invalid Project Id test case.\n");
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
    startTime :  empty string
    */
    @Test
    public void pttTest7() throws Exception {
        emptyStartTime();
    }


    /*
    Test Case 8  		<error>  (follows [if])
    startTime :  invalid ISO-8601 format
    */
    @Test
    public void pttTest8() throws Exception {
        invalidStartTime();
    }


    /*
    Test Case 9  		<error>  (follows [if])
    endTime :  empty string
    */
    @Test
    public void pttTest9() throws Exception {
        emptyEndTime();
    }


    /*
    Test Case 10 		<error>  (follows [if])
    endTime :  invalid ISO-8601 format
    */
    @Test
    public void pttTest10() throws Exception {
        invalidEndTime();
    }


    /*
    Test Case 11 		<error>  (follows [if])
    counter :  negative
    */
    @Test
    public void pttTest11() throws Exception {
        negativeCounter();
    }


    /*
    Test Case 12 		(Key = 1.3.3.3.1.0.0.0.)
    Endpoint      :  /users/{userId}/projects/{projectId}/sessions/{sessionId}/PUT
    userID        :  valid int64 value
    projectID     :  valid int64 value
    sessionID     :  valid int64 value
    sessionSchema :  invalidSchema
    startTime     :  <n/a>
    endTime       :  <n/a>
    counter       :  <n/a>
    */
    @Test
    public void pttTest12() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        String id = null;
        String old_schema = null;
        String new_schema = null;

        try {
            // 1 Create Session
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            old_schema = getValidSessionSchemaTypeString(id, startTime1, endTime1, counter1);

            // 2 Update Session
            String startTime2 = df.format(new Date());
            String endTime2 = df.format(new Date());
            String counter2 = "2";
            new_schema = getInvalidSessionSchemaTypeString(id, startTime2, endTime2, counter2);
            response = updateSession(userId, projectId, id, new_schema);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we tried to update the info with invalid schema!!");
            }

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();

        } catch (Exception e) {
            System.out.println("\nFailed Invalid Schema Put test case.\n");
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
    Test Case 13 		(Key = 1.3.3.3.2.3.3.2.)
    Endpoint      :  /users/{userId}/projects/{projectId}/sessions/{sessionId}/PUT
    userID        :  valid int64 value
    projectID     :  valid int64 value
    sessionID     :  valid int64 value
    sessionSchema :  validSchema
    startTime     :  valid ISO-8601 format
    endTime       :  valid ISO-8601 format
    counter       :  nonnegative
    */
    @Test
    public void pttTest13() throws Exception {
        //Connection Setup
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;

        String old_id = null;
        String new_id = null;
        String old_schema = null;
        String new_schema = null;

        try {
            // 1 Create Session
            String startTime1 = df.format(new Date());
            String endTime1 = df.format(new Date());
            String counter1 = "1";
            response = createSession(userId, projectId, startTime1, endTime1, counter1);
            old_id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            old_schema = getValidSessionSchemaTypeString(old_id, startTime1, endTime1, counter1);

            // Clear
            EntityUtils.consume(response.getEntity());
            response.close();

            // 2 Update Session
            String startTime2 = df.format(new Date());
            String endTime2 = df.format(new Date());
            String counter2 = "2";
            new_schema = getValidSessionSchemaTypeString(old_id, startTime2, endTime2, counter2);
            response = updateSession(userId, projectId, old_id, new_schema);

            // Check Status
            HttpEntity entity;
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 200 instead of " + status + " because we tried to update the info with invalid schema!!");
            }

            String strResponse;
            strResponse = EntityUtils.toString(entity);
            new_id = extractFieldFromJSONObjectTypeString(strResponse, "id");

            /* Assert correctness */
            JSONAssert.assertEquals(new_id, old_id, true);
            JSONAssert.assertEquals(strResponse, new_schema, true);


            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();

        } catch (Exception e) {
            System.out.println("\nFailed valid Schema Put test case.\n");
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
}