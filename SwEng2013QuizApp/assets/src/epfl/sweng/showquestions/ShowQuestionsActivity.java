package epfl.sweng.showquestions;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import epfl.sweng.ui.QuestionActivity;

/**
 * Activity to download a question and display it
 */
public class ShowQuestionsActivity extends QuestionActivity {

	private final static int PADDING_RIGHT = 23;
	private final static int PADDING = 0;

	private QuizQuestion mRandomQuestion;
	private Button[] mAnswer;
	private Button mNextQuestion;
	private TextView[] mCorrectness;
	private LinearLayout mLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ServerCommunicator.getInstance().addObserver(this);
		getQuestion();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}

	/**
	 * Downloads and displays a new random question
	 */
	public void getQuestion() {
		// creates the main layout
		mLinearLayout = new LinearLayout(this);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		mLinearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// downloads a random question from the server
		ServerCommunicator.getInstance().getRandomQuestion();
		// show a progress dialog while waiting for question
		showProgressDialog();
	}

	@Override
	protected boolean mustTakeAccountOfUpdate() {
		return ServerCommunicator.getInstance().isFetchingQuestion();
	}

	@Override
	protected void processDownloadedData(Object data) {
		mRandomQuestion = (QuizQuestion) data;
		showQuestion();
	}

	public void showQuestion() {
		// Display the text of the question
		TextView question = new TextView(this);
		question.setText(mRandomQuestion.getQuestion());
		mLinearLayout.addView(question);

		// display the answers
		displayAnswers();

		// initializes the button nextQuestion
		mNextQuestion = new Button(this);
		mNextQuestion.setText(R.string.next_question);
		mNextQuestion.setEnabled(false);
		mLinearLayout.addView(mNextQuestion);
		mNextQuestion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestion();
			}
		});

		// Display the tags
		displayTags();

		// Debug: Display the solution 
		//displaySolutionIndex();
		 

		setContentView(mLinearLayout);
		TestingTransactions.check(TTChecks.QUESTION_SHOWN);
	}

	public void displayAnswers() {
		// Initialize the arrays of answers and correctness sign
		int totalAnswer = mRandomQuestion.getAnswers().length;
		mAnswer = new Button[totalAnswer];
		mCorrectness = new TextView[totalAnswer];

		// initializes all the buttons of answer
		for (int indexAnswer = 0; indexAnswer < totalAnswer; indexAnswer++) {
			displayAnswer(indexAnswer);
		}
	}

	private void displayAnswer(int indexAnswer) {
		// New Layout for containing answer button and correctness sign
		LinearLayout answerLayout = new LinearLayout(this);
		answerLayout.setOrientation(LinearLayout.HORIZONTAL);
		answerLayout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// Initialize correctness sign (hidden for now)
		mCorrectness[indexAnswer] = new TextView(this);
		if (mRandomQuestion.isSolution(indexAnswer)) {
			mCorrectness[indexAnswer].setText("\u2714");
		} else {
			mCorrectness[indexAnswer].setText("\u2718");
		}
		mCorrectness[indexAnswer].setVisibility(View.INVISIBLE);

		// Initialize answer button
		mAnswer[indexAnswer] = new Button(this);
		mAnswer[indexAnswer].setClickable(true);
		mAnswer[indexAnswer].setText(mRandomQuestion.getAnswers()[indexAnswer]);
		mAnswer[indexAnswer].setOnClickListener(new AnswerOnClickListener());
		answerLayout.addView(mAnswer[indexAnswer]);
		answerLayout.addView(mCorrectness[indexAnswer]);
		mLinearLayout.addView(answerLayout);
	}

	public void displayTags() {
		int totalTags = mRandomQuestion.getTags().length;
		LinearLayout tagLayout = new LinearLayout(this);
		tagLayout.setOrientation(LinearLayout.HORIZONTAL);
		tagLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < totalTags; i++) {
			TextView tagText = new TextView(this);
			tagText.setText(mRandomQuestion.getTags()[i]);
			tagText.setPadding(PADDING, PADDING, PADDING_RIGHT, PADDING);
			tagText.setTextColor(Color.GRAY);
			tagLayout.addView(tagText);
		}
		mLinearLayout.addView(tagLayout);

	}

	public void displaySolutionIndex() {
		TextView solutionIndex = new TextView(this);
		int index = mRandomQuestion.getSolutionIndex();
		String solutionText = String.valueOf(index);
		solutionIndex.setText(solutionText);
		mLinearLayout.addView(solutionIndex);
	}
	
	/**
	 * Private class for handling click on answer buttons
	 */
	private final class AnswerOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			// Find index of actual answer that was clicked on
			int actualAnswer = 0;
			for (int i = 0; i < mRandomQuestion.getAnswers().length; i++) {
				if (v == mAnswer[i]) {
					actualAnswer = i;
				}
			}
			
			// Set correctness sign visible
			mCorrectness[actualAnswer].setVisibility(View.VISIBLE);
			
			// If it's the right answer then disable all other answers and
			// enable "next question" button
			if (mRandomQuestion.isSolution(actualAnswer)) {
				mNextQuestion.setEnabled(true);
				for (int i = 0; i < mRandomQuestion.getAnswers().length; i++) {
					mAnswer[i].setEnabled(false);
					if (i != actualAnswer) {
						mCorrectness[i].setVisibility(View.INVISIBLE);
					}
				}
			}
			
			TestingTransactions.check(TTChecks.ANSWER_SELECTED);
		}
	}

}
