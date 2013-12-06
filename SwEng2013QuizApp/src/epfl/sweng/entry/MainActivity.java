package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserStorage;
import epfl.sweng.context.AppContext;
import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.context.ConnectionEvent.ConnectionEventType;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.proxy.OnlineEvent;
import epfl.sweng.proxy.PostConnectionErrorEvent;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.searchquestions.SearchActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Entry Point of the SwEng2013QuizApp
 */
public class MainActivity extends Activity implements EventListener {
	private static boolean mIsLogged = false;
	private EventEmitter emitter;
	private Button mLogButton;
	private Button mShowQuestionButton;
	private Button mSubmitQuestionButton;
	private Button mSearchButton;
	private LinearLayout mLinearLayout;
	private MainActivity mThis;
	private CheckBox isOfflineCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String sessionID = UserStorage.getInstance(this).getSessionID();

		mIsLogged = sessionID != null;
		emitter = new MainActivityEventEmitter();
		AppContext.getContext().addAsListener(emitter);
		emitter.addListener(Proxy.getInstance(getApplicationContext()));
		Proxy.getInstance(getApplicationContext()).addListener(this);
		displayInit();
		if (AppContext.getContext().isOnline()) {
			emitter.emit(new OnlineEvent());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void on(PostConnectionErrorEvent event) {
		isOfflineCheckBox.setChecked(true);
	}

	public void on(SwitchSuccessfulEvent event) {
		// TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_DISABLED);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Proxy.getInstance(getApplicationContext()).removeListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// let the testing infrastructure know that entry point has been
		// initialized
		TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);

	}

	private void displayShowQuestion(View view) {
		Proxy.getInstance(getApplicationContext()).resetState();
		Intent displayActivityIntent = new Intent(this,
				ShowQuestionsActivity.class);
		startActivity(displayActivityIntent);
	}

	private void displayEditQuestions(View view) {
		Intent displayEditQuestionsIntent = new Intent(this,
				EditQuestionActivity.class);
		startActivity(displayEditQuestionsIntent);
	}

	private void displayAuthentication(View view) {
		Intent displayAuthenticationIntent = new Intent(this,
				AuthenticationActivity.class);
		startActivity(displayAuthenticationIntent);
	}

	private void displaySearchQuestion(View view) {
		Intent displaySearchIntent = new Intent(this, SearchActivity.class);
		startActivity(displaySearchIntent);
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
		mSearchButton = new Button(this);
		mSearchButton.setText(R.string.search_button_text);
		isOfflineCheckBox = new CheckBox(this);
		isOfflineCheckBox.setId(R.id.onlineModeCheckBox);
		isOfflineCheckBox.setText(R.string.offline_mode);
		isOfflineCheckBox.setChecked(!AppContext.getContext().isOnline());

		if (mIsLogged) {
			mLogButton.setText(R.string.log_out);
			mShowQuestionButton.setEnabled(true);
			mSubmitQuestionButton.setEnabled(true);
			mSearchButton.setEnabled(true);
			isOfflineCheckBox.setVisibility(View.VISIBLE);
		} else {
			mLogButton.setText(R.string.log_button);
			mShowQuestionButton.setEnabled(false);
			mSubmitQuestionButton.setEnabled(false);
			mSearchButton.setEnabled(false);
			isOfflineCheckBox.setVisibility(View.INVISIBLE);
		}

		mThis = this;
		mLogButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsLogged) {
					mIsLogged = false;

					UserStorage.getInstance(mThis).removeSessionID();

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
		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displaySearchQuestion(v);

			}
		});

		isOfflineCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						emitter.emit(new ConnectionEvent(
								ConnectionEventType.OFFLINE_CHECKBOX_CLICKED));
						// offline to online
						if (!isChecked) {
							emitter.emit(new OnlineEvent());
						} else {
							// TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
						}
					}
				});
		mLinearLayout.addView(mLogButton);
		mLinearLayout.addView(mShowQuestionButton);
		mLinearLayout.addView(mSubmitQuestionButton);
		mLinearLayout.addView(mSearchButton);
		mLinearLayout.addView(isOfflineCheckBox);

	}
	
	public static void setIsLogged(boolean isLogged) {
		mIsLogged = isLogged;
	}

	public static boolean isLogged() {
		return mIsLogged;
	}
}
