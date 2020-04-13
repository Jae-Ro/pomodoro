package edu.gatech.cs6301.Mobile1;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userid_projects_projectid extends PTTBackendTests {
    @Test
    // Purpose: Test GET users/userid/projects/projectid
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = getProject(projectid, userid);
            HttpEntity entity;
            String strResponse;

            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            String expectedJson = "{\"id\":" + projectid + ",\"projectname\":\"project1\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test GET users/userid/projects/projectid with bad request
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();
            String badid = "asdf";

            response = getProject("asdf", badid);

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) { // changed from 400 to 404 by Phalguna
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test GET users/userid/projects/projectid with user/project not found
    public void pttTest3() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = getProject(projectid, userid + "123");

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response = getProject("123", userid);
            status = response.getStatusLine().getStatusCode();
            ;
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test PUT users/userid/projects/projectid
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = updateProject(userid, projectid, "newproject1");

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

            String expectedJson = "{\"id\":" + projectid + ",\"projectname\":\"newproject1\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test PUT users/userid/projects/projectid with bad request
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = updateProject("asdf", projectid, "project1");

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) { // 400 to 404 by phalguna
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response = updateProject(userid, "asdf", "project1");
            status = response.getStatusLine().getStatusCode();
            ;
            if (status != 404) { // phalguna
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test PUT users/userid/projects/projectid with user/project not found
    public void pttTest6() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = updateProject(userid + "123", projectid, "project1");

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response = updateProject(userid, projectid + "123", "project1");
            status = response.getStatusLine().getStatusCode();
            ;
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test PUT users/userid/projects/projectid with user/project resource
    // conflict
    public void pttTest7() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = createProject("project2", userid);
            String projectid2 = getIdFromResponse(response);
            response.close();

            response = updateProject(userid, projectid, "project2");

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 409) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test DELETE users/userid/projects/projectid
    public void pttTest8() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = deleteProject(userid, projectid);

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

            String expectedJson = "{\"id\":" + projectid + ",\"projectname\":\"project1\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

            response = getAllProjects(userid);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);
            expectedJson = "[]";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test DELETE users/userid/projects/projectid with bad request
    public void pttTest9() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = deleteProject("asdf", projectid);

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) { // 400 to 404 by tphaguna
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response = deleteProject(userid, "asdf");
            status = response.getStatusLine().getStatusCode();
            ;
            if (status != 404) { // phalguna
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Test DELETE users/userid/projects/projectid with user/project not
    // found
    public void pttTest10() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");
            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            String projectid = getIdFromResponse(response);
            response.close();

            response = deleteProject(userid + "123", projectid);

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response = deleteProject(userid, projectid + "123");
            status = response.getStatusLine().getStatusCode();
            ;
            if (status != 404) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }
}
