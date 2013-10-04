package epfl.sweng.ui;

import java.util.ArrayList;

import epfl.sweng.R;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
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
		if (first) {
			mCorrectButton.setText(R.string.button_check);
			mCorrectButton.setEnabled(false);
			mCorrect = true;
		} else {
			mCorrectButton.setText(R.string.button_cross);
			mCorrect = false;
		}
		mCorrectButton.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		linearLayout.addView(mCorrectButton);

		mCorrectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<AnswerEditor> answers = activity.getAnswers();
				for (AnswerEditor a : answers) {
					if (a.isCorrect()) {
						a.setCorrect(false);
					}
				}
				setCorrect(true);
			}
		});

		// Create Button remove
		mRemoveButton = new Button(activity);
		mRemoveButton.setText(R.string.button_remove);
		linearLayout.addView(mRemoveButton);

		if (first) {
			mRemoveButton.setEnabled(false);
		} else {
			activity.getAnswers().get(0).mRemoveButton.setEnabled(true);
		}

		mRemoveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				remove();
				TestingTransactions.check(TTChecks.QUESTION_EDITED);
			}
		});
		
		TestingTransactions.check(TTChecks.QUESTION_EDITED);
	}

	public String getContent() {
		return mContent.getText().toString();
	}

	public boolean isCorrect() {
		return mCorrect;
	}

	public void setCorrect(Boolean correct) {
		if (correct) {
			mCorrectButton.setText(R.string.button_check);
			mCorrectButton.setEnabled(false);
			mCorrect = true;
		} else {
			mCorrectButton.setText(R.string.button_cross);
			mCorrectButton.setEnabled(true);
			mCorrect = false;
		}
		
		TestingTransactions.check(TTChecks.QUESTION_EDITED);
	}

	public Button getRemoveButton() {
		return mRemoveButton;
	}

	public void remove() {
		ArrayList<AnswerEditor> answers = mActivity.getAnswers();
		answers.remove(this);

		if (this.isCorrect()) {
			answers.get(0).setCorrect(true);
		}

		if (answers.size() == 1) {
			answers.get(0).mRemoveButton.setEnabled(false);
		}
		((ViewGroup) linearLayout.getParent()).removeView(linearLayout);
		
	}

	public void resetContent() {
		mContent.setText("");
		
	}
/**
 *Test when the text of an answer change
 */
	private final class AnswerTextWatcher implements TextWatcher {
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
			if (!mActivity.isResettingUI()) {
				TestingTransactions.check(TTChecks.QUESTION_EDITED);
			}
		}
	}
}
