package epfl.sweng.searchquestions;

import static epfl.sweng.util.StringHelper.containsNonWhitespaceCharacters;

public class SearchQuery {
    private final String mQuery;

    private static final String QUERY_CHAR_CLASS_REGEX = "^[a-zA-Z0-9\\(\\)\\*\\+ ]+$";
    private static final int MAX_QUERY_LENGTH = 500;

    public SearchQuery(final String query) throws UnvalidSearchQueryException {
        
        int errorsCount = auditQueryStr(query);
        if (errorsCount > 0) {
            throw new UnvalidSearchQueryException(errorsCount);
        }
        
        mQuery = query;
    }

    public String getQuery() {
        return mQuery;
    }

    public static int auditQueryStr(String query) {
        int errors = 0;

        // we make sure the query respects its char class
        if (!query.matches(QUERY_CHAR_CLASS_REGEX)) {
            System.err.println(query+": qccr");
            errors++;
        }

        if (query.length() >= MAX_QUERY_LENGTH) {
            errors++;
        }

        // we make sure the query contains at least one alphanumeric char
        if (!containsNonWhitespaceCharacters(query)) {
            System.err.println(query+": aoacr");
            errors++;
        }

        if (!isExpressionNestingCorrect(query)) {
            errors++;
        }

        return errors;
    }

    private static boolean isExpressionNestingCorrect(String query) {
        int openParenthesisCount = 0;
        int closedParenthesisCount = 0;
        for (int i = 0; i < query.length(); i++) {
            char current = query.charAt(i);
            if (current == '(') {
                openParenthesisCount++;
            } else if (current == ')') {
                closedParenthesisCount++;
            }
        }
        return openParenthesisCount == closedParenthesisCount;
    }

    public class UnvalidSearchQueryException extends Exception {

        private static final long serialVersionUID = 6028719701104283474L;

        public UnvalidSearchQueryException(int errorsCount) {
            super("invalid search query (" + errorsCount + " errors): '"
                    + mQuery + "'");
        }
    }

}
