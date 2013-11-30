package epfl.sweng.test;

import android.test.AndroidTestCase;
import epfl.sweng.searchquestions.parser.QueryParser;
import epfl.sweng.searchquestions.parser.SQLQueryCompiler;

public class QueryParserTest extends AndroidTestCase {

    public void test() {
        assertTrue(QueryParser.parse("(a b) a").isDone());
        assertFalse(QueryParser.parse(")a b( a").isDone());
        assertTrue(QueryParser.parse("a (a + b)").isDone());
        assertTrue(QueryParser.parse("a b + c").isDone());
        assertTrue(QueryParser.parse("a + b c").isDone());
        assertFalse(QueryParser.parse("(a b+) a").isDone());

        assertEquals("(tag = 'd' AND (tag = 'a' OR tag = 'b'))",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d (a + b)").getAST())
        );

        assertEquals("((tag = 'd' AND tag = 'a') OR tag = 'b')",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d a + b").getAST())
        );

        assertEquals("((tag = 'd' AND (tag = 'a' AND (tag = 'r' AND tag = 'q'))) OR tag = 'b')",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d a r q + b").getAST())
        );

        assertEquals("((tag = 'd' AND tag = 'a') OR (tag = 'g' AND tag = 'q'))",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d * a + g * q").getAST())
        );
    }
}
