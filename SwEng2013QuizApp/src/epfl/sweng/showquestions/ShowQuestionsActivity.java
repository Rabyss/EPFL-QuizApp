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
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.services.NothingInCacheEvent;
import epfl.sweng.services.Service;
import epfl.sweng.services.ServiceFactory;
import epfl.sweng.services.ShowQuestionEvent;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.ui.QuestionActivity;

/**
 * Activity to download a question and display it
 */
public class ShowQuestionsActivity extends QuestionActivity {

	private final static int PADDING_RIGHT = 23;
	private final static int PADDING_ZERO = 0;
	private final static int PADDING_FIVE = 5;
	private final static int PADDING_TEN = 10;
	private final static int PADDING_TWENTY = 20;

	private ShowQuestionsActivity mSelf;
	private QuizQuestion mRandomQuestion;
	private Button mNextQuestion;
	private TextView mCorrectness;
	private LinearLayout mLinearLayout;
	private ListView mAnswersList;
	private Service mService;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelf = this;
		getQuestion();
	}

	private void getQuestion() {
		// downloads a random question from the server
		mService = ServiceFactory.getServiceFor(this);
		mService.execute();
		showProgressDialog();
	}

	public void on(NothingInCacheEvent event) {
		hideProgressDialog();
		Toast.makeText(this, R.string.nothing_in_cache, Toast.LENGTH_LONG)
				.show();
		TestCoordinator.check(TTChecks.QUESTION_SHOWN);
	}

	public void on(ShowQuestionEvent event) {
		hideProgressDialog();
		// creates the main layout
		mLinearLayout = new LinearLayout(this);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		mLinearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mRandomQuestion = event.getQuizQuestion();
		showQuestion();
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
		mCorrectness.setPadding(PADDING_TEN, PADDING_TWENTY, PADDING_ZERO,
				PADDING_ZERO);
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
		// displaySolutionIndex();

		setContentView(mLinearLayout);
		TestCoordinator.check(TTChecks.QUESTION_SHOWN);
	}

	private void displayAnswers() {

		// Initialize Answers List
		mAnswersList = new ListView(this);
		mAnswersList.setPadding(PADDING_TEN, PADDING_TWENTY, PADDING_ZERO,
				PADDING_ZERO);

		mAnswersList.setOnItemClickListener(new AnswerOnClickListener());
		mAnswersList.setAdapter(new AnswerListAdapter());

		mLinearLayout.addView(mAnswersList);
	}

	private void displayTags() {
		LinearLayout tagLayout = new LinearLayout(this);
		tagLayout.setOrientation(LinearLayout.HORIZONTAL);
		tagLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		for (String tag : mRandomQuestion.getTags()) {
			TextView tagText = new TextView(this);
			tagText.setText(tag);
			tagText.setPadding(PADDING_ZERO, PADDING_ZERO, PADDING_RIGHT,
					PADDING_ZERO);
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
		public void onItemClick(AdapterView<?> listView, View view,
				int position, long id) {

			if (mRandomQuestion.isSolution(position)) {
				mCorrectness.setText(getString(R.string.button_check));
				mNextQuestion.setEnabled(true);
				mAnswersList.setEnabled(false);
			} else {
				mCorrectness.setText(getString(R.string.button_cross));
			}

			TestCoordinator.check(TTChecks.ANSWER_SELECTED);
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
			answerText.setPadding(PADDING_FIVE, PADDING_TWENTY, PADDING_ZERO,
					PADDING_TWENTY);
			answerText.setText(mRandomQuestion.getAnswers().get(position));
			return answerText;
		}

	}

	@Override
	protected void serverFailure() {
		Toast.makeText(this, R.string.fetch_server_failure, Toast.LENGTH_LONG)
				.show();
		TestCoordinator.check(TTChecks.QUESTION_SHOWN);
	}
}
