package epfl.sweng.quizquestions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Represents a question of the quiz.
 */
public class QuizQuestion {

	// Uses Integer instead of int to allow them to be nullable.
	private final int mId;
	private final String mQuestion;
	private final AnswersList mAnswers;
	private final int mSolutionIndex;
	private final TagsSet mTags;
	private final String mOwner;

    private static final int MAX_QUESTION_LENGTH = 500;
    private static final int MIN_ANSWERS_NUM = 2;
    private static final int MAX_ANSWERS_NUM = 10;
    private static final int MIN_TAGS_NUM = 1;
    private static final int MAX_TAGS_NUM = 20;

	public QuizQuestion(final String question, final List<String> answers,
			final int solutionIndex, final Set<String> tags, final int id,
			final String owner) {
		mId = id;
		mQuestion = question;

        mAnswers = new AnswersList();
        mAnswers.addAll(answers);

        mSolutionIndex = solutionIndex;

        mTags = new TagsSet();
        mTags.addAll(tags);

		mOwner = owner;
	}

	/**
	 * Constructs the class from a JSONObject
	 * 
	 * @param jsonText
	 * @throws MalformedQuestionException
	 * @throws MalformedQuestionException
	 *             if the JSONObject is malformed
	 */
	public QuizQuestion(final String jsonText) throws JSONException {
		JSONObject jsonModel = new JSONObject(jsonText);
		mId = jsonModel.getInt("id");
		mQuestion = jsonModel.getString("question");

        mAnswers = new AnswersList();
        mAnswers.addAll(extractCollectionFromJSONArray(jsonModel.getJSONArray("answers")));

		mSolutionIndex = jsonModel.getInt("solutionIndex");

		mTags = new TagsSet();
        mTags.addAll(extractCollectionFromJSONArray(jsonModel.getJSONArray("tags")));

		mOwner = jsonModel.getString("owner");
	}

	public int getId() {
		return mId;
	}

	public String getQuestion() {
		return mQuestion;
	}

	public List<String> getAnswers() {
		return Collections.unmodifiableList(mAnswers);
	}

	/**
	 * Checks whether the given index is the index of the solution.
	 */
	public boolean isSolution(int index) {
		return mSolutionIndex == index;
	}

	public Set<String> getTags() {
		return Collections.unmodifiableSet(mTags);
	}

	public String getOwner() {
		return mOwner;
	}

	/**
	 * Converts the object to its JSON description.
	 * 
	 * @return a string containing the JSON description.
	 * @throws MalformedQuestionException
	 *             if the question is malformed
	 */
	public String toJSON() throws MalformedQuestionException {
		if (this.auditErrors() != 0) {
			throw new MalformedQuestionException("Question that must be "
					+ " converted to JSON is malformed");
		}
		Map<String, Object> questionMap = new HashMap<String, Object>();

		if (mId != -1) {
			questionMap.put("id", mId);
		}

		questionMap.put("question", mQuestion);
		questionMap.put("answers", new JSONArray(mAnswers));
		questionMap.put("solutionIndex", mSolutionIndex);
		questionMap.put("tags", new JSONArray(mTags));

		if (mOwner != null) {
			questionMap.put("owner", mOwner);
		}

		return new JSONObject(questionMap).toString();
	}

	/**
	 * Audit method for QuizQuestion
	 * 
	 * @return the number of errors in this instance representation
	 */
	public int auditErrors() {
		int errors = 0;

		if (containsNotSpaceChar(mQuestion) || mQuestion.length() <= MAX_QUESTION_LENGTH) {
			errors++;
		}

        errors += mAnswers.auditErrors();
        errors += mTags.auditErrors();

		return errors;
	}

	private static Collection<String> extractCollectionFromJSONArray(JSONArray jsonArray) throws JSONException {
        final List<String> sourceList = new LinkedList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            sourceList.add(jsonArray.getString(i));
        }


        return new AbstractCollection<String>() {

            private final List<String> strEntries = sourceList;

            @Override
            public Iterator<String> iterator() {
                return strEntries.iterator();
            }

            @Override
            public int size() {
                return strEntries.size();
            }
        };
	}

    private boolean containsNotSpaceChar(String str) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private class AnswersList extends LinkedList<String> {

        public int auditErrors() {
            int errors = 0;
            int numAnswers = size();

            if (numAnswers < MIN_ANSWERS_NUM || numAnswers > MAX_ANSWERS_NUM) {
                errors++;
            }

            if (mSolutionIndex < 0 || mSolutionIndex >= numAnswers) {
                errors++;
            }

            for (String answer : this) {
                if (!containsNotSpaceChar(answer)) {
                    errors++;
                }
            }

            return errors;
        }

    }

    private class TagsSet extends HashSet<String> {

        public int auditErrors() {
            int errors = 0;
            int numTags = size();

            if (numTags < MIN_TAGS_NUM || numTags > MAX_TAGS_NUM) {
                errors++;
            }

            for (String tag : this) {
                if (!containsNotSpaceChar(tag)) {
                    errors++;
                }
            }

            return errors;
        }

    }
}
