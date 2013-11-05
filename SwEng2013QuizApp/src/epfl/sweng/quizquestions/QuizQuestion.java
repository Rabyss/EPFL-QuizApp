package epfl.sweng.quizquestions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a question of the quiz.
 */
public class QuizQuestion {

	// Uses Integer instead of int to allow them to be nullable.
	private final int mId;
	private final String mQuestion;
	private final List<String> mAnswers;
	private final int mSolutionIndex;
	private final Set<String> mTags;
	private final String mOwner;

	public QuizQuestion(final String question, final List<String> answers,
			final int solutionIndex, final Set<String> tags, final int id,
			final String owner) {
		mId = id;
		mQuestion = question;

        mAnswers = new LinkedList<String>();
        mAnswers.addAll(answers);

        mSolutionIndex = solutionIndex;

        mTags = new HashSet<String>();
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
		mAnswers = new LinkedList<String>(extractCollectionFromJSONArray(jsonModel.getJSONArray("answers")));
		mSolutionIndex = jsonModel.getInt("solutionIndex");
		mTags = new HashSet<String>(extractCollectionFromJSONArray(jsonModel.getJSONArray("tags")));
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
		if (this.audit() != 0) {
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
	public int audit() {
		int auditErrors = 0;
		boolean mustCheckAnswers = true;
		boolean mustCheckTags = true;
		if (mQuestion == null || mQuestion.trim().isEmpty()) {
			auditErrors++;
		}
		if (mAnswers == null || mAnswers.size() < 2) {
			mustCheckAnswers = false;
			auditErrors++;
		}
		if (mSolutionIndex < 0 || mSolutionIndex >= mAnswers.size()) {
			auditErrors++;
		}
		if (mTags == null || mTags.size() < 1) {
			mustCheckTags = false;
			auditErrors++;
		}

		if (mustCheckAnswers) {
			for (String answer : mAnswers) {
				if (answer == null || answer.trim().isEmpty()) {
					auditErrors++;
				}
			}
		}
		if (mustCheckTags) {
			for (String tag : mTags) {
				if (tag == null || tag.trim().isEmpty()) {
					auditErrors++;
				}
			}
		}

		return auditErrors;
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

}
