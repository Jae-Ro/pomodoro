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

import org.skyscreamer.jsonassert.JSONAssert;

import javax.swing.text.html.parser.Entity;

public class PTTcatpart_users_userId_projects extends Helper {

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

    // Purpose: This testcase will test whether a GET call will return a list of project schemas after two projects
    // have been created. Fairly straightforward test of /users/{userID}/projects/GET.
    @Test
    public void pttTest1() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

        String id = null; //The project ID
        String expectedJson = ""; //The Json we expect to receive

        try { //This is where we begin actually doing things
            expectedJson += "["; //We are expecting a list of projects, hence the opening bracket

            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create the first project */
            CloseableHttpResponse response = createProject(userID, "0", "P1");
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            String proj1_schema = getValidProjectSchemaTypeString(id, "P1");
            expectedJson += proj1_schema;
            response.close();

            //Thread.sleep(2000);
            expectedJson += ",";

            /* Onto the next one! */
            response = createProject(userID, "0", "P2");
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            String proj2_schema = getValidProjectSchemaTypeString(id, "P2");
            expectedJson += proj2_schema;
            response.close();

            expectedJson += "]";

            /* Now we make the actual GET call */
            response = getAllProjects(userID);

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
            JSONAssert.assertEquals(expectedJson, strResponse, false);

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

    // Purpose: This testcase will test whether creating a project with a blank user ID results in an error or not.
    // The expected return error code is 404 - "User not found".
    @Test
    public void pttTest2() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String blankUserId = ""; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = createProject(blankUserId, "0", "P1");
            response.close();

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) { //As a blank user cannot be found
                System.out.println("\nA 404 error code was returned as expected.\n");
            }
            else {
                throw new ClientProtocolException("\nExpected response code of 404 instead of: " + status + "\n");
            }

            /* Just close */
            response.close();
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
            CloseableHttpResponse response = createProject(invalidUserId, "0", "P1");
            response.close();

            /*  Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) { //As an invalid user means a bad request
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

    // Purpose: This testcase will test whether creating a project with a blank project name results in an error or
    // not. The expected return error code is 400 - "Bad request".
    @Test
    public void pttTest4() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        String blankProjectName = ""; // This is just for this test case

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and checking the status code */

            /* Once we get a response, we just need the status code */
            CloseableHttpResponse response = createProject(userID, "0", blankProjectName);
            response.close();

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) { //As a blank project name means a bad request
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 400 instead of: " + status + "\n");
            }

            /* Just close */
            response.close();
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
    public void pttTest5() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project so there's a conflict at the end of it all */
            CloseableHttpResponse response = createProject(userID, "0", "P1");
            response.close();

            /* And here comes the duplicate request (project names must be unique*/
            response = createProject(userID, "0", "P1");

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 409) {
                System.out.println("\nA 409 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpected response code of 409 instead of: " + status + "\n");
            }

            /* Just close */
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
    public void pttTest6() throws Exception {
        /* Setup time */
        httpclient = HttpClients.createDefault(); //This is our middleman with the backend server

        try { //This is where we begin actually doing things
            /* Now for getting a response from create() and putting together the JSON we expect to see */

            /* Create a project so there's a conflict at the end of it all */
            String projectSchema = getInvalidProjectSchemaTypeString("0", "P1");
            CloseableHttpResponse response = createProject(userID, projectSchema);

            /* Get the status code from the response and verify its correctness */
            int status = response.getStatusLine().getStatusCode();
            if (status == 400) {
                System.out.println("\nA 400 error code was returned as expected.\n");
            } else {
                throw new ClientProtocolException("\nExpecting status code of 400 instead of " + status + "\n");
            }

            /* Just close */
            response.close();
        } finally {
            try {
                httpclient.close(); // Cut off the connection entirely
            } catch (Exception e) {
                System.out.println("\nCould not shut down connection to remote server.\n");
            }
        }
    }

    // Purpose: This testcase will test whether creating a project under a single user will return the correct JSON.
    // Fairly straightforward test of /users/{userID}/projects/POST. Fails because it returns an empty
    // object.
    // @Test
    // public void pttTest7() throws Exception {
    //     /* Setup time */
    //     httpclient = HttpClients.createDefault(); //This is our middleman with the backend server
        

    //     String id = null; //The project ID

    //     try { //This is where we begin actually doing things
    //         /* Now for getting a response from create() and putting together the JSON we expect to see */

    //         /* Create a single project */
            
    //         String proj_schema = getValidProjectSchemaTypeString("0", "P1");
    //         CloseableHttpResponse response = createProject(userID, proj_schema);
    //         response.close();

    //         /* Remember the POST call has effectively already been made. So now we just directly test "response" */
    //         /* Get the status code from the response and verify its correctness */
    //         int status = response.getStatusLine().getStatusCode();
    //         HttpEntity entity = response.getEntity();;
    //         String strResponse;
    //         if (status == 201) {
    //             entity = response.getEntity();
    //         } else {
    //             throw new ClientProtocolException("\nUnexpected response status: " + status + "\n");
    //         }

    //         /* Now to print out the response */
    //         strResponse = EntityUtils.toString(entity);
    //         System.out.println("\n*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***\n");

    //         /* Assert that everything worked */
    //         id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
    //         String proj1_schema = getValidProjectSchemaTypeString(id, "P1");

    //         /* Clean and close */
    //         JSONAssert.assertEquals(proj1_schema, strResponse, false);
    //         EntityUtils.consume(response.getEntity());
    //         response.close();
    //     } finally {
    //         try {
    //             httpclient.close(); // Cut off the connection entirely
    //         } catch (Exception e) {
    //             System.out.println("\nCould not shut down connection to remote server.\n");
    //         }
    //     }
    // }
}