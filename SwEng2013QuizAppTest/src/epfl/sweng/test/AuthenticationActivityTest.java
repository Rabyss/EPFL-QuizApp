package epfl.sweng.test;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.context.AppContext;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;


public class AuthenticationActivityTest extends ActivityInstrumentationTestCase2<AuthenticationActivity> {
	private Solo solo;
	private MockHttpClient httpClient;
	private static final int ERROR_400 = 400;
	private static final int STATUS_200 = 200;
	private static final int STATUS_302 = 302;
	
	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);
	}
	
	@Override
	protected void setUp() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		solo = new Solo(getInstrumentation(), getActivity());
		AppContext.getContext().resetState();
	}
	
	@Override
	public void tearDown() {
	    solo.finishOpenedActivities();
	}
	
	/**
	 * Test if the AuthenticationActivity is correctly initialized
	 */
	public void testDisplayCorrectlyAuthenticationActivity() {
		assertTrue("GASPAR username editText is displayed", solo.searchEditText("GASPAR Username"));
		assertTrue("GASPAR password editText is displayed", solo.searchEditText("GASPAR Password"));
		assertTrue("Button log in is displayed", solo.searchButton("Log in using Tequila"));
		assertTrue("Button log in is enabled", solo.getButton("Log in using Tequila").isEnabled());
	}
	/**
	 * Test if the fields are cleared after a bad authentication
	 */
	public void testBadAuthenticationDisplaying() {
		httpClient= new MockHttpClient();
		SwengHttpClientFactory.setInstance(httpClient);
		httpClient.pushCannedResponse("/*/", ERROR_400, 
				null, "application/json");
		EditText username= solo.getEditText("GASPAR Username");
		EditText password = solo.getEditText("GASPAR Password");
		solo.typeText(username, "WhiteSnow");
		solo.typeText(password, "SevenDwarfs");
		solo.clickOnButton("Log in using Tequila");
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		assertFalse("GASPAR Username must be empty", solo.searchEditText("SnowWhite"));
		assertFalse("GASPAR Password must be empty", solo.searchEditText("SevenDwarfs"));
		assertTrue("Button log in must be displayed", solo.searchButton("Log in using Tequila"));
		assertTrue("Button log in must be enabled", solo.getButton("Log in using Tequila").isEnabled());
	}
	
	public void testCorrectAuthenticationDisplaying() {
		httpClient= new MockHttpClient();
		SwengHttpClientFactory.setInstance(httpClient);
		
		httpClient.pushCannedResponse("POST https://sweng-quiz.appspot.com/login", STATUS_200, 
				"{\"session\": \"<random_string>\","
			 +" \"message\": \"Here's your session id. Please include the following HTTP"
			 +"             header in your subsequent requests:\n"
			 +"            Authorization: Tequila <random_string>\"}", "application/json");
		
		httpClient.pushCannedResponse("POST https://tequila.epfl.ch/cgi-bin/tequila/login", STATUS_302, "{"
				+"  \"token\": \"rqtvk5d3za2x6ocak1a41dsmywogrdlv5\","
				+"  \"message\": \"Here's your authentication token. Please validate it "
				+"              with Tequila at https://tequila.epfl.ch/cgi-bin/tequila/login\" }",
				"application/json");	
		
		
		httpClient.pushCannedResponse("GET.*https://sweng-quiz.appspot.com/login", 
				STATUS_200, "{"
				+"  \"token\": \"rqtvk5d3za2x6ocak1a41dsmywogrdlv5\","
				+"  \"message\": \"Here's your authentication token. Please validate it "
				+"              with Tequila at https://tequila.epfl.ch/cgi-bin/tequila/login\" }",
				"application/json");
		EditText username= solo.getEditText("GASPAR Username");
		EditText password = solo.getEditText("GASPAR Password");
		solo.typeText(username, "SnowWhite");
		solo.typeText(password, "SevenDwarfs");
		solo.clickOnButton("Log in using Tequila");
		
		
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		
	}
	private void getActivityAndWaitFor(final TestCoordinator.TTChecks expected) {
		TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
			@Override
			public void initiate() {
				getActivity();
			}

			@Override
			public void verify(TestCoordinator.TTChecks notification) {
				assertEquals(String.format(
						"Expected notification %s, but received %s", expected,
						notification), expected, notification);
			}

			@Override
			public String toString() {
				return String.format("getActivityAndWaitFor(%s)", expected);
			}
		});
	}
}
