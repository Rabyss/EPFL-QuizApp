package epfl.sweng.test;

import android.test.AndroidTestCase;
import epfl.sweng.searchquestions.parser.QueryTokenizer;
import epfl.sweng.searchquestions.parser.entities.Token;
import epfl.sweng.searchquestions.parser.entities.TokenKind;

import java.util.List;

import static epfl.sweng.searchquestions.parser.entities.TokenKind.AND;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.CLOSE;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.ERROR;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.OPEN;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.OR;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.SPACE;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.TAG;
import static java.util.Arrays.asList;

public class QueryTokenizerTest extends AndroidTestCase {
    public void testConjunction() {
        test("A * B", asList(TAG, SPACE, AND, SPACE, TAG));
    }

    public void testDisjunction() {
        test("C + D", asList(TAG, SPACE, OR, SPACE, TAG));
    }

    public void testParentheses() {
        test("(C) (B)",
                asList(OPEN, TAG, CLOSE, SPACE, OPEN, TAG, CLOSE));
    }

    public void testSimpleInput() {
        test("(", asList(OPEN));
    }

    public void testComb() {
        test("a (a + b)", asList(TAG, SPACE, OPEN, TAG, SPACE, OR, SPACE, TAG, CLOSE));
    }

    public void testLongTag() {
        test("(Asflsadlkfja4ljdhfJjalsdhfk0jlsdhfasdkfhkgfk)",
                asList(OPEN, TAG, CLOSE));
    }

    public void testForDebug() {
        test("(Banana)(Kiwi)", asList(OPEN, TAG, CLOSE, OPEN, TAG, CLOSE));
    }

    public void testInvalid() {
        QueryTokenizer tokenizer = new QueryTokenizer("asdlkjlkj'éààààààasdjkhasd");

        boolean hasErrorAsExpected = false;
        while (tokenizer.hasNextToken()) {
            if (tokenizer.nextToken().isKind(ERROR)) {
                hasErrorAsExpected = true;
            }
        }

        if (!hasErrorAsExpected) {
            fail("should have errors");
        }

    }

    private void test(String queryStr, List<TokenKind> tokenKinds) {
        QueryTokenizer tokenizer = new QueryTokenizer(queryStr);

        for (TokenKind tokenKind : tokenKinds) {
            Token nextToken = tokenizer.nextToken();
            assertTrue("bad kind. expected: "+nextToken.getKind()+", found: "+tokenKind, nextToken.isKind(tokenKind));
        }

        assertFalse("the tokenizer should have no more token at that point", tokenizer.hasNextToken());

    }
}
