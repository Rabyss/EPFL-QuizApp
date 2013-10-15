package epfl.sweng.authentication;



import epfl.sweng.R;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class AuthenticationActivity extends Activity {
	private Authenticator mAuthenticator;
	private EditText mUsername;
	private EditText mPassword;
	private Button mLogin;
	private LinearLayout mLinearLayout;
	private boolean isAuthenticate;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayAuthentication();
    }

	@Override
    public void onBackPressed() {
        Intent displayActivitxIntent = new Intent(this, MainActivity.class);
        startActivity(displayActivitxIntent);
    }
	private void displayAuthentication() {
		mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
       
        mUsername = new EditText(this);
        mUsername.setHint(R.string.username);
        mLinearLayout.addView(mUsername);
        
        mPassword = new EditText(this);
        mPassword.setHint(R.string.password);
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mLinearLayout.addView(mPassword);
        
        mLogin = new Button(this);
        mLogin.setText(R.string.log_button);
        mLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAuthenticator = new Authenticator(mUsername.getText().toString(), mPassword.getText().toString());
				//TODO: send a authentication request
				//mAuthenticator.auth();
				//isAuthenticate = mAuthenticator.getIsAuthenticate();
				isAuthenticate = true;
				if (isAuthenticate) {
					MainActivity.setIsLogged(true);
					displayMainActivity(v);
				} else {
					clearEditField();
				}
				
			}
		});
        mLinearLayout.addView(mLogin);
        setContentView(mLinearLayout);
        TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
	}
	public void clearEditField() {
		mUsername.setText("");
		mPassword.setText("");
		TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
	}
	public void displayMainActivity(View view) {
		Intent displayMainActivityIntent = new Intent(this,
				MainActivity.class);
		startActivity(displayMainActivityIntent);
	}
}
