package epfl.sweng.test;

import epfl.sweng.authentication.UserStorage;
import epfl.sweng.context.AppContext;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class UserStorageTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private Context mContext;
	
	public UserStorageTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		mContext = getInstrumentation().getTargetContext();
		AppContext.getContext().resetState();
	}
	
	public void testStoreAndGetSession() {
		UserStorage.getInstance(mContext).storeSessionID("poney");
		assertEquals(UserStorage.getInstance(mContext).getSessionID(), "poney");
		UserStorage.getInstance(mContext).removeSessionID();
	}
	
	public void testRemoveSession() {
		UserStorage.getInstance(mContext).storeSessionID("kittens");
		UserStorage.getInstance(mContext).removeSessionID();
		assertEquals(UserStorage.getInstance(mContext).getSessionID(), null);
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
