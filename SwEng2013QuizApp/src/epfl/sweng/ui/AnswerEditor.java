package epfl.sweng.ui;

import java.util.ArrayList;

import epfl.sweng.R;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.editquestions.MalformedEditorButtonException;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * An answer as represented in the GUI
 */
public class AnswerEditor {
	private Button mCorrectButton;
	private Button mRemoveButton;
	private EditText mContent;
	private boolean mCorrect;
	private LinearLayout linearLayout;
	private EditQuestionActivity mActivity;

	public AnswerEditor(final EditQuestionActivity activity, ViewGroup view,
			boolean first) {

		mActivity = activity;
		// Create Layout
		linearLayout = new LinearLayout(activity);
		linearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		view.addView(linearLayout);

		// Create EditText
		mContent = new EditText(activity);
		mContent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		mContent.setHint(R.string.edit_answer_text);
		mContent.addTextChangedListener(new AnswerTextWatcher());
		linearLayout.addView(mContent);

		// Create Button Correct
		mCorrectButton = new Button(activity);

		mCorrectButton.setText(R.string.button_cross);
		mCorrect = false;

		mCorrectButton.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		linearLayout.addView(mCorrectButton);

		mCorrectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<AnswerEditor> answers;
				try {
					answers = activity.getAnswers();

					if (!isCorrect()) {
						for (AnswerEditor a : answers) {
							if (a.isCorrect()) {
								a.setCorrect(false);
							}
						}
						setCorrect(true);
					} else {
						setCorrect(false);
					}
				} catch (MalformedEditorButtonException e) {
					e.printStackTrace();
				}
				
				TestCoordinator.check(TTChecks.QUESTION_EDITED);
			}
		});

		// Create Button remove
		mRemoveButton = new Button(activity);
		mRemoveButton.setText(R.string.button_remove);
		linearLayout.addView(mRemoveButton);

		mRemoveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				remove();
				mActivity.updateSubmitButton();
				TestCoordinator.check(TTChecks.QUESTION_EDITED);
			}
		});

		if (!first) {
			TestCoordinator.check(TTChecks.QUESTION_EDITED);
		}

		mActivity.updateSubmitButton();
	}

	public String getContent() {
		return mContent.getText().toString();
	}
	
	public EditText getEditText() {
		return mContent;
	}

	public boolean isCorrect() {
		return mCorrect;
	}

    public void setCorrect(Boolean correct) {
		if (correct) {
			mCorrectButton.setText(R.string.button_check);
			mCorrect = true;
		} else {
			mCorrectButton.setText(R.string.button_cross);
			mCorrect = false;
		}
		mActivity.updateSubmitButton();
	}

	public Button getRemoveButton() {
		return mRemoveButton;
	}
	
	public Button getCorrectButton() {
		return mCorrectButton;
	}

	public void remove() {
		ArrayList<AnswerEditor> answers;
		try {
			answers = mActivity.getAnswers();
			answers.remove(this);
		} catch (MalformedEditorButtonException e) {
			e.printStackTrace();
		}

		((ViewGroup) linearLayout.getParent()).removeView(linearLayout);

	}

	public void resetContent() {
		mContent.setText("");

	}

	/**
	 * Test when the text of an answer change
	 */
	private final class AnswerTextWatcher implements TextWatcher {
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
			if (!mActivity.isResettingUI()) {
				mActivity.updateSubmitButton();
				TestCoordinator.check(TTChecks.QUESTION_EDITED);
			}
		}
	}
}
