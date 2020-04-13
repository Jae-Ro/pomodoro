package edu.gatech.cs6301.DevOps34;

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

public class Users_userId_projects_projectId {
	
	/* Adding the Backend testing server for DevOps34 Team */
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

    private CloseableHttpResponse addUser(String firstName, String lastName, String email) throws IOException {
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
    private CloseableHttpResponse addProject(String userid, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userid + "/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
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
    private CloseableHttpResponse updateProject(String userid, String projectid, String projectname) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userid + "/projects/" + projectid);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        CloseableHttpResponse response;
        response= httpclient.execute(httpRequest);
        return response;
    }

    private CloseableHttpResponse deleteProject(String userid, String projectid) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + userid + "/projects/" + projectid);
        httpDelete.addHeader("accept", "application/json");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        return response;
    }
    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        return response;
    }
    private CloseableHttpResponse getProject(String userid, String projectid) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userid + "/projects/" + projectid);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }
    //TestCase1:InvalidRequest
    @Test
    public void InvalidrequestTEst() throws Exception {
        CloseableHttpResponse response = addUser("test0", "test1", "test0@test.com");
        String id = getIdFromResponse(response);
        response.close();

        response = addProject(id, "proj1");
        String projectid = getIdFromResponse(response);
        response.close();


        try {
            HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + id + "/projects/" + projectid);
            httpRequest.addHeader("accept", "application/json");
            StringEntity input = new StringEntity("{\"projectname\":\"" + projectid + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);

            response= httpclient.execute(httpRequest);
            response = updateProject(id, projectid+"xyz", "proj1");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }


        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }
    //TestCase2: InvalidPath
    @Test
    public void InvalidPathTest() throws Exception {
        CloseableHttpResponse response = addUser("test0", "test1", "test0@test.com");
        String id = getIdFromResponse(response);
        response.close();

        response = addProject(id, "proj1");
        String projectid = getIdFromResponse(response);
        response.close();


        try {
            HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id + "/projects/" + projectid);
            httpRequest.addHeader("accept", "application/json");
            StringEntity input = new StringEntity("{\"projectname\":\"" + projectid + "\"}");
            input.setContentType("application/json");
            httpRequest.setEntity(input);

            response= httpclient.execute(httpRequest);
            response = updateProject(id, projectid+"xyz", "proj1");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }


        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }
    //TestCase3: Duplicate putname
    @Test
    public void putDuplicateTest() throws Exception {
        CloseableHttpResponse response = addUser("test0", "test1", "test0@test.com");
        String id = getIdFromResponse(response);
        response.close();

        response = addProject(id, "proj1");
        String projectid = getIdFromResponse(response);
        response.close();


        try {
            response = updateProject(id, projectid+"1", "proj1");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }


        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }

    //TestCase4: Test for Get
    @Test
    public void getProjectTest() throws Exception {
        httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;
        response =  addUser("test0", "test1", "test0@test.com");
        String id = getIdFromResponse(response);
        response.close();
        response = addProject(id, "proj0");
        String projID;
        projID= getIdFromResponse(response);
        response.close();
        try {
            response = getProject(id, projID);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + projID + ",\"projectname\":\"proj0\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            response = deleteProject(id, projID);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }

    //TestCase5: Test for put
    @Test
    public void updateProjectTest() throws Exception {
        CloseableHttpResponse response = addUser("test0", "test1", "test0@test.com");
        String id = getIdFromResponse(response);
        response.close();

        response = addProject(id, "proj1");
        String projectid = getIdFromResponse(response);
        response.close();


        try {
            response = updateProject(id, projectid, "proj2");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"id\":" + projectid + ",\"projectname\":\"proj2\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            response = deleteProject(id, projectid);
            response.close();
            response = deleteUser(id);
            response.close();
            httpclient.close();
        }
    }

    //TestCase6: Test for delete
    @Test
    public void DeleteUserTest() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = null;
        CloseableHttpResponse response = addUser("test2", "test3", "test1@test.com");            // EntityUtils.consume(response.getEntity());
        String id = getIdFromResponse(response);
        response.close();
        try {
            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteUser(id);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            expectedJson = "{\"id\":" + id + ",\"firstName\":\"test2\",\"lastName\":\"test3\",\"email\":\"test1@test.com\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            response = getAllUsers();
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
	/***********************************************************************************/

	/* Helper Functions */
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
}
