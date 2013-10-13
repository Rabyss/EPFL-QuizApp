package epfl.sweng.test.minimalmock;





import org.apache.http.HttpStatus;
import org.junit.Assert;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

import epfl.sweng.QuizQuestion;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class HttpClientTest extends ActivityInstrumentationTestCase2<EditQuestionActivity> { 
	
	private Solo solo;
	private QuizQuestion quizQuestion;
	
	  public HttpClientTest() {
	        super(EditQuestionActivity.class);
	    }
	  @Override
	    public void setUp() throws Exception {
	        super.setUp();

	        solo = new Solo(getInstrumentation());
	     
	        String[] answer = {"42", "27"};
	        String[] tags = {"hello", "salut"};
	        quizQuestion= new QuizQuestion(1, "What is the answer to life, the universe, and everything?", answer ,0, tags, "sweng");
	        
	    }
	
    public void testSubmitAQuestion(){
    	ServerCommunicator.getInstance().submitQuizQuestion(quizQuestion, new EditQuestionActivity());
    	getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
    	getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
    }
    
   /* public void testSubmitAQuestionFails(){
    	try{
    		ServerCommunicator.getInstance().submitQuizQuestion(quizQuestion, new EditQuestionActivity());
    		MockHttpClient httpClient = new MockHttpClient();
    		SwengHttpClientFactory.setInstance(httpClient);
    		httpClient.pushCannedResponse("POST (?:https?://[^/]+|[^/]+)?/+quizquestions/\\b",
                    HttpStatus.SC_EXPECTATION_FAILED,
                    null,
                    "application/json");
    		
    		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
        	getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
    		Assert.fail(" Exception must be catch");
    	}catch(Exception e){
    		assertTrue("A error message must be display", solo.searchText("Could not upload the question to the server"));
    		
    	}
    	
    }*/
    
    protected void getActivityAndWaitFor(final TestCoordinator.TTChecks expected) {
        TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
            @Override
            public void initiate() {
                getActivity();
            }

            @Override
            public void verify(TestCoordinator.TTChecks notification) {
                assertEquals(String.format("Expected notification %s, but received %s", expected, notification),
                        expected, notification);
            }

            @Override
            public String toString() {
                return String.format("getActivityAndWaitFor(%s)", expected);
            }
        });
    }

}
