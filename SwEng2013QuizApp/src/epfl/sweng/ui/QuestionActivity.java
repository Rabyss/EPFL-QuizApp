package epfl.sweng.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import epfl.sweng.authentication.UserStorage;
import epfl.sweng.context.AppContext;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerEvent;
import epfl.sweng.servercomm.ServerResponse;

/**
 * Contains common treatments of activities dealing with quiz questions.
 */
public abstract class QuestionActivity extends Activity implements
        EventListener {

    private ProgressDialog progressDialog;

    protected final static int TOAST_DISPLAY_TIME = 2000;
    private final static int HTTP_ERROR_THRESHOLD = 400;
    private RequestContext mReqContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        
        mReqContext = new RequestContext();

        AppContext.getContext().setSessionID(UserStorage.getInstance(this).getSessionID());
        mReqContext.addHeader("Authorization", "Tequila "+AppContext.getContext().getSessionID());

        ServerCommunicator.getInstance().addListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent displayActivitxIntent = new Intent(this, MainActivity.class);
        startActivity(displayActivitxIntent);
    }

    public void processEvent(ServerEvent event) {

        ServerResponse data = event.getResponse();

        // Checks whether the update concern the currrent activity
        hideProgressDialog();

        if (data != null && data.getStatusCode() < HTTP_ERROR_THRESHOLD) {
            processDownloadedData(data);
        } else {
            serverFailure();
        }
    }

    protected abstract void serverFailure();

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

    protected RequestContext getRequestContext() {
        return mReqContext;
    }

}
