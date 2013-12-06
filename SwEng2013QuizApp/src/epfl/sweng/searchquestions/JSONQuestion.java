package epfl.sweng.searchquestions;

import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.quizquestions.QuizQuestion;

public class JSONQuestion {
    private JSONObject jsonObject;
    
    public JSONQuestion(JSONObject json) throws JSONException {
        this.jsonObject = json;
    }
    
    public QuizQuestion getQuizQuestion() throws JSONException {
        return new QuizQuestion(jsonObject.toString());
    }
}
