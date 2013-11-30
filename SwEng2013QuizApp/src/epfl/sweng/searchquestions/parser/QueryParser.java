package epfl.sweng.searchquestions.parser;

import epfl.sweng.searchquestions.parser.entities.Tag;
import epfl.sweng.searchquestions.parser.entities.Token;
import epfl.sweng.searchquestions.parser.entities.TokenKind;
import epfl.sweng.searchquestions.parser.tree.SingleChildTreeNode;
import epfl.sweng.searchquestions.parser.tree.TreeAnd;
import epfl.sweng.searchquestions.parser.tree.TreeLeaf;
import epfl.sweng.searchquestions.parser.tree.TreeNode;
import epfl.sweng.searchquestions.parser.tree.TreeOr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static epfl.sweng.searchquestions.parser.entities.TokenKind.AND;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.CLOSE;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.EOF;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.ERROR;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.OPEN;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.OR;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.SPACE;
import static epfl.sweng.searchquestions.parser.entities.TokenKind.TAG;


public final class QueryParser {

    //global variables used during parsing
    private static Iterator<Token> mTokens = null;
    private static Token mCurrentToken = null;

    private QueryParser() {
    }

    public static QueryParserResult parse(String queryStr) {

        List<Token> allTokens = preprocessTokens(new QueryTokenizer(queryStr));

        //The end-of-file token at the end of the list acts as a sentinel
        allTokens.add(new Token(EOF));

        //If a token is of type error, then there is an error
        //and the parser cannot parse.
        for (Token token : allTokens) {
            if (token.isKind(ERROR)) {
                return new QueryParserResult(queryStr, false, null);
            }
        }

        mTokens = allTokens.iterator();
        mCurrentToken = mTokens.next();

        TreeNode root = new SingleChildTreeNode();
        boolean done = parseStart(root);
        //FOR DEBUG: System.out.println(root);

        return new QueryParserResult(queryStr, done, root);
    }


    public static class QueryParserResult {
        private final boolean mDone;
        private final TreeNode mAST;
        private final String mQueryStr;

        public QueryParserResult(final String queryStr, final boolean done, final TreeNode ast) {
            assert !(done && ast == null) : "invalid initialization of the query parser result.";
            mQueryStr = queryStr;
            mDone = done;
            mAST = ast;
        }

        public boolean isDone() {
            return mDone;
        }

        public TreeNode getAST() {
            if (mDone) {
                return mAST;
            } else {
                throw new NoSuchElementException();
            }
        }

        public String getQueryString() {
            return mQueryStr;
        }
    }

    // ----   Recusive-descent methods  ----
    private static boolean eat(TokenKind kind) {
        if (mCurrentToken.isKind(kind)) {
            if (mTokens.hasNext()) {
                mCurrentToken = mTokens.next();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean parseStart(TreeNode parent) {
        boolean done = parseExpression(parent);
        return mCurrentToken.isKind(EOF) && done;
    }

    private static boolean parseExpression(TreeNode parent) {
        SingleChildTreeNode exprNode = new SingleChildTreeNode();
        boolean done = parseTerm(exprNode) && parseExpressionCont(exprNode, parent);
        parent.addChild(exprNode);
        return done;
    }

    private static boolean parseTerm(SingleChildTreeNode parent) {
        SingleChildTreeNode termNode = new SingleChildTreeNode();
        boolean done = parseFactor(termNode) && parseTermCont(termNode, parent);
        parent.addChild(termNode);
        return done;
    }

    private static boolean parseExpressionCont(TreeNode leftHandSide, TreeNode parent) {
        switch (mCurrentToken.getKind()) {
            case OR:
                SingleChildTreeNode rightHandSide = new SingleChildTreeNode();
                TreeNode orNode = new TreeOr();
                orNode.addChild(leftHandSide);
                boolean done = eat(OR) && parseTerm(rightHandSide) && parseExpressionCont(rightHandSide, orNode);
                orNode.addChild(rightHandSide);
                parent.addChild(orNode);

                return done;
            case CLOSE:
                return true;
            case EOF:
                return true;
            default:
                return false;
        }
    }

    private static boolean parseTermCont(TreeNode leftHandSide, TreeNode parent) {
        switch (mCurrentToken.getKind()) {
            case AND:
                TreeNode andNode = new TreeAnd();
                andNode.addChild(leftHandSide);
                SingleChildTreeNode rightHandSide = new SingleChildTreeNode();
                boolean done = eat(AND) && parseFactor(rightHandSide) && parseTermCont(rightHandSide, andNode);
                andNode.addChild(rightHandSide);
                parent.addChild(andNode);
                return done;
            case OR:
                return true;
            case CLOSE:
                return true;
            case EOF:
                return true;
            default:
                return false;
        }
    }

    private static boolean parseFactor(TreeNode parent) {
        switch (mCurrentToken.getKind()) {
            case OPEN:
                return eat(OPEN) && parseExpression(parent) && eat(CLOSE);
            case TAG:
                if (mCurrentToken.isKind(TAG)) {
                    TreeLeaf tagLeaf = new TreeLeaf(((Tag) mCurrentToken).getStringFormat());
                    parent.addChild(tagLeaf);
                }
                return eat(TAG);
            default:
                return false;
        }
    }


    //transforms the and-whitespace into AND and delete the others
    private static List<Token> preprocessTokens(QueryTokenizer tokenizer) {
        Token lastToken = null;
        Token currentToken = null;
        Token nextToken = null;
        List<Token> tokens = new LinkedList<Token>();

        while (tokenizer.hasNextToken() ||
                lastToken != null ||
                currentToken != null ||
                nextToken != null) {
            lastToken = currentToken;
            currentToken = nextToken;
            nextToken = (tokenizer.hasNextToken()) ? tokenizer.nextToken() : null;

            if (currentToken != null &&
                lastToken != null &&
                nextToken != null &&
                currentToken.isKind(SPACE)) {

                if ((lastToken.isKind(CLOSE) || lastToken.isKind(TAG)) &&
                        (nextToken.isKind(OPEN) || nextToken.isKind(TAG))) {
                    currentToken = new Token(AND);
                }

            }

            if (lastToken != null && !lastToken.isKind(SPACE)) {
                tokens.add(lastToken);
            }
        }

        return tokens;
    }
}
