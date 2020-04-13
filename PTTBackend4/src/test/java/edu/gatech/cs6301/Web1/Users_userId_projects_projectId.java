package edu.gatech.cs6301.Web1;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
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
//import edu.gatech.cs6301.Users;

import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects_projectId extends PTTBackendTests {

    public Users_userId_projects_projectId() {
        super();
        runBefore();
    }

    private Users usersConstruct = new Users();
    private Users_userId_projects projectsConstruct = new Users_userId_projects();

    @Test
    public void pttTest1() throws Exception {
        // Purpose: test the put method with valid userId and valid projectId but an
        // empty projectname (body length 0)
        usersConstruct.deleteAllUsers();
        JSONObject user1 = usersConstruct.createUser("joey", "tribi", "joey@gatech.edu");
        String idForUser1 = user1.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser1, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser1, "project2");

        String emptyProName = "";

        String idForPro1 = pro1.getString("id");
        // JSONObject updatePro = updateProject(idForUser1, idForPro1, emptyProName);
        CloseableHttpResponse res = updateProjectHTTPResponse(idForUser1, idForPro1, emptyProName);
        int status = res.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        // I am expecting that a empty project name would get an error with status code
        // of 400, but it seems
        // it's sending back a status code of 200.
        if (status == 400) { // changed by Phalguna from 200 to 400
            entity = res.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

        strResponse = EntityUtils.toString(entity);

        System.out.println("*** String response " + strResponse + " (" + res.getStatusLine().getStatusCode() + ") ***");
    }

    @Test
    public void pttTest2() throws Exception {
        // Purpose: test the get request with non-existing user
        usersConstruct.deleteAllUsers();
        JSONObject user2 = usersConstruct.createUser("monica", "geller", "monica@gatech.edu");
        int nonexistingId = usersConstruct.getBadUserId();
        String proId = "1";
        CloseableHttpResponse res = getProjectByIdHTTPResponse(String.valueOf(nonexistingId), proId);
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest3() throws Exception {
        // Purpose: test the put request with non-existing user
        usersConstruct.deleteAllUsers();
        JSONObject user3 = usersConstruct.createUser("bing", "chandler", "bing@gatech.edu");
        int nonexistingId = usersConstruct.getBadUserId();
        String proId = "10";
        CloseableHttpResponse res = updateProjectHTTPResponse(String.valueOf(nonexistingId), proId, "proName1");
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest4() throws Exception {
        // Purpose: test the delete request with non-existing user
        usersConstruct.deleteAllUsers();
        JSONObject user4 = usersConstruct.createUser("rachel", "green", "rachel@gatech.edu");
        int nonexistingId = usersConstruct.getBadUserId();
        String proId = "20";
        CloseableHttpResponse res = deleteProjectById(String.valueOf(nonexistingId), proId);
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest5() throws Exception {
        // Purpose: test the get method with valid userId and non-existing projectId
        usersConstruct.deleteAllUsers();
        JSONObject user5 = usersConstruct.createUser("ross", "geller", "ross@gatech.edu");
        String idForUser5 = user5.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser5, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser5, "project2");
        JSONObject pro3 = projectsConstruct.createProject(idForUser5, "project3");
        JSONObject pro4 = projectsConstruct.createProject(idForUser5, "project4");

        int badProId = getBadProjectId(idForUser5);
        CloseableHttpResponse res = getProjectByIdHTTPResponse(idForUser5, String.valueOf(badProId));
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest6() throws Exception {
        // Purpose: test the put method with valid userId and non-existing projectId
        usersConstruct.deleteAllUsers();
        JSONObject user6 = usersConstruct.createUser("phoebe", "buffay", "phoebe@gatech.edu");
        String idForUser6 = user6.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser6, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser6, "project2");
        JSONObject pro3 = projectsConstruct.createProject(idForUser6, "project3");
        JSONObject pro4 = projectsConstruct.createProject(idForUser6, "project4");

        int badProId = getBadProjectId(idForUser6);
        CloseableHttpResponse res = updateProjectHTTPResponse(idForUser6, String.valueOf(badProId), "proName");
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest7() throws Exception {
        // Purpose: test the delete method with valid userId and non-existing projectId
        usersConstruct.deleteAllUsers();
        JSONObject user7 = usersConstruct.createUser("ross", "geller", "ross@gatech.edu");
        String idForUser7 = user7.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser7, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser7, "project2");
        JSONObject pro3 = projectsConstruct.createProject(idForUser7, "project3");
        JSONObject pro4 = projectsConstruct.createProject(idForUser7, "project4");

        int badProId = getBadProjectId(idForUser7);
        CloseableHttpResponse res = deleteProjectById(idForUser7, String.valueOf(badProId));
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest8() throws Exception {
        // Purpose: test the get method with valid userId and valid projectId
        usersConstruct.deleteAllUsers();
        JSONObject user8 = usersConstruct.createUser("joey", "tribi", "joey@gatech.edu");
        String idForUser8 = user8.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser8, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser8, "project2");

        String idForPro1 = pro1.getString("id");
        JSONObject foundProject = getProjectById(idForUser8, idForPro1);
        Assert.assertEquals("project1", foundProject.getString("projectname"));
        Assert.assertEquals(idForPro1, foundProject.getString("id"));

        CloseableHttpResponse res = getProjectByIdHTTPResponse(idForUser8, idForPro1);
        int status = res.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        if (status == 200) {
            entity = res.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

        strResponse = EntityUtils.toString(entity);

        System.out.println("*** String response " + strResponse + " (" + res.getStatusLine().getStatusCode() + ") ***");
    }

    @Test
    public void pttTest9() throws Exception {
        // Purpose: test the put method with valid userId and valid projectId
        usersConstruct.deleteAllUsers();
        JSONObject user9 = usersConstruct.createUser("joey", "tribi", "joey@gatech.edu");
        String idForUser9 = user9.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser9, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser9, "project2");

        String newProName = "project1Updated";

        String idForPro1 = pro1.getString("id");
        JSONObject updatedProject = updateProject(idForUser9, idForPro1, newProName);
        Assert.assertEquals(newProName, updatedProject.getString("projectname"));
        Assert.assertEquals(idForPro1, updatedProject.getString("id"));

        CloseableHttpResponse res = updateProjectHTTPResponse(idForUser9, idForPro1, newProName);
        int status = res.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        if (status == 409) {
            entity = res.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

        strResponse = EntityUtils.toString(entity);

        System.out.println("*** String response " + strResponse + " (" + res.getStatusLine().getStatusCode() + ") ***");
    }

    @Test
    public void pttTest10() throws Exception {
        // Purpose: test the delete method with valid userId and valid projectId
        usersConstruct.deleteAllUsers();
        JSONObject user10 = usersConstruct.createUser("joey", "tribi", "joey@gatech.edu");
        String idForUser10 = user10.getString("id");

        JSONObject pro1 = projectsConstruct.createProject(idForUser10, "project1");
        JSONObject pro2 = projectsConstruct.createProject(idForUser10, "project2");

        String deletedId = pro1.getString("id");
        CloseableHttpResponse res = deleteProjectById(idForUser10, deletedId);
        int status = res.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        if (status == 200) {
            entity = res.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

        strResponse = EntityUtils.toString(entity);
        System.out.println("*** String response " + strResponse + " (" + res.getStatusLine().getStatusCode() + ") ***");

        // check that this project was indeed deleted
        CloseableHttpResponse response = getProjectByIdHTTPResponse(idForUser10, deletedId);
        int getStatus = response.getStatusLine().getStatusCode();
        Assert.assertEquals(404, getStatus);
    }

    public CloseableHttpResponse updateProjectHTTPResponse(String userId, String projectId, String projectname)
            throws IOException, JSONException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        return response;
    }

    public JSONObject updateProject(String userId, String projectId, String projectname)
            throws IOException, JSONException {
        CloseableHttpResponse response = updateProjectHTTPResponse(userId, projectId, projectname);
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse getProjectByIdHTTPResponse(String userId, String projectId)
            throws IOException, JSONException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        return response;
    }

    public JSONObject getProjectById(String userId, String projectId) throws IOException, JSONException {
        CloseableHttpResponse response = getProjectByIdHTTPResponse(userId, projectId);
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse deleteProjectById(String userId, String projectId) throws IOException {
        System.out.println("Deleting project: " + userId + "/" + projectId);
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    public int getBadProjectId(String userId) throws JSONException, IOException {
        JSONArray projects = projectsConstruct.getProjects(userId);
        int max_n = 0;
        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.getJSONObject(i);
            int id = project.getInt("id");
            if (id > max_n) {
                max_n = id;
            }
        }
        return PTTBackendTests.getRandomNumberInRange(max_n + 100, max_n + 1100);
    }
}
