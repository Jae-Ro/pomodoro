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

public class PTTcatpart_users_userId_projects_projectId extends Helper {

    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private boolean setupdone;
    private String userID;

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

            /* Now we create a base user and projects on which all operations are performed */
            try {
                //First we create a user and get a response back. That response is what is captured
                CloseableHttpResponse response = createUser("F1", "L1", "E1@gmail.com");

                //Set up a user ID for the remaining project test cases
                userID = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");

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

            // Just to make sure the setup happens in the off chance something went wrong between tests
            setupdone = true;
        }
        System.out.println("\n*** STARTING NEXT PROJECT TEST ***\n");
    }

    // Purpose: Test whether the app can get a specific project given a specific user.
    @Test
    public void pttTest1() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

        String id = null; //The project ID

        try { //This is where we begin actually doing things

            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create the first project */
            String projSchema = getValidProjectSchemaTypeString("0", "P1");
            CloseableHttpResponse response = createProject(userID, projSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            /* Now we make the actual GET call using the ID from the project we created */
            response = getProject(userID, id);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nUnexpected response status: " + status + "\n");
            }

            /* Print out the response */
            strResponse = EntityUtils.toString(entity);
            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Assert correctness */
            String expectedProjSchema = getValidProjectSchemaTypeString(id, "P1");
            JSONAssert.assertEquals(expectedProjSchema, strResponse, false);

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to create a project with an empty userID. Expected
    // response is a 400 - Bad request.
    @Test
    public void pttTest2() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String blankUserId = ""; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String projSchema = getValidProjectSchemaTypeString(blankUserId, "P1");
            CloseableHttpResponse response = createProject(blankUserId, "0", projSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) { //As a blank user ID would lead to a bad URL
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close(); //Close out the response
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether creating a project with an invalid userID (non int64) results in an
    // error or not. The expected return error code is 400 - "Bad request".
    @Test
    public void pttTest3() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String invalidUserId = "invalid"; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String projSchema = getValidProjectSchemaTypeString(invalidUserId, "P1");
            CloseableHttpResponse response = createProject(invalidUserId, "0", projSchema);

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) { //As an invalid user id makes for a bad request
                System.out.println("\nA 404 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }

            // Nothing to clear in the buffer because a 400 error means an empty output buffer
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to create a project with an empty projectID. Expected
    // response is a 400 - Bad request.
    @Test
    public void pttTest4() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String blankProjectId = ""; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String projSchema = getValidProjectSchemaTypeString(blankProjectId, "P1");
            CloseableHttpResponse response = createProject(userID, blankProjectId, projSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a blank project ID leads to an invalid endpoint URL
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

            // Nothing to clear in the buffer because a 400 error means an empty output buffer
            response.close(); //Close out the response
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to create a project with an invalid projectID. Expected
    // response is a 400 - Bad request.
    @Test
    public void pttTest5() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String invalidProjectId = "invalid"; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String projSchema = getValidProjectSchemaTypeString(invalidProjectId, "P1");
            CloseableHttpResponse response = createProject(userID, invalidProjectId, projSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As an invalid project id makes for a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

            // Nothing to clear in the buffer because a 400 error means an empty output buffer
            response.close(); //Close out the response
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: Test whether the app throws an error when trying to create a project with an empty project name.
    // Expected response is a 400 - Bad request.
    @Test
    public void pttTest6() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String blankProjectName = ""; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            String projSchema = getValidProjectSchemaTypeString("0", blankProjectName);
            CloseableHttpResponse response = createProject(userID, projSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a blank project name cannot be found
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

            // Nothing to clear in the buffer because a 400 error means an empty output buffer
            response.close(); //Close out the response
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether creating a duplicate project results in an error or not. The expected
    // return error code is 409 - "Resource conflict".
    @Test
    public void pttTest7() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String id = null; //This is the project ID

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project so there's a conflict at the end of it all */
            String projSchema = getValidProjectSchemaTypeString("0", "P1");
            CloseableHttpResponse response = createProject(userID, projSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            /* And here comes the duplicate request (project names must be unique) */
            response = createProject(userID, projSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 409) {
                System.out.println("\nA 409 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 409 instead of: " + status + "\n");
            }

            // Nothing to clear in the buffer because a 409 error means an empty output buffer
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether creating a project with an invalid schema results in an error or
    // not. The expected return error code is 400 - "Bad request".
    @Test
    public void pttTest8() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project so there's a conflict at the end of it all */
            String projectSchema = getInvalidProjectSchemaTypeString("200", "P1");
            CloseableHttpResponse response = createProject(userID, "200", projectSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpecting status code of 400 instead of " + status + "\n");
            }

            // Nothing to clear in the buffer because a 400 error means an empty output buffer
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether creating a project with all valid parameters works out. Fails for now
    // because PUT does not create a project if it does not exist yet. Since such a situation seems to be handled
    // different ways by different implementations, if we assume the PUT works, this test case should work.
    @Test
    public void pttTest9() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String id = null; //This is the project ID

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project with everything valid */
            String projectSchema = getValidProjectSchemaTypeString("200", "P1");
            CloseableHttpResponse response = updateProject(userID, "200", projectSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                System.out.println("\nA 404 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpecting status code of 404 instead of " + status + "\n");
            }

            // Nothing to clear in the buffer because a 404 error means an empty output buffer
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether deleting a project normally works out.
    @Test
    public void pttTest10() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String id = null; // This is the project id

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project with everything valid */
            String projectSchema = getValidProjectSchemaTypeString("0", "P1");
            CloseableHttpResponse response = createProject(userID, projectSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            /* Now to delete the project */
            response = deleteProject(userID, id);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpecting status code of 200 instead of " + status + "\n");
            }

            /* We can still print responses */
            strResponse = EntityUtils.toString(entity);
            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Assert correctness */
            String expectedProjSchema = getValidProjectSchemaTypeString(id, "P1");
            JSONAssert.assertEquals(expectedProjSchema, strResponse, false);

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether deleting a project normally works out. It's a duplicate, as the
    // variable being tested is whether the user gives consent. Assumption for deliverable 3b: Consent is always given.
    @Test
    public void pttTest11() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String id = null; // This is the project id

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project with everything valid */
            String projectSchema = getValidProjectSchemaTypeString("0", "P1");
            CloseableHttpResponse response = createProject(userID, projectSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            /* Now to delete the project */
            response = deleteProject(userID, id);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpecting status code of 200 instead of " + status + "\n");
            }

            /* We can still print responses */
            strResponse = EntityUtils.toString(entity);
            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Assert correctness */
            String expectedProjSchema = getValidProjectSchemaTypeString(id, "P1");
            JSONAssert.assertEquals(expectedProjSchema, strResponse, false);

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether deleting a project normally works out. It's a duplicate, as the
    // variable being tested is whether the user gives consent. Assumption for deliverable 3b: Consent is always given.
    @Test
    public void pttTest12() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String id = null; // This is the project id

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project with everything valid */
            String projectSchema = getValidProjectSchemaTypeString("0", "P1");
            CloseableHttpResponse response = createProject(userID, projectSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            /* Now to delete the project */
            response = deleteProject(userID, id);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("\nExpecting status code of 200 instead of " + status + "\n");
            }

            /* We can still print responses */
            strResponse = EntityUtils.toString(entity);
            System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

            /* Assert correctness */
            String expectedProjSchema = getValidProjectSchemaTypeString(id, "P1");
            JSONAssert.assertEquals(expectedProjSchema, strResponse, false);

            /* Clear and close */
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }
}
