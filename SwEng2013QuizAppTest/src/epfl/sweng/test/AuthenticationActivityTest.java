package epfl.sweng.test;

import org.apache.http.HttpStatus;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;


public class AuthenticationActivityTest extends ActivityInstrumentationTestCase2<AuthenticationActivity> {
	private Solo solo;
	private MockHttpClient httpClient;
	
	public AuthenticationActivityTest(
			Class<AuthenticationActivity> activityClass) {
		super(activityClass);
	}
	
	@Override
	protected void setUp() {
		solo = new Solo(getInstrumentation());
	}
	/**
	 * Test if the AuthenticationActivity is correctly initialized
	 */
	public void displayCorrectlyAuthenticationActivity(){
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		assertTrue("GASPAR username editText is displayed", solo.searchEditText("GASPAR Username"));
		assertTrue("GASPAR password editText is displayed", solo.searchEditText("GASPAR Password"));
		assertTrue("Button log in is displayed", solo.searchButton("Log in using Tequila"));
		assertTrue("Button log in is enabled", solo.getButton("Log in using Tequila").isEnabled());
	}
	/**
	 * Test if the fields are cleared after a bad authentication
	 */
	public void badAuthenticationDisplaying(){
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		EditText username= solo.getEditText("GASPAR Username");
		EditText password = solo.getEditText("GASPAR Password");
		solo.typeText(username, "WhiteSnow");
		solo.typeText(password, "SevenDwarfs");
		solo.clickOnButton("Log in using Tequila");
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		assertFalse("GASPAR Username must be empty", solo.searchEditText("WhiteSnow"));
		assertFalse("GASPAR Password must be empty", solo.searchEditText("SevenDwarfs"));
		assertTrue("Button log in must be displayed", solo.searchButton("Log in using Tequila"));
		assertTrue("Button log in must be enabled", solo.getButton("Log in using Tequila").isEnabled());
	}
	
	public void correctAuthenticationDisplaying(){
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		EditText username= solo.getEditText("GASPAR Username");
		EditText password = solo.getEditText("GASPAR Password");
		solo.typeText(username, "WhiteSnow");
		solo.typeText(password, "SevenDwarfs");
		solo.clickOnButton("Log in using Tequila");
		
		//Envoyer la requete avec le mockHttpClient
		
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
