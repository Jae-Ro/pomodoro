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

public class Users_userid_projects_projectid_sessions {
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
    // Purpose: <test user id being empty>
    public void pttTest1() throws Exception {

        // we determined this case is unnecessary as get will pull all users if empty
    }

    @Test
    // Purpose: <test user id being a valid integer but not in database>
    public void pttTest2() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "billiam72@doe.org");

            String id = getIdFromResponse(response);
            response.close();

            response = getUser(789999999);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(id));
            deleteresponse.close();
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test project id being empty>
    public void pttTest3() throws Exception {

        // we determined this case is unnecessary as get will pull all projects if empty
    }

    @Test
    // Purpose: <test project id being a valid integer but not in database>
    public void pttTest4() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            response = getProject(Integer.valueOf(user_id), 789999999);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteProject.close();
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the start time having an invalid
    // format>
    public void pttTest5() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2019-02-20T20:00Z",
                    "April 2023", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the start time being empty>
    public void pttTest6() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "", "2019-02-20T20:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the start time after the current time
    // (this should cause an error)>
    public void pttTest7() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T21:00Z",
                    "2022-02-20T20:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the end time having an invalid format>
    public void pttTest8() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T20:00Z",
                    "June 13 10:40", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the end time being empty>
    public void pttTest9() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T20:00Z", "", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the end time before the start time
    // (this should error)>
    public void pttTest10() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T05:00Z",
                    "2022-02-19T10:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the counter being empty (in our
    // implementation, this fails)>
    public void pttTest11() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T20:00Z",
                    "2022-02-20T20:00Z", "");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the counter not an integer>
    public void pttTest12() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T20:00Z",
                    "2022-02-20T20:00Z", "5.789");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with the counter being a negative integer>
    public void pttTest13() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T20:00Z",
                    "2022-02-20T20:00Z", "-10");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 400) {
                entity = Sessionresponse.getEntity();
            } else {
                if (status == 201) {
                    entity = Sessionresponse.getEntity();

                    String strResponse = EntityUtils.toString(entity);

                    System.out.println("*** String response " + strResponse + " ("
                            + response.getStatusLine().getStatusCode() + ") ***");

                    CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
                    deleteUser.close();
                    CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id),
                            Integer.valueOf(project_id));
                    deleteProject.close();
                    response.close();
                }
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            Assert.assertEquals(400, status);

            EntityUtils.consume(response.getEntity());
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            response.close();

        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: <test creating a session with user id, project id, start time, end
    // time, and counter all being correct>
    public void pttTest14() throws Exception {

        try {
            CloseableHttpResponse response = createUser("John", "Doe", "bella88@doe.org");
            String user_id = getIdFromResponse(response);
            response.close();

            CloseableHttpResponse cpResponse = createProject(user_id, "Project KooName");
            String project_id = getIdFromResponse(cpResponse);
            cpResponse.close();

            CloseableHttpResponse Sessionresponse = createSession(user_id, project_id, "2022-02-20T20:00Z",
                    "2022-02-20T20:00Z", "0");

            int status = Sessionresponse.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = Sessionresponse.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);
            String id = getIdFromStringResponse(strResponse);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            System.out.println("user:" + user_id);
            System.out.println("project:" + project_id);
            String expectedJson = "{\"id\":" + id
                    + ",\"startTime\":\"2022-02-20T20:00Z\",\"endTime\":\"2022-02-20T20:00Z\",\"counter\":" + 0 + "}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            if (status == 201) {
                String Deleteid = getIdFromStringResponse(strResponse);
                CloseableHttpResponse deleteresponse = deleteUser(Integer.valueOf(Deleteid));
                response.close();
                deleteresponse.close();
            }
            CloseableHttpResponse deleteUser = deleteUser(Integer.valueOf(user_id));
            deleteUser.close();
            CloseableHttpResponse deleteProject = deleteProject(Integer.valueOf(user_id), Integer.valueOf(project_id));
            deleteProject.close();
            response.close();
        } finally {
            httpclient.close();
        }
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

    private CloseableHttpResponse createUser(String firstName, String lastName, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstName + "\"," + "\"lastName\":\"" + lastName
                + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getUser(int id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createSession(String Userid, String Projectid, String startTime, String endTime,
            String counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + Userid + "/projects/" + Projectid + "/sessions");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"startTime\":\"" + startTime + "\"," + "\"endTime\":\"" + endTime
                + "\"," + "\"counter\":\"" + counter + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String user_id, String projectName) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + user_id + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectName + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

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
        while (keyList.hasNext()) {
            String key = keyList.next();
            if (key.equals("id")) {
                id = object.get(key).toString();
            }
        }
        return id;
    }
}
