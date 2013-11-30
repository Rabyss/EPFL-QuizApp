package epfl.sweng.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import epfl.sweng.cache.SQLiteCache;
import epfl.sweng.quizquestions.QuizQuestion;
import android.test.AndroidTestCase;

public class SQLiteCacheTest extends AndroidTestCase {
	
	private SQLiteCache cache;
	private QuizQuestion qu1;
	private QuizQuestion qu2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// what is mContext?
		cache = new SQLiteCache(mContext);
		
		String[] answersArray = {"Answer a", "Answer b", "Answer c"};
		List<String> answers = Arrays.asList(answersArray);
		Set<String> tags = new HashSet<String>();
		tags.add("Tag_a"); tags.add("Tag_b");
		qu1 = 
				new QuizQuestion("Question 1", answers, 1, tags, 1212, "owner");
		
		String[] answersArray2 = {"Answer 1", "Answer 2", "Answer 3"};
		List<String> answers2 = Arrays.asList(answersArray2);
		Set<String> tags2 = new HashSet<String>();
		tags2.add("Tag_a"); tags2.add("Tag_c");
		qu2 = 
				new QuizQuestion("Question 2", answers2, 1, tags2, 2424, "otherOwner");
	}
	
	public void testSearchOneTagQuestion() {
		cache.cacheQuestion(qu1);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
