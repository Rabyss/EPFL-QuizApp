package epfl.sweng.searchquestions.parser;

import epfl.sweng.searchquestions.parser.entities.Tag;
import epfl.sweng.searchquestions.parser.entities.Token;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static epfl.sweng.searchquestions.parser.entities.TokenKind.AND;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.CLOSE;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.ERROR;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.OPEN;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.OR;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.SPACE;

/**
 * Transforms a query string into its corresponding list of token.
 */
public final class QueryTokenizer {
    private final String mQueryString; //the string to tokenize
    private int mPointer; //keeps track or where the tokenizer is in the string

    private final static String ALPHANUMERIC_REGEX = "^([A-Za-z0-9]+)";

    public QueryTokenizer(final String queryString) {
        mQueryString = queryString;
        mPointer = 0;
    }

    /**
     * Checks if there still token to tokenize
     *
     * @return true if there are such a token
     */
    public boolean hasNextToken() {
        return mPointer < mQueryString.length();
    }

    /**
     * Returns the next token. In case of error, add the error token.
     *
     * @return the next token
     */
    public Token nextToken() {

        if (!hasNextToken()) {
            throw new NoSuchElementException();
        } else {

            Token nextToken;
            char currentChar = mQueryString.charAt(mPointer);

            switch (currentChar) {
                case '(':
                    mPointer++;
                    nextToken = new Token(OPEN);
                    break;
                case ')':
                    mPointer++;
                    nextToken = new Token(CLOSE);
                    break;
                case '*':
                    mPointer++;
                    nextToken = new Token(AND);
                    break;
                case '+':
                    mPointer++;
                    nextToken = new Token(OR);
                    break;
                case ' ':
                    mPointer++;
                    nextToken = new Token(SPACE);
                    break;
                default:

                    Pattern alphanumeric = Pattern.compile(ALPHANUMERIC_REGEX);
                    Matcher matcher = alphanumeric.matcher(mQueryString.substring(mPointer));

                    if (matcher.find()) {
                        String tagStr = matcher.group(1);
                        nextToken = new Tag(tagStr);
                        mPointer += tagStr.length();
                    } else {
                        mPointer++;
                        nextToken = new Token(ERROR);
                    }
            }

            return nextToken;
        }
    }
}

