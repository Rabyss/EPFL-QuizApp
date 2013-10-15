package epfl.sweng.editquestions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;

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
import epfl.sweng.MalformedQuestionException;
import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.ui.AnswerEditor;
import epfl.sweng.ui.QuestionActivity;

/**
 * 
 * Activity to edit questions
 * 
 */
public class EditQuestionActivity extends QuestionActivity {

    private ArrayList<AnswerEditor> answers;

    private boolean resettingUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        answers = new ArrayList<AnswerEditor>();
        answers.add(new AnswerEditor(this,
                (ViewGroup) findViewById(R.id.linearLayoutAnswers), true));

        findViewById(R.id.editQuestionText).requestFocus();
        ((EditText) findViewById(R.id.editQuestionText))
                .addTextChangedListener(new EditTextWatcher());
        ((EditText) findViewById(R.id.editTags))
                .addTextChangedListener(new EditTextWatcher());
        ((Button) findViewById(R.id.butttonSubmitQuestion)).setEnabled(false);

        // let the testing infrastructure know that edit question has been
        // initialized
        TestCoordinator.check(TTChecks.EDIT_QUESTIONS_SHOWN);
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
        tryAudit();
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
        RequestContext reqContext;
        try {
            reqContext = new RequestContext(
                    ServerCommunicator.SWENG_SUBMIT_QUESTION_URL,
                    new StringEntity(quizQuestion.toJSON()));
            reqContext.addHeader("Content-type", "application/json");
            ServerCommunicator.getInstance().doHttpPost(reqContext, new PostedQuestionEvent());
        } catch (MalformedQuestionException e) {
            Toast.makeText(this, e.getMessage(), TOAST_DISPLAY_TIME).show();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, e.getMessage(), TOAST_DISPLAY_TIME).show();
        }

        showProgressDialog();
    }
    
    public void on(PostedQuestionEvent event) {
    	super.processEvent(event);
    }

    public void tryAudit() {
        QuizQuestion quizQuestion = extractQuizQuestion();
        if (quizQuestion.audit() == 0) {
            ((Button) findViewById(R.id.butttonSubmitQuestion))
                    .setEnabled(true);
        } else {
            ((Button) findViewById(R.id.butttonSubmitQuestion))
                    .setEnabled(false);
        }
    }

    private QuizQuestion extractQuizQuestion() {
        String question = ((EditText) findViewById(R.id.editQuestionText))
                .getText().toString();

        ArrayList<String> answersText = new ArrayList<String>();
        int solutionIndex = -1;

        for (AnswerEditor answer : answers) {
            answersText.add(answer.getContent());
            if (answer.isCorrect()) {
                solutionIndex = answers.indexOf(answer);
            }
        }

        String tagsText = ((EditText) findViewById(R.id.editTags)).getText()
                .toString();
        String[] tags = (tagsText.trim().isEmpty()) ? null : cleanUp(tagsText
                .trim().split("\\W+"));

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
    protected void processDownloadedData(Object data) {
        Toast.makeText(this, R.string.successful_submit, TOAST_DISPLAY_TIME)
                .show();

        // Reset UI
        resettingUI = true;
        ((EditText) findViewById(R.id.editQuestionText)).setText("");
        ((EditText) findViewById(R.id.editTags)).setText("");
        ((Button) findViewById(R.id.butttonSubmitQuestion)).setEnabled(false);
        while (answers.size() > 1) {
            answers.get(answers.size() - 1).remove();
        }
        answers.get(0).resetContent();
        answers.get(0).setCorrect(false);
        resettingUI = false;
        TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
    }
    @Override
	protected void serverFailure() {
		Toast.makeText(this, R.string.submit_server_failure, Toast.LENGTH_LONG).show();
		TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
		
	}

    /**
     * Test when texts change
     */
    private final class EditTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
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
                TestCoordinator.check(TTChecks.QUESTION_EDITED);
            }

        }
    }
    
    

}
