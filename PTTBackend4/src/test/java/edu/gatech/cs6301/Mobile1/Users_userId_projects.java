package edu.gatech.cs6301.Mobile1;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class Users_userId_projects extends PTTBackendTests {

    @Test
    // Purpose: Tests POST /users/users_id/projects
    public void pttTest1() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity);
            String projectid = getIdFromStringResponse(strResponse);
            String id = getIdFromStringResponse(strResponse);

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status != 201) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            System.out.println(
                    "*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String expectedJson = "{\"id\":" + projectid + ",\"projectname\":\"project1\"}";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests POST /users/users_id/projects with bad request
    public void pttTest2() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();
            String badid = "asdf";

            response = createProject("project1", badid);

            HttpEntity entity;

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status == 404) { // changed from 400 to 404 by Phalguna
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }

            response = createProject("", userid);
            status = response.getStatusLine().getStatusCode();
            ;
            if (status != 400) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests POST /users/users_id/projects user not found
    public void pttTest3() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid + "123");

            int status = response.getStatusLine().getStatusCode();
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
    // Purpose: Tests POST /users/users_id/projects with resource conflict
    public void pttTest4() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);
            response.close();

            response = createProject("project1", userid);

            int status = response.getStatusLine().getStatusCode();
            ;

            if (status != 409) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users_userid_projects
    public void pttTest5() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);

            String projectid = getIdFromResponse(response);
            response.close();

            response = getAllProjects(userid);
            int status = response.getStatusLine().getStatusCode();
            ;
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

            String expectedJson = "[{\"id\":" + projectid + ",\"projectname\":\"project1\"}]";
            JSONAssert.assertEquals(expectedJson, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users_userid_projects with bad request
    public void pttTest6() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();
            String badid = "asfd";

            response = createProject("project1", userid);

            response.close();

            response = getAllProjects(badid);
            int status = response.getStatusLine().getStatusCode();
            ;

            if (status != 404) { // Changed to 404 from 400 by Phalguna
                throw new ClientProtocolException("Unexpected response status:" + status);
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        } finally {
            httpclient.close();
        }
    }

    @Test
    // Purpose: Tests GET /users_userid_projects with user not found
    public void pttTest7() throws Exception {
        deleteAllUsers();
        try {
            CloseableHttpResponse response = createUser("John", "Doe", "john@doe.org");

            String userid = getIdFromResponse(response);
            response.close();

            response = createProject("project1", userid);

            response.close();

            response = getAllProjects(userid + "123");
            int status = response.getStatusLine().getStatusCode();
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
