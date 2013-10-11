package epfl.sweng.test;

import static org.junit.Assert.*;
import junit.framework.TestSuite;

import org.json.JSONException;
import org.junit.Test;

import epfl.sweng.authentication.JSONToken;

public class JSONTokenTest extends TestSuite {
	@Test
	public void testConstruction() {
		String response = "{ message: \"Here's your authentication token.\", " +
							"token: \"5jiju5n4edckf6264u6kd5w7gpx73zdb\" }";
		try {
			JSONToken jsonToken = new JSONToken(response);
			assertNotNull(jsonToken);
		} catch (JSONException e) {
			fail("The JSON string is correct");
		}
	}
	
	@Test
	public void testInvalidConstruction() {
		String invalidResponse = "{ [} }";
		try {
			JSONToken jsonToken = new JSONToken(invalidResponse);
			fail("The consturctor should throw an exception.");
		} catch (JSONException e) {
			assertTrue(true);
		}
	}
}
