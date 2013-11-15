package epfl.sweng.test;

import epfl.sweng.context.AppContext;
import epfl.sweng.searchquestions.SearchQuery;
import android.test.AndroidTestCase;

public class SearchQueryTest extends AndroidTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		AppContext.getContext().resetState();
	}
	
	
    public void testExprNesting() {
        String validExpr = "(())";
        String invalidExpr = "()(";
        
        assertEquals(validExpr + " is a valid expression nesting", SearchQuery.auditQueryStr(validExpr), 0);
        assertTrue(invalidExpr + " is an invalid expression nesting", SearchQuery.auditQueryStr(invalidExpr) > 0);
    }
    
    public void testQueryCharClass() {
        String firstInvalidQuery = "     ";
        String secInvalidQuery = "(hey) (')";
        String thirdInvalidQuery = "��";
        String fourthInvalidQuery = "\t";
        String validQuery = "       a     ";
        

        assertTrue(generateErrorMessage(firstInvalidQuery), SearchQuery.auditQueryStr(firstInvalidQuery) > 0);
        assertTrue(generateErrorMessage(secInvalidQuery), SearchQuery.auditQueryStr(secInvalidQuery) > 0);
        assertTrue(generateErrorMessage(thirdInvalidQuery), SearchQuery.auditQueryStr(thirdInvalidQuery) > 0);
        assertTrue(generateErrorMessage(fourthInvalidQuery), SearchQuery.auditQueryStr(fourthInvalidQuery) > 0);
        assertEquals(validQuery + " is a valid query.", 0, SearchQuery.auditQueryStr(validQuery));
    }
    
    private String generateErrorMessage(String invalidQuery) {
        return invalidQuery + " is an invalid query.";
    }
}
