package epfl.sweng.test;

import java.util.NoSuchElementException;

import android.test.AndroidTestCase;
import android.util.Log;
import epfl.sweng.searchquestions.parser.tree.TreeAnd;
import epfl.sweng.searchquestions.parser.tree.TreeLeaf;
import epfl.sweng.searchquestions.parser.tree.TreeOr;

public class ParserTreeTest extends AndroidTestCase {
	
	private static final String TAG = "ParserTreeTest";
	
	public void testToString() {
		TreeAnd and1= new TreeAnd();
		TreeOr or1 = new TreeOr();
		TreeLeaf tag = new TreeLeaf("tag");
		and1.addChild(or1);
		or1.addChild(tag);
		assertTrue("addChild does not work", and1.getChildCount() == 1);
		TreeAnd and2 = new TreeAnd();
		and2.addChild(tag);
		and1.changeChild(or1, and2);
		
		try {
			and1.changeChild(or1, and2);
			assertTrue(false);
		} catch (NoSuchElementException e) {
			Log.d(TAG, e.getMessage(),e);
		}
		
		
	}
}
