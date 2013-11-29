package epfl.sweng.searchquestions;

import android.content.Context;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.QueryParser;
import epfl.sweng.searchquestions.parser.QueryParser.QueryParserResult;

import java.util.HashSet;
import java.util.Set;

public class TagStorage {
	private Context context;
	
	public TagStorage(Context context) {
		this.context = context;
	}
	
	public Set<QuizQuestion> query(String queryStr) {
        QueryParserResult parserResult = QueryParser.parse(queryStr);
        if (parserResult.isDone()) {
            return getQuestions(parserResult.getAST().getIDs());
        } else {
            return new HashSet<QuizQuestion>(); //returns the empty set
        }
	}

	
	private Set<QuizQuestion> getQuestions(Set<Integer> tagIDs) {
//        QuestionCache cache = QuestionCache.getInstance(context);
		Set<QuizQuestion> questions = new HashSet<QuizQuestion>();
		
		for (Integer id : tagIDs) {
//			QuizQuestion question = cache.getQuestionById(id);
//			
//			if (question != null) {
//				questions.add(question);
//			}
		}
		
		return questions;
	}
}
