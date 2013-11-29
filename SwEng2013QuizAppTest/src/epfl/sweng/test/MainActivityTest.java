package epfl.sweng.test;

import org.apache.http.HttpStatus;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import epfl.sweng.authentication.UserStorage;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	private MockHttpClient httpClient;
	private static final int STATUS_200 = 200;
	private static final int STATUS_302 = 302;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		solo = new Solo(getInstrumentation(), getActivity());
		if (solo.searchButton("Log out")) {
			solo.clickOnButton("Log out");
			getActivityAndWaitFor(TTChecks.LOGGED_OUT);
		}
		//AppContext.getContext().resetState();
	}

	@Override
	public void tearDown() {
		UserStorage.getInstance(getActivity()).removeSessionID();
		solo.finishOpenedActivities();
	}

	/**
	 * Test the main activity if an user push the log out button
	 */
	public void testLogOut() {
		login();
		solo.clickOnButton("Log out");
		getActivityAndWaitFor(TTChecks.LOGGED_OUT);
		assertTrue("Show question button is shown",
				solo.searchButton("Show a random question.", 1));
		assertTrue("Submit question button is shown",
				solo.searchText("Submit a quiz question."));
		assertTrue("Log in button is shown",
				solo.searchText("Log in using Tequila"));
		assertFalse("show question button is disabled",
				solo.getButton("Show a random question.").isEnabled());
		assertFalse("submit question button is disabled",
				solo.getButton("Submit a quiz question.").isEnabled());
		assertTrue("Log in button is enabled",
				solo.getButton("Log in using Tequila").isEnabled());
		assertTrue("Search button is shown", solo.searchText("Search"));
		assertFalse("Search Button is enabled", solo.getButton("Search").isEnabled());
	}

	/**
	 * Test the main activity if an user is logged
	 */
	public void testLoggedUser() {
		login();
		assertTrue("Show question button is shown",
				solo.searchButton("Show a random question.", 1));
		assertTrue("Submit question button is shown",
				solo.searchText("Submit a quiz question."));
		assertTrue("Log out button is shown", solo.searchText("Log out"));
		assertTrue("show question button is enabled",
				solo.getButton("Show a random question.").isEnabled());
		assertTrue("submit question button is enabled",
				solo.getButton("Submit a quiz question.").isEnabled());
		assertTrue("Log in button is enabled", solo.getButton("Log out")
				.isEnabled());
		assertTrue("Search button is shown", solo.searchText("Search"));
		assertTrue("Search button is enabled", solo.getButton("Search").isEnabled());

	}

	public void testSubmitQuestionButton() {
		login();
		solo.clickOnButton("Submit a quiz question.");
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	public void testShowQuestionButton() {
		login();
		solo.clickOnButton("Show a random question.");
		httpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
	}

	public void testAuthenticateButton() {
		solo.clickOnButton("Log in using Tequila");
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
	}
	public void testSearchButton() {
		login();
		solo.clickOnButton("Search");
		getActivityAndWaitFor(TTChecks.SEARCH_ACTIVITY_SHOWN);
	}

	private void login() {

		solo.clickOnButton("Log in using Tequila");
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		httpClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(httpClient);

		httpClient
				.pushCannedResponse(
						"POST https://sweng-quiz.appspot.com/login",
						STATUS_200,
						"{\"session\": \"<random_string>\","
								+ " \"message\": \"Here's your session id. Please include the following HTTP"
								+ "             header in your subsequent requests:\n"
								+ "            Authorization: Tequila <random_string>\"}",
						"application/json");

		httpClient
				.pushCannedResponse(
						"POST https://tequila.epfl.ch/cgi-bin/tequila/login",
						STATUS_302,
						"{"
								+ "  \"token\": \"rqtvk5d3za2x6ocak1a41dsmywogrdlv5\","
								+ "  \"message\": \"Here's your authentication token. Please validate it "
								+ "              with Tequila at https://tequila.epfl.ch/cgi-bin/tequila/login\" }",
						"application/json");

		httpClient
				.pushCannedResponse(
						"GET.*https://sweng-quiz.appspot.com/login",
						STATUS_200,
						"{"
								+ "  \"token\": \"rqtvk5d3za2x6ocak1a41dsmywogrdlv5\","
								+ "  \"message\": \"Here's your authentication token. Please validate it "
								+ "              with Tequila at https://tequila.epfl.ch/cgi-bin/tequila/login\" }",
						"application/json");
		EditText username = solo.getEditText("GASPAR Username");
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
