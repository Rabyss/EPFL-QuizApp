package epfl.sweng.testing;

/**
 *	Interface of a TestingTransaction
 */
public interface TestingTransaction {
	
	/** Contains the code to initiate the transaction. For example, this might
	* press a button on the user interface.
	*/
	void initiate();
	
	/** Contains the code to verify that a transaction succeeded. For example, this
	* might check that the user interface has been updated correctly after a button
	* has been pressed.
	* @param completedCheck: The notification that was passed to check() */
	void verify(TestingTransactions.TTChecks completedCheck);
}
