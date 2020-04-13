package edu.gatech.cs6301.DevOps12;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class Users_get extends BaseTestCase {

	// *** YOU SHOULD NOT NEED TO CHANGE ANYTHING ABOVE THIS LINE ***

	// Purpose: Test getting all users successfully
	@Test
	public void pttTest1() throws Exception {
		try {
			// Create two users
			String id1, id2, email1 = TestUtils.generateEmailId("john1@doe.org");
			CloseableHttpResponse createResponse = TestUtils.createUser(httpclient, "John1", "Doe", email1);
			id1 = TestUtils.getIdFromResponse(createResponse);

			String email2 = TestUtils.generateEmailId("john2@doe.org");
			CloseableHttpResponse createResponse2 = TestUtils.createUser(httpclient, "John2", "Doe", email2);
			id2 = TestUtils.getIdFromResponse(createResponse2);

			// Get all users
			CloseableHttpResponse response = TestUtils.getAllUsers(httpclient);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			// Verify if both the users are returned
			HttpEntity entity = response.getEntity();
			String strEntity = EntityUtils.toString(entity);

			Assert.assertThat(strEntity, CoreMatchers.containsString(id1));
			Assert.assertThat(strEntity, CoreMatchers.containsString(email1));

			Assert.assertThat(strEntity, CoreMatchers.containsString(id2));
			Assert.assertThat(strEntity, CoreMatchers.containsString(email2));

			EntityUtils.consume(response.getEntity());
			response.close();
		} finally {
			httpclient.close();
		}
	}
}
