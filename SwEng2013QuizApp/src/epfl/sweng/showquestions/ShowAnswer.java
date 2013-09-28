package epfl.sweng.showquestions;

import java.lang.Character.UnicodeBlock;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
/**
 * Display an answer of a question
 *
 */
public class ShowAnswer extends View{
	private String mAnswer;
	private boolean mIsSolution;
	private LinearLayout mLayout;
	private Button button;
	private TextView correctness;
	private static Context mContext;
	
	public ShowAnswer(String answer, boolean isSolution) {
		super(mContext);
		this.mAnswer=answer;
		this.mIsSolution=isSolution;
		
		mLayout = new LinearLayout(mContext);
		mLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		if(mIsSolution){
			correctness.setText("\u2714");
		}else{
			correctness.setText("\u2718");
		}
		correctness.setVisibility(View.INVISIBLE);
		mLayout.addView(correctness);
		
		button.setText(mAnswer);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				correctness.setVisibility(v.VISIBLE);
				if(mIsSolution){
					ShowQuestionsActivity.setCorrectAnswerPressed();
				}
			}
			
		});
		mLayout.addView(button);
	}

}
