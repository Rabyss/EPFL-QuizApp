package epfl.sweng.quizquestions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a question of the quiz.
 */
public class QuizQuestion {

	// Uses Integer instead of int to allow them to be nullable.
	private final Integer mId;
	private final String mQuestion;
	private final String[] mAnswers;
	private final Integer mSolutionIndex;
	private final String[] mTags;
	private final String mOwner;

	public QuizQuestion(final String question, final List<String> answers,
			final int solutionIndex, final Set<String> tags, final int id,
			final String owner) {
		mId = id;
		mQuestion = question;
		mAnswers = answers.toArray(new String[answers.size()]);
		mSolutionIndex = solutionIndex;
		mTags = tags.toArray(new String[tags.size()]);
		mOwner = owner;
	}

	/**
	 * COnstructs the class from a JSONObject
	 * 
	 * @param jsonModel
	 * @throws MalformedQuestionException
	 * @throws MalformedQuestionException
	 *             if the JSONObject is malformed
	 */
	public QuizQuestion(final String jsonText) throws JSONException {
		JSONObject jsonModel = new JSONObject(jsonText);
		mId = jsonModel.getInt("id");
		mQuestion = jsonModel.getString("question");
		mAnswers = extractArrayFromJson(jsonModel.getJSONArray("answers"));
		mSolutionIndex = jsonModel.getInt("solutionIndex");
		mTags = extractArrayFromJson(jsonModel.getJSONArray("tags"));
		mOwner = jsonModel.getString("owner");
	}

	public int getId() {
		return mId;
	}

	public String getQuestion() {
		return mQuestion;
	}

	public String[] getAnswers() {
		return mAnswers.clone();
	}

	/**
	 * Checks whether the given index is the index of the solution.
	 */
	public boolean isSolution(int index) {
		return mSolutionIndex == index;
	}

	public String[] getTags() {
		return mTags.clone();
	}

	public String getOwner() {
		return mOwner;
	}

	public int getSolutionIndex() {
		return mSolutionIndex;
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
		HashMap<String, Object> questionMap = new HashMap<String, Object>();

		if (mId != null) {
			questionMap.put("id", mId);
		}

		questionMap.put("question", mQuestion);
		questionMap.put("answers", new JSONArray(Arrays.asList(mAnswers)));
		questionMap.put("solutionIndex", mSolutionIndex);
		questionMap.put("tags", new JSONArray(Arrays.asList(mTags)));

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
		if (mAnswers == null || mAnswers.length < 2) {
			mustCheckAnswers = false;
			auditErrors++;
		}
		if (mSolutionIndex < 0 || mSolutionIndex >= mAnswers.length) {
			auditErrors++;
		}
		if (mTags == null || mTags.length < 1) {
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

	private static String[] extractArrayFromJson(JSONArray jsonArray)
			throws JSONException {
		String[] stringArray = new String[jsonArray.length()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

}
