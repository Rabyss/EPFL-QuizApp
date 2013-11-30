package epfl.sweng.searchquestions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.searchquestions.SearchQuery.InvalidSearchQueryException;
import epfl.sweng.searchquestions.parser.QueryParser;
import epfl.sweng.searchquestions.parser.QueryParser.QueryParserResult;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivity extends Activity {

	private EditText mEditQuery; // the edit text field where the user type the
									// request
	private Button mSearchButton; // the button the user press when he wants to
									// submit its search query
	private QueryParserResult mParserResult = null; // the result of the parsing
													// of the question
	private SearchActivity mSelf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mSelf =this;
		mEditQuery = (EditText) findViewById(R.id.editSearchQuery);
		mEditQuery.addTextChangedListener(new QueryWatcher());

		mSearchButton = (Button) findViewById(R.id.submitSearchButton);

		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				String shouldNotHappenMessage = "should not happen: invalid query in input and submit button activated";
				SearchQuery query;
				try {
					query = new SearchQuery(mSearchButton.getText().toString(),
							mParserResult, mSelf);
					
				} catch (InvalidSearchQueryException e) {
					throw new RuntimeException(shouldNotHappenMessage);
				}
				assert mParserResult != null;
				assert mParserResult.isDone();
				Proxy.getInstance(getApplicationContext()).giveAST(mParserResult.getAST());
				displayShowQuestionActivity(view);
			}

			
		});

	}
	public void displayShowQuestionActivity(View view) {
		Intent showQuestionIntent = new Intent(this, ShowQuestionsActivity.class);
		startActivity(showQuestionIntent);
	}
	

	@Override
	protected void onStart() {
		TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	private class QueryWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable editable) {

			// we activate the Search button if the query is a valid one
			QueryParserResult parserResult = QueryParser.parse(editable
					.toString());
			if (parserResult.isDone()) { // if the input is a valid query
				mParserResult = parserResult;
				mSearchButton.setEnabled(true);
			} else {
				mParserResult = null;
				mSearchButton.setEnabled(false);
			}
			TestCoordinator.check(TTChecks.QUERY_EDITED);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

	}


}
