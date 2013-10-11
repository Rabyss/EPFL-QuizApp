package epfl.sweng.test;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.editquestions.EditQuestionActivity;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.testing.*;

public class EditQuestionActivityTest 
    extends ActivityInstrumentationTestCase2<EditQuestionActivity>
{
    
    Solo solo;

    public EditQuestionActivityTest() {
        super(EditQuestionActivity.class);
    }
    
    @Override
    protected void setUp() {
	//solo = new Solo(getInstrumentation());
    }

    public void testSubmitButton() {
	
    }
}
