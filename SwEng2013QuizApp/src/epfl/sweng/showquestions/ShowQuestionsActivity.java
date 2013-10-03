package epfl.sweng.showquestions;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import epfl.sweng.entry.MainActivity;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import epfl.sweng.ui.QuestionActivity;

/**
 * Uploads a new question and displays it
 * 
 */
public class ShowQuestionsActivity extends QuestionActivity {
	private final int PADDING_RIGHT=23;
	private final int PADDING=0;
	private QuizQuestion mRandomQuestion = null;
	private Button[] mAnswer;
	private Button mNextQuestion;
	private TextView[] mCorrectness;
	private int mIndexButton;
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
	 * Updates and displays a new random question
	 */
	public void getQuestion() {
		// creates the main layout
		mLinearLayout = new LinearLayout(this);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		mLinearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// uploads a random question from the server
		ServerCommunicator.getInstance().getRandomQuestion();
		
		showProgressDialog();
	}

	public void showQuestion() {
		// initializes the title of the question
		TextView question = new TextView(this);
		question.setText(mRandomQuestion.getQuestion());
		mLinearLayout.addView(question);
		// initializes the button nextQuestion
		mNextQuestion = new Button(this);
		mNextQuestion.setText("Next Question");
		mNextQuestion.setEnabled(false);
		mNextQuestion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestion();
			}
		});

		displayAnswer();

		mLinearLayout.addView(mNextQuestion);
		displayTags();
		//displaySolutionIndex();
		setContentView(mLinearLayout);
		TestingTransactions.check(TTChecks.QUESTION_SHOWN);
	}

	public void displayAnswer() {
		int totalAnswer = mRandomQuestion.getAnswers().length;
		mAnswer = new Button[totalAnswer];
		mCorrectness = new TextView[totalAnswer];
		// initializes all the buttons of answer
		for (mIndexButton = 0; mIndexButton < totalAnswer; mIndexButton++) {
			LinearLayout answerLayout = new LinearLayout(this);
			answerLayout.setOrientation(LinearLayout.HORIZONTAL);
			answerLayout.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			mCorrectness[mIndexButton] = new TextView(this);
			if (mRandomQuestion.isSolution(mIndexButton)) {
				mCorrectness[mIndexButton].setText("\u2714");
			} else {
				mCorrectness[mIndexButton].setText("\u2718");
			}
			mCorrectness[mIndexButton].setVisibility(View.INVISIBLE);
			mAnswer[mIndexButton] = new Button(this);
			mAnswer[mIndexButton].setClickable(true);
			mAnswer[mIndexButton]
					.setText(mRandomQuestion.getAnswers()[mIndexButton]);
			mAnswer[mIndexButton]
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							int actualAnswer = 0;
							for (int i = 0; i < mRandomQuestion.getAnswers().length; i++) {
								if (v == mAnswer[i]) {
									actualAnswer = i;
								}
							}
							mCorrectness[actualAnswer]
									.setVisibility(View.VISIBLE);
							if (mRandomQuestion.isSolution(actualAnswer)) {
								mNextQuestion.setEnabled(true);
								for (int i = 0; i < mRandomQuestion
										.getAnswers().length; i++) {
									mAnswer[i].setEnabled(false);
									if (i != actualAnswer) {
										mCorrectness[i]
												.setVisibility(View.INVISIBLE);
									}
								}
							}
							TestingTransactions.check(TTChecks.ANSWER_SELECTED);
						}
					});
			answerLayout.addView(mAnswer[mIndexButton]);
			answerLayout.addView(mCorrectness[mIndexButton]);
			mLinearLayout.addView(answerLayout);
		}
	}

	public void displayTags() {
		int totalTags = mRandomQuestion.getTags().length;
		LinearLayout tagLayout= new LinearLayout(this);
		tagLayout.setOrientation(LinearLayout.HORIZONTAL);
		tagLayout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < totalTags; i++) {
			TextView tagText = new TextView(this);
			tagText.setText(mRandomQuestion.getTags()[i]);
			tagText.setPadding(PADDING, PADDING, PADDING_RIGHT, PADDING);
			tagText.setTextColor(Color.GRAY);
			tagLayout.addView(tagText);
		}
		mLinearLayout.addView(tagLayout);

	}

	/*public void displaySolutionIndex() {
		TextView solutionIndex = new TextView(this);
		int index = mRandomQuestion.getSolutionIndex();
		String solutionText = String.valueOf(index);
		solutionIndex.setText(solutionText);
		mLinearLayout.addView(solutionIndex);
	}*/
	

    @Override
    protected boolean mustTakeAccountOfUpdate() {
        return ServerCommunicator.getInstance().isFetchingQuestion();
    }

    @Override
    protected void processDownloadedData(Object data) {
        mRandomQuestion = (QuizQuestion) data;
        showQuestion();
    }
}
