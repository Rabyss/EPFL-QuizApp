package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.R;
import epfl.sweng.authentication.UserStorage;
import epfl.sweng.context.AppContext;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;

public class CachingTest extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    private MockHttpClient httpClient;
    private static final int STATUS_200 = 200;
    private static final int STATUS_302 = 302;
    private String mRandomQuestionButton;
    
    public CachingTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() {
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
        solo = new Solo(getInstrumentation(), getActivity());
        if (solo.searchButton("Log out")) {
            solo.clickOnButton("Log out");
        }
        
        AppContext.getContext().resetState();
        mRandomQuestionButton = getActivity().getString(R.string.show_random_question);
        
    }

    public void testCacheInAction() {
        login();
        QuizQuestion cachedQuiz = cacheAQuestion();
        setOfflineMode();
        
        solo.clickOnButton(mRandomQuestionButton);
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        QuizQuestion displayedQuiz = ((ShowQuestionsActivity) solo.getCurrentActivity()).getCurrentQuizQuestion();
        compareQuizQuestion(cachedQuiz, displayedQuiz);
        
    }
    
    @Override
    public void tearDown() {
        UserStorage.getInstance(getActivity()).removeSessionID();
        solo.finishOpenedActivities();
    }
    
    public void testWhenNoInternetAccess() {
        login();
        QuizQuestion cachedQuiz = cacheAQuestion();
        
        solo.clickOnButton(mRandomQuestionButton);
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        QuizQuestion displayedQuiz = ((ShowQuestionsActivity) solo.getCurrentActivity()).getCurrentQuizQuestion();
        compareQuizQuestion(cachedQuiz, displayedQuiz);
    }
    
    private void compareQuizQuestion(QuizQuestion cachedQuiz, QuizQuestion displayedQuiz) {
        assertEquals("Cached question body and displayed are not the same.", cachedQuiz.getQuestion(), 
                displayedQuiz.getQuestion());
        
        int numCachedAnswers = cachedQuiz.getAnswers().size();
        assertEquals("Cached question has not the same number of answers as the displayed one.", 
                numCachedAnswers, displayedQuiz.getAnswers().size());
        
        for (int i = 0; i < numCachedAnswers; i++) {
            assertEquals("Cached answer is not the same as the displayed one.",
                    cachedQuiz.getAnswers().get(i), displayedQuiz.getAnswers().get(i));
            assertEquals("Not the same right answers.", cachedQuiz.isSolution(i), displayedQuiz.isSolution(i));
        }
        
        assertEquals("Cached question has not the same number of tags as the displayed one.",
                cachedQuiz.getTags().size(), displayedQuiz.getTags().size());
        
        assertTrue("Tags are not the same between the cached and the displayed quiz.",
                cachedQuiz.getTags().containsAll(displayedQuiz.getTags()));
    }
    
    private QuizQuestion cacheAQuestion() {
        
        solo.clickOnButton(mRandomQuestionButton);
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        QuizQuestion cachedQuiz = ((ShowQuestionsActivity) solo.getCurrentActivity()).getCurrentQuizQuestion();
        
        //now there is a question in the cache
        solo.goBack();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);

        httpClient.popCannedResponse();
        httpClient.popCannedResponse();
        httpClient.popCannedResponse();
        httpClient.popCannedResponse();
        
        return cachedQuiz;
    }
    
    private void setOfflineMode() {
        
        CheckBox cBox = (CheckBox) getActivity().findViewById(R.id.onlineModeCheckBox);
        assertFalse("Cannot find the checkbox", cBox == null);
        System.out.println(cBox.isChecked());
        if (!cBox.isChecked()) {
            solo.clickOnView(cBox); //check the offline-mode checkbox if unchecked
            getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
        }
        
        assertFalse("The app must be in offline mode after checking the check box.", AppContext.getContext().isOnline());
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
        httpClient
        .pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK,
                "{\"question\": \"What is the answer to life, the universe, and everything?\","
                        + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                        + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
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
