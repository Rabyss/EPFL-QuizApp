package epfl.sweng.test;

import java.util.HashSet;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import epfl.sweng.context.AppContext;
import epfl.sweng.quizquestions.QuizQuestion;

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
		AppContext.getContext().resetState();
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
	
	public void testRemainings() {
		LinkedList<String> answers = new LinkedList<String>();
		answers.add("hihi");
		answers.add("huhu");
		HashSet<String> tags = new HashSet<String>();
		tags.add("te");
		tags.add("st");
		QuizQuestion question1 = new QuizQuestion("What ?", answers, 0, tags, 0, "test");
		QuizQuestion question2 = new QuizQuestion("What ?", answers, 0, tags, 0, "test");
		assertTrue("Equals() does not work", question1.equals(question2));
		assertTrue("GetId() does not owrk", question1.getId() == 0);
		assertTrue("GetOwner() does not work", question1.getOwner().equals("test"));
		assertTrue("GetSolution() does not work", question1.getSolution() == 0);
		assertTrue("HashCode() does not work", question1.hashCode() == question2.hashCode());
		assertTrue("ToString does not work", question1.toString().equals(question2.toString()));
	}
}
