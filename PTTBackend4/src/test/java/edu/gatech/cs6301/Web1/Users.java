package edu.gatech.cs6301.Web1;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class Users extends PTTBackendTests {

    public Users() {
        super();
        runBefore();
    }

    public CloseableHttpResponse createUserHTTPResponse(String jsonString) throws Exception {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse createUserHTTPResponse(String firstname, String lastname, String email)
            throws Exception {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("{" + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\""
                + lastname + "\"," + "\"email\":\"" + email + "\"}");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        return httpclient.execute(httpRequest);
    }

    public JSONObject createUser(String firstname, String lastname, String email) throws Exception {
        return createUser("{" + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\"" + lastname + "\","
                + "\"email\":\"" + email + "\"}");
    }

    public JSONObject createUser(String jsonString) throws Exception {
        CloseableHttpResponse response = createUserHTTPResponse(jsonString);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            System.out.println("*** ERROR response " + response + "***");
            throw new Exception("Error creating user. See log");
        }
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse updateUserHTTPResponse(String userId, String jsonString) throws Exception {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse updateUserHTTPResponse(String userId, String firstname, String lastname, String email)
            throws Exception {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");
        String jsonString = "{" + "\"id\":\"" + userId + "\"," + "\"firstName\":\"" + firstname + "\","
                + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}";
        StringEntity input = new StringEntity(jsonString);
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        return httpclient.execute(httpRequest);
    }

    public JSONObject updateUser(String userId, String jsonString) throws Exception {
        CloseableHttpResponse response = updateUserHTTPResponse(userId, jsonString);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            System.out.println("*** ERROR response " + response + "***");
            throw new Exception("Error updating user. See log");
        }
        System.out.println("*** Raw response " + response + "***");
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public JSONObject updateUser(String userId, String firstname, String lastname, String email) throws Exception {
        return updateUser(userId, "{" + "\"id\":\"" + userId + "\"," + "\"firstName\":\"" + firstname + "\","
                + "\"lastName\":\"" + lastname + "\"," + "\"email\":\"" + email + "\"}");
    }

    public JSONArray getUsers() throws IOException, JSONException {
        System.out.println("run test");
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONArray(strResponse);
    }

    public CloseableHttpResponse getUserResponse(String userId) throws IOException, JSONException {
        System.out.println("run test");
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId);
        httpRequest.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        return httpclient.execute(httpRequest);
    }

    public JSONObject getUser(String userId) throws IOException, JSONException {
        CloseableHttpResponse response = getUserResponse(userId);
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        return new JSONObject(strResponse);
    }

    public CloseableHttpResponse deleteUser(String id) throws IOException {
        System.out.println("Deleting user id: " + id);
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/" + id);
        httpDelete.addHeader("accept", "application/json");

        System.out.println("*** Executing request " + httpDelete.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        System.out.println("*** Raw response " + response + "***");
        // EntityUtils.consume(response.getEntity());
        // response.close();
        return response;
    }

    public CloseableHttpResponse deleteUserByEmail(String email) throws IOException, JSONException {
        JSONArray users = getUsers();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String userId = user.getString("id");
            String emailAddress = user.getString("email");

            if (email.equals(emailAddress)) {
                return deleteUser(userId);
            }
        }
        System.out.println("Email not found, so not deleted:" + email);
        return null;
    }

    public void deleteAllUsers() throws IOException, JSONException {
        JSONArray users = getUsers();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            int userId = user.getInt("id");
            deleteUser(String.valueOf(userId));
        }

    }

    public int getBadUserId() throws JSONException, IOException {
        JSONArray users = getUsers();
        int max_n = 0;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            int id = user.getInt("id");
            if (id > max_n) {
                max_n = id;
            }
        }
        return PTTBackendTests.getRandomNumberInRange(max_n + 100, max_n + 1100);
    }

    @Test
    public void pttTest1() throws Exception {
        // Purpose: Testing POSTing an empty String

        System.out.println("run test");
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", "application/json");
        StringEntity input = new StringEntity("");
        input.setContentType("application/json");
        httpRequest.setEntity(input);

        System.out.println("*** Executing request " + httpRequest.getRequestLine() + "***");
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("*** Raw response " + response + "***");
        Assert.assertEquals(400, statusCode);

    }

    @Test
    public void pttTest2() throws Exception {
        // Purpose: Testing GETing users with no body
        deleteAllUsers();
        System.out.println("run test");

        JSONArray users = getUsers();
        Assert.assertEquals(0, users.length());

        // now create a user, and response should have test user
        JSONObject user = createUser("test", "last", "test@gatech.edu");

        // get users after new user created
        JSONArray usersAfterCreate = getUsers();

        // get the newly created user
        JSONObject newlyCreatedUser = getUser(user.getString("id"));

        // verify we have new user
        Assert.assertNotEquals(0, usersAfterCreate.length());

        // make user objects are same
        Assert.assertEquals(newlyCreatedUser.get("id"), usersAfterCreate.getJSONObject(0).get("id"));
    }

    @Test
    public void pttTest3() throws Exception {
        // Purpose: TEST POSTing Non-parseable or Malformed JSON
        String badJSON = "{" + "\"firstName\":bilbo\"," + "\"lastName\":\"baggings\","
                + "\"email:\"baggings@shire.com\"";

        CloseableHttpResponse response = createUserHTTPResponse(badJSON);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(400, statusCode);
    }

    @Test
    public void pttTest4() throws Exception {
        // Purpose: TEST User missing email
        String firstname = "louie";
        String lastname = "the dog";

        String jsonString = "{" + "\"firstName\":\"" + firstname + "\"," + "\"lastName\":\"" + lastname + "\"" + "}";
        CloseableHttpResponse response = createUserHTTPResponse(jsonString);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(400, statusCode);
    }

    @Test
    public void pttTest5() throws Exception {
        // Purpose: TEST resource conflicts (same email being added twice)
        deleteAllUsers();
        System.out.println("run test");

        // create user 1
        JSONObject user1 = createUser("louie", "the dog", "dog@dog.com");

        // create user 2 (mapping to JSON object should trigger exception, so just get
        // response)
        CloseableHttpResponse response = createUserHTTPResponse("edie", "the dog", "dog@dog.com");

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(409, statusCode);
    }

    @Test
    public void pttTest6() throws Exception {
        // Purpose: TEST succesfully creating user (Has email)

        deleteAllUsers();

        // create user 1
        JSONObject user1 = createUser("louie", "the dog", "louie@dog.com");

        // create user 2
        JSONObject user2 = createUser("edie", "the dog", "edie@dog.com");

        // verify both have been created w/ different user ids
        Assert.assertNotEquals(user1.getString("id"), user2.getString("id"));
    }

    @Test
    public void pttTest7() throws Exception {
        // Purpose: TEST getting all users - standard way

        deleteAllUsers();
        System.out.println("run test");

        JSONArray users = getUsers();
        Assert.assertEquals(0, users.length());

        // now create a user, and response should have test user
        JSONObject user = createUser("test", "last", "test@gatech.edu");

        users = getUsers();
        Assert.assertEquals(1, users.length());

        deleteAllUsers();
        // now create a bunch of users
        for (int i = 0; i < 5; i++) {
            String key = String.valueOf(i);
            createUser("test" + key, "last" + key, "test" + key + "@gatech.edu");
            Thread.sleep(500);
        }

        // validate all users are created
        users = getUsers();
        Assert.assertEquals(5, users.length());
    }

    @Test
    public void pttTest8() throws Exception {
        // Purpose: POST Parseable and Well-formed JSON
        deleteUserByEmail("dog@dog.com");

        // test user
        CloseableHttpResponse response = createUserHTTPResponse("edie", "the dog", "dog@dog.com");
        Assert.assertEquals(201, response.getStatusLine().getStatusCode());
    }

    @Test
    public void pttTest9() throws Exception {
        // Purpose: GET users

        StringEntity input = new StringEntity("{}");
        input.setContentType("application/json");

        deleteAllUsers();
        System.out.println("run test");

        JSONArray users = getUsers();
        Assert.assertEquals(0, users.length());

    }
}
