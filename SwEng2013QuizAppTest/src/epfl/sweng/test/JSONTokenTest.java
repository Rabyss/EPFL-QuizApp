package epfl.sweng.test;


import org.json.JSONException;

import android.test.AndroidTestCase;

import epfl.sweng.authentication.JSONToken;

public class JSONTokenTest extends AndroidTestCase {
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
	
	public void testInvalidConstruction() {
		String invalidResponse = "{ [} }";
		try {
			new JSONToken(invalidResponse);
			fail("The consturctor should throw an exception.");
		} catch (JSONException e) {
			assertTrue(true);
		}
	}
}
