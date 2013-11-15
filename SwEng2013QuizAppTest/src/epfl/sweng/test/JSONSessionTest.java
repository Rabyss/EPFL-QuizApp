package epfl.sweng.test;

import org.json.JSONException;
import epfl.sweng.authentication.JSONSession;
import epfl.sweng.context.AppContext;
import android.test.AndroidTestCase;

public class JSONSessionTest extends AndroidTestCase {
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		AppContext.getContext().resetState();
	}
	
	
	public void testConstruction() {
		String response = "{\"session\": \"c1b897bc7b79ac7b\", " +
					"\"message\": \"Here's your session id.\"}";
		
		try {
			JSONSession jsonSession = new JSONSession(response);
			assertNotNull(jsonSession);
		} catch (JSONException e) {
			fail("The JSON string is correct");
		}
	}
	
	public void testInvalidConstruction() {
		String invalidResponse = "{ [} }";
		try {
			new JSONSession(invalidResponse);
			fail("The consturctor should throw an exception.");
		} catch (JSONException e) {
			assertTrue(true);
		}
	}
	
	public void testGetter() {
		String response = "{\"session\": \"c1b897bc7b79ac7b\", " +
				"\"message\": \"Here's your session id.\"}";
		
		try {
			JSONSession jsonSession = new JSONSession(response);
			assertEquals(jsonSession.getSession(), "c1b897bc7b79ac7b");
		} catch (JSONException e) {
			fail("The JSON string is correct");
		}
	}
}
