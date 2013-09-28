package epfl.sweng.entry;

import epfl.sweng.R;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

/**
 * Entry Point of the SwEng2013QuizApp
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// let the testing infrastructure know that entry point has been initialized
		TestingTransactions.check(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void displayShowQuestion(View view) {
	    Toast.makeText(this, "You clicked diplay a random question", Toast.LENGTH_SHORT).show();
	    Intent displayActivityIntent = new Intent(this, ShowQuestionsActivity.class);
	    startActivity(displayActivityIntent);
	}
}
