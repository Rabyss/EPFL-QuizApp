package epfl.sweng.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.test.AndroidTestCase;
import epfl.sweng.cache.SQLiteCache;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.QueryParser;
import epfl.sweng.searchquestions.parser.QueryParser.QueryParserResult;

public class SQLiteCacheTest extends AndroidTestCase {

    private SQLiteCache cache;
    private QuizQuestion qu1;
    private QuizQuestion qu2;
    private QuizQuestion qu3;
    private QuizQuestion qu4;

    private static final int TAG_N_1212 = 1212;
    private static final int TAG_N_2424 = 2424;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // what is mContext?
        cache = new SQLiteCache(mContext);

        String[] answersArray = {"Answer a", "Answer b", "Answer c"};
        List<String> answers = Arrays.asList(answersArray);
        Set<String> tags = new HashSet<String>();
        tags.add("Taga");
        tags.add("Tagb");
        qu1 = new QuizQuestion("Question 1", answers, 1, tags, TAG_N_1212,
                "owner");

        String[] answersArray2 = {"Answer 1", "Answer 2", "Answer 3"};
        List<String> answers2 = Arrays.asList(answersArray2);
        Set<String> tags2 = new HashSet<String>();
        tags2.add("Taga");
        tags2.add("Tagc");
        qu2 = new QuizQuestion("Question 2", answers2, 1, tags2, TAG_N_2424,
                "otherOwner");

        qu3 = new QuizQuestion("{\"id\": \"1000002\",\"owner\": \"fruitninja\"," +
                "\"question\": \"How many calories are in a banana?\",\"answers\": [ \"Just enough\", \"Too many\" ]," +
                "\"solutionIndex\": 0,\"tags\": [ \"tagGeneric\", \"tag31892\", \"tag34524\"," +
                "\"tag48771\", \"tag64957\", \"tag43688\" ]}");
        qu4 = new QuizQuestion("{\"id\": \"1000003\",\"owner\": \"fruitninja\"," +
                "\"question\": \"How many calories are in a banana?\",\"answers\": [ \"Just enough\", \"Too many\" ]," +
                "\"solutionIndex\": 0,\"tags\": [ \"tagGeneric\", \"tag57061\", \"tag34283\", " +
                "\"tag49816\", \"tag55981\", \"tag8642\" ]}");
    }

    public void testSearchOneTagOneQuestion() {
        cache.cacheQuestion(qu1);
        QueryParserResult res = QueryParser.parse("Taga");
        assertTrue(res.isDone());
        Set<QuizQuestion> set = cache.getQuestionSetByTag(res.getAST());
        assertTrue(set.contains(qu1));
    }

    public void testSearchOneTagTwoQuestion() {
        cache.cacheQuestion(qu1);
        cache.cacheQuestion(qu2);
        QueryParserResult res = QueryParser.parse("Taga");
        assertTrue(res.isDone());
        Set<QuizQuestion> set = cache.getQuestionSetByTag(res.getAST());
        assertTrue(set.contains(qu1));
        assertTrue(set.contains(qu2));
    }

    public void testSearchTwoTagOr() {
        cache.cacheQuestion(qu1);
        cache.cacheQuestion(qu2);
        QueryParserResult res = QueryParser.parse("NotTag+Taga");
        assertTrue(res.isDone());
        Set<QuizQuestion> set = cache.getQuestionSetByTag(res.getAST());
        assertTrue(set.contains(qu1));
        assertTrue(set.contains(qu2));
    }

    public void testSearchTwoTagAnd() {
        cache.cacheQuestion(qu1);
        cache.cacheQuestion(qu2);
        QueryParserResult res = QueryParser.parse("Taga Tagb");
        assertTrue(res.isDone());
        Set<QuizQuestion> set = cache.getQuestionSetByTag(res.getAST());
        assertTrue(set.contains(qu1));
        assertFalse(set.contains(qu2));
    }

    public void testSearchTwoTagAndOr() {
        cache.cacheQuestion(qu1);
        cache.cacheQuestion(qu2);
        QueryParserResult res = QueryParser.parse("Taga (Tagb + Tagc)");
        assertTrue(res.isDone());
        Set<QuizQuestion> set = cache.getQuestionSetByTag(res.getAST());
        assertTrue(set.contains(qu1));
        assertTrue(set.contains(qu2));
    }

    public void testSearchBigExpression() {
        cache.cacheQuestion(qu1);
        cache.cacheQuestion(qu3);
        cache.cacheQuestion(qu4);
        QueryParserResult res = QueryParser
                .parse("Tagb + tag31892 (tag34524 tag64957 + nulltag) tag48771 + tag57061");
        assertTrue(res.isDone());
        Set<QuizQuestion> set = cache.getQuestionSetByTag(res.getAST());
        assertTrue(set.contains(qu1));
        assertFalse(set.contains(qu2));
        assertTrue(set.contains(qu3));
        assertTrue(set.contains(qu4));
    }

    public void testGetRandomQuestion() {
        cache.cacheQuestion(qu1);
        assertTrue(cache.getRandomQuestion().auditErrors() == 0);
    }
}
