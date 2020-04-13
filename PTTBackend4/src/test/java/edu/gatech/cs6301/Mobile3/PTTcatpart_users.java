package edu.gatech.cs6301.Mobile3;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.skyscreamer.jsonassert.JSONAssert;

public class PTTcatpart_users extends Helper {

    // Purpose: This testcase will test the /users/GET.
    @Test
    public void pttTest1() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;
        String expectedJson = "";

        try {

            expectedJson += "[";

            CloseableHttpResponse response = createUser("F1", "L1", "E1@gmail.com");
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            String user1_schema = getValidUserSchemaTypeString(id, "F1", "L1", "E1@gmail.com");
            expectedJson += user1_schema;
            response.close();

            //Thread.sleep(2000);
            expectedJson += ",";

            response = createUser("F2", "L2", "E2@gmail.com");
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            String user2_schema = getValidUserSchemaTypeString(id, "F2", "L2", "E2@gmail.com");
            expectedJson += user2_schema;
            response.close();

            expectedJson += "]";

            response = getAllUsers();
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 200) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            strResponse = EntityUtils.toString(entity);

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            //System.out.println(expectedJson);
            //System.out.println(strResponse);

            JSONAssert.assertEquals(expectedJson,strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will test whether creating an existing user results in error or not.
    // Any 2 users with same email Id are supposed to be the same users.
    @Test
    public void pttTest2() throws Exception {
        // httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            CloseableHttpResponse response = createUser("F1", "L1", "E1@gmail.com");
            id = extractFieldFromJSONObjectTypeString(EntityUtils.toString(response.getEntity()), "id");
            response.close();

            response = createUser("F2", "L2", "E1@gmail.com");

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 409) {
                // System.out.println("Correct response for empty email");
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 409 instead of " + status + " because we just tried to create an user with the email Id that is already taken!!");
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            // EntityUtils.consume(response.getEntity());
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to create an user with invalid schema.
    @Test
    public void pttTest3() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getInvalidUserSchemaTypeString("0", "F1", "L1", "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we passed invalid user schema!!");
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to create an user by passing empty strings in user schema.
    // This should result in bad request as we expect all fields to be nonempty
    @Test
    public void pttTest4() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1", "");
            CloseableHttpResponse response = createUser(userSchema);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse;
            if (status == 400) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 400 instead of " + status + " because we passed empty strings in user schema!!");
            }
            strResponse = EntityUtils.toString(entity);
            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            response.close();
        }
        finally
        {
            httpclient.close();
        }
    }

    // Purpose: This testcase will try to create an user by passing valid schema with valid content.
    @Test
    public void pttTest5() throws Exception {
        httpclient = HttpClients.createDefault();
        deleteAllUsers();
        String id = null;

        try {

            String userSchema = getValidUserSchemaTypeString("0", "F1", "L1", "E1@gmail.com");
            CloseableHttpResponse response = createUser(userSchema);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            String strResponse = null;

            if (status == 201) {
                entity = response.getEntity();
            } else {
                throw new ClientProtocolException("Expecting status code of 201 instead of " + status + " because we expect a new user to be created!!");
            }

            strResponse = EntityUtils.toString(entity);

            id = extractFieldFromJSONObjectTypeString(strResponse, "id");
            String user1_schema = getValidUserSchemaTypeString(id, "F1", "L1", "E1@gmail.com");

            System.out.println("*** String response " + strResponse + " (" + response.getStatusLine().getStatusCode() + ") ***");
            JSONAssert.assertEquals(user1_schema, strResponse, false);
            EntityUtils.consume(response.getEntity());
            response.close();

        }
        finally
        {
            httpclient.close();
        }
    }
}
