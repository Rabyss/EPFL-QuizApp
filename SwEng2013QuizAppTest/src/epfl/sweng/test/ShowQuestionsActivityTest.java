package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;

public class ShowQuestionsActivityTest extends
		ActivityInstrumentationTestCase2<ShowQuestionsActivity> {

	private Solo solo;
	private MockHttpClient httpClient;

	public ShowQuestionsActivityTest() {
		super(ShowQuestionsActivity.class);
	}

	@Override
	protected void setUp() {
        httpClient = new MockHttpClient();
        SwengHttpClientFactory.setInstance(httpClient);
	    pushCalculQuestion();
        pushLifeQuestion();
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testShowQuestion() {
		assertTrue(
				"Question is displayed",
				solo.searchText("What is the answer to life, the universe, and everything?"));
		assertTrue("Correct answer is displayed", solo.searchText("Forty-two"));
		assertTrue("Incorrect answer is displayed",
				solo.searchText("Twenty-seven"));
		assertTrue("Wait for an answer is displayed", solo.searchText("Wait for an answer..."));
		Button nextQuestionButton = solo.getButton("Next question");
		assertFalse("Next question button is disabled",
				nextQuestionButton.isEnabled());
		assertTrue("Tag 1 is display", solo.searchText("h2g2"));
		assertTrue("Tag 2 is display", solo.searchText("trivia"));
	}
	
	public void testBadAnswerSelected() {
		solo.clickOnText("Twenty-seven");
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
		assertTrue("Cross is displayed", solo.searchText("\u2718"));
		Button nextQuestionButton = solo.getButton("Next question");
		assertFalse("Next question button is disabled",
				nextQuestionButton.isEnabled());
		assertFalse("Wait for an answer is not displayed", solo.searchText("Wait for an answer..."));
	}
	
	public void testGoodAnswerSelected() {
		solo.clickOnText("Forty-two");
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
		assertTrue("Check is displayed", solo.searchText("\u2714"));
		assertFalse("Cross is not displayed", solo.searchText("\u2718"));
		Button nextQuestionButton = solo.getButton("Next question");
		assertTrue("Next question button is enabled",
				nextQuestionButton.isEnabled());
		// TODO Test if listView is disabled but how ???
		assertFalse("Wait for an answer is not displayed", solo.searchText("Wait for an answer..."));
	}
	
	public void testReloadQuestion() {
		
		assertTrue(
				"Question is displayed",
				solo.searchText("What is the answer to life, the universe, and everything?"));
		assertTrue("Correct answer is displayed", solo.searchText("Forty-two"));
		assertTrue("Incorrect answer is displayed",
				solo.searchText("Twenty-seven"));
		assertTrue("Wait for an answer is displayed", solo.searchText("Wait for an answer..."));
		Button nextQuestionButton = solo.getButton("Next question");
		assertFalse("Next question button is disabled",
				nextQuestionButton.isEnabled());
		
		
		httpClient.popCannedResponse();
		solo.clickOnText("Forty-two");
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
		solo.clickOnText("Next question");
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assertTrue(
				"Question is displayed",
				solo.searchText("ONE PLUS ONE"));
		assertTrue("Correct answer is displayed", solo.searchText("TWO"));
		assertTrue("Incorrect answer is displayed",
				solo.searchText("ONE"));
		assertTrue("Wait for an answer is displayed", solo.searchText("Wait for an answer..."));
		nextQuestionButton = solo.getButton("Next question");
		assertFalse("Next question button is disabled",
				nextQuestionButton.isEnabled());
	}

	private void pushLifeQuestion() {
		httpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");
	}
	
	private void pushCalculQuestion() {
		httpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"ONE PLUS ONE\","
								+ " \"answers\": [\"ONE\", \"TWO\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 1, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");
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