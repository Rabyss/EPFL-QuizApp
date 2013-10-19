package epfl.sweng.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.MalformedQuestionException;
import epfl.sweng.QuizQuestion;
import android.test.AndroidTestCase;

public class QuizQuestionTest extends AndroidTestCase {

	private JSONArray answersExamples;
	private JSONArray tagsExamples;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		answersExamples = new JSONArray();
		answersExamples.put("A");
		answersExamples.put("B");

		tagsExamples = new JSONArray();
		tagsExamples.put("A");
		tagsExamples.put("B");
	}

	public void testJSONConstruction() {

		JSONObject jsonObject = new JSONObject();
		try {
			new QuizQuestion(jsonObject);
			fail("An empty JSON object cannot be used to build a QuizQuestion.");
		} catch (MalformedQuestionException e) {

		}

		try {
			jsonObject.put("question", "?");
			jsonObject.put("answers", answersExamples);
			jsonObject.put("solutionIndex", 0);
			jsonObject.put("tags", tagsExamples);
			jsonObject.put("owner", "no");
			new QuizQuestion(jsonObject);
			fail("A question without an ID cannot be built.");
		} catch (MalformedQuestionException e) {

		} catch (JSONException e) {
			fail("The test seems to be buggy.");
		}

		try {
			jsonObject.put("id", 2);
			System.out.println(jsonObject);
			new QuizQuestion(jsonObject);
		} catch (MalformedQuestionException e) {
			fail("A valid question JSON description generates an exception.");
		} catch (JSONException e) {
			fail("A valid question JSON description generates an exception.");
		}

	}
}
