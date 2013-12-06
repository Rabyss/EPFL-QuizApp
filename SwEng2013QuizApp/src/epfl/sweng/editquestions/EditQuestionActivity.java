package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import epfl.sweng.R;
import epfl.sweng.proxy.PostConnectionErrorEvent;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.services.ServiceFactory;
import epfl.sweng.services.SuccessfulSubmitEvent;
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
        ((Button) findViewById(R.id.buttonSubmitQuestion)).setEnabled(false);

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

    public void addAnswer(View view) throws MalformedEditorButtonException {
        ViewGroup linearLayout = (ViewGroup) findViewById(R.id.linearLayoutAnswers);
        answers.add(new AnswerEditor(this, linearLayout, false));
        updateSubmitButton();
    }

    public ArrayList<AnswerEditor> getAnswers() throws MalformedEditorButtonException {
        return answers;
    }

    public boolean isResettingUI() {
        return resettingUI;
    }

    public void submitQuestion(View view) throws MalformedEditorButtonException {
        if (auditErrors() > 0) {
            return;
        }

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        showProgressDialog(); // PUT THIS BEFOR EXECUTE
        ServiceFactory.getServiceFor(this).execute();
    }

    public void on(SuccessfulSubmitEvent event) {
        hideProgressDialog();
        Toast.makeText(this, R.string.successful_submit, TOAST_DISPLAY_TIME)
                .show();

        clearFields();
        TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
    }

    public void on(PostConnectionErrorEvent event) {
        super.on(event);
        clearFields();
    }

    public void updateSubmitButton() {
        QuizQuestion quizQuestion = extractQuizQuestion();
        if (quizQuestion.auditErrors() == 0) {
            ((Button) findViewById(R.id.buttonSubmitQuestion)).setEnabled(true);
        } else {
            ((Button) findViewById(R.id.buttonSubmitQuestion))
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
        String[] tags = (tagsText.trim().isEmpty()) ? new String[0]
                : cleanUp(tagsText.trim().split("\\W+"));

        Set<String> tagsSet = new HashSet<String>();
        for (String tag : tags) {
            tagsSet.add(tag);
        }

        QuizQuestion quizQuestion = new QuizQuestion(question, answersText,
                solutionIndex, tagsSet, -1, null);
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
    protected void serverFailure() {
        Toast.makeText(this, R.string.submit_server_failure, Toast.LENGTH_LONG)
                .show();
        TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);

    }

    /**
     * Test when texts change
     */
    private final class EditTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            // Nothing to do

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // Nothing to do

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!resettingUI) {
                updateSubmitButton();
                TestCoordinator.check(TTChecks.QUESTION_EDITED);
            }

        }
    }

    public int auditErrors() {
        int errors = 0;

        errors += auditAnswers();
        errors += auditButtons();
        errors += auditEditTexts();
        errors += auditSubmitButton();

        return errors;
    }

    private int auditButtons() {
        int errors = 0;

        if (!"+".equals(((Button) findViewById(R.id.buttonAddAnswer)).getText())) {
            errors++;
        } else if ((findViewById(R.id.buttonAddAnswer)).getVisibility() != View.VISIBLE) {
            errors++;
        }

        if (!"Submit".equals(((Button) findViewById(R.id.submitSearchButton))
                .getText())) {
            errors++;
        } else if ((findViewById(R.id.buttonSubmitQuestion)).getVisibility() != View.VISIBLE) {
            errors++;
        }

        for (AnswerEditor answer : answers) {
            Button removeBtn = answer.getRemoveButton();
            Button correctBtn = answer.getCorrectButton();

            if (!removeBtn.getText().equals("-")) {
                errors++;
            } else if (View.VISIBLE != removeBtn.getVisibility()) {
                errors++;
            }

            if (View.VISIBLE != removeBtn.getVisibility()) {
                errors++;
            } else if (!correctBtn.getText().equals("\u2718")
                    && !correctBtn.getText().equals("\u2714")) {
                errors++;
            }
        }

        return errors;
    }

    private int auditAnswers() {
        int tickCount = 0;
        for (AnswerEditor answer : answers) {
            String answerText = answer.getCorrectButton().getText().toString();
            if (answerText.equals(getString(R.string.button_check))) {
                tickCount++;
            }
        }
        return tickCount <= 1 ? 0 : 1;
    }

    private int auditEditTexts() {
        int totalErrors = 0;

        EditText questionText = (EditText) findViewById(R.id.editQuestionText);
        EditText tagsText = (EditText) findViewById(R.id.editTags);

        if (questionText == null
                || !questionText.getHint().equals(
                        getResources().getText(R.string.hint_question_text))
                || questionText.getVisibility() != View.VISIBLE) {
            totalErrors++;
        }

        if (tagsText == null
                || !tagsText.getHint().equals(
                        getResources().getText(R.string.edit_tags))
                || tagsText.getVisibility() != View.VISIBLE) {
            totalErrors++;
        }

        if (answers.size() < 0) {
            totalErrors++;
        } else {
            for (AnswerEditor answer : answers) {
                if (!answer
                        .getEditText()
                        .getHint()
                        .equals(getResources().getText(
                                R.string.edit_answer_text))
                        || answer.getEditText().getVisibility() != View.VISIBLE) {
                    totalErrors++;
                }
            }
        }
        return totalErrors;
    }

    private int auditSubmitButton() {
        int errors = 0;
        QuizQuestion quizQuestion = extractQuizQuestion();

        if (findViewById(R.id.buttonSubmitQuestion).isEnabled()
                && quizQuestion.auditErrors() != 0) {
            errors = 1;
        }
        if (!findViewById(R.id.buttonSubmitQuestion).isEnabled()
                && quizQuestion.auditErrors() == 0) {
            errors = 1;
        }

        return errors;
    }

    public QuizQuestion getQuestion() {
        QuizQuestion quizQuestion = extractQuizQuestion();
        if (quizQuestion.auditErrors() == 0) {
            return quizQuestion;
        } else {
            return null;
        }
    }

    @Override
    protected void clientFailure() {
        TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
        Toast.makeText(this, getString(R.string.submit_server_failure),
                Toast.LENGTH_LONG).show();
    }

    private void clearFields() {
        // Reset UI
        resettingUI = true;
        ((EditText) findViewById(R.id.editQuestionText)).setText("");
        ((EditText) findViewById(R.id.editTags)).setText("");
        ((Button) findViewById(R.id.buttonSubmitQuestion)).setEnabled(false);
        while (answers.size() > 1) {
            answers.get(answers.size() - 1).remove();
        }
        answers.get(0).resetContent();
        answers.get(0).setCorrect(false);
        resettingUI = false;
    }
}
