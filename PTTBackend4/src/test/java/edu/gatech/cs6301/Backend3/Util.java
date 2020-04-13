package edu.gatech.cs6301.Backend3;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6301.Backend3.Model.Project;
import edu.gatech.cs6301.Backend3.Model.Session;
import edu.gatech.cs6301.Backend3.Model.User;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Date;

public class Util {
    protected String baseUrl = "http://gazelle.cc.gatech.edu:9305/";
    private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpclient;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String contentType = "application/json";
    private final String EMAIL_POSTFIX = "@gatech.edu";
    private final String PROJECT_PREFIX = "project-";
    private final String DEFAULT_SESSION_START = "2019-02-18T20:00Z";
    private final String DEFAULT_SESSION_END = "2019-02-18T20:30Z";
    private final int DEFAULT_SESSION_COUNTER = 1;


    public Util() {
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(10);
        HttpHost localhost = new HttpHost("http://127.0.0.1", 8000);
        cm.setMaxPerRoute(new HttpRoute(localhost), 10);
        httpclient = HttpClients.custom().setConnectionManager(cm).build();
    }

    public void close() throws IOException {
        httpclient.close();
    }

    public String genEmail() {
        Date date = new Date();
        return date.getTime() + EMAIL_POSTFIX;
    }

    public String genProjectName() {
        Date date = new Date();
        return PROJECT_PREFIX + date.getTime();
    }

    public CloseableHttpResponse addUserWithResponse(User user) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users");
        httpRequest.addHeader("accept", contentType);
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(user));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        return httpclient.execute(httpRequest);
    }

    public User addUserSuccess() throws IOException {
        User user = new User("Qifan", "Zhang", this.genEmail());
        CloseableHttpResponse response = this.addUserWithResponse(user);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        User responseObject = objectMapper.readValue(responseBody, User.class);
        assert (user.equalExceptId(responseObject));
        return responseObject;
    }

    public CloseableHttpResponse putUserById(int id, User user) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", contentType);
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(user));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        return httpclient.execute(httpRequest);
    }


    public CloseableHttpResponse deleteUserById(int id) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", "application/json");
        return httpclient.execute(httpRequest);
    }

    public void deleteAllUsers() throws IOException {
        CloseableHttpResponse response = getAllUsers();
        String responseBody = EntityUtils.toString(response.getEntity());
        User[] usersToDelete = objectMapper.readValue(responseBody, User[].class);
        for (User user : usersToDelete) {
            deleteUserById(user.getId());
        }
        response.close();
    }

    public CloseableHttpResponse getAllUsers() throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users");
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse getUserById(int id) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + id);
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse getProjectsByUserId(int userId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse createProjectWithResponse(int userId, Project project) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects");
        httpRequest.addHeader("accept", contentType);
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(project));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        return httpclient.execute(httpRequest);
    }

    public Project createProjectSuccess(int userId) throws IOException {
        String projectName1 = this.genProjectName();
        Project project1 = new Project(projectName1);
        CloseableHttpResponse response = this.createProjectWithResponse(userId, project1);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        Project project = objectMapper.readValue(responseBody, Project.class);
        return project;
    }

    public Project createProjectSuccess(int userId, String projectName) throws IOException {
        Project project1 = new Project(projectName);
        CloseableHttpResponse response = this.createProjectWithResponse(userId, project1);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        Project project = objectMapper.readValue(responseBody, Project.class);
        return project;
    }

    public CloseableHttpResponse getProjectByProjectId(int userId, int projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse updateProjectByProjectId(int userId, int projectId, Project project) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", contentType);
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(project));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse deleteProjectByProjectId(int userId, int projectId) throws IOException {
        HttpDelete httpRequest = new HttpDelete(baseUrl + "/users/" + userId + "/projects/" + projectId);
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse getSessionsByProjectId(int userId, int projectId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse createSessionWithResponse(int userId, int projectId, Session session) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(session));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public Session createSessionSuccess(int userId, int projectId, String start, String end, int counter) throws IOException {
        HttpPost httpRequest = new HttpPost(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions");
        Session session = new Session(start, end, counter);
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(session));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        httpRequest.addHeader("accept", contentType);
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.CREATED);
        String responseBody = EntityUtils.toString(response.getEntity());
        return objectMapper.readValue(responseBody, Session.class);
    }

    public Session createSessionSuccess(int userId, int projectId) throws IOException {
        return createSessionSuccess(userId, projectId, DEFAULT_SESSION_START, DEFAULT_SESSION_END, DEFAULT_SESSION_COUNTER);
    }

    public CloseableHttpResponse getSessionBySessionId(int userId, int projectId, int sessionId) throws IOException {
        HttpGet httpRequest = new HttpGet(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse putSessionBySessionId(int userId, int projectId, int sessionId, Session session) throws IOException {
        HttpPut httpRequest = new HttpPut(baseUrl + "/users/" + userId + "/projects/" + projectId + "/sessions/" + sessionId);
        httpRequest.addHeader("accept", contentType);
        StringEntity input = new StringEntity(objectMapper.writeValueAsString(session));
        input.setContentType(contentType);
        httpRequest.setEntity(input);
        return httpclient.execute(httpRequest);
    }

    public CloseableHttpResponse getReport(String userId, String projectId, String from, String to, boolean includeCompletedP, boolean includeTotalH) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl).append("/users/").append(userId)
                .append("/projects/").append(projectId)
                .append("/report")
                .append("?from=").append(from)
                .append("&to=").append(to);
        if (includeCompletedP)
                sb.append("&includeCompletedPomodoros=true");
        if (includeTotalH)
                sb.append("&includeTotalHoursWorkedOnProject=true");
        HttpGet httpRequest = new HttpGet(sb.toString());
        httpRequest.addHeader("accept", contentType);
        return httpclient.execute(httpRequest);
    }
}
