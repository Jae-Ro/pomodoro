package edu.gatech.cs6301.Backend3;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6301.Backend3.Model.Project;
import edu.gatech.cs6301.Backend3.Model.Session;
import edu.gatech.cs6301.Backend3.Model.User;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UsersUserIdProjectsProjectIdSessionsSessionId {
    private final String SESSION_START = "2019-02-18T20:00Z";
    private final String SESSION_END = "2019-02-18T20:30Z";
    private boolean setupdone = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    Util util;

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

    @After
    public void runAfter() {
        try {
            util.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Test update a session with non-existing user id
     */
    public void testPutNonExistingUser() throws IOException{
        Session session = new Session();
        CloseableHttpResponse response = util.putSessionBySessionId(99,999,9999, session);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test update a session with non-existing project id
     */
    public void testPutNonExistingProject() throws IOException{
        Session session = new Session();
        User user = util.addUserSuccess();
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(),999,9999, session);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test update a session with non-existing session id
     */
    public void testPutNonExistingSession() throws IOException{
        Session session = new Session();
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(),project.getId(),9999, session);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test update a session with wrong start time format
     * Should return 404
     */
    public void testPutSessionWrongFormatStartTime() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setStartTime("^(&&");
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test update a session with invalid start time, start time > end time
     * Should return 404
     */
    public void testPutSessionInvalidStartTime() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setStartTime("2019-02-19T20:00Z");
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }



    @Test
    /**
     * Test update a session with wrong end time format
     * Should return 404
     */
    public void testPutSessionWrongFormatEndTime() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setEndTime("^(&&");
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test update a session with invalid end time, start time > end time
     * Should return 404
     */
    public void testPutSessionInvalidEndTime() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setEndTime("2019-02-15T20:00Z");
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test update a session with invalid counter
     * Should return 404
     */
    public void testPutSessionInvalidCounter() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setCounter(-1);
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test update a session with start time = end time, and counter = 0
     * Should return 200 and correct session
     */
    public void testPutSessionZeroCounter() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setEndTime(SESSION_START);
        session.setCounter(0);
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        assert(session.equals(objectMapper.readValue(responseBody, Session.class)));
        response.close();
    }

    @Test
    /**
     * Test update a session with start time < end time, and counter > 0
     * Should return 200 and correct session
     */
    public void testPutSessionPositiveCounter() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());

        session.setStartTime(SESSION_START);
        session.setEndTime(SESSION_END);
        session.setCounter(3);
        CloseableHttpResponse response = util.putSessionBySessionId(user.getId(), project.getId(), session.getId(), session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        assert(session.equals(objectMapper.readValue(responseBody, Session.class)));
        response.close();
    }



}
