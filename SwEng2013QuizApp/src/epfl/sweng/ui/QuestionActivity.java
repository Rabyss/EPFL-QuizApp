package epfl.sweng.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

    protected final static int TOAST_DISPLAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        AppContext.getContext().setSessionID(
                UserStorage.getInstance(this).getSessionID());
    }

    @Override
    public void onBackPressed() {
        Intent displayActivitxIntent = new Intent(this, MainActivity.class);
        startActivity(displayActivitxIntent);
    }

    protected abstract void serverFailure();

    protected void showProgressDialog() {
    	progressDialog.show();
    	System.out.println("Triggered: " + progressDialog.isShowing());
    }

    protected void hideProgressDialog() {
    	System.out.println("Before: " + progressDialog.isShowing());
    	progressDialog.dismiss();
    	System.out.println("After: " + progressDialog.isShowing());
    }

    public void on(ConnexionErrorEvent event) {
        hideProgressDialog();
        serverFailure();
    }
}
