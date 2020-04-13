package edu.gatech.cs6301.Mobile3;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class PTTcatpart_users_userId_projects_projectId_report extends Helper {

    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private boolean setupdone;
    private String userID;
    private String projectID;
    private String validStart = "2007-04-05T14:30";
    private String invalidStart = "January102019";
    private String validEnd = "2007-04-05T16:30";
    private String invalidEnd = "January202019";

    /* Overloading the below method from the Helper file. This is because the project tests need a user with which to
    create projects. */
    @Before
    public void runBefore() {
        if (!setupdone) {
            System.out.println("\n*** SETTING UP PROJECT TEST ***\n");
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

            /* Now we create a base user*/
            try {
                //First we create a user and get a response back. That response is what is captured
                CloseableHttpResponse response = createUser("F1", "L1", "E1@gmail.com");

                //Set up a user ID for the remaining project test cases
                userID = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
                response = createProject(userID, "0", "P1");
                projectID = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
                //Close out the response. That is, the connection made from this HTTP call is closed off (?)
                response.close();

                //This clears out the entity stream. That is, it makes sure that "response" has been processed fully
                //and the HTTP client does not have anything left in its output buffer
                EntityUtils.consume(response.getEntity());

                //Now shut off the connection to the remote server completely
                httpclient.close();
            } catch (Exception e) {
                System.out.println("\nError in trying to add a user during test case setup.\n");
            }
            System.out.println("\n*** STARTING NEXT PROJECT REPORT TEST ***\n");
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an empty userID String. Expected
    // response is a 404 - User not found.
    @Test
    public void pttTest1 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String blankUserId = ""; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(blankUserId, projectID, validStart, validEnd, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) { //As a blank user ID would lead to a bad URL
                System.out.println("\nA 404 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where userID is blank.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an invalid user ID. Expected
    // response is a 404 - User not found.
    @Test
    public void pttTest2 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String invalidUserID = ""+(Integer.valueOf(userID)+20); // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(invalidUserID, projectID, validStart, validEnd, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) { //As a blank user ID would lead to a bad URL
                System.out.println("\nA 404 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where userID is invalid.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an empty from field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest3 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = "";  //This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, emptyString, validEnd, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a blank from would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where from field is blank.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an invalid from field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest4 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */
            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, invalidStart, validEnd, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid from would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where from field is invalid.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an empty to field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest5 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, emptyString, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a blank to would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where to field is blank.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an invalid to field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest6 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, invalidEnd, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            System.out.println("Test 6 Status: "+status);
            if (status == 400) { //As an invalid to would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where to field is invalid.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an empty boolean field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest7 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, null, "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a null boolean would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where include complete Pomodoros field is null.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an invalid boolean field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest8 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, emptyString, "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid boolean would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where complete Pomodoros field is empty.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an empty field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest9 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, "true", null);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a null boolean would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where include total hours field is null.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to get a report with an invalid field. Expected
    // response is a 400 - bad request.
    @Test
    public void pttTest10 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, "true", emptyString);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid boolean would lead to a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where include total hours field is blank.\n");
            throw e;

        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app returns a valid report schema when all parameters are valid and
    // all optional parameters are true
    @Test
    public void pttTest11 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, "true", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid boolean would lead to a bad request
                System.out.println("\nA 200 response code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 200 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where all parameters are valid and all optional parameters are true.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app returns a valid report schema when all parameters are valid and
    // all optional parameters are true except for include total hours.
    @Test
    public void pttTest12 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, "true", "false");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid boolean would lead to a bad request
                System.out.println("\nA 200 response code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 200 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where all parameters are valid and all optional parameters are true except for include total hours.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app returns a valid report schema when all parameters are valid and
    // all optional parameters are true except for include pomodoros.
    @Test
    public void pttTest13 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, "false", "true");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid boolean would lead to a bad request
                System.out.println("\nA 200 response code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 200 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where all parameters are valid and all optional parameters are true except for include pomodoros.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }
    // Purpose: Test whether the app returns a valid report schema when all parameters are valid and
    // all optional parameters are false.
    @Test
    public void pttTest14 () throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String emptyString = ""; //Just for this test.

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = getReport(userID, projectID, validStart, validEnd, "false", "false");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a invalid boolean would lead to a bad request
                System.out.println("\nA 200 response code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 200 instead of: " + status + "\n");
            }
            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } catch (Exception e) {
            System.out.println("\nFailed test case where all parameters are valid and all optional parameters are false.\n");
            throw e;
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }
}
