package epfl.sweng;

import java.util.HashMap;

import org.json.JSONObject;

/**
 * 
 * Submits a question that the user has created.
 * 
 * @author Rabyss (jeremy.rabasco@epfl.ch)
 * 
 */
public class QuizQuestionSubmit {

	private String mQuestion;
	private String[] mAnswers;
	private int mSolutionIndex;
	private String[] mTags;

	public QuizQuestionSubmit(String question, String[] answers,
			int solutionIndex, String[] tags) {
		mQuestion = question;
		mAnswers = answers;
		mSolutionIndex = solutionIndex;
		mTags = tags;
	}

	
	public void submitViaObject() {
		HashMap<String, Object> question = new HashMap<String, Object>();
		
		question.put("question", mQuestion);
		question.put("answers", mAnswers);
		question.put("solutionIndex", mSolutionIndex);
		question.put("tags", mTags);
		
		System.out.println(new JSONObject(question).toString());
		
	}
	
	
	public void submitViaString() {
		
		
		
	}
	
	public static void main(String[] args) {

		String question = "What is the answer to the ultimate question about the life and the universe ?";
		String[] answers = {"24", "God", "Satan", "42"};
		final int solutionIndex = 3;
		String[] tags = {"h2g2", "yolo", "swag", "bff"};

		new QuizQuestionSubmit(question, answers, solutionIndex, tags)
				.submitViaObject();

	}
}
