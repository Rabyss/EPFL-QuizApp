package epfl.sweng.test;

import android.test.AndroidTestCase;
import epfl.sweng.searchquestions.parser.QueryParser;

public class QueryParserTest extends AndroidTestCase {

	public void test() {
		assertTrue(QueryParser.parse("(a b) a").isDone());
		assertFalse(QueryParser.parse(")a b( a").isDone());
		assertTrue(QueryParser.parse("a (a + b)").isDone());
		assertTrue(QueryParser.parse("a b + c").isDone());
		assertTrue(QueryParser.parse("a + b c").isDone());
		assertFalse(QueryParser.parse("(a b+) a").isDone());
	}
}
