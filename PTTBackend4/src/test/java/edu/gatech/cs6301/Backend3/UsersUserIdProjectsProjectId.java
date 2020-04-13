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

public class UsersUserIdProjectsProjectId {
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
     * Test get a project with non-existing project id
     * should return 404
     */
    public void testGetNonExistingUserProject() throws IOException {
        int nonExistingUserId = -1;
        CloseableHttpResponse response = util.getProjectByProjectId(nonExistingUserId, 999);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test put/update a non-existing user id
     * should return 404
     */
    public void testUpdateNonExistingUserProject() throws IOException {
        int nonExistingUserId = -1;
        Project project = new Project(util.genProjectName());
        CloseableHttpResponse response = util.updateProjectByProjectId(nonExistingUserId, 999, project);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test delete a non-existing user id
     * should return 404
     */
    public void testDeleteNonExistingUserProject() throws IOException {
        int nonExistingUserId = -1;
        CloseableHttpResponse response = util.deleteProjectByProjectId(nonExistingUserId, 99);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test get a non-existing project id
     * should return 404
     */
    public void testGetNonExistingProjectId() throws IOException {
        int nonExistingProjectId = -1;
        //first create a user and add to system.
        User responseObject = util.addUserSuccess();

        //currently user has no project, test get an non-existing project id should return 404
        CloseableHttpResponse response = util.getProjectByProjectId(responseObject.getId(), nonExistingProjectId);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test put/update a non-existing project id
     * should return 404
     */
    public void testPutNonExistingProjectId() throws IOException {
        int nonExistingProjectId = -1;
        //first create a user and add to system.
        User responseObject = util.addUserSuccess();

        //currently user has no project, update an non-existing project id should return 404
        Project project = new Project("project");
        CloseableHttpResponse response = util.updateProjectByProjectId(responseObject.getId(), nonExistingProjectId, project);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test delete a non-existing project id
     * should return 404
     */
    public void testDeleteNonExistingProjectId() throws IOException {
        int nonExistingProjectId = -1;
        //first create a user and add to system.
        User responseObject = util.addUserSuccess();

        //currently user has no project, delete an non-existing project id should return 404
        CloseableHttpResponse response = util.deleteProjectByProjectId(responseObject.getId(), nonExistingProjectId);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    // We plan to give more flexibility to users with project name
    // 
    // @Test
    // /**
    //  * Test put/update a project with an invalid project name
    //  * should return 400
    //  */
    // public void testPutInvalidProjectName() throws IOException {
    //     User user = util.addUserSuccess();
    //     Project project = util.createProjectSuccess(user.getId());

    //     project.setProjectname("(*^&#%*(");
    //     CloseableHttpResponse response = util.updateProjectByProjectId(user.getId(), project.getId(), project);
    //     assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    //     response.close();
    // }

    @Test
    /**
     * Test put/update a project with an empty project name
     * should return 400
     */
    public void testPutEmptyProjectName() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());

        project.setProjectname("");
        CloseableHttpResponse response = util.updateProjectByProjectId(user.getId(), project.getId(), project);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test put/update a project with an existing duplciated project name
     * should return 409
     */
    public void testPutDuplicateProjectName() throws IOException {
        User user = util.addUserSuccess();
        Project project1 = util.createProjectSuccess(user.getId());
        Project project2 = util.createProjectSuccess(user.getId());

        project1.setProjectname(project2.getProjectname());

        CloseableHttpResponse response = util.updateProjectByProjectId(user.getId(), project1.getId(), project1);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.CONFLICT);
        response.close();
    }

    @Test
    /**
     * Test get an existing project
     * Should return 200 and the project
     */
    public void testGetProjectSuccess() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());

        CloseableHttpResponse response = util.getProjectByProjectId(user.getId(), project.getId());
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        Project projectResponse = objectMapper.readValue(responseBody, Project.class);
        assert(project.equals(projectResponse));
    }

    @Test
    /**
     * Test put/update an existing project
     * Should return 200 and the project we updated
     */
    public void testPutProjectSuccess() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        project.setProjectname("NEW_PROJECT_NAME");

        CloseableHttpResponse response = util.updateProjectByProjectId(user.getId(), project.getId(),project);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        Project projectResponse = objectMapper.readValue(responseBody, Project.class);
        assert(project.equals(projectResponse));
    }


}
