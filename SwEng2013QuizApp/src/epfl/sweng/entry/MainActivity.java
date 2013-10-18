package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Entry Point of the SwEng2013QuizApp
 */
public class MainActivity extends Activity {
	private static boolean mIsLogged = false;
	private Button mLogButton;
	private Button mShowQuestionButton;
	private Button mSubmitQuestionButton;
	private LinearLayout mLinearLayout;
	private Activity mThis;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		displayInit();
		

		// let the testing infrastructure know that entry point has been
		// initialized
		TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void displayShowQuestion(View view) {
		Intent displayActivityIntent = new Intent(this,
				ShowQuestionsActivity.class);
		startActivity(displayActivityIntent);
	}

	public void displayEditQuestions(View view) {
		Intent displayEditQuestionsIntent = new Intent(this,
				EditQuestionActivity.class);
		startActivity(displayEditQuestionsIntent);
	}
	
	public void displayAuthentication(View view) {
		Intent displayAuthenticationIntent = new Intent(this, AuthenticationActivity.class);
		startActivity(displayAuthenticationIntent);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public void displayInit() {
		mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        displayButton();
        setContentView(mLinearLayout);
	}
	
	public void displayButton() {
		mLogButton = new Button(this);
		mShowQuestionButton = new Button(this);
		mShowQuestionButton.setText(R.string.show_random_question);
		mSubmitQuestionButton = new Button(this);
		mSubmitQuestionButton.setText(R.string.submit_quiz);
		
		if (mIsLogged) {
			mLogButton.setText(R.string.log_out);
			mShowQuestionButton.setEnabled(true);
			mSubmitQuestionButton.setEnabled(true);
		} else {
			mLogButton.setText(R.string.log_button);
			mShowQuestionButton.setEnabled(false);
			mSubmitQuestionButton.setEnabled(false);
		}
		
		mThis = this;
		mLogButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIsLogged) {
					mIsLogged = false;
					//TODO log out
					// Delete stored session
					SharedPreferences prefs = mThis.getPreferences(MODE_PRIVATE);
					prefs.edit().remove("sessionID");
					prefs.edit().apply();
					
					displayInit();
				
					TestCoordinator.check(TTChecks.LOGGED_OUT);
				} else {
					displayAuthentication(v);
				}
					
			}
		});
		
		mShowQuestionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayShowQuestion(v);
				
			}
		});
		
		mSubmitQuestionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayEditQuestions(v);
				
			}
		});
		mLinearLayout.addView(mLogButton);
		mLinearLayout.addView(mShowQuestionButton);
		mLinearLayout.addView(mSubmitQuestionButton);
		
	}
	public static void setIsLogged(boolean isLogged) {
		mIsLogged = isLogged;
	}
}
