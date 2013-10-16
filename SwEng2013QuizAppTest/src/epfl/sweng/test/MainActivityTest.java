package epfl.sweng.test;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private Solo solo;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() {
		System.out.println("Attend Main Activity !!!!!!!!!!!");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		System.out.println("Salut Main Activity !!!!!!!!!!!");
		solo = new Solo(getInstrumentation());
	}
	
	/**
	 * Test the main activity if a user is not logged
	 * The submit button and the show question button must be disabled
	 */
	public void testUnLoggedUser(){
		assertTrue("Show question button is shown", solo.searchButton("Show a random question.", 1));
		assertTrue("Submit question button is shown", solo.searchText("Submit a quiz question."));
		assertTrue("Log in button is shown", solo.searchText("Log in using Tekila"));
		assertFalse("show question button is disabled", solo.getButton("Show a random question.").isEnabled());
		assertFalse("submit question button is disabled", solo.getButton("Submit a quiz question.").isEnabled());
		assertTrue("Log in button is enabled", solo.getButton("Log in using Tekila").isEnabled());
	}
	/**
	 * Test the main activity if an user is logged
	 */
	public void loggedUserTest(){
		
	}
	/**
	 * Test the main activity if an user push the log out button
	 */
	public void logOutTest(){
		
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
