package edu.gatech.cs6301.Web4;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class users_userid_project {

    //private String baseUrl = "http://localhost:8080";
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
	    //HttpHost localhost = new HttpHost("locahost", 8080);
        HttpHost remotehost = new HttpHost("http://gazelle.cc.gatech.edu", 9008); 
	    cm.setMaxPerRoute(new HttpRoute(remotehost), 10);
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
    public void createProjectTest() throws Exception {
    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack3", "Do3","john3@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            String projectname = "Proj3";
            response = createProject(id,projectname);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            //String id = getIdFromStringResponse(strResponse);
            //int userId = Integer.parseInt(id);

            //String expectedJson = "{\"id\":\"" + id + "\",\"projectname\":\"Proj3\"}";
            String expectedJson = "{\"projectname\":\"Proj3\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void createProjectNoNameTest() throws Exception {
    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack20", "Do20","john20@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            String projectname = "";
            response = createProject(id,projectname);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            Assert.assertEquals(400, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void createProjectUserNotExistTest() throws Exception {
    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack21", "Do21","john21@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            String projectname = "someproject";
            response = createProject("11111111",projectname);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }



    @Test
    public void createOneProjectgetProjectTest() throws Exception {
    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack5", "Do5","john5@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            String projectname = "Projectfor5";
            response = createProject(id,projectname);
            response.close();
            response = getAllProjects(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            //String id = getIdFromStringResponse(strResponse);
            //int userId = Integer.parseInt(getIdFromStringResponse(strResponse));

            //String expectedJson = "{\"id\":\"" + userId + "\",\"projectname\":\"Proj1\"}";
            String expectedJson = "[{\"projectname\":\"Projectfor5\"}]";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void createTwoProjectsgetProjectTest() throws Exception {
    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack4", "Do4","john4@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            String projectname = "Proj1";
            response = createProject(id,projectname);
            response.close();
            response = createProject(id,"Proj2");
            response.close();
            response = getAllProjects(id);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            String strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            //String id = getIdFromStringResponse(strResponse);
            //int userId = Integer.parseInt(getIdFromStringResponse(strResponse));

            //String expectedJson = "{\"id\":\"" + userId + "\",\"projectname\":\"Proj1\"}";
            String expectedJson = "[{\"projectname\":\"Proj1\"},{\"projectname\":\"Proj2\"}]";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void getProjectUserIdNotExistTest() throws Exception {
    deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack22", "Do22","john22@doe.org");
            String id = getIdFromResponse(response);
            response.close();
            String projectname = "Projectfor5";
            response = createProject(id,projectname);
            response.close();
            response = getAllProjects("11111111");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            Assert.assertEquals(404, status);
            response.close();
        } finally {
            httpclient.close();
        }
    }


    private CloseableHttpResponse createUser(String firstname, String lastname, String email) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"lastName\":\"" + lastname + "\","+
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createProject(String userId, String projectname) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/"+userId+"/projects");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateProject(String userId, String projectId, String projectname) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/"+userId+"/projects/"+projectId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"projectname\":\"" + projectname + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getProject(String userId, String projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/"+ userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getAllProjects(String userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/"+ userId + "/projects" );
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getUser(String id) throws IOException {
        Integer userId = Integer.parseInt(id);
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse updateUser(int id, String firstname, String familyname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"lastName\":\"" + familyname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse deleteUser(String id) throws IOException {
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
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
        //System.out.println("strResponse:"+strResponse);
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

    private CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

   private void deleteUsers() throws IOException, JSONException {
        CloseableHttpResponse response = getAllUsers();
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity;
        String strResponse;
        if (status == 200) {
            entity = response.getEntity();
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
        strResponse = EntityUtils.toString(entity);
        JSONArray jsonArray = new JSONArray(strResponse);
        response.close();

        ArrayList<String> arrList = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.equals("id")) {
                    int id = Integer.parseInt(json.get(key).toString());
                    HttpDelete httpDelete = new HttpDelete(baseUrl + "/ptt/users/" + id);
                    httpDelete.addHeader("accept", "application/json");
                    // System.out.println("*** Executing request " + httpDelete.getRequestLine() +
                    // "***");
                    CloseableHttpResponse response1 = httpclient.execute(httpDelete);
                    entity = response1.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    System.out.println(responseString);
                    arrList.add(responseString);
                    // response1.close();
                    // System.out.println("*** Raw response " + response + "***");
                }
            }
        }
        //System.out.println("Deleted Users: " + arrList);
    }

}
