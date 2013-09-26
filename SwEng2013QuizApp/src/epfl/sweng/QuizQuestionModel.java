package epfl.sweng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Represents a question of the quiz.
 *  @author Philemon Favrod (philemon.favrod@epfl.ch)
 */
public class QuizQuestionModel {
    private final int mId;
    private final String mQuestion;
    private final String[] mAnswers;
    private final int mSolutionIndex;
    private final String[] mTags;
    private final String mOwner;
    
    public QuizQuestionModel(int id, String question, String[] answers, int solutionIndex, String[] tags, String owner) {
        mId = id;
        mQuestion = question;
        mAnswers = answers;
        mSolutionIndex = solutionIndex;
        mTags = tags;
	    mOwner = owner;
    }

    public QuizQuestionModel(JSONObject jsonModel) throws JSONException {
        mId = jsonModel.getInt("id");
        mQuestion = jsonModel.getString("question");
        mAnswers = extractArrayFromJson(jsonModel.getJSONArray("answers"));
        mSolutionIndex = jsonModel.getInt("solutionIndex");
        mTags = extractArrayFromJson(jsonModel.getJSONArray("tags"));
        mOwner = jsonModel.getString("owner");
    }
    
    private static String[] extractArrayFromJson(JSONArray jsonArray) throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }
    
    public int getId() {
        return mId;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String[] getAnsers() {
        return mAnswers;
    }

    /** Checks whether the given index is the index of the solution.
    */
    public boolean isSolution(int index) {
        return (mSolutionIndex == index);
    }

    public String[] getTags() {
        return mTags;
    }

    public String getOwner() {
        return mOwner;
    }
    
}
