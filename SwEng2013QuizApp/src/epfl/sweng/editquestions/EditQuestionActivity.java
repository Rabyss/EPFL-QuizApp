package epfl.sweng.editquestions;

import epfl.sweng.R;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

/**
 * 
 * Activity to edit questions
 *
 */
public class EditQuestionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);
		
		
		
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
		createNewAnswer(linearLayout);
	}
	
	public void createNewAnswer(ViewGroup view) {
		Button btn = new Button(this);
		btn.setText("test");
		
		LayoutParams btnLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		view.addView(btn, btnLayout);
	}

}
