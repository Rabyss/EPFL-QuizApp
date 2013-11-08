package epfl.sweng.services;

import epfl.sweng.events.Event;
import epfl.sweng.quizquestions.QuizQuestion;

public class ShowQuestionEvent extends Event {

    private static final long serialVersionUID = 934118282185080517L;

    private QuizQuestion mQuizQuestion;

    public ShowQuestionEvent(QuizQuestion quizQuestion) {
        mQuizQuestion = quizQuestion;
    }
    
    public QuizQuestion getQuizQuestion() {
        return mQuizQuestion;
    }

}
