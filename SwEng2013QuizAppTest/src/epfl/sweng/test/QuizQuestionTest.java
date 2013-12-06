package epfl.sweng.test;

import java.util.HashSet;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;
import epfl.sweng.context.AppContext;
import epfl.sweng.quizquestions.QuizQuestion;

public class QuizQuestionTest extends AndroidTestCase {
<<<<<<< HEAD
    private JSONArray answersExamples;
    private JSONArray tagsExamples;
    
    private static final String TAG = "QuizQuestionTest";
    private static final String WHAT = "What ?";

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
            Log.d(TAG, e.getMessage(), e);
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
            Log.d(TAG, e.getMessage(), e);
        }

        try {
            jsonObject.put("id", 2);
            System.out.println(jsonObject);
            new QuizQuestion(jsonObject.toString());
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
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
        QuizQuestion question1 = new QuizQuestion(WHAT, answers, 0, tags,
                0, "test");
        QuizQuestion question2 = new QuizQuestion(WHAT, answers, 0, tags,
                0, "test");
        assertTrue("Equals() does not work", question1.equals(question2));
        assertTrue("GetId() does not owrk", question1.getId() == 0);
        assertTrue("GetOwner() does not work",
                question1.getOwner().equals("test"));
        assertTrue("GetSolution() does not work", question1.getSolution() == 0);
        assertTrue("HashCode() does not work",
                question1.hashCode() == question2.hashCode());
        assertTrue("ToString does not work",
                question1.toString().equals(question2.toString()));
    }

    public void testEqualsFailure() {
        HashSet<String> tags = new HashSet<String>();
        tags.add("te");
        tags.add("st");
        LinkedList<String> answers = new LinkedList<String>();
        answers.add("hihi");
        answers.add("huhu");
        String owner = "moi";
        String question = "what?";
        QuizQuestion question1 = new QuizQuestion(question, answers, 0, tags,
                0, owner);
        assertTrue("Equals does not work", question1.equals(question1));
        assertFalse("Equals does not work", question1 == null);

        QuizQuestion question2 = new QuizQuestion(null, null, 0, null, 0, null);
        assertFalse("Equals does not work", question2.equals(question1));
        QuizQuestion question3 = new QuizQuestion(null, answers, 0, tags, 0,
                owner);
        assertFalse("Equals does not work", question2.equals(question3));
        QuizQuestion question4 = new QuizQuestion(null, null, 1, tags, 0, owner);
        assertFalse("Equals does not work", question2.equals(question4));
        QuizQuestion question5 = new QuizQuestion(null, null, 0, tags, 0, owner);
        assertFalse("Equals does not work", question2.equals(question5));
        QuizQuestion question6 = new QuizQuestion(null, null, 0, null, 1, owner);
        assertFalse("Equals does not work", question2.equals(question6));
        QuizQuestion question7 = new QuizQuestion(null, null, 0, null, 0, owner);
        assertFalse("Equals does not work", question2.equals(question7));
        HashSet<String> tagsBis = new HashSet<String>();
        tags.add("faux");
        tags.add("tags");
        LinkedList<String> answersBis = new LinkedList<String>();
        answers.add("bad number of answers");
        String questionBis = "wtf?";
        String ownerBis = "toi";
        QuizQuestion question8 = new QuizQuestion(questionBis, answers, 0,
                tags, 0, owner);
        assertFalse("Equals does not work", question1.equals(question8));
        QuizQuestion question9 = new QuizQuestion(question, answersBis, 0,
                tags, 0, owner);
        assertFalse("Equals does not work", question1.equals(question9));
        QuizQuestion question10 = new QuizQuestion(question, answers, 0,
                tagsBis, 0, owner);
        assertFalse("Equals does not work", question1.equals(question10));
        QuizQuestion question11 = new QuizQuestion(question, answers, 0, tags,
                0, ownerBis);
        assertFalse("Equals does not work", question1.equals(question11));
    }

    public void testAddAnswerAndTags() {
        HashSet<String> tags = new HashSet<String>();
        tags.add("te");
        tags.add("st");
        LinkedList<String> answers = new LinkedList<String>();
        answers.add("hihi");
        answers.add("huhu");
        String owner = "moi";
        String question = "what?";
        QuizQuestion question1 = new QuizQuestion(question, answers, 0, tags,
                0, owner);
        question1.addAnswer("salut");
        question1.addTag("ici");
        assertTrue("AddAnswer does not work",
                question1.getAnswers().contains("salut"));
        assertTrue("addTag() does not work", question1.getTags()
                .contains("ici"));

    }
=======

	private JSONArray answersExamples;
	private JSONArray tagsExamples;
	
	private static final String TAG = "QuizQuestionTest";

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
			Log.d(TAG, e.getMessage());
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
			Log.d(TAG, e.getMessage());
		}

		try {
			jsonObject.put("id", 2);
			System.out.println(jsonObject);
			new QuizQuestion(jsonObject.toString());
		} catch (JSONException e) {
			Log.d(TAG, e.getMessage());
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
		QuizQuestion question1 = new QuizQuestion("What ?", answers, 0, tags,
				0, "test");
		QuizQuestion question2 = new QuizQuestion("What ?", answers, 0, tags,
				0, "test");
		assertTrue("Equals() does not work", question1.equals(question2));
		assertTrue("GetId() does not owrk", 0 == question1.getId());
		assertTrue("GetOwner() does not work",
		        "test".equals(question1.getOwner()));
		assertTrue("GetSolution() does not work", 0 == question1.getSolution());
		assertTrue("HashCode() does not work",
				question1.hashCode() == question2.hashCode());
		assertTrue("ToString does not work",
				question1.toString().equals(question2.toString()));
	}

	public void testEqualsFailure() {
		HashSet<String> tags = new HashSet<String>();
		tags.add("te");
		tags.add("st");
		LinkedList<String> answers = new LinkedList<String>();
		answers.add("hihi");
		answers.add("huhu");
		String owner = "moi";
		String question = "what?";
		QuizQuestion question1 = new QuizQuestion(question, answers, 0, tags,
				0, owner);
		assertTrue("Equals does not work", question1.equals(question1));
		assertFalse("Equals does not work", question1 == null);

		QuizQuestion question2 = new QuizQuestion(null, null, 0, null, 0, null);
		assertFalse("Equals does not work", question2.equals(question1));
		QuizQuestion question3 = new QuizQuestion(null, answers, 0, tags, 0,
				owner);
		assertFalse("Equals does not work", question2.equals(question3));
		QuizQuestion question4 = new QuizQuestion(null, null, 1, tags, 0, owner);
		assertFalse("Equals does not work", question2.equals(question4));
		QuizQuestion question5 = new QuizQuestion(null, null, 0, tags, 0, owner);
		assertFalse("Equals does not work", question2.equals(question5));
		QuizQuestion question6 = new QuizQuestion(null, null, 0, null, 1, owner);
		assertFalse("Equals does not work", question2.equals(question6));
		QuizQuestion question7 = new QuizQuestion(null, null, 0, null, 0, owner);
		assertFalse("Equals does not work", question2.equals(question7));
		HashSet<String> tagsBis = new HashSet<String>();
		tags.add("faux");
		tags.add("tags");
		LinkedList<String> answersBis = new LinkedList<String>();
		answers.add("bad number of answers");
		String questionBis = "wtf?";
		String ownerBis = "toi";
		QuizQuestion question8 = new QuizQuestion(questionBis, answers, 0,
				tags, 0, owner);
		assertFalse("Equals does not work", question1.equals(question8));
		QuizQuestion question9 = new QuizQuestion(question, answersBis, 0,
				tags, 0, owner);
		assertFalse("Equals does not work", question1.equals(question9));
		QuizQuestion question10 = new QuizQuestion(question, answers, 0,
				tagsBis, 0, owner);
		assertFalse("Equals does not work", question1.equals(question10));
		QuizQuestion question11 = new QuizQuestion(question, answers, 0, tags,
				0, ownerBis);
		assertFalse("Equals does not work", question1.equals(question11));
	}

	public void testAddAnswerAndTags() {
		HashSet<String> tags = new HashSet<String>();
		tags.add("te");
		tags.add("st");
		LinkedList<String> answers = new LinkedList<String>();
		answers.add("hihi");
		answers.add("huhu");
		String owner = "moi";
		String question = "what?";
		QuizQuestion question1 = new QuizQuestion(question, answers, 0, tags,
				0, owner);
		question1.addAnswer("salut");
		question1.addTag("ici");
		assertTrue("AddAnswer does not work",
				question1.getAnswers().contains("salut"));
		assertTrue("addTag() does not work", question1.getTags()
				.contains("ici"));

	}
>>>>>>> Merge
}
