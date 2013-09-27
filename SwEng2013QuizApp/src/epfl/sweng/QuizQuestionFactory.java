package epfl.sweng;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.servercomm.SwengHttpClientFactory;

public class QuizQuestionFactory {
    
    private static final String REQUEST_URL = "https://sweng-quiz.appspot.com/quizquestions/random";
    
    public static QuizQuestion getRandomQuizQuestion() {    
        HttpGet randomQuestionRequest = new HttpGet(REQUEST_URL);
        ResponseHandler<String> randomQuestionHandler = new BasicResponseHandler();
        
        QuizQuestion randomQuestion = null;
        
        try {
            
            String randomQuestionRaw = SwengHttpClientFactory.getInstance().execute(
                    randomQuestionRequest, randomQuestionHandler);
            randomQuestion = new QuizQuestion(new JSONObject(randomQuestionRaw));
        } catch (JSONException e) {
            // TODO Handle parsing error.
        } catch (ClientProtocolException e) {
            // TODO Handle
        } catch (IOException e) {
            // TODO Handle: fatal error ?
        }
        
        return randomQuestion;
    }
}
