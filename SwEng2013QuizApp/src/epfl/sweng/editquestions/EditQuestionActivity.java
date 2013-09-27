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
import android.widget.EditText;
import android.widget.LinearLayout;

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
		
		createNewAnswer((ViewGroup) findViewById(R.id.linearLayoutAnswers), true);
		
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
		createNewAnswer(linearLayout, false);
	}
	
	public void createNewAnswer(ViewGroup view, boolean correct) {
		// Create Layout
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		view.addView(linearLayout);
		
		// Create EditText
		EditText editText = new EditText(this);
		editText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		editText.setHint(R.string.edit_answer_text);
		linearLayout.addView(editText);
		
		// Create Button Correct
		Button btn = new Button(this);
		if (correct) {
			btn.setText(R.string.button_check);
			btn.setEnabled(false);
		} else {
			btn.setText(R.string.button_cross);
		}
		btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		linearLayout.addView(btn);
		
		// Create Button delete
		Button btnDelete = new Button(this);
		btnDelete.setText(R.string.button_delete);
		linearLayout.addView(btnDelete);
	}

}
