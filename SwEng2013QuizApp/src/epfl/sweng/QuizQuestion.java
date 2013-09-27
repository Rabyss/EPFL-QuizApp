package epfl.sweng;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a question of the quiz.
 * 
 * @author Philemon Favrod (philemon.favrod@epfl.ch)
 */
public class QuizQuestion {
    private final Integer mId;
    private final String mQuestion;
    private final String[] mAnswers;
    private final Integer mSolutionIndex;
    private final String[] mTags;
    private final String mOwner;

    public QuizQuestion(Integer id, 
                             String question, 
                             String[] answers,
                             Integer solutionIndex, 
                             String[] tags, 
                             String owner) {
        mId = id;
        mQuestion = question;
        mAnswers = answers;
        mSolutionIndex = solutionIndex;
        mTags = tags;
        mOwner = owner;
    }

    public QuizQuestion(JSONObject jsonModel) throws JSONException {
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
        return mAnswers;
    }

    /**
     * Checks whether the given index is the index of the solution.
     */
    public boolean isSolution(int index) {
        return mSolutionIndex == index;
    }

    public String[] getTags() {
        return mTags;
    }

    public String getOwner() {
        return mOwner;
    }

    /**Converts the object to its JSON description.
     * 
     * @return a string containing the JSON description.
     */
    public String toJSON() {
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
    
    private static String[] extractArrayFromJson(JSONArray jsonArray)
    	throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }

}
