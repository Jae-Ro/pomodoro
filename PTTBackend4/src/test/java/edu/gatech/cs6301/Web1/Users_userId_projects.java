package edu.gatech.cs6301.Web1;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import edu.gatech.cs6301.Web1.Users;

public class Users_userId_projects extends PTTBackendTests {

    public Users_userId_projects() {
        super();
        runBefore();
    }

    @Test
    public void pttTest1() throws Exception {
        System.out.println("run test 1");
        // Purpose: get user's projects when the user does not exist (user's id is
        // maxint)

        // delete all users
        Users usersObj = new Users();
        usersObj.deleteAllUsers();

        // create project for unknown user
        CloseableHttpResponse response = createProjectHTTPResponse(String.valueOf(Integer.MAX_VALUE),
                "pttTest1Project");
        int status = response.getStatusLine().getStatusCode();
        Assert.assertEquals(404, status);
    }

    @Test
    public void pttTest2() throws Exception {
        // Purpose: post project when the projectname length is 0

        // create user
        Users usersObj = new Users();
        usersObj.deleteAllUsers();
        JSONObject newUser = usersObj.createUser("Alex", "H", "ah@gmail.com");
        String newUserId = newUser.getString("id");

        // delete all previous projects
        deleteAllProjects(newUserId);
        Assert.assertEquals(0, getProjects(newUserId).length());

        // create a project for newUser with empty project name
        CloseableHttpResponse response = createProjectHTTPResponse(newUserId, "");

        int status = response.getStatusLine().getStatusCode();

        Assert.assertEquals(400, status);
    }

    @Test
    public void pttTest3() throws Exception {
        // Purpose: post new project without project name

        // create user
        Users usersObj = new Users();
        usersObj.deleteAllUsers();
        JSONObject newUser = usersObj.createUser("Alex", "H", "ah@gmail.com");
        String newUserId = newUser.getString("id");

        // check no project created yet
        deleteAllProjects(newUserId);
        Assert.assertEquals(0, getProjects(newUserId).length());

        // create a project for newUser without project name
        String jsonString = "{" + "}";
        CloseableHttpResponse response = createProjectHTTPResponseWithJsonString(newUserId, jsonString);

        int status = response.getStatusLine().getStatusCode();
        Assert.assertEquals(400, status);
    }

    @Test
    public void pttTest4() throws Exception {
        // Purpose: get user's projects successfully

        // create user
        Users usersObj = new Users();
        usersObj.deleteAllUsers();
        JSONObject newUser = usersObj.createUser("Alex", "H", "ah@gmail.com");
        String newUserId = newUser.getString("id");

        // delete all previous projects
        deleteAllProjects(newUserId);
        Assert.assertEquals(0, getProjects(newUserId).length());

        // create a project for newUser
        CloseableHttpResponse response = createProjectHTTPResponse(newUserId, "pttTest4Project");

        // get user's project
        JSONArray projects = getProjects(newUserId);

        // check project's name
        JSONObject newProject = projects.getJSONObject(0);
        Assert.assertEquals(newProject.getString("projectname"), "pttTest4Project");

    }

    @Test
    public void pttTest5() throws Exception {
        // Purpose: post new project successfully

        // create user
        Users usersObj = new Users();
        usersObj.deleteAllUsers();
        JSONObject newUser = usersObj.createUser("Alex", "H", "ah@gmail.com");
        String newUserId = newUser.getString("id");

        // check no project created yet
        deleteAllProjects(newUserId);
        Assert.assertEquals(0, getProjects(newUserId).length());

        // create a project for newUser
        CloseableHttpResponse response = createProjectHTTPResponse(newUserId, "pttTest5Project");

        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity;
        if (status == 201) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

        // check new project's name is correct
        String strResponse = EntityUtils.toString(entity);
        JSONObject newProject = new JSONObject(strResponse);
        Assert.assertEquals(newProject.getString("projectname"), "pttTest5Project");
    }

    // functions
    public CloseableHttpResponse getProjectsHTTPResponse(String userId) throws IOException, JSONException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        return httpclient.execute(httpRequest);
    }

    public JSONArray getProjects(String userId) throws IOException, JSONException {
        CloseableHttpResponse response = getProjectsHTTPResponse(userId);
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONArray(strResponse);
    }

    public CloseableHttpResponse createProjectHTTPResponse(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{" + "\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    public CloseableHttpResponse createProjectHTTPResponseWithJsonString(String userId, String jsonString)
            throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    public JSONObject createProject(String userId, String projectname) throws Exception {
        CloseableHttpResponse response = createProjectHTTPResponse(userId, projectname);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            System.out.println("*** ERROR response " + response + "***");
            throw new Exception("Error creating project. See log");
        }
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse deleteProject(String userId, String proId) throws IOException {
        System.out.println("Deleting user id, pro id: " + userId + ", " + proId);
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + proId);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    public void deleteAllProjects(String userId) throws IOException, JSONException {
        JSONArray projects = getProjects(userId);
        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.getJSONObject(i);
            int proId = project.getInt("id");
            deleteProject(userId, String.valueOf(proId));
        }
    }

}
