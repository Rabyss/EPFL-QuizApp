package epfl.sweng.test;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{
	private Solo solo;
	
	public MainActivityTest(Class<MainActivity> activityClass) {
		super(activityClass);
		MainActivity.setIsLogged(true);
	}
	
	@Override
	protected void setUp() {
		solo = new Solo(getInstrumentation());
	}
	
	/**
	 * Test the main activity if an user is logged
	 */
	public void loggedUserTest(){	
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);	
		assertTrue("Show question button is shown", solo.searchButton("Show a random question.", 1));
		assertTrue("Submit question button is shown", solo.searchText("Submit a quiz question."));
		assertTrue("Log out button is shown", solo.searchText("Log out"));
		assertTrue("show question button is enabled", solo.getButton("Show a random question.").isEnabled());
		assertTrue("submit question button is enabled", solo.getButton("Submit a quiz question.").isEnabled());
		assertTrue("Log in button is enabled", solo.getButton("Log out").isEnabled());
		
	}
	/**
	 * Test the main activity if an user push the log out button
	 */
	public void logOutTest(){
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		solo.clickOnButton("Log out");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		getActivityAndWaitFor(TTChecks.LOGGED_OUT);
		assertTrue("Show question button is shown", solo.searchButton("Show a random question.", 1));
		assertTrue("Submit question button is shown", solo.searchText("Submit a quiz question."));
		assertTrue("Log in button is shown", solo.searchText("Log in using Tequila"));
		assertFalse("show question button is disabled", solo.getButton("Show a random question.").isEnabled());
		assertFalse("submit question button is disabled", solo.getButton("Submit a quiz question.").isEnabled());
		assertTrue("Log in button is enabled", solo.getButton("Log in using Tequila").isEnabled());		
	}
	public void SubmitQuestionButtonTest(){
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		solo.clickOnButton("Submit a quiz question.");
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
	}
	public void ShowQuestionButton(){
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		solo.clickOnButton("Show a random question.");
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
	}
	public void AuthenticateButton(){
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		solo.clickOnButton("Log out");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		getActivityAndWaitFor(TTChecks.LOGGED_OUT);
		solo.clickOnButton("Log in using Tequila");
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
