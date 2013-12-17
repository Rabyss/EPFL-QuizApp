package epfl.sweng.searchquestions.parser.entities;

/**
 * Represents a token of the query language.
 */
public class Token {
    private final TokenKind mKind;

    public Token(TokenKind kind) {
        mKind = kind;
    }

    public TokenKind getKind() {
        return mKind;
    }

    public boolean isKind(TokenKind kind) {
        return mKind == kind;
    }
}