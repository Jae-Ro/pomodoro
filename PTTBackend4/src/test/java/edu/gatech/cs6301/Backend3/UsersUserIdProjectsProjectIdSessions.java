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

public class UsersUserIdProjectsProjectIdSessions {
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
     * Test get from a non-existing user id
     * Should return 404
     */
    public void getNonExistingUserIdSession() throws IOException {
        CloseableHttpResponse response = util.getSessionsByProjectId(-1, 99);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test post/create a session to a non-existing user id
     * Should return 404
     */
    public void postNonExistingUserIdSession() throws IOException {
        Session session = new Session(SESSION_START, SESSION_END, 1);
        CloseableHttpResponse response = util.createSessionWithResponse(-1, 99, session);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test get a session from a non-existing project id
     * Should return 404
     */
    public void getNonExistingProjectIdSession() throws IOException {
        User user = util.addUserSuccess();
        CloseableHttpResponse response = util.getSessionsByProjectId(user.getId(), 999);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test post/create a session to a non-existing project id
     * Should return 404
     */
    public void testPostNonExistingProjectId() throws IOException {
        User user = util.addUserSuccess();
        Session session = new Session(SESSION_START, SESSION_END, 1);
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(), 999, session);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test post a session with a wrong format starting time format
     * Should return 400
     */
    public void testPostWrongFormatStartTimeSession() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = new Session("$#%@",SESSION_END, 1);
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(),project.getId(),session);
        assert(response.getStatusLine().getStatusCode()==HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test post a session with an invalid starting time format ( start time > end time for example)
     * Should return 400
     */
    public void testPostInvalidStartTimeSession() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        String wrongStartTime = "2019-02-18T21:30Z"; //this is later than end time, should be invalid
        Session session = new Session(wrongStartTime,SESSION_END, 1);
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(),project.getId(),session);
        assert(response.getStatusLine().getStatusCode()==HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test post a session with a wrong format ending time format
     * Should return 400
     */
    public void testPostWrongFormatEndTimeSession() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = new Session(SESSION_START,"*&^", 1);
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(),project.getId(),session);
        assert(response.getStatusLine().getStatusCode()==HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test post a session with an invalid ending time ( start time > end time for example)
     * Should return 400
     */
    public void testPostInvalidEndTimeSession() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        String wrongEndTime = "2019-02-17T21:30Z"; //this is later than end time, should be invalid
        Session session = new Session(SESSION_START,wrongEndTime, 1);
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(),project.getId(),session);
        assert(response.getStatusLine().getStatusCode()==HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test post a session with an invalid counter (counter <0 for example)
     * Should return 400
     */
    public void testPostInvalidCounterSession() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = new Session(SESSION_START,SESSION_END, -1);
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(),project.getId(),session);
        assert(response.getStatusLine().getStatusCode()==HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test get sessions from a project with sessions.
     * Should return 200 and exactly the same sessions we added
     */
    public void testGetSessionsSuccess() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session1 = util.createSessionSuccess(user.getId(),project.getId());
        Session session2 = util.createSessionSuccess(user.getId(),project.getId());

        CloseableHttpResponse response = util.getSessionsByProjectId(user.getId(), project.getId());
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        Session[] responseSessions = objectMapper.readValue(responseBody, Session[].class);
        assert(responseSessions.length == 2);
        assert(responseSessions[0].equals(session1));
        assert(responseSessions[1].equals(session2));
        response.close();
    }

    @Test
    /**
     * Test get sessions from a project without sessions.
     * Should return 200 and 0 session
     */
    public void testGetProjectNoSessionsSuccess() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());

        CloseableHttpResponse response = util.getSessionsByProjectId(user.getId(), project.getId());
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        Session[] responseSessions = objectMapper.readValue(responseBody, Session[].class);
        assert(responseSessions.length == 0);
        response.close();
    }

    @Test
    /**
     * Test create a valid session with positive counter
     * Should return 201 and the session
     */
    public void testPostSessionPositiveCounter() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = new Session(SESSION_START, SESSION_END, 1 );
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(), project.getId(),session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        Session responseSession = objectMapper.readValue(responseBody, Session.class);
        assert(responseSession.equalsExceptId(session));
        response.close();
    }

    @Test
    /**
     * Test create a valid session with 0 counter
     * Should return 201 and the session
     */
    public void testPostSession0Counter() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = new Session(SESSION_START, SESSION_END, 0 );
        CloseableHttpResponse response = util.createSessionWithResponse(user.getId(), project.getId(),session);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        Session responseSession = objectMapper.readValue(responseBody, Session.class);
        assert(responseSession.equalsExceptId(session));
        response.close();
    }

}
