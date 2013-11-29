package epfl.sweng.searchquestions;

import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.QueryParser.QueryParserResult;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static epfl.sweng.util.StringHelper.containsNonWhitespaceCharacters;

public class SearchQuery {
    private final String mQuery;
    private final QueryParserResult mParserResult;

    private static final String QUERY_CHAR_CLASS_REGEX = "^[a-zA-Z0-9\\(\\)\\*\\+ ]+$";
    private static final int MAX_QUERY_LENGTH = 500;
    
	private static final int SWENG_OK = 200;

    public SearchQuery(final String query, final QueryParserResult parserResult) throws InvalidSearchQueryException {
        mParserResult = parserResult;
        mQuery = query;

        int errorsCount = auditErrors();
        if (errorsCount > 0) {
            throw new InvalidSearchQueryException(errorsCount);
        }
    }

    public String getQuery() {
        return mQuery;
    }

    public int auditErrors() {
        int errors = 0;

        // we make sure the query respects its char class
        if (!mQuery.matches(QUERY_CHAR_CLASS_REGEX)) {
            errors++;
        }

        if (mQuery.length() >= MAX_QUERY_LENGTH) {
            errors++;
        }

        // we make sure the query contains at least one alphanumeric char
        if (!containsNonWhitespaceCharacters(mQuery)) {
            errors++;
        }

        if (mParserResult == null || !mParserResult.isDone()) {
            errors++;
        }

        return errors;
    }


    public class InvalidSearchQueryException extends Exception {

        private static final long serialVersionUID = 6028719701104283474L;

        public InvalidSearchQueryException(int errorsCount) {
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

	public static int auditQueryStr(String invalidExpr) {
		// TODO Auto-generated method stub
		return 0;
	}
}
