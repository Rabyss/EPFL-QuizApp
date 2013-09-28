package epfl.sweng.showquestions;

import java.util.concurrent.ExecutionException;


import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.servercomm.ServerCommunicator;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Upload a new question and display it
 *
 */
public class ShowQuestionsActivity extends Activity {
	
	private QuizQuestion mRandomQuestion=null;
	private Button[] answer;
	private Button nextQuestion;
	private TextView[] correctness;
	private int indexButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateQuestion();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}
	/**
	 * Updates and displays a new random question
	 */
	public void updateQuestion() {
		//creates the main layout
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//uploads a random question from the server
		try {
			mRandomQuestion=ServerCommunicator.getInstance().getRandomQuestion();
		} catch (InterruptedException e) {
			e.printStackTrace();
			
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//initializes the title of the question
		TextView question =new TextView(this);
		question.setText(mRandomQuestion.getQuestion());
		linearLayout.addView(question);
		//initializes the button nextQuestion
		nextQuestion= new Button(this);
		nextQuestion.setText("Next Question");
		nextQuestion.setEnabled(false);
		nextQuestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateQuestion();
			}
		});
		
		int totalAnswer=mRandomQuestion.getAnswers().length;
		answer=new Button[totalAnswer];
		correctness=new TextView[totalAnswer];
		//initializes all the buttons of answer
		for (indexButton=0; indexButton<totalAnswer; indexButton++) {
			LinearLayout buttonLayout=new LinearLayout(this);
			buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
			buttonLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			correctness[indexButton] = new TextView(this);
			if (mRandomQuestion.isSolution(indexButton)) {
				correctness[indexButton].setText("\u2714");
			} else {
				correctness[indexButton].setText("\u2718");
			}
			correctness[indexButton].setVisibility(View.INVISIBLE);
			answer[indexButton] = new Button(this);
			answer[indexButton].setText(mRandomQuestion.getAnswers()[indexButton]);
			//answer[indexButton].setTag(mRandomQuestion.getTags()[indexButton]);
			answer[indexButton].setOnClickListener(new OnClickListener() {
				
					@Override
					public void onClick(View v) {
				
						int actualAnswer=0;
						for (int i=0; i<mRandomQuestion.getAnswers().length; i++) {
							if (v==answer[i]) {
								actualAnswer=i;
							}
						}
						correctness[actualAnswer].setVisibility(View.VISIBLE);
						if (mRandomQuestion.isSolution(actualAnswer)) {
							nextQuestion.setEnabled(true);
							for (int i=0; i<mRandomQuestion.getAnswers().length; i++) {
								answer[i].setEnabled(false);
								if (i != actualAnswer) {
									correctness[i].setVisibility(View.INVISIBLE);
								}
							}
						}
					}
				});
			buttonLayout.addView(answer[indexButton]);
			buttonLayout.addView(correctness[indexButton]);
			linearLayout.addView(buttonLayout);
		}
		linearLayout.addView(nextQuestion);
		setContentView(linearLayout);
	}
	
}
