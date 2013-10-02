package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * 
 * Activity to edit questions
 * 
 */
public class EditQuestionActivity extends Activity implements Observer {

	private ArrayList<Answer> answers;

	private final int displayTime = 2000;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);

		ServerCommunicator.getInstance().addObserver(this);

		answers = new ArrayList<Answer>();
		answers.add(new Answer(this,
				(ViewGroup) findViewById(R.id.linearLayoutAnswers), true));

		findViewById(R.id.editQuestionText).requestFocus();

		// let the testing infrastructure know that edit question has been
		// initialized
		TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_question, menu);
		return true;
	}

	public void addAnswer(View view) {
		ViewGroup linearLayout = (ViewGroup) findViewById(R.id.linearLayoutAnswers);
		answers.add(new Answer(this, linearLayout, false));
	}

	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	public void submitQuestion(View view) {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(
				getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		String question = ((EditText) findViewById(R.id.editQuestionText))
				.getText().toString();

		ArrayList<String> answersText = new ArrayList<String>();
		int solutionIndex = 0;

		for (Answer answer : answers) {
			answersText.add(answer.getContent());
			if (answer.isCorrect()) {
				solutionIndex = answers.indexOf(answer);
			}
		}

		String tagsText = ((EditText) findViewById(R.id.editTags)).getText()
				.toString();
		String[] tags = tagsText.split("\\W+");

		QuizQuestion quizQuestion = new QuizQuestion(null, question,
				answersText.toArray(new String[answers.size()]), solutionIndex,
				tags, null);

		ServerCommunicator.getInstance().submitQuizQuestion(quizQuestion, this);

		progDialog = new ProgressDialog(this);
		progDialog.setMessage("Submitting...");
		progDialog.setIndeterminate(false);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setCancelable(true);
		progDialog.show();

	}

	@Override
	public void onBackPressed() {
		Intent displayActivitxIntent = new Intent(this, MainActivity.class);
		startActivity(displayActivitxIntent);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (ServerCommunicator.getInstance().isSubmittingQuestion()) {
			String response = (String) data;
			progDialog.dismiss();
			if (response != null) {
				Toast.makeText(this, "Successful Submit", displayTime).show();
				Intent displayActivityIntent = new Intent(this,
						EditQuestionActivity.class);
				startActivity(displayActivityIntent);
			} else {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

				dlgAlert.setMessage("Server unreacheable or question's fields empty/misformatted");
				dlgAlert.setTitle("Submit failed");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();

				dlgAlert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
			}
		}
	}

}
