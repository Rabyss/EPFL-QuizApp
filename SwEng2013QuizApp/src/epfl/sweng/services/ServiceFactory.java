package epfl.sweng.services;

import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;

public class ServiceFactory {

	private static QuestionPublisherService publisher = null;
	private static QuestionFetcherService fetcher = null;
    public static Service getServiceFor(EditQuestionActivity activity) {
    	if(publisher == null){
    		publisher = new QuestionPublisherService(activity);
    	} else {
    		publisher.setActivity(activity);
    	}
        return publisher;
    }

    public static Service getServiceFor(ShowQuestionsActivity activity) {
    	if(fetcher == null){
    		fetcher = new QuestionFetcherService(activity);
    	} else {
    		fetcher.setActivity(activity);
    	}
        return fetcher;
    }

}
