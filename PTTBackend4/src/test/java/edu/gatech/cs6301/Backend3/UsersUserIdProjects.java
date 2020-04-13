package edu.gatech.cs6301.Backend3;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6301.Backend3.Model.Project;
import edu.gatech.cs6301.Backend3.Model.User;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UsersUserIdProjects {
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
        System.out.println("*** STARTING TEST ***");
        util.deleteAllUsers();
    }

    @Test
    /**
     * Test get projects from a non-existing user id
     * The response should have 404 status code.
     */
    public void testNonExistingUserId() throws IOException {
        int nonExistingUserId = -1; //Logically this should not exist.
        CloseableHttpResponse response = util.getProjectsByUserId(nonExistingUserId);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
    }

    @Test
    /**
     * Test create project and associate to a non-existing user id
     * The response should have 404 status code.
     */
    public void testCreateProjectToNEUserId() throws IOException {
        int nonExistingUserId = -1;
        Project project = new Project(util.genProjectName());
        CloseableHttpResponse response = util.createProjectWithResponse(nonExistingUserId, project);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
    }

    @Test
    /**
     * Test create user, and create a project with empty project name
     * The response should have 400 status code.
     */
    public void testCreateProjectEmptyName() throws Exception {
        User responseObject = util.addUserSuccess();

        Project project = new Project("");
        CloseableHttpResponse response = util.createProjectWithResponse(responseObject.getId(), project);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    }

    // We plan to give more flexibility to users with project name
    // 
    // @Test
    // /**
    //  * Test create user, and create a project with an invalid project name (like **@#*)
    //  * The response should have 400 status code.
    //  * IT SEEMS THAT THE SERVER STUB HAS NO REGEXP CHECK ...
    //  */
    // public void testCreateProjectInvalidName() throws Exception {
    //     User responseObject = util.addUserSuccess();

    //     Project project = new Project("**@#*");
    //     CloseableHttpResponse response = util.createProjectWithResponse(responseObject.getId(), project);
    //     assert(response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
    // }

    @Test
    /**
     * Test create a project with an existing project name
     * The response should have 409 status code.
     */
    public void testCreateExistingProjectName() throws Exception {
        User responseObject = util.addUserSuccess();
        String projectName = util.genProjectName();
        //first creation of project having projectName
        CloseableHttpResponse response = util.createProjectWithResponse(responseObject.getId(), new Project(projectName));
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);

        //second creation of project having same projectName, should fail
        response = util.createProjectWithResponse(responseObject.getId(), new Project(projectName));
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.CONFLICT);
    }

    @Test
    /**
     * Test get projects of an user, who has more than 0 projects.
     * The response should have 200 status code.
     * The response should contain exactly the same projects as we add to the user.
     */
    public void testGetUserProjectsWithProject() throws IOException {
        //create user
        User user = new User("Qifan", "Zhang",util.genEmail());
        CloseableHttpResponse response = util.addUserWithResponse(user);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        int userId = objectMapper.readValue(responseBody, User.class).getId();

        //create and add to user TWO projects
        Project project1 = util.createProjectSuccess(userId);
        Project project2 = util.createProjectSuccess(userId);

        //response should have 200 status code, and contains 2 projects exactly as we created
        response = util.getProjectsByUserId(userId);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        responseBody = EntityUtils.toString(response.getEntity());
        Project[] projects = objectMapper.readValue(responseBody, Project[].class);
        assert(projects.length == 2);
        assert(projects[0].equalsExceptId(project1));
        assert(projects[1].equalsExceptId(project2));
    }

    @Test
    /**
     * Test get projects of an user, who has 0 project.
     * The response should have 200 status code.
     * The response should contain 0 project.
     */
    public void testGetUserProjectsWithoutProject() throws IOException {
        //create user
        User user = util.addUserSuccess();
        int userId = user.getId();
        //response should have 200 status code, and contains 0 project
        CloseableHttpResponse response = util.getProjectsByUserId(userId);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);
        String responseBody = EntityUtils.toString(response.getEntity());
        Project[] projects = objectMapper.readValue(responseBody, Project[].class);
        assert(projects.length == 0);
    }

    @Test
    /**
     * Test create new user and associate a new project to it.
     * The response should have 200 status code.
     * The response body should contain the same Project name.
     */
    public void testCreateProject() throws Exception {
        User user = util.addUserSuccess();

        Project project = new Project(util.genProjectName());
        CloseableHttpResponse response = util.createProjectWithResponse(user.getId(), project);
        assert(response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        Project responseProject = objectMapper.readValue(responseBody, Project.class);
        assert(project.equalsExceptId(responseProject));
    }
}
