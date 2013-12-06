package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.context.AppContext;
import epfl.sweng.proxy.Proxy;
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
    
    private static final String QUESTION_UNIVERSE = "What is the answer to life, the universe, and everything?";
    private static final String GET_RANDOM = "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b";
    private static final String CORRECT_ANSWER_IS_DISPLAYED = "Correct answer is displayed";
    private static final String INCORRECT_ANSWER_IS_DISPLAYED = "Incorrect answer is displayed";
    private static final String WAIT_NOT_DISPLAYED = "Wait for an answer is not displayed";
    private static final String NEXT_QUESTION_DISABLED = "Next question button is disabled";
    private static final String WAIT_DISPLAYED = "Wait for an answer is displayed";
    private static final String NEXT_QUESTION = "Next question";
    private static final String MIME_JSON = "application/json";
    private static final String QUESTION_IS_DISPLAYED = "Question is displayed";
    private static final String CROSS = "\u2718";
    private static final String WAIT_FOR_ANSWER = "Wait for an answer...";

    private static final String A42 = "Forty-two";
    private static final String A27 = "Twenty-seven";

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
        AppContext.getContext().resetState();
    }
    
    @Override
    public void tearDown() {
        Proxy.getInstance(getActivity().getApplicationContext()).resetState();
        solo.finishOpenedActivities();
    }

    public void testShowQuestion() {
        assertTrue(
                QUESTION_IS_DISPLAYED,
                solo.searchText(QUESTION_UNIVERSE));
        assertTrue(CORRECT_ANSWER_IS_DISPLAYED, solo.searchText(A42));
        assertTrue(INCORRECT_ANSWER_IS_DISPLAYED,
                solo.searchText(A27));
        assertTrue(WAIT_DISPLAYED, solo.searchText(WAIT_FOR_ANSWER));
        Button nextQuestionButton = solo.getButton(NEXT_QUESTION);
        assertFalse(NEXT_QUESTION_DISABLED,
                nextQuestionButton.isEnabled());
        assertTrue("Tag 1 is display", solo.searchText("h2g2"));
        assertTrue("Tag 2 is display", solo.searchText("trivia"));
    }
    
    public void testBadAnswerSelected() {
        Proxy.getInstance(getActivity().getApplicationContext()).resetState();
        solo.clickOnText(A27);
        getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
        assertTrue("Cross is displayed", solo.searchText(CROSS));
        Button nextQuestionButton = solo.getButton(NEXT_QUESTION);
        assertFalse(NEXT_QUESTION_DISABLED,
                nextQuestionButton.isEnabled());
        assertFalse(WAIT_NOT_DISPLAYED, solo.searchText(WAIT_FOR_ANSWER));
    }
    
    public void testGoodAnswerSelected() {
        solo.clickOnText(A42);
        getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
        assertTrue("Check is displayed", solo.searchText("\u2714"));
        assertFalse("Cross is not displayed", solo.searchText(CROSS));
        Button nextQuestionButton = solo.getButton(NEXT_QUESTION);
        assertTrue("Next question button is enabled",
                nextQuestionButton.isEnabled());
        assertFalse(WAIT_NOT_DISPLAYED, solo.searchText(WAIT_FOR_ANSWER));
    }
    
    public void testReloadQuestion() {
        
        assertTrue(
                QUESTION_IS_DISPLAYED,
                solo.searchText(QUESTION_UNIVERSE));
        assertTrue(CORRECT_ANSWER_IS_DISPLAYED, solo.searchText(A42));
        assertTrue(INCORRECT_ANSWER_IS_DISPLAYED,
                solo.searchText(A27));
        assertTrue(WAIT_DISPLAYED, solo.searchText(WAIT_FOR_ANSWER));
        Button nextQuestionButton = solo.getButton(NEXT_QUESTION);
        assertFalse(NEXT_QUESTION_DISABLED,
                nextQuestionButton.isEnabled());
        
        
        httpClient.popCannedResponse();
        solo.clickOnText(A42);
        getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
        solo.clickOnText(NEXT_QUESTION);
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        assertTrue(
                QUESTION_IS_DISPLAYED,
                solo.searchText("ONE PLUS ONE"));
        assertTrue(CORRECT_ANSWER_IS_DISPLAYED, solo.searchText("TWO"));
        assertTrue(INCORRECT_ANSWER_IS_DISPLAYED,
                solo.searchText("ONE"));
        assertTrue(WAIT_DISPLAYED, solo.searchText(WAIT_FOR_ANSWER));
        nextQuestionButton = solo.getButton(NEXT_QUESTION);
        assertFalse(NEXT_QUESTION_DISABLED,
                nextQuestionButton.isEnabled());
    }
    
    public void testBadRequest() {
        httpClient.pushCannedResponse(
                GET_RANDOM, 
                HttpStatus.SC_BAD_REQUEST, 
                "", 
                MIME_JSON);
        solo.clickOnText(A42);
        getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
        solo.clickOnText(NEXT_QUESTION);
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        assertTrue("Client is not notified of the error.", 
                solo.searchText(getActivity().getString(epfl.sweng.R.string.fetch_server_failure)));
    }

    private void pushLifeQuestion() {
        httpClient
                .pushCannedResponse(
                        GET_RANDOM,
                        HttpStatus.SC_OK,
                        "{\"question\": \""+QUESTION_UNIVERSE+"\","
                                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                        MIME_JSON);
        
    }
    
    private void pushCalculQuestion() {
        httpClient
                .pushCannedResponse(
                        GET_RANDOM,
                        HttpStatus.SC_OK,
                        "{\"question\": \"ONE PLUS ONE\","
                                + " \"answers\": [\"ONE\", \"TWO\"], \"owner\": \"sweng\","
                                + " \"solutionIndex\": 1, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                        MIME_JSON);
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