package epfl.sweng.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.quizquestions.QuizQuestion;
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
			new QuizQuestion(jsonObject.toString());
			fail("An empty JSON object cannot be used to build a QuizQuestion.");
		} catch (JSONException e) {

		}

		try {
			jsonObject.put("question", "?");
			jsonObject.put("answers", answersExamples);
			jsonObject.put("solutionIndex", 0);
			jsonObject.put("tags", tagsExamples);
			jsonObject.put("owner", "no");
			new QuizQuestion(jsonObject.toString());
			fail("A question without an ID cannot be built.");
		} catch (JSONException e) {
		}

		try {
			jsonObject.put("id", 2);
			System.out.println(jsonObject);
			new QuizQuestion(jsonObject.toString());
		} catch (JSONException e) {
			fail("A valid question JSON description generates an exception.");
		}

	}
}
