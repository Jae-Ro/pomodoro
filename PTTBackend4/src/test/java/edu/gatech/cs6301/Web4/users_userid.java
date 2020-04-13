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

public class users_userid {

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
    //Get users/userid 
    @Test
    public void getUserTest() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        //deleteContacts();

        try {
            CloseableHttpResponse response = createUser("Jack7", "Do7","john7@doe.org");
            String id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            response = getUser(id);

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

            int userid = Integer.parseInt(getIdFromStringResponse(strResponse));
            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"Jack7\",\"lastName\":\"Do7\",\"email\":\"john7@doe.org\"}";
        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            //response=deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void getUserNotExistIdTest() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteUsers();
        //deleteContacts();

        try {
            CloseableHttpResponse response = createUser("Jack12", "Do12","john12@doe.org");
            String id = getIdFromResponse(response);
            // EntityUtils.consume(response.getEntity());
            response.close();

            response = getUser("1111111");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            
        } finally {
            httpclient.close();
        }
    }



    //Put users/userid
    @Test
    public void updateUserTest() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack8", "Doe8", "john8@doe.org");
            String id = getIdFromResponse(response);
            System.out.println("return id from test update:"+id);
            Integer userId = Integer.parseInt(id);
            response.close();

            response = updateUser(userId, "Tom4", "Doe4", "john8@doe.org");

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

            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"Tom4\",\"lastName\":\"Doe4\",\"email\":\"john8@doe.org\"}";
        JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            //response=deleteUser(id);
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void updateUserIdNotExistTest() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack15", "Doe15", "john15@doe.org");
            String id = getIdFromResponse(response);
            System.out.println("return id from test update:"+id);
            Integer userId = Integer.parseInt(id);
            response.close();

            response = updateUser(111111, "Tom4", "Doe4", "john8@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(404, status);
            
        } finally {
            httpclient.close();
        }
    }

    @Test
    public void updateUserBadInputTest() throws Exception {
        deleteUsers();

        try {
            CloseableHttpResponse response = createUser("Jack17", "Doe17", "john17@doe.org");
            String id = getIdFromResponse(response);
            System.out.println("return id from test update:"+id);
            Integer userId = Integer.parseInt(id);
            response.close();

            response = BadupdateUser(userId, "Tom4",  "john8@doe.org");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            Assert.assertEquals(400, status);
            
        } finally {
            httpclient.close();
        }
    }





    @Test
    public void DeleteUserTest() throws Exception {
        httpclient = HttpClients.createDefault();
        //deleteUsers();
        String expectedJson = null;

        try {
            CloseableHttpResponse response = createUser("Jack11", "Doe11", "john11@doe.org");
            // EntityUtils.consume(response.getEntity());
            String id = getIdFromResponse(response);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;
            Integer deleteid = Integer.parseInt(id);

            response = deleteUser(deleteid);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "{\"id\":" + deleteid
                    + ",\"firstName\":\"Jack11\",\"lastName\":\"Doe11\",\"email\":\"john11@doe.org\"}";
            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }


   @Test
    public void DeleteUserIdNotExistTest() throws Exception {
        httpclient = HttpClients.createDefault();
        //deleteUsers();
        String expectedJson = null;

        try {
            CloseableHttpResponse response = createUser("Jack16", "Doe16", "john16@doe.org");
            // EntityUtils.consume(response.getEntity());
            String deleteid = getIdFromResponse(response);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteUser(11111111);

            status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);
            
            

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

    private CloseableHttpResponse BadupdateUser(int id, String firstname, String email) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{\"firstName\":\"" + firstname + "\"," +
                "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
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
        System.out.println("Deleted Users: " + arrList);
    }

}
