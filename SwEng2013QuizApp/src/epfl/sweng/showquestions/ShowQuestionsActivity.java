package epfl.sweng.showquestions;


import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	private final static int PADDING_ZERO = 0;
	private final static int PADDING_FIVE = 5;
	private final static int PADDING_TEN=10;
	private final static int PADDING_TWENTY= 20;

	private ShowQuestionsActivity mSelf;
	private QuizQuestion mRandomQuestion;
	private Button mNextQuestion;
	private TextView mCorrectness;
	private LinearLayout mLinearLayout;
	private ListView mAnswersList;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ServerCommunicator.getInstance().addObserver(this);
		mSelf = this;
		getQuestion();
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

	private void getQuestion() {
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

	private void showQuestion() {
		// Display the text of the question
		TextView question = new TextView(this);
		question.setText(mRandomQuestion.getQuestion());
		mLinearLayout.addView(question);

		// display the answers
		displayAnswers();
		
		mCorrectness = new TextView(this);
		mCorrectness.setText("Wait for an answer...");
		mCorrectness.setPadding(PADDING_TEN, PADDING_TWENTY, PADDING_ZERO, PADDING_ZERO);
		mLinearLayout.addView(mCorrectness);
		
		// initializes the button nextQuestion
		mNextQuestion = new Button(this);
		mNextQuestion.setText(R.string.next_question);
		mNextQuestion.setEnabled(false);
		mNextQuestion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestion();
			}
		});
		mLinearLayout.addView(mNextQuestion);

		// Display the tags
		displayTags();

		// Debug: Display the solution 
		//displaySolutionIndex();
		 

		setContentView(mLinearLayout);
		TestingTransactions.check(TTChecks.QUESTION_SHOWN);
	}

	private void displayAnswers() {
		// Initialize the arrays of answers and correctness sign
			

		mAnswersList = new ListView(this);
		mAnswersList.setPadding(PADDING_TEN, PADDING_TWENTY, PADDING_ZERO, PADDING_ZERO);
		
		mAnswersList.setOnItemClickListener(new AnswerOnClickListener());
		mAnswersList.setAdapter(new AnswerListAdapter());
		
		mLinearLayout.addView(mAnswersList);
	}


	private void displayTags() {
		int totalTags = mRandomQuestion.getTags().length;
		LinearLayout tagLayout = new LinearLayout(this);
		tagLayout.setOrientation(LinearLayout.HORIZONTAL);
		tagLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < totalTags; i++) {
			TextView tagText = new TextView(this);
			tagText.setText(mRandomQuestion.getTags()[i]);
			tagText.setPadding(PADDING_ZERO, PADDING_ZERO, PADDING_RIGHT, PADDING_ZERO);
			tagText.setTextColor(Color.GRAY);
			tagLayout.addView(tagText);
		}
		mLinearLayout.addView(tagLayout);

	}
	
	/**
	 * Private class for handling click on answer buttons
	 */
	private final class AnswerOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position,
				long id) {
			
			if (mRandomQuestion.isSolution(position)) {
				mCorrectness.setText(getString(R.string.button_check));
				mNextQuestion.setEnabled(true);
				mAnswersList.setEnabled(false);
			} else {
				mCorrectness.setText(getString(R.string.button_cross));
			}
			
			TestingTransactions.check(TTChecks.ANSWER_SELECTED);
		}
	}

	/**
	 * Adapter for the list view of the answer
	 */
	private class AnswerListAdapter extends ArrayAdapter<String> {

		public AnswerListAdapter() {
			super(mSelf, 0, mRandomQuestion.getAnswers());
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView answerText = new TextView(mSelf);
			answerText.setPadding(PADDING_FIVE, PADDING_TWENTY, PADDING_ZERO, PADDING_TWENTY);
			answerText.setText(mRandomQuestion.getAnswers()[position]);
			return answerText;
		}
	
	}
}
