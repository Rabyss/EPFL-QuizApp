package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.context.AppContext;
import epfl.sweng.proxy.Proxy;
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
    
    private static final String SEARCH = "Search";
    private static final String TYPE_IN = "Type in the search query";
    
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
        Proxy.getInstance(getActivity().getApplicationContext()).resetState();
        solo.finishOpenedActivities();
    }

    public void testDisplayActivityCorrectly() {
        assertTrue("Edit text is found",
                solo.searchEditText(TYPE_IN));
        assertTrue("Button search is found", solo.searchText(SEARCH));
        assertFalse("Button is disabled", solo.getButton(SEARCH).isEnabled());
        EditText editText = solo.getEditText(TYPE_IN);
        solo.typeText(editText, "b");
        assertTrue("Button is enabled", solo.getButton(SEARCH).isEnabled());
    }

    public void testSubmitSearch() {
        httpClient = new MockHttpClient();
        SwengHttpClientFactory.setInstance(httpClient);
        httpClient
                .pushCannedResponse(
                        "POST (?:https?://[^/]+|[^/]+)?/+sweng-quiz.appspot.com/search\\b",
                        HttpStatus.SC_OK,
                        "{ "
                                + "\"questions\": ["
                                + "  { "
                                + "  \"id\": 7654765, "
                                + "  \"owner\": \"fruitninja\", "
                                + "  \"question\": \"How many calories are in a banana?\", "
                                + "  \"answers\": [ \"Just enough\", \"Too many\" ], "
                                + "  \"solutionIndex\": 0, "
                                + "  \"tags\": [ \"fruit\", \"banana\", \"trivia\" ] "
                                + " } "
                                + "], "
                                + " \"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
                                + "}", "application/json");
        EditText text = solo.getEditText(TYPE_IN);
        solo.typeText(text, "b");
        solo.clickOnButton(SEARCH);
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
