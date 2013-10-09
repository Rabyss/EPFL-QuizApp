package epfl.sweng.utils;

/**
 * Exception when a QuizQuestion is malformed
 * 
 * @see QuizQuestion
 */
public class MalformedQuestionException extends Exception {

	private static final long serialVersionUID = -2338686991196869040L;

	public MalformedQuestionException(String msg) {
		super(msg);
	}

}
