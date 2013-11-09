package epfl.sweng.services;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

import epfl.sweng.context.AppContext;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.editquestions.PostedQuestionEvent;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.quizquestions.MalformedQuestionException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerResponse;

public class QuestionPublisherService extends EventEmitter implements Service,
        EventListener {
	private EventListener mListener;
    private EditQuestionActivity mActivity;

    public QuestionPublisherService(EditQuestionActivity activity) {
        addListener(activity);
        mActivity = activity;
        this.mListener = activity;
        Proxy.getInstance().addListener(this);
    }

    @Override
    public void execute() {
        RequestContext reqContext = new RequestContext();
        QuizQuestion quizQuestion = mActivity.getQuestion();
        reqContext.addHeader("Authorization", "Tequila "
                + AppContext.getContext().getSessionID());
        reqContext.setServerURL(ServerCommunicator.SWENG_SUBMIT_QUESTION_URL);
        try {
            reqContext.setEntity(new StringEntity(quizQuestion.toJSON()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedQuestionException e) {
            e.printStackTrace();
        }
        reqContext.addHeader("Content-type", "application/json");

        Proxy.getInstance().doHttpPost(reqContext, new PostedQuestionEvent());

    }

    public void on(PostedQuestionEvent event) {
        ServerResponse response = event.getResponse();
        int status = response.getStatusCode();
        if (status >= HttpStatus.SC_BAD_REQUEST) {
            this.emit(new ConnexionErrorEvent());
        } else {
            this.emit(new SuccessfulSubmitEvent());
        }
        removeListener(this.mListener);
    }
    
	 public void setListener(EventListener listener){
		 mListener = listener;
		 addListener(listener);
	 }
}
