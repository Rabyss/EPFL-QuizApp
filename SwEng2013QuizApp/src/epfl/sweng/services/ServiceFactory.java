package epfl.sweng.services;

import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;

public class ServiceFactory {

    public static Service getServiceFor(EditQuestionActivity activity) {
        return new QuestionPublisherService(activity);
    }

    public static Service getServiceFor(ShowQuestionsActivity activity) {
        return new QuestionFetcherService(activity);
    }

}
