package epfl.sweng.editquestions;

import java.util.ArrayList;

import epfl.sweng.R;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 * Activity to edit questions
 *
 */
public class EditQuestionActivity extends Activity {
	
	private ArrayList<Answer> answers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);
		
		answers = new ArrayList<Answer>();
		answers.add(new Answer(this, (ViewGroup) findViewById(R.id.linearLayoutAnswers), true));
		
		findViewById(R.id.editQuestionText).requestFocus();
		
		// let the testing infrastructure know that edit question has been initialized
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
		Toast.makeText(this, "Submitting", 1000).show();
	}

}
