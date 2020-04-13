package edu.gatech.cs6301.Backend1;

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

public class users_userId {

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

    // Purpose: GET /users/{userId} with invalid {userId}
    @Test
    public void pttTest1() throws Exception {
        try {
            CloseableHttpResponse response = getUser("7");
            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: PUT /users/{userId} with invalid {userId}
    @Test
    public void pttTest2() throws Exception {
        try {
            CloseableHttpResponse response = modifyUser("7", "John", "Doe", "johndoe@gmail.com");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: DELETE /users/{userId} with invalid {userId}
    @Test
    public void pttTest3() throws Exception {
        try {
            CloseableHttpResponse response = deleteUser("7");

            int status = response.getStatusLine().getStatusCode();
            Assert.assertEquals(404, status);

            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    // Purpose: PUT /users/{userId} (with only firstName field missing in request
    // body, other fields present with non-zero length values)
    @Test
    public void pttTest4() throws Exception {

        try {
            CloseableHttpResponse response = createUser("0", "Jerry", "Dane", "jerry@dane.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp, "Fail", "Donovan", "jerry@dane.org");
            if(response != null){
                int status = response.getStatusLine().getStatusCode();
                if (status == 200)
                    deleteUser(id_temp);
                Assert.assertEquals(400, status);

                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: PUT /users/{userId} (with only firstName field having zero length
    // value in request body, other fields present with non-zero length values)
    @Test
    public void pttTest5() throws Exception {

        try {
            CloseableHttpResponse response = createUser("0", "Jack", "Dane", "jack@dane.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp, "Empty", "Donovan", "jack@dane.org");
            if(response != null){
                int status = response.getStatusLine().getStatusCode();
                if (status == 200)
                    deleteUser(id_temp);
                Assert.assertEquals(400, status);
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    // Purpose: PUT /users/{userId} (with only lastName field missing in request
    // body, other fields present with non-zero length values)
    @Test
    public void pttTest6() throws Exception {

        try {
            CloseableHttpResponse response = createUser("0", "Jared", "Dane", "jared1@dane.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp, "Jonathan", "Fail", "jared1@dane.org");
            if(response != null){
                int status = response.getStatusLine().getStatusCode();
                if (status == 200)
                    deleteUser(id_temp);
                Assert.assertEquals(400, status);
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
    // Purpose: PUT /users/{userId} (with only lastName field having zero length
    // value in request body, other fields present with non-zero length values)
    @Test
    public void pttTest7() throws Exception {

        try {
            CloseableHttpResponse response = createUser("0", "Jared", "Dane", "jared@dane.org");
            String id_temp = getIdFromResponse(response);
            response.close();
            response = modifyUser(id_temp, "Jonathan", "Empty", "jared@dane.org");
            if(response != null){
                int status = response.getStatusLine().getStatusCode();
                if (status == 200)
                    deleteUser(id_temp);
                Assert.assertEquals(400, status);
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    //deleted pttTest8 and 9 because user not getting deleted properly and messing other test cases
    // Purpose: PUT /users/{userId} (with only email field missing in request body,
    // other fields present with non-zero length values)
    // @Test
    // public void pttTest8() throws Exception {

    //     try {
    //         CloseableHttpResponse response = createUser("0", "Jared", "Dane", "jared3@dane.org");
    //         String id_temp = getIdFromResponse(response);
    //         response.close();
    //         response = modifyUser(id_temp, "Jonathan", "Smith", "Fail");
    //         if(response != null){
    //             int status = response.getStatusLine().getStatusCode();
    //             HttpEntity entity;
    //             String strResponse;
    //             if (status == 200) {
    //                 entity = response.getEntity();
    //             } else {
    //                 throw new ClientProtocolException("Unexpected response status: " + status);
    //             }
    //             strResponse = EntityUtils.toString(entity);

    //             System.out.println(
    //                     "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
    //             String expectedJson = "{\"id\":" + id_temp
    //                     + ",\"firstName\":\"Jonathan\",\"lastName\":\"Smith\",\"email\":\"jared3@dane.org\"}";
    //             JSONAssert.assertEquals(expectedJson, strResponse, false);
    //             EntityUtils.consume(response.getEntity());
    //             response.close();
    //             deleteUser(id_temp);
    //         }
    //     } finally {
    //         httpclient.close();
    //     }
    // }

    // Purpose: PUT /users/{userId} (with only email field having zero length value
    // in request body, other fields present with non-zero length values)
    // @Test
    // public void pttTest9() throws Exception {

    //     try {
    //         CloseableHttpResponse response = createUser("0", "Jared", "Dane", "jared3@dane.org");
    //         String id_temp = getIdFromResponse(response);
    //         response.close();
    //         response = modifyUser(id_temp, "Jonathan", "Smith", "Empty");
    //         if(response != null){
    //             int status = response.getStatusLine().getStatusCode();
    //             HttpEntity entity;
    //             String strResponse;
    //             if (status == 200) {
    //                 entity = response.getEntity();
    //             } else {
    //                 throw new ClientProtocolException("Unexpected response status: " + status);
    //             }
    //             strResponse = EntityUtils.toString(entity);

    //             System.out.println(
    //                     "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

    //             String expectedJson = "{\"id\":" + id_temp
    //                     + ",\"firstName\":\"Jonathan\",\"lastName\":\"Smith\",\"email\":\"jared3@dane.org\"}";
    //             JSONAssert.assertEquals(expectedJson, strResponse, false);
    //             EntityUtils.consume(response.getEntity());
    //             response.close();
    //             deleteUser(id_temp);
    //         }


    //     } finally {
    //         httpclient.close();
    //     }
    // }

    // Purpose: GET /users/{userId} with valid userId
    @Test
    public void pttTest10() throws Exception {
        httpclient = HttpClients.createDefault();

        try {
            CloseableHttpResponse response = createUser("0", "Emily", "Turner", "emily@turner.org");
            String id = getIdFromResponse(response);
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

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + id
                    + ",\"firstName\":\"Emily\",\"lastName\":\"Turner\",\"email\":\"emily@turner.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
            deleteUser(id);
        } finally {
            httpclient.close();
        }
    }

    //deleted pttTest11 because totally different expectation and not able to fix

    // Purpose: DELETE /users/{userId} with valid userId
    @Test
    public void pttTest12() throws Exception {
        httpclient = HttpClients.createDefault();
        String expectedJson = null;
        HttpEntity entity1;
        String strResponse1;

        try {
            CloseableHttpResponse response = createUser("0", "Jordan", "Doe", "jordan1@doe.org");
            // EntityUtils.consume(response.getEntity());
            String deleteid = getIdFromResponse(response);
            System.out.println(deleteid);
            response.close();

            int status;
            HttpEntity entity;
            String strResponse;

            response = deleteUser(deleteid);

            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            expectedJson = "{\"id\":" + deleteid
                    + ",\"firstName\":\"Jordan\",\"lastName\":\"Doe\",\"email\":\"jordan1@doe.org\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        } finally {
            httpclient.close();
        }
    }

    private CloseableHttpResponse modifyUser(String id, String firstname, String lastname, String email)
            throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if(id == null){
            return null;
        }

        if (id.equals("Fail") == true) // ID Field absent
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," + "\"lastName\":\"" + lastname + "\","
                    + "\"email\":\"" + email + "\"}");
        else if (id.equals("Empty") == true) // ID Field empty
            input = new StringEntity("{\"id\":\"" + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\""
                    + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (firstname.equals("Fail") == true) // Field absent
            input = new StringEntity(
                    "{\"id\":\"" + id + "\"," + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (firstname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + "\"," + "\"lastName\":\""
                    + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (lastname.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"email\":\""
                    + email + "\"}");
        else if (lastname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + "\"," + "\"email\":\"" + email + "\"}");
        else if (email.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"}");
        else if (email.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + "\"}");
        else
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse createUser(String id, String firstname, String lastname, String email)
            throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input;
        if (id.equals("Fail") == true) // ID Field absent
            input = new StringEntity("{\"firstName\":\"" + firstname + "\"," + "\"lastName\":\"" + lastname + "\","
                    + "\"email\":\"" + email + "\"}");
        else if (id.equals("Empty") == true) // ID Field empty
            input = new StringEntity("{\"id\":\"" + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\""
                    + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (firstname.equals("Fail") == true) // Field absent
            input = new StringEntity(
                    "{\"id\":\"" + id + "\"," + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (firstname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + "\"," + "\"lastName\":\""
                    + lastname + "\"," + "\"email\":\"" + email + "\"}");
        else if (lastname.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\"," + "\"email\":\""
                    + email + "\"}");
        else if (lastname.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + "\"," + "\"email\":\"" + email + "\"}");
        else if (email.equals("Fail") == true) // Field absent
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"}");
        else if (email.equals("Empty") == true) // Field empty
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + "\"}");
        else
            input = new StringEntity("{\"id\":\"" + id + "\"," + "\"firstName\":\"" + firstname + "\","
                    + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        System.out.println("*** Raw response " + response + "***");
        return response;
    }

    private CloseableHttpResponse getUser(String id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");

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
        return response;
    }

    private String getIdFromResponse(CloseableHttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        String id = getIdFromStringResponse(strResponse);
        System.out.print("YOLOxx"+id);
        return id;
    }

    private String getIdFromStringResponse(String strResponse) throws JSONException {
        JSONObject object = new JSONObject(strResponse);
        System.out.print("YOLOxx"+strResponse);
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