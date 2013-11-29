package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.context.AppContext;
import epfl.sweng.searchquestions.SearchActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;

public class SearchActivityTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {
	private Solo solo;
	private MockHttpClient httpClient;
	public SearchActivityTest() {
		super(SearchActivity.class);

	}
	@Override
	protected void setUp() throws Exception {
		getActivityAndWaitFor(TTChecks.SEARCH_ACTIVITY_SHOWN);
		solo = new Solo(getInstrumentation(), getActivity());
		AppContext.getContext().resetState();
	}
	@Override
	public void tearDown() {
	    solo.finishOpenedActivities();
	}
	public void testDisplayActivityCorrectly(){
		assertTrue("Edit text is found", solo.searchEditText("Type in the search query"));
		assertTrue("Button search is found", solo.searchText("Search"));
		assertFalse("Button is disabled", solo.getButton("Search").isEnabled());
		EditText editText = solo.getEditText("Type in the search query");
		solo.typeText(editText, "b");
		//getActivityAndWaitFor(TTChecks.QUERY_EDITED);
		assertTrue("Button is enabled", solo.getButton("Search").isEnabled());
	}
	public void testSubmitSearch(){
		httpClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(httpClient);
		httpClient.pushCannedResponse("/POST*/", HttpStatus.SC_OK, "{ " +
			  "\"questions\": [" +
			              "  { " +
			                "  \"id\": \"7654765\", " +
			                "  \"owner\": \"fruitninja\", " +
			                "  \"question\": \"How many calories are in a banana?\", " +
			                "  \"answers\": [ \"Just enough\", \"Too many\" ], " +
			                "  \"solutionIndex\": 0, " +
			                "  \"tags\": [ \"fruit\", \"banana\", \"trivia\" ] " +
			                " } " +
			              "], " +
			              " \"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""+
			            "}", "application/json");
		httpClient
		.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"{\"question\": \"What is the answer to life, the universe, and everything?\","
						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
						+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
				"application/json");
		EditText text = solo.getEditText("Type in the search query");
		solo.typeText(text, "b");
		//getActivityAndWaitFor(TTChecks.QUERY_EDITED);
		solo.clickOnButton("Search");
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
	}
	public void testBadSubmitSearch(){
		httpClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(httpClient);
		httpClient.pushCannedResponse("/POST*/", HttpStatus.SC_BAD_REQUEST, null, "application/json");
		httpClient
		.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"{\"question\": \"What is the answer to life, the universe, and everything?\","
						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
						+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
				"application/json");
		EditText text = solo.getEditText("Type in the search query");
		solo.typeText(text, "b");
		solo.clickOnButton("Search");
		//assertTrue("Error message displayed", solo.searchText("Error 400 on Tequila Server."));
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
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
