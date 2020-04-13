package edu.gatech.cs6301.Backend3;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6301.Backend3.Model.Project;
import edu.gatech.cs6301.Backend3.Model.User;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UsersUserId {
    Util util;
    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean setupdone;

    @Before
    public void runBefore() throws IOException {
        if (!setupdone) {
            System.out.println("*** SETTING UP TESTS ***");
            util = new Util();
            setupdone = true;
        }
        util.deleteAllUsers();

        System.out.println("*** STARTING TEST ***");
    }

    @Test
    /**
     * Test get user by id
     * Should return 200, and exactly the user as we created
     */
    public void testGetUserByIdSuccess() throws IOException {
        // create a user, then put this exist user info
        User user = util.addUserSuccess();

        // Get the created user Id, get user info through this user id
        int id = user.getId();
        CloseableHttpResponse responseGet = util.getUserById(id);
        assert (responseGet.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responsePutBody = EntityUtils.toString(responseGet.getEntity());
        User getUser = objectMapper.readValue(responsePutBody, User.class);
        assert (getUser.equals(user));
    }

    @Test
    /**
     * Test get a non existing user id
     * Should return 404
     */
    public void testGetNonExistingUserID() throws IOException {
        CloseableHttpResponse response = util.getUserById(12345);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
    }

    @Test
    /**
     * Test update a non existing user id
     * Should return 404
     */
    public void testPutNonExistingUserID() throws IOException {
        User user = new User("qifan","zhang",util.genEmail());
        CloseableHttpResponse response = util.putUserById(12345, user);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
    }

    @Test
    /**
     * Test successfully delete an user having projects
     * Should return 200, and exactly the user that we would like to delete
     */
    public void testDeleteUsersProjectsSuccess() throws IOException {
        // create a user, then put this exist user info
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());

        // Delete user through the user Id
        int id = user.getId();
        CloseableHttpResponse responseDelete = util.deleteUserById(id);
        assert (responseDelete.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responsePutBody = EntityUtils.toString(responseDelete.getEntity());
        User deleteUser = objectMapper.readValue(responsePutBody, User.class);
        assert (deleteUser.equals(user));
    }

    @Test
    /**
     * Test successfully delete an user without projects
     * Should return 200, and exactly the user that we would like to delete
     */
    public void testDeleteUsersNoProjectsSuccess() throws IOException {
        // create a user, then put this exist user info
        User user = util.addUserSuccess();

        // Delete user through the user Id
        int id = user.getId();
        CloseableHttpResponse responseDelete = util.deleteUserById(id);
        assert (responseDelete.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responsePutBody = EntityUtils.toString(responseDelete.getEntity());
        User deleteUser = objectMapper.readValue(responsePutBody, User.class);
        assert (deleteUser.equals(user));
    }

    @Test
    /**
     * Test delete a non-existing user ID
     * Should return 404
     */
    public void testDeleteNonExistingUserID() throws IOException {
        CloseableHttpResponse response = util.deleteUserById(12345);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
    }

    @Test
    /**
     * Test put/update an exist user info with valid first name, last name and email
     * Create a user with valid info, this response should have a 201 status.
     * Put a user's info with valid first name and last name, the response should have a 200 status.
     * The body should contain same user except ID.
     */
    public void testPutUserSuccess() throws IOException {
        // create a user, then put this exist user info
        User responseObject = util.addUserSuccess();

        int id = responseObject.getId();
        responseObject.setFirstName("FirstName");
        responseObject.setLastName("LastName");
        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responsePutBody = EntityUtils.toString(responsePut.getEntity());
        User updateUser = objectMapper.readValue(responsePutBody, User.class);
        assert (updateUser.equalExceptId(responseObject));
    }

    @Test
    /**
     * Test put/update an existing user info with invalid first name.
     * Create a user to get id, then put/update an exist user info with invalid first name.
     * The response should have a 400 status.
     */
    public void testPutUserInvalidFirstName() throws IOException {
        // create a user, then put this exist user info
        User responseObject = util.addUserSuccess();

        int id = responseObject.getId();
        responseObject.setFirstName("@#$%^&*(");
        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }

    @Test
    /**
     * Test put/update an existing user info with an empty first name.
     * Create a user to get id, then put/update an exist user info with an empty first name.
     * The response should have a 400 status.
     */
    public void testPutUserEmptyFirstName() throws IOException {
        // create a user, then put this exist user info
        User responseObject = util.addUserSuccess();

        int id = responseObject.getId();
        responseObject.setFirstName("");
        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }

    @Test
    /**
     * Test put/update an existing user info with invalid last name.
     * Create a user to get id, then put/update an exist user info with invalid last name.
     * The response should have a 400 status.
     */
    public void testPutUserInvalidLastName() throws IOException {
        User responseObject = util.addUserSuccess();
        int id = responseObject.getId();
        responseObject.setLastName("@#$%^&*(");

        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }

    @Test
    /**
     * Test put/update an existing user info with an empty last name.
     * Create a user to get id, then put/update an exist user info with an empty last name.
     * The response should have a 400 status.
     */
    public void testPutUserEmptyLastName() throws IOException {
        // create a user, then put this exist user info
        User responseObject = util.addUserSuccess();

        int id = responseObject.getId();
        responseObject.setLastName("");
        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }

    @Test
    /**
     * Test put/update an existing user info with invalid user email.
     * Create a user to get id, then put/update an exist user info with invalid user email.
     * The response should have a 400 status.
     */
    public void testPutUserInvalidEmail() throws IOException {
        // create a user, then put this exist user info
        User responseObject = util.addUserSuccess();
        int id = responseObject.getId();
        responseObject.setEmail("@#$%^&*(");

        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }

    @Test
    /**
     * Test put/update an existing user info with an empty user email.
     * Create a user to get id, then put/update an exist user info with an empty user email.
     * The response should have a 400 status.
     */
    public void testPutUserEmptyEmail() throws IOException {
        // create a user, then put this exist user info
        User responseObject =util.addUserSuccess();

        int id = responseObject.getId();
        responseObject.setEmail("");
        CloseableHttpResponse responsePut = util.putUserById(id, responseObject);
        assert (responsePut.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }
}
