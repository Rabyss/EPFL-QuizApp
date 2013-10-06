package epfl.sweng.ui;

import java.util.Observable;
import java.util.Observer;

import epfl.sweng.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import epfl.sweng.entry.MainActivity;

/**
 * Contains common treatments of activities dealing with quiz questions. 
 *
 */
public abstract class QuestionActivity extends Activity implements Observer {

    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
    }
    
    @Override
    public void onBackPressed() {
        Intent displayActivitxIntent = new Intent(this, MainActivity.class);
        startActivity(displayActivitxIntent);
    }

    @Override
    public void update(Observable observable, Object data) {
        
        //Checks whether the update concern the currrent activity
        if (mustTakeAccountOfUpdate()) {
            
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
     * Checks whether the current activity is concerned by an update from Observable(s).
     * @return true if the current activity must take the update in account
     */
    protected abstract boolean mustTakeAccountOfUpdate();
    
    /**
     * Process the data after download.
     * @param data the data
     */
    protected abstract void processDownloadedData(Object data);
    
    protected void showProgressDialog() {
        progressDialog.show();
    }
    
    protected void hideProgressDialog() {
        progressDialog.dismiss();
    }
    
}
