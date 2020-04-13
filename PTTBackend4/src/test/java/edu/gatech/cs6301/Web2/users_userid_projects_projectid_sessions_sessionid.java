package edu.gatech.cs6301.Web2;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.Iterator;


public class users_userid_projects_projectid_sessions_sessionid {

    private String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
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

    // Test Case 1    <error>
    // Put_User_ID :  Empty
    // Purpose: Test if the user id is empty
    @Test
    public void pttTest1() throws Exception {
//       we determined this case is unnecessary as get will pull all users if empty
    }

    // Test Case 2  		<error>
    // Put_User_ID :  Not empty but not in database
    // Purpose: Test if the user id is not empty but doesn't exist in database
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            int id = 3443434;//getIdFromResponse(response);

            CloseableHttpResponse response = getUser(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Test Case 3  		<error>
    // Put_Project_ID :  Empty
    // Purpose: Test if the project id is empty
    @Test
    public void pttTest3() throws Exception {
//       we determined this case is unnecessary as get will pull all projects if empty
    }

    // Test Case 4  		<error>
    // Put_Project_ID :  Not empty but not in database
    // Purpose: Test if the project id is not empty but doesn't exist in database
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john@doe.org");
            int user_id = Integer.valueOf(getIdFromResponse(response));
            // EntityUtils.consume(response.getEntity());
            response.close();
            int project_id = 4354534;//getIdFromResponse(response);

            response = getProject(user_id, project_id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 5  		<error>
    // Put_Session_ID :  Empty
    // Purpose: Test if the session id is empty
    @Test
    public void pttTest5() throws Exception {
//       we determined this case is unnecessary as get will pull all sessions if empty
    }

    // Test Case 6  		<error>
    // Put_Session_ID :  Not empty but not in database
    // Purpose: Test if the session id is not empty but doesn't exist in database
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john12@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "ProjectName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String session_id = "324243";

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            response = putSession(user_id, project_id, session_id, startTime, endTime, counter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    // Test Case 7  		<error>
    // Put_Start Time :  Invalid time format
    // Purpose: Test if the start time format is correct
    @Test
    public void pttTest7() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john2@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-18T20";
            String newEndTime = "2019-02-18T20:00Z";
            int newCounter = 0;

            response = putSession(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 8  		<error>
    // Put_Start Time :  Start time is after current time
    // Purpose: Test if the start time is after the current time
    @Test
    public void pttTest8() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john33@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-04-26T20:00Z";
            String newEndTime = "2019-04-20T20:00Z";
            int newCounter = 0;

            response = putSession(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //    Test Case 9  		<error>
    //    Put_End Time :  Invalid time format
    // Purpose: Test if the end time format is correct
    @Test
    public void pttTest9() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john4@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-18T20:00Z";
            String newEndTime = "2019-02-18T20";
            int newCounter = 0;

            response = putSession(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 10 		<error>
    // Put_End Time :  End time is before start time
    // Purpose: Test if the end time is before the start time
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john5@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "ProjectName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-20T20:00Z";
            String newEndTime = "2019-02-18T20:00Z";
            int newCounter = 0;

            response = putSession(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 11 		<error>
    // Put_Counter :  Not an integer
    // Purpose: Test if the counter is not an interger
    @Test
    public void pttTest11() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john67@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-18T20:00Z";
            String newEndTime = "2019-02-20T20:00Z";
            int newCounter = 0;

            response = putSessionString(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Test Case 12 		<error>
    // Put_Counter :  Negative integer
    // Purpose: Test if the counter is a negative number
    @Test
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john7@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-20T20:00Z";
            String newEndTime = "2019-02-18T20:00Z";
            int newCounter = -4;

            response = putSession(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /* Test Case 13 		(Key = 1.1.1.1.1.1.)
    Put_User_ID    :  User_ID exists in database
    Put_Project_ID :  Project_ID exists in database
    Put_Session_ID :  Session_ID exists in database
    Put_Start Time :  Valid time format
    Put_End Time   :  Valid time format
    Put_Counter    :  Valid positive integer */
    // Purpose: Test if session can be created (everything is valid)
    @Test
    public void pttTest13() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john8@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-18T20:00Z";
            String newEndTime = "2019-02-20T20:00Z";
            int newCounter = 34;

            response = putSession(user_id, project_id, session_id, newStartTime, newEndTime, newCounter);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(200, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }


    /* Test Case 14 		(Key = 1.1.1.1.1.2.)
    Put_User_ID    :  User_ID exists in database
    Put_Project_ID :  Project_ID exists in database
    Put_Session_ID :  Session_ID exists in database
    Put_Start Time :  Valid time format
    Put_End Time   :  Valid time format
    Put_Counter    :  Empty */
    // Purpose: Test if the counter can be empty
    @Test
    public void pttTest14() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john9@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-20T20:00Z";
            String newEndTime = "2019-02-18T20:00Z";

            response = putSessionNoCounter(user_id, project_id, session_id, newStartTime, newEndTime);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /* Test Case 15 		(Key = 1.1.1.1.3.2.)
    Put_User_ID    :  User_ID exists in database
    Put_Project_ID :  Project_ID exists in database
    Put_Session_ID :  Session_ID exists in database
    Put_Start Time :  Valid time format
    Put_End Time   :  Empty
    Put_Counter    :  Empty */
    // Purpose: Test if the counter and the end time can be empty
    @Test
    public void pttTest15() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john10@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "2019-02-20T20:00Z";
            String newEndTime = "";

            response = putSessionNoCounter(user_id, project_id, session_id, newStartTime, newEndTime);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    /* Test Case 16 		(Key = 1.1.1.3.3.2.)
    Put_User_ID    :  User_ID exists in database
    Put_Project_ID :  Project_ID exists in database
    Put_Session_ID :  Session_ID exists in database
    Put_Start Time :  Empty
    Put_End Time   :  Empty
    Put_Counter    :  Empty */
    // Purpose: Test if the counter and the end time and the start time can be empty
    @Test
    public void pttTest16() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "john12@doe.org");
            String user_id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project Name");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            String startTime = "2019-02-18T20:00Z";
            String endTime = "2019-02-18T20:00Z";
            int counter = 0;

            CloseableHttpResponse sResponse =
                    createSession(user_id, project_id, startTime, endTime, counter);
            String session_id = getIdFromResponse(sResponse);
            cpResponse.close();

            String newStartTime = "";
            String newEndTime = "";

            response = putSessionNoCounter(user_id, project_id, session_id, newStartTime, newEndTime);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
                deleteresponse.close();

                CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
                deleteprojectresponse.close();

                CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
                deletesessioneesponse.close();
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(400, status);

            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(user_id));
            deleteresponse.close();

            CloseableHttpResponse deleteprojectresponse = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteprojectresponse.close();

            CloseableHttpResponse deletesessioneesponse = deleteSession(Integer.valueOf(user_id), Integer.valueOf(project_id), Integer.valueOf(session_id));
            deletesessioneesponse.close();

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse getUser(int id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProject(int user_id, int project_id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUsers() throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users");
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();

        return response;
    }

    private CloseableHttpResponse deleteUser(int id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse deleteProject(int user_id, int project_id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse deleteSession(int user_id, int project_id, int session_id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id+ "/sessions/"+session_id);

        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    private CloseableHttpResponse createSession(String user_id, String project_id, String startTime, String endTime, int counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+user_id+ "/projects/" + project_id + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String user_id, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+user_id+"/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putUser(String id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putProject(String user_id, String project_id) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSession(String user_id, String project_id, String session_id, String startTime, String endTime, int counter) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+user_id+ "/projects/" + project_id + "/sessions/"+session_id);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":" + counter + "}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessionString(String user_id, String project_id, String session_id, String startTime, String endTime, int counter) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+user_id+ "/projects/" + project_id + "/sessions/"+session_id);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"email\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse putSessionNoCounter(String user_id, String project_id, String session_id, String startTime, String endTime) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+user_id+ "/projects/" + project_id + "/sessions/"+session_id);
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);
        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }


    private String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
    }

    private String getIdFromStringResponse(String strResponse) throws JSONException {
        System.out.println("********STR\t\n\n\t\t"+strResponse);
        JSONObject object = new JSONObject(strResponse);

        String id = null;
        Iterator<String> keyList = object.keys();
        while (keyList.hasNext()){
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }
}