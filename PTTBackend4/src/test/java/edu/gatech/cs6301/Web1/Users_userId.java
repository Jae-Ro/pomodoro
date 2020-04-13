package edu.gatech.cs6301.Web1;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class Users_userId extends PTTBackendTests {

    private Users usersObj = new Users();

    @Test
    public void pttTest1() throws Exception {

        // Purpose: TEST PUTs with empty body

        String email = "test@gatech.edu";
        String first = "test";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        // now update with empty body
        CloseableHttpResponse updatedResponse = usersObj.updateUserHTTPResponse(userId, "");
        int statusCode = updatedResponse.getStatusLine().getStatusCode();

        Assert.assertEquals(400, statusCode);

        HttpEntity entity = updatedResponse.getEntity();
        // String strResponse = EntityUtils.toString(entity);
        // JSONObject updatedUser = new JSONObject(strResponse);

        // // no changes
        // Assert.assertEquals(first, updatedUser.getString("firstName"));
        // Assert.assertEquals(last, updatedUser.getString("lastName"));
        // Assert.assertEquals(email, updatedUser.getString("email"));

    }

    @Test
    public void pttTest2() throws Exception {
        // Purpose: TEST PUT with Non-parseable or Malformed JSON
        String email = "test@gatech.edu";
        String first = "test";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        String badJSON = "{" + "\"id\": ," + "\"firstName\":\"" + first + "\"," + "\"lastName\":\"" + last + "\","
                + "\"email\":\"" + email + "\"";

        CloseableHttpResponse updatedResponse = usersObj.updateUserHTTPResponse(userId, badJSON);
        int statusCode = updatedResponse.getStatusLine().getStatusCode();

        Assert.assertEquals(400, statusCode);
    }

    @Test
    public void pttTest3() throws Exception {
        // Purpose: GET User that does not exist

        // find all the ids, find a user id that's not yet been created
        int badId = usersObj.getBadUserId();
        CloseableHttpResponse res = usersObj.getUserResponse(String.valueOf(badId));
        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);

    }

    @Test
    public void pttTest4() throws Exception {
        // Purpose: Update a user (PUT) that does not exist

        int badId = usersObj.getBadUserId();
        CloseableHttpResponse res = usersObj.updateUserHTTPResponse(String.valueOf(badId), "mr", "jones",
                "crows@joes.com");

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest5() throws Exception {
        // Purpose: DELETE a user that does not exist

        int badId = usersObj.getBadUserId();

        CloseableHttpResponse res = usersObj.deleteUser(String.valueOf(badId));

        int statusCode = res.getStatusLine().getStatusCode();

        // should be not found
        Assert.assertEquals(404, statusCode);
    }

    @Test
    public void pttTest6() throws Exception {
        // Purpose: GET a user successfully, verify all fields are there and set

        String email = "test@gatech.edu";
        String first = "test";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        JSONObject foundUser = usersObj.getUser(userId);
        Assert.assertEquals(email, foundUser.getString("email"));
        Assert.assertEquals(first, foundUser.getString("firstName"));
        Assert.assertEquals(last, foundUser.getString("lastName"));
    }

    @Test
    public void pttTest7() throws Exception {
        // Purpose: PUT (update) a user successfully

        String email = "6301@gatech.edu";
        String first = "great";
        String last = "fun";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        String newFirst = "home";
        String newLast = "work";
        JSONObject foundUser = usersObj.updateUser(userId, newFirst, newLast, email);
        Assert.assertEquals(newFirst, foundUser.getString("firstName"));
        Assert.assertEquals(newLast, foundUser.getString("lastName"));
    }

    @Test
    public void pttTest8() throws Exception {
        // Purpose: DELETE a user successfully
        String email = "test@gatech.edu";
        String first = "test";
        String last = "time";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        CloseableHttpResponse res = usersObj.deleteUser(String.valueOf(userId));

        int statusCode = res.getStatusLine().getStatusCode();
        Assert.assertEquals(200, statusCode);

        // check if it's really gone
        CloseableHttpResponse reallyGoneResponse = usersObj.getUserResponse(userId);
        int getStatus = reallyGoneResponse.getStatusLine().getStatusCode();
        Assert.assertEquals(404, getStatus);

    }

    @Test
    public void pttTest9() throws Exception {
        // Purpose: GET a user, verify by checking fields

        System.out.println("run test");

        String email = "test9@gatech.edu";
        String first = "9";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        JSONObject foundUser = usersObj.getUser(userId);
        Assert.assertEquals(email, foundUser.getString("email"));
        Assert.assertEquals(first, foundUser.getString("firstName"));
        Assert.assertEquals(last, foundUser.getString("lastName"));

    }

    @Test
    public void pttTest10() throws Exception {
        // Purpose: GET a user, verify by status code

        String email = "test10@gatech.edu";
        String first = "10";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        CloseableHttpResponse res = usersObj.getUserResponse(userId);
        int statusCode = res.getStatusLine().getStatusCode();
        Assert.assertEquals(200, statusCode);
    }

    @Test
    public void pttTest11() throws Exception {
        // Purpose: PUT a user with valid JSON, make sure fields update

        String email = "test@gatech.edu";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser("test", "user", email);
        String userId = newUser.getString("id");
        String firstname = newUser.getString("firstName");
        Assert.assertEquals("test", firstname);

        JSONObject updatedUser = usersObj.updateUser(userId, "updated", "user", email);
        String updatedFirstname = updatedUser.getString("firstName");

        Assert.assertEquals("updated", updatedFirstname);
    }

    @Test
    public void pttTest12() throws Exception {
        // Purpose: DELETE a user; verify by status code

        String email = "test12@gatech.edu";
        String first = "12";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        CloseableHttpResponse deleted = usersObj.deleteUser(userId);
        int statusCode = deleted.getStatusLine().getStatusCode();
        Assert.assertEquals(200, statusCode);
    }

    @Test
    public void pttTest13() throws Exception {
        // Purpose: DELETE a user; verify by GET

        String email = "test13@gatech.edu";
        String first = "13";
        String last = "user";
        usersObj.deleteUserByEmail(email);

        JSONObject newUser = usersObj.createUser(first, last, email);
        String userId = newUser.getString("id");

        usersObj.deleteUser(userId);

        // try to get and make sure it's gone
        CloseableHttpResponse res = usersObj.getUserResponse(userId);
        int statusCode = res.getStatusLine().getStatusCode();
        Assert.assertEquals(404, statusCode);
    }
}
