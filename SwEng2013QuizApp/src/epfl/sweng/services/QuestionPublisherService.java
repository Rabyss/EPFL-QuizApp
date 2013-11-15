package epfl.sweng.services;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import epfl.sweng.context.AppContext;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.editquestions.PostedQuestionEvent;
import epfl.sweng.events.EventListener;
import epfl.sweng.proxy.PostConnectionErrorEvent;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.quizquestions.MalformedQuestionException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;

public class QuestionPublisherService extends QuestionActivityService implements
        Service, EventListener {

    public QuestionPublisherService(EditQuestionActivity activity) {
        super(activity);
    }

    @Override
    public void execute() {
        RequestContext reqContext = new RequestContext();
        QuizQuestion quizQuestion = ((EditQuestionActivity) (super
                .getActivity())).getQuestion();
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
        if (event.getResponse() == null) {
            this.emit(new ClientErrorEvent());
        } else {
            this.emit(new SuccessfulSubmitEvent());
        }
        removeListener(super.getActivity());
    }

    public void on(PostConnectionErrorEvent event) {
        this.emit(event);
        removeListener(super.getActivity());
    }

    public void setActivity(EditQuestionActivity activity) {
        super.setActivity(activity);
    }
}
