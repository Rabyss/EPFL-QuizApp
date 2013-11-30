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

        assertEquals("(tagsSel.tag = 'd' AND (tagsSel.tag = 'a' OR tagsSel.tag = 'b'))",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d (a + b)").getAST())
        );

        assertEquals("((tagsSel.tag = 'd' AND tagsSel.tag = 'a') OR tagsSel.tag = 'b')",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d a + b").getAST())
        );

        assertEquals("((tagsSel.tag = 'd' AND (tagsSel.tag = 'a' AND (tagsSel.tag = 'r' AND tagsSel.tag = 'q'))) OR tagsSel.tag = 'b')",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d a r q + b").getAST())
        );

        assertEquals("((tagsSel.tag = 'd' AND tagsSel.tag = 'a') OR (tagsSel.tag = 'g' AND tagsSel.tag = 'q'))",
                new SQLQueryCompiler().toSQL(QueryParser.parse("d * a + g * q").getAST())
        );
    }
}
