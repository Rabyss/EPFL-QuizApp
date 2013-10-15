package epfl.sweng.test;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;


public class EditQuestionActivityTest 
    extends ActivityInstrumentationTestCase2<EditQuestionActivity>
{
    
    private static final String SUBMIT_BUTTON_TEXT = "Submit";
    private static final String QUESTION_EDITOR_TEXT = "Type in the question's text body";
    private static final String ANSWER_EDITOR_TEXT = "Type in the answer";
    private static final String PLUS_BUTTON_TEXT = "+";
    private static final String MINUS_BUTTON_TEXT = "-";
    private static final String TAGS_EDITOR_TEXT = "Type in the question's tags";
    private static final String FALSE_ANSWER_BUTTON = "\u2718";
    private static final String TRUE_ANSWER_BUTTON_TEXT = "\u2714";
    private Solo solo;

    public EditQuestionActivityTest() {
        super(EditQuestionActivity.class);
    }
    
    @Override
    protected void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testTags() {
        fillQuestionBody("Question body");
        fillNextAnswerBody("First answer");
        
        assertTrue("+ button cannot be found.", solo.searchButton(PLUS_BUTTON_TEXT));
        solo.clickOnButton(PLUS_BUTTON_TEXT);
        waitForChange();
        
        fillNextAnswerBody("Second answer");
        assertTrue(FALSE_ANSWER_BUTTON + " button cannot be found.", solo.searchButton(FALSE_ANSWER_BUTTON));
        solo.clickOnButton(FALSE_ANSWER_BUTTON);
        
        assertTrue("No submit button can be found.", solo.searchButton(SUBMIT_BUTTON_TEXT));
        Button submitButton = solo.getButton(SUBMIT_BUTTON_TEXT);
        
        assertTrue("Tags editor cannot be found.", solo.searchEditText(TAGS_EDITOR_TEXT));
        EditText tagsEditor = solo.getEditText(TAGS_EDITOR_TEXT);
        
        solo.typeText(tagsEditor, "        ");
        assertFalse("Submit button should not be enabled when tags are just spaces.", submitButton.isEnabled());
        solo.clearEditText(tagsEditor);
        
        solo.typeText(tagsEditor, "\t");
        assertFalse("Submit button should not be enabled when tags are just tabs.", submitButton.isEnabled());
        solo.clearEditText(tagsEditor);
        
        solo.typeText(tagsEditor, "\n\n\n");
        assertFalse("Submit button should not be enabled when tags are just \\n.", submitButton.isEnabled());
        solo.clearEditText(tagsEditor);
        
        solo.typeText(tagsEditor, "\t\n\n   ");
        assertFalse("Submit button should not be enabled when tags are just spaces of different sorts.", submitButton.isEnabled());
        solo.clearEditText(tagsEditor);
        
        solo.typeText(tagsEditor, " , ");
        assertFalse("Submit button should not be enabled when tags are just spaces and commas.", submitButton.isEnabled());
        solo.clearEditText(tagsEditor);
        
        solo.typeText(tagsEditor, "a b");
        assertTrue("Submit button should be enabled when tags are valid ones.", submitButton.isEnabled());
        solo.clearEditText(tagsEditor);
    }
    
    public void testRemovingAllAnswers() {
        
        try {
            assertTrue("No button to delete the answer can be found.", solo.searchButton(MINUS_BUTTON_TEXT, 1));
            solo.clickOnButton(MINUS_BUTTON_TEXT);
            waitForChange();
            assertTrue(true);
        } catch (Exception e) {
            fail("No exception must be thrown if all answers are removed.");
        }
    }
    
    public void testCheckAndUncheck() {
        
        fillQuestionBody("Question body");
        
        assertFalse("A question is already checked at first.", solo.searchButton(TRUE_ANSWER_BUTTON_TEXT));
        assertTrue("No button to check the answer exists.", solo.searchButton(FALSE_ANSWER_BUTTON));
        
        solo.clickOnButton(PLUS_BUTTON_TEXT);
        waitForChange();
        solo.clickOnButton(PLUS_BUTTON_TEXT);
        waitForChange();
        assertTrue("The + button does not add answer as expected.", solo.searchText(ANSWER_EDITOR_TEXT, 3));
        
        solo.clickOnButton(FALSE_ANSWER_BUTTON);
        waitForChange();
        assertTrue("No checked question after having checked one.", solo.searchButton(TRUE_ANSWER_BUTTON_TEXT));
        
        solo.clickOnButton(FALSE_ANSWER_BUTTON);
        assertFalse("Two question can be checked at the same time.", solo.searchButton(TRUE_ANSWER_BUTTON_TEXT, 2));
        assertTrue("Checking two question is equivalent to check no question.", solo.searchButton(TRUE_ANSWER_BUTTON_TEXT));
    }
    
    public void testSubmitButton() throws InterruptedException {
        
        //Make sure button is not enabled at first
        assertTrue("Submit button cannot be found.", solo.searchButton(SUBMIT_BUTTON_TEXT));
        Button submitButton = solo.getButton(SUBMIT_BUTTON_TEXT);
        assertFalse("Submit button must be disabled at first.", submitButton.isEnabled());
        
        //Fill the question and make sure the submit button is still inactive
        fillQuestionBody("Quesiton body");
        assertFalse("Editing the question is not sufficient to get the submit button active.", submitButton.isEnabled());
        
        //Fill only one answer and make sure the button is still inactive
        assertTrue("No answer editor can be found at first.", solo.searchEditText(ANSWER_EDITOR_TEXT));
        fillNextAnswerBody("First answer body");
        
        //Fill a second answer and make sure that the button is still inactive because no tags
        assertTrue("+ button cannot be found.", solo.searchButton(PLUS_BUTTON_TEXT));
        solo.clickOnButton(PLUS_BUTTON_TEXT);
        waitForChange();
        
        assertTrue("No new answer editor can be found after pressing the + button.", solo.searchEditText(ANSWER_EDITOR_TEXT));
        String secondAnswerBody = "Second answer body";
        fillNextAnswerBody(secondAnswerBody);
        assertFalse("The submit button can become enabled with no tags", submitButton.isEnabled());
        
        //Fill tags
        assertTrue("Tags editor cannot be found.", solo.searchEditText(TAGS_EDITOR_TEXT));
        EditText tagsEditor = solo.getEditText(TAGS_EDITOR_TEXT);
        solo.typeText(tagsEditor, "      ");
        assertFalse("The submit button can become enabled with only spaces as tags.", submitButton.isEnabled());
        
        solo.typeText(tagsEditor, "tag1 tag2");
        assertFalse("The submit button can become enabled when no answer is marked as true.", submitButton.isEnabled());
        
        assertTrue(FALSE_ANSWER_BUTTON+" cannot be found.", solo.searchButton(FALSE_ANSWER_BUTTON));
        solo.clickOnButton(FALSE_ANSWER_BUTTON);
        waitForChange();
        assertTrue("The submit button does not become enabled when it should.", submitButton.isEnabled());
    
        clearEditText(secondAnswerBody);
        assertFalse("The submit button can become enabled with a empty answer.", submitButton.isEnabled());
    }
    
    private void fillQuestionBody(String questionBody) {
        assertTrue("Question editor cannot be found.", solo.searchEditText(QUESTION_EDITOR_TEXT));
        EditText questionEditor = solo.getEditText(QUESTION_EDITOR_TEXT);
        solo.typeText(questionEditor, questionBody);
    }
    
    private void fillNextAnswerBody(String answerBody) {
        fillAnswerBody(ANSWER_EDITOR_TEXT, answerBody);
    }
    
    private void fillAnswerBody(String currentAnswerBody, String nextAnswerBody) {
        assertTrue("No answer editor can be found.", solo.searchEditText(currentAnswerBody));
        EditText firstAnswerEditor = solo.getEditText(currentAnswerBody);
        solo.typeText(firstAnswerEditor, nextAnswerBody);
    }
    
    private void clearEditText(String currentBody) {
        assertTrue("No editor can be found with the body '"+currentBody+"'", solo.searchEditText(currentBody));
        solo.clearEditText(solo.getEditText(currentBody));
    }
    
    private void waitForChange() {
        getActivityAndWaitFor(TestCoordinator.TTChecks.QUESTION_EDITED);
    }
    
    private void getActivityAndWaitFor(final TestCoordinator.TTChecks expected) {
        TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
            @Override
            public void initiate() {
                getActivity();
            }

            @Override
            public void verify(TestCoordinator.TTChecks notification) {
                assertEquals(String.format(
                        "Expected notification %s, but received %s", expected,
                        notification), expected, notification);
            }

            @Override
            public String toString() {
                return String.format("getActivityAndWaitFor(%s)", expected);
            }
        });
      }
}
