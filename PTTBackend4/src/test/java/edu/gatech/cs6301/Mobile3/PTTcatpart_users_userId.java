package edu.gatech.cs6301.Mobile3;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PTTcatpart_users_userId extends Helper {

    // Purpose: This testcase will verify whether info of created user is correct or not.
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1", "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");

            response = getUser(id);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we passed invalid user schema!!");
            }

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String user1_schema = getValidUserSchemaTypeString(id, "F1", "L1", "E1@gmail.com");
            JSONAssert.assertEquals(user1_schema, strResponse, false);
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will verify whether server handles invalid userId.
    //TODO: This testcase should be removed from tsl file as well.
    @Test
    public void pttTest2() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1", "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");

            response = getUser(id + "000");
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 404) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 404 instead of " + status + " because we passed invalid userId!!");
            }
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will verify that if we update the user info such that it clashes with user's info.
    //This test case fails bcz server's PUT API is wrong.
    //TODO: This testcase should be removed from tsl file as well.
    @Test
    public void pttTest3() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1",   "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            userSchema = getValidUserSchemaTypeString("0", "F2", "L2", "E2@gmail.com");
            response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            userSchema = getValidUserSchemaTypeString(id, "F3", "L3", "E1@gmail.com");
            response = updateUser(id, userSchema);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we tried to update the info such that it conflicts with other user info. Ideally it should be 409 (Resource Conflict)!!");
            }
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to create an user with invalid schema.
    @Test
    public void pttTest4() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1",   "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            userSchema = getInvalidUserSchemaTypeString(id, "F3", "L3", "E3@gmail.com");
            response = updateUser(id, userSchema);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we tried to update the info with invalid schema!!");
            }

            response.close();
            deleteAllUsers();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to create an user with valid schema but empty content.
    //This test case is not working as PUT is accepting empty strings.
    @Test
    public void pttTest5() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1",   "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            userSchema = getValidUserSchemaTypeString(id, "", "", "E1@gmail.com");
            response = updateUser(id, userSchema);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we tried to update the info with invalid content (empty strings)!!");
            }

            response.close();
            deleteAllUsers();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will verify PUT api succesfully update the info.
    //PUT API not working.
    @Test
    public void pttTest6() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1", "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            userSchema = getValidUserSchemaTypeString("1", "F2", "L2", "E3@gmail.com");   // changes to "E3@gmail.com"
            response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            userSchema = getValidUserSchemaTypeString(id, "F3", "L3", "E3@gmail.com");
            response = updateUser(id, userSchema);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
            } else {
                throw new ClientProtocolException("Expecting status code of 200 instead of " + status + " because it is supposed to successfuly update the user info!!");
            }
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String user2_schema = getValidUserSchemaTypeString(id, "F3", "L3", "E3@gmail.com");
            JSONAssert.assertEquals(user2_schema, strResponse, false);
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    //Purpose: This testcase will try to delete an user with no projects.
    @Test
    public void pttTest7() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1",   "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            response = deleteUser(id);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
            } else {
                throw new ClientProtocolException("Expecting status code of 200 instead of " + status + " because we are successfuly supposed to delete user!!");
            }
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String user1_schema = getValidUserSchemaTypeString(id, "F1", "L1", "E1@gmail.com");
            JSONAssert.assertEquals(user1_schema, strResponse, false);
            response.close();
            System.out.println("Cleanup==============");
            deleteAllUsers();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to delete an user with projects and admin confirm deletion.
    // The test is incomplete bcz we dont know how admin consent will be given
    @Test
    public void pttTest8() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1",   "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            String projectSchema = getValidProjectSchemaTypeString("0", "P1");
            response = createProject(id, projectSchema);
            String pid = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            response = deleteUser(id);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
                strResponse = EntityUtils.toString(entity);
            } else {
                throw new ClientProtocolException("Expecting status code of 200 instead of " + status + " because admin give consent to delete user with projects!!");
            }
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

            String user1_schema = getValidUserSchemaTypeString(id, "F1", "L1", "E1@gmail.com");
            JSONAssert.assertEquals(user1_schema, strResponse, false);
            response.close();
            System.out.println("Cleanup==============");
            deleteAllUsers();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to delete an user with projects and admin confirm deletion.
    // The test is incomplete bcz we dont know how admin consent will be given
    // @Test
    // public void pttTest9() throws Exception {
    //     // httpclient = HttpClients.createDefault();
    //     deleteAllUsers();
    //     String id = null;

    //     try {

    //         String userSchema = getValidUserSchemaTypeString("0", "F1", "L1",   "E1@gmail.com");
    //         CloseableHttpResponse response = createUser(userSchema);
    //         id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
    //         response.close();

    //         String projectSchema = getValidProjectSchemaTypeString("0", "P1");
    //         response = createProject(id, projectSchema);
    //         String pid = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
    //         response.close();

    //         response = deleteUser(id);
    //         int status = response.getStatusLine().getStatusCode();
    //         HttpEntity entity;
    //         String strResponse;
    //         if (status == 400) {
    //             entity = response.getEntity();
    //             strResponse = EntityUtils.toString(entity);
    //         } else {
    //             throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because admin doesnt give consent to delete user with projects!!");
    //         }
    //         System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");

    //         String user1_schema = getValidUserSchemaTypeString(id, "F1", "L1", "E1@gmail.com");
    //         JSONAssert.assertEquals(user1_schema, strResponse, false);
    //         response.close();
    //         System.out.println("Cleanup==============");
    //         deleteAllUsers();
    //     }
    //     finally
    //     {
    //         httpclient.close();
    //     }
    // }
}
