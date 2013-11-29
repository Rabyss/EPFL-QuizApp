package epfl.sweng.test;

import epfl.sweng.context.AppContext;
import epfl.sweng.searchquestions.SearchQuery;
import android.test.AndroidTestCase;
import epfl.sweng.searchquestions.SearchQuery.InvalidSearchQueryException;
import epfl.sweng.searchquestions.parser.QueryParser;

public class SearchQueryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        AppContext.getContext().resetState();
    }


    public void testExprNesting() {
        String validExpr = "((a b))";
        String invalidExpr = ")(";

        assertValidity(validExpr);
        assertInvalidity(invalidExpr);
    }

    public void testQueryCharClass() {
        String firstInvalidQuery = "     ";
        String secInvalidQuery = "(hey) (')";
        String thirdInvalidQuery = "0à";
        String fourthInvalidQuery = "\t";
        String validQuery = "       a     ";


        assertInvalidity(firstInvalidQuery);
        assertInvalidity(secInvalidQuery);
        assertInvalidity(thirdInvalidQuery);
        assertInvalidity(fourthInvalidQuery);
        assertValidity(validQuery);
    }

    private String generateErrorMessage(String invalidQuery) {
        return invalidQuery + " is an invalid query.";
    }

    private void assertValidity(String queryStr) {
        try {
            SearchQuery q = new SearchQuery(queryStr, QueryParser.parse(queryStr));
        } catch (InvalidSearchQueryException e) {
            fail(queryStr + " is valid.");
        }
    }

    private void assertInvalidity(String queryStr) {
        try {
            SearchQuery q = new SearchQuery(queryStr, QueryParser.parse(queryStr));
            fail(queryStr + " is invalid.");
        } catch (InvalidSearchQueryException e) {

        }
    }
}
