package epfl.sweng.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import epfl.sweng.authentication.UserStorage;
import epfl.sweng.context.AppContext;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.events.EventListener;
import epfl.sweng.services.ConnexionErrorEvent;

/**
 * Contains common treatments of activities dealing with quiz questions.
 */
public abstract class QuestionActivity extends Activity implements
        EventListener {

    private ProgressDialog progressDialog;
    private Handler handler;

    protected final static int TOAST_DISPLAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        
        handler = new Handler();

        AppContext.getContext().setSessionID(
                UserStorage.getInstance(this).getSessionID());
    }
    
    public Handler getHandler() {
		return handler;
	}

    @Override
    public void onBackPressed() {
        Intent displayActivitxIntent = new Intent(this, MainActivity.class);
        startActivity(displayActivitxIntent);
    }

    protected abstract void serverFailure();

    protected void showProgressDialog() {
        progressDialog.show();
    }

    protected void hideProgressDialog() {
    	getHandler().post(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
			}
		});
    }

    public void on(ConnexionErrorEvent event) {
        hideProgressDialog();
        serverFailure();
    }
}
