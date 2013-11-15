package epfl.sweng.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import epfl.sweng.R;
import epfl.sweng.authentication.UserStorage;
import epfl.sweng.context.AppContext;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.events.EventListener;
import epfl.sweng.services.ClientErrorEvent;
import epfl.sweng.services.ConnectionErrorEvent;

/**
 * Contains common treatments of activities dealing with quiz questions.
 */
public abstract class QuestionActivity extends Activity implements
        EventListener {

    private ProgressDialog progressDialog;

    protected final static int TOAST_DISPLAY_TIME = 4000;
    
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
    protected abstract void clientFailure();
    
    protected void showProgressDialog() {
    	progressDialog.show();
    }

    protected void hideProgressDialog() {
    	progressDialog.dismiss();
    }

    public void on(ConnectionErrorEvent event) {
        hideProgressDialog();
        serverFailure();
    }
    
    public void on(ClientErrorEvent event) {
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.client_error), TOAST_DISPLAY_TIME).show();
        clientFailure();
    }
}
