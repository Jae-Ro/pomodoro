package edu.gatech.cs6301.Web2;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
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

public class Users_userId_projects_projectId_report {
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

    @Test
    //    Test Case 1  		<error>
    //    Get_User_ID :  Empty
    // Purpose: Test if the user id is empty
    public void pttTest1() throws Exception {

    //       we determined this case is unnecessary as get will pull all users if empty
    }

    // Test Case 2  		<error>
    // Get_User_ID :  Not empty but not in database
    // Purpose: Test if the user id is not empty but doesn't exist in database
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        try {
            CloseableHttpResponse user1_response =
                    createUser("John", "Doe", "johndoe@doe.org");

            CloseableHttpResponse user2_response =
                    createUser("James", "Doe", "Jamesdoe@doe.org");

            HttpEntity user1_entity;
            HttpEntity user2_entity;

            user1_entity = user1_response.getEntity();
            user2_entity = user2_response.getEntity();

            String user1_strResponse = EntityUtils.toString(user1_entity);
            String user2_strResponse = EntityUtils.toString(user2_entity);

            System.out.println("*** String response " + user1_strResponse + " (" + user1_response.getStatusLine().getStatusCode() + ") ***");

            String id1 = getIdFromStringResponse(user1_strResponse);
            String id2 = getIdFromStringResponse(user2_strResponse);
            String missingId = id1 + id2; // making sure the ID is not present
    //            String missingId = "xyz" + id1 + id2; // making sure the ID is not present

            CloseableHttpResponse response = getUser(missingId);
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            System.out.println("*** String response " + response + " (" + status + ") ***");

            EntityUtils.consume(response.getEntity());
            response.close();
            user1_response.close();
            user2_response.close();

        }
        finally {
            httpclient.close();
        }
    }

    //    Test Case 3  		<error>
    //    Get_Project_ID :  Empty
    // Purpose: Test if the project id is empty
    @Test
    public void pttTest3() throws Exception {

    //       we determined this case is unnecessary as get will pull all projects if empty
    }

    //    Test Case 4  		<error>
    //    Get_Project_ID :  Not empty but not in database
    // Purpose: Test if the project id is not empty but doesn't exist in database
    @Test
    public void pttTest4() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            response = getProject((user_id), "789");

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

            CloseableHttpResponse deleteProject = deleteProject((user_id), (project_id));
            deleteProject.close();
            response.close();
        } finally {
            httpclient.close();
        }
    }

    //    Test Case 5  		<error>
    //    Post_Start_Time :  Invalid time format
    // Purpose: Test if the start time format is correct
    @Test
    public void pttTest5() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"2019-02-18T20", "April 2023", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if(status == 201 ) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());

            response.close();


        } finally {
            httpclient.close();
        }
    }


    //    Test Case 6  		<error>
    //    Post_Start_Time :  Empty
    // Purpose: Test if the start time format is empty
    @Test
    public void pttTest6() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"", "2019-02-20T20:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if(status == 201 ) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);


                    System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();


        } finally {
            httpclient.close();
        }
    }

    //    Test Case 7  		<error>
    //    Post_Start_Time :  Start time is after current time
    // Purpose: Test if the start time format is after current time
    @Test
    public void pttTest7() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();


            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"2022-03-20T20:00Z", "2022-02-20T20:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if(status == 201 ) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());

            response.close();


        } finally {
            httpclient.close();
        }
    }

    //  Test Case 8  		<error>
    //   Get_To_End_Time :  Invalid time format
    // Purpose: Test if the end time format is correct
    @Test
    public void pttTest8() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"2022-02-20T20:00Z", "2022-02-20T20Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if(status == 201 ) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    //    Test Case 9  		<error>
    //    Post_End_Time :  Empty
    // Purpose: Test if the end time format is empty
    @Test
    public void pttTest9() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"2022-02-20T20:00Z", "", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if(status == 201 ) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    //    Test Case 10 		<error>
    //    Post_End_Time :  End time is before start time
    // Purpose: Test if the end time is before the start time
    @Test
    public void pttTest10() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();


            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"2022-02-20T21:00Z", "2022-02-20T20:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if(status == 201 ) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());

            response.close();

        } finally {
            httpclient.close();
        }
    }

    //   Test Case 11 		<error>
    //   Get_Include_Completed_Pomodoros :  Invalid input
    @Test
    public void pttTest11() throws Exception {
        //       we determined this case is unnecessary as it just wanna test if it is a boolean
    }

    //   Test Case 12 		<error>
    //   Get_Include_Total_Hours_Worked_On_Project :  Invalid input
    @Test
    public void pttTest12() throws Exception {
        //       we determined this case is unnecessary as it just wanna test if it is a boolean
    }

    //   Test Case 13 		(Key = 1.1.1.1.1.1.)
    //   Get_User_ID                               :  User_ID exists in database
    //   Get_Project_ID                            :  Project_ID exists in database
    //   Get_From_Start_Time                       :  Valid time format
    //   Get_To_End_Time                           :  Valid time format
    //   Get_Include_Completed_Pomodoros           :  Valid boolean
    //   Get_Include_Total_Hours_Worked_On_Project :  Valid
    //   Purpose: Test the getting report is working correctly
    @Test
    public void pttTest13() throws Exception {
        deleteUsers();
        try {
            CloseableHttpResponse response =
                    createUser("John", "Doe", "bellaS@doe.org");
            String user_id = getIdFromResponse(response);
//            System.out.println("*** String response " + response + " (" + response.getStatusLine().getStatusCode() + ") ***");
            response.close();

            CloseableHttpResponse cpResponse =
                    createProject(user_id, "Project SooName");
            String project_id = getIdFromResponse(cpResponse);
//            System.out.println("*** String response " + cpResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            cpResponse.close();

            CloseableHttpResponse Sessionresponse =
                    createSession(user_id,project_id,"2022-02-20T20:00Z", "2022-02-21T20:00Z", "2");
//            System.out.println("*** String response " + Sessionresponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Sessionresponse.close();

            String from = "2022-02-20T20:00Z";
            String to = "2022-02-21T20:00Z";
            CloseableHttpResponse reportResponse = getReport(user_id, project_id, from, to);

            int status = reportResponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = reportResponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            System.out.println(strResponse);
//            String id = getIdFromStringResponse(strResponse);
//
//            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
//            System.out.println("user:" + user_id);
//            System.out.println("project:" + project_id);
            String expectedJson = "{\"sessions\":[{\"startingTime\":\"2022-02-20T20:00Z\",\"endingTime\":\"2022-02-21T20:00Z\",\"hoursWorked\":24}]}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
//            EntityUtils.consume(response.getEntity());

            response.close();
        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

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

    private CloseableHttpResponse updateUser(String id, String firstName, String lastName, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
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

    private String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        return id;
    }
    private String getIdFromStringResponse(String strResponse) throws JSONException {
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

    public void getAllUsersId() throws Exception {
        CloseableHttpResponse response = getAllUsers();
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String strResponse = EntityUtils.toString(entity);
            System.out.println(strResponse);
            // parsing JSON
            JSONArray result = new JSONArray(strResponse);
            for(int i=0 ; i<result.length() ; i++) {
                JSONObject object = result.getJSONObject(i);
                String id = null;
                Iterator<String> keyList = object.keys();
                while (keyList.hasNext()){
                    String key = keyList.next();
                    if (key.equals("id")) {
                        id = object.get(key).toString();
                        System.out.println(id);
                    }
                }
            }

        }
        response.close();
    }


//    private CloseableHttpResponse deleteUser(int id) throws IOException {
//        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
//        httpDelete.addHeader("accept", "application/json");
//
//        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
//        CloseableHttpResponse response = httpclient.execute(httpDelete);
//        System.out.println("*** Raw response " + response + "***");
//        // EntityUtils.consume(response.getEntity());
//        // response.close();
//        return response;
//    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
//        response.close();
        return response;
    }

    private void deleteUsers() throws IOException {
        try {
            CloseableHttpResponse response = getAllUsers();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String strResponse = EntityUtils.toString(entity);
                System.out.println(strResponse);
                // parsing JSON
                JSONArray result = new JSONArray(strResponse);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = result.getJSONObject(i);
                    String id = null;
                    Iterator<String> keyList = object.keys();
                    while (keyList.hasNext()) {
                        String key = keyList.next();
                        if (key.equals("id")) {
                            id = object.get(key).toString();
                            System.out.println(id);
                            response = deleteUser(id);
                            response.close();
                        }
                    }
                }
            }
            response.close();
        } catch (Exception e) {
            System.out.print("Can not delete all users");
        }
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

    private CloseableHttpResponse getProject(String user_id, String project_id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    private CloseableHttpResponse deleteProject(String user_id, String project_id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + user_id + "/projects/" + project_id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }


    private CloseableHttpResponse createSession(String Userid, String Projectid, String startTime, String endTime, String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + Userid + "/projects/" + Projectid + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," +
                "\"endTime\":\"" + endTime + "\"," +
                "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    //    GET /users/{userId}/projects/{projectId}/report
    private CloseableHttpResponse getReport(String user_id, String project_id, String from, String to) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + user_id + "/projects/" + project_id + "/report" + "?from=" + from + "&to=" + to);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

}
