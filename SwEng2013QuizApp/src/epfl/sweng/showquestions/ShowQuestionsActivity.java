package epfl.sweng.showquestions;

import org.json.JSONException;
import org.json.JSONObject;

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
	private static boolean mCorrectAnswerPressed=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		try {
			mRandomQuestion=new QuizQuestion(new JSONObject());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//mRandomQuestion= ServerCommunicator.getRandomQuestion();
		TextView question =new TextView(this);
		//String questionText=mRandomQuestion.getQuestion();
		question.setText("Are you my mummy?");
		linearLayout.addView(question);
		//int totalAnswer=mRandomQuestion.getAnswers().length;
		/*for (int i=0; i< totalAnswer; i++) {
			ShowAnswer answer=new ShowAnswer(mRandomQuestion.getAnswers()[i], mRandomQuestion.isSolution(i));
			answer.setId(i);
			linearLayout.addView(answer);
		}*/
		Button nextQuestion= new Button(this);
		nextQuestion.setText("Next Question");
		nextQuestion.setEnabled(true);
		nextQuestion.setVisibility(View.VISIBLE);
		nextQuestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayShowNewQuestion(v);
			}
		});
		
		linearLayout.addView(nextQuestion);
		setContentView(linearLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}
	public static void setCorrectAnswerPressed() {
		mCorrectAnswerPressed=true;
	}
	public void displayShowNewQuestion(View view) {
	    Toast.makeText(this, "You clicked diplay a new random question", Toast.LENGTH_SHORT).show();
	    Intent displayActivityIntent = new Intent(this, ShowQuestionsActivity.class);
	    startActivity(displayActivityIntent);
	}
}
