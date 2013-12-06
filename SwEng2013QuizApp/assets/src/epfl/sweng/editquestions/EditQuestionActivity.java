package epfl.sweng.editquestions;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import epfl.sweng.ui.AnswerEditor;
import epfl.sweng.ui.QuestionActivity;

/**
 * 
 * Activity to edit questions
 * 
 */
public class EditQuestionActivity extends QuestionActivity {

    private ArrayList<AnswerEditor> answers;

    private static final int TOAST_DISPLAY_TIME = 2000;
    private boolean resettingUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        ServerCommunicator.getInstance().addObserver(this);

        answers = new ArrayList<AnswerEditor>();
        answers.add(new AnswerEditor(this,
                (ViewGroup) findViewById(R.id.linearLayoutAnswers), true));

        findViewById(R.id.editQuestionText).requestFocus();
        ((EditText) findViewById(R.id.editQuestionText)).addTextChangedListener(new EditTextWatcher());
        ((EditText) findViewById(R.id.editTags)).addTextChangedListener(new EditTextWatcher());
        ((Button) findViewById(R.id.butttonSubmitQuestion)).setEnabled(false);

        // let the testing infrastructure know that edit question has been
        // initialized
        TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_question, menu);
        return true;
    }

    public void addAnswer(View view) {
        ViewGroup linearLayout = (ViewGroup) findViewById(R.id.linearLayoutAnswers);
        answers.add(new AnswerEditor(this, linearLayout, false));
    }

    public ArrayList<AnswerEditor> getAnswers() {
        return answers;
    }
    
    public boolean isResettingUI() {
        return resettingUI;
    }

    public void submitQuestion(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        QuizQuestion quizQuestion = extractQuizQuestion();

        try {
            quizQuestion.audit();
            ServerCommunicator.getInstance().submitQuizQuestion(quizQuestion,
                    this);

            showProgressDialog();

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), TOAST_DISPLAY_TIME).show();
        }

    }
    
    public void tryAudit() {
        QuizQuestion quizQuestion = extractQuizQuestion();
        try {
            quizQuestion.audit();
            ((Button) findViewById(R.id.butttonSubmitQuestion)).setEnabled(true);
        } catch (IllegalArgumentException e) {
            ((Button) findViewById(R.id.butttonSubmitQuestion)).setEnabled(false);
        }
    }

    private QuizQuestion extractQuizQuestion() {
        String question = ((EditText) findViewById(R.id.editQuestionText))
                .getText().toString();

        ArrayList<String> answersText = new ArrayList<String>();
        int solutionIndex = 0;

        for (AnswerEditor answer : answers) {
            answersText.add(answer.getContent());
            if (answer.isCorrect()) {
                solutionIndex = answers.indexOf(answer);
            }
        }

        String tagsText = ((EditText) findViewById(R.id.editTags)).getText()
                .toString();
        String[] tags = (tagsText.trim().isEmpty()) ? null : cleanUp(tagsText.trim()
                .split("\\W+"));

        QuizQuestion quizQuestion = new QuizQuestion(null, question,
                answersText.toArray(new String[answers.size()]), solutionIndex,
                tags, null);
        return quizQuestion;
    }
    private String[] cleanUp(String[] strArray) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i] != null && !strArray[i].equals("")) {
                result.add(strArray[i]);
            }
        }
        
        return result.toArray(new String[result.size()]);
    }
    @Override
    protected boolean mustTakeAccountOfUpdate() {
        return ServerCommunicator.getInstance().isSubmittingQuestion();
    }

    @Override
    protected void processDownloadedData(Object data) {
        Toast.makeText(this, R.string.successful_submit, TOAST_DISPLAY_TIME)
                .show();
        TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);
        
        // Reset UI
        resettingUI = true;
        ((EditText) findViewById(R.id.editQuestionText)).setText("");
        ((EditText) findViewById(R.id.editTags)).setText(""); 
        ((Button) findViewById(R.id.butttonSubmitQuestion)).setEnabled(false);
        while (answers.size() > 1) {
            answers.get(answers.size()-1).remove();
        }
        answers.get(0).resetContent();
        resettingUI = false;
        TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);
    }

    /**
     * Test when texts change
     */
    private final class EditTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            
        }
    
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // TODO Auto-generated method stub
            
        }
    
        @Override
        public void afterTextChanged(Editable s) {
            if (!resettingUI) {
                tryAudit();
                TestingTransactions.check(TTChecks.QUESTION_EDITED);
            }
            
        }
    }

}
