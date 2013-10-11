package epfl.sweng.test;

import org.junit.Test;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.editquestions.EditQuestionActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;


public class EditQuestionActivityTest 
    extends ActivityInstrumentationTestCase2<EditQuestionActivity>
{
    
    private static final String SUBMIT_BUTTON_TEXT = "Submit";
    private static final String QUESTION_EDITOR_TEXT = "Type in the question's text body";
    
    private Solo solo;

    public EditQuestionActivityTest() {
        super(EditQuestionActivity.class);
    }
    
    @Override
    protected void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testSubmitButton() {
        assertTrue("Submit button cannot be found.", solo.searchButton(SUBMIT_BUTTON_TEXT));
        Button submitButton = solo.getButton(SUBMIT_BUTTON_TEXT);
        
        assertFalse("Submit button must be disabled at first.", submitButton.isEnabled());
        
        assertTrue("Question editor cannot be found.", solo.searchEditText(QUESTION_EDITOR_TEXT));
        
    }
}
