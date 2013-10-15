package epfl.sweng.ui;

import epfl.sweng.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerEvent;

/**
 * Contains common treatments of activities dealing with quiz questions.
 */
public abstract class QuestionActivity extends Activity implements EventListener {

    private ProgressDialog progressDialog;
    
    private boolean waitingForServer = false;
    
    protected final static int TOAST_DISPLAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        
        ServerCommunicator.getInstance().addListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent displayActivitxIntent = new Intent(this, MainActivity.class);
        startActivity(displayActivitxIntent);
    }

    public void processEvent(ServerEvent event) {
    	
    	String data = event.getResponse();

        // Checks whether the update concern the currrent activity
        if (mustTakeAccountOfUpdate()) {
            setWaiting(false);
            hideProgressDialog();

            if (data != null) {
                processDownloadedData(data);
            } else {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

                dlgAlert.setMessage(R.string.error_server_comm);
                dlgAlert.setTitle(R.string.error_failure);
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {

                            }
                        });
            }
        }
    }

    /**
     * Checks whether the current activity is concerned by an update from
     * Observable(s).
     * 
     * @return true if the current activity must take the update in account
     */
    protected boolean mustTakeAccountOfUpdate() {
        return waitingForServer;
    }

    /**
     * Sets the waitingForServer boolean.
     * 
     * @param isWaiting
     */
    protected void setWaiting(boolean isWaiting) {
        waitingForServer = isWaiting;
    }

    /**
     * Process the data after download.
     * 
     * @param data
     *            the data
     */
    protected abstract void processDownloadedData(Object data);

    protected void showProgressDialog() {
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        progressDialog.dismiss();
    }

}
