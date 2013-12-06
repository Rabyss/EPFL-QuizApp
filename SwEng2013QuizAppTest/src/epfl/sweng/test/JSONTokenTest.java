package epfl.sweng.test;


import org.json.JSONException;

import android.test.AndroidTestCase;

import epfl.sweng.authentication.JSONToken;
import epfl.sweng.context.AppContext;

public class JSONTokenTest extends AndroidTestCase {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        AppContext.getContext().resetState();
    }
    
    
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
    
    public void testGetter() {
        String response = "{ message: \"Here's your authentication token.\", " +
                "token: \"5jiju5n4edckf6264u6kd5w7gpx73zdb\" }";
        try {
            JSONToken jsonToken = new JSONToken(response);
            assertEquals(jsonToken.getToken(), "5jiju5n4edckf6264u6kd5w7gpx73zdb");
        } catch (JSONException e) {
            fail("The JSON string is correct");
        }
    }
}
