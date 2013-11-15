package epfl.sweng.searchquestions;

import static epfl.sweng.util.StringHelper.containsNonWhitespaceCharacters;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;

public class SearchQuery {
    private final String mQuery;

    private static final String QUERY_CHAR_CLASS_REGEX = "^[a-zA-Z0-9\\(\\)\\*\\+ ]+$";
    private static final int MAX_QUERY_LENGTH = 500;
    
	private static final int SWENG_OK = 200;

    public SearchQuery(final String query) throws UnvalidSearchQueryException {
        int errorsCount = auditQueryStr(query);
        if (errorsCount > 0) {
            throw new UnvalidSearchQueryException(errorsCount);
        }
        
        mQuery = query;
    }

    public String getQuery() {
        return mQuery;
    }

    public static int auditQueryStr(String query) {
        int errors = 0;

        // we make sure the query respects its char class
        if (!query.matches(QUERY_CHAR_CLASS_REGEX)) {
            System.err.println(query+": qccr");
            errors++;
        }

        if (query.length() >= MAX_QUERY_LENGTH) {
            errors++;
        }

        // we make sure the query contains at least one alphanumeric char
        if (!containsNonWhitespaceCharacters(query)) {
            System.err.println(query+": aoacr");
            errors++;
        }

        if (!isExpressionNestingCorrect(query)) {
            errors++;
        }

        return errors;
    }

    private static boolean isExpressionNestingCorrect(String query) {
        int openParenthesisCount = 0;
        int closedParenthesisCount = 0;
        for (int i = 0; i < query.length(); i++) {
            char current = query.charAt(i);
            if (current == '(') {
                openParenthesisCount++;
            } else if (current == ')') {
                closedParenthesisCount++;
            }
        }
        return openParenthesisCount == closedParenthesisCount;
    }

    public class UnvalidSearchQueryException extends Exception {

        private static final long serialVersionUID = 6028719701104283474L;

        public UnvalidSearchQueryException(int errorsCount) {
            super("invalid search query (" + errorsCount + " errors): '"
                    + mQuery + "'");
        }
    }
    
    public void query() {
    	query("");
    }
    
    public void query(String from) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("query", this.mQuery));
		
		if (!from.equals("")) {
			params.add(new BasicNameValuePair("from", from));
		}
		
		try {
			UrlEncodedFormEntity entity;
			entity = new UrlEncodedFormEntity(params);
			RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/search", entity);
			req.addHeader(entity.getContentType());
			
            ServerCommunicator.getInstance().doHttpPost(req, new ServerQueryEvent());
		} catch (UnsupportedEncodingException e) {
			this.error("Error: Unsupported Encoding Exception.");
		}
	}
    
	public void on(ServerQueryEvent event) {
		int status = event.getStatus();
		
		if (status == SWENG_OK) {
			try {
				// questions
				String json = event.getJSONQuestions();
				JSONObject jsonObj = new JSONObject(json);
		        
				JSONArray questionArray = jsonObj.getJSONArray("questions");
				Set<QuizQuestion> questionSet = new HashSet<QuizQuestion>();
				
				for (int i = 0; i<questionArray.length(); i++) {
					JSONQuestion jsonQuestion = new JSONQuestion(questionArray.getJSONObject(i));
					questionSet.add(jsonQuestion.getQuizQuestion());
				}
				
				// next
				String next = jsonObj.optString("next");
				
				if (next.equals("")) {
					// TODO your application must use to request the next batch of questions from the server
				}
				
		        // this.emit(new XXXEvent(questionSet));
			} catch (JSONException e) {
				this.error("Error: malformed JSON (session).");
			}
		} else {
			this.error("Error "+status+" on Tequila Server.");
		}
	}
	
	public void error(String err) {
		// TODO implement error
	}
}
