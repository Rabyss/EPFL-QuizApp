package epfl.sweng.searchquestions.parser.entities;

/**
 * Reprensents a tag token.
 */
public class Tag extends Token {
    private String mStringFormat;

    public Tag(String stringFormat) {
        super(TokenKind.TAG);
        mStringFormat = stringFormat;
    }

    public String getStringFormat() {
        return mStringFormat;
    }

}
