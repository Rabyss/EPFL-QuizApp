package epfl.sweng.searchquestions;

import static epfl.sweng.util.StringHelper.containsNonWhitespaceCharacters;
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
import epfl.sweng.searchquestions.parser.QueryParser;
import epfl.sweng.searchquestions.parser.QueryParser.QueryParserResult;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivity extends Activity {

    private EditText mEditQuery;
// the edit text field where the user type the
    // request
    private Button mSearchButton;
// the button the user press when he wants to
    // submit its search query
    private QueryParserResult mParserResult = null;
// the result of the parsing

    // of the question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mEditQuery = (EditText) findViewById(R.id.editSearchQuery);
        mEditQuery.addTextChangedListener(new QueryWatcher());

        mSearchButton = (Button) findViewById(R.id.submitSearchButton);

        mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                assert mParserResult != null;
                assert mParserResult.isDone();
                Proxy.getInstance(getApplicationContext()).giveQuery(
                        mParserResult);
                displayShowQuestionActivity(view);
            }

        });

    }

    public void displayShowQuestionActivity(View view) {
        Intent showQuestionIntent = new Intent(this,
                ShowQuestionsActivity.class);
        startActivity(showQuestionIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    private class QueryWatcher implements TextWatcher {

        private static final String QUERY_CHAR_CLASS_REGEX = "^[a-zA-Z0-9\\(\\)\\*\\+ ]+$";
        private static final int MAX_QUERY_LENGTH = 500;

        @Override
        public void afterTextChanged(Editable editable) {

            String queryStr = editable.toString();
            // we activate the Search button if the query is a valid one
            QueryParserResult parserResult = QueryParser.parse(queryStr);
            mParserResult = parserResult;
            if (isQueryValid(queryStr)) {
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

        private boolean isQueryValid(String query) {
            int errors = 0;

            if (!query.matches(QUERY_CHAR_CLASS_REGEX)) {
                errors++;
            }

            if (query.length() >= MAX_QUERY_LENGTH) {
                errors++;
            }

            // we make sure the query contains at least one alphanumeric char
            if (!containsNonWhitespaceCharacters(query)) {
                errors++;
            }

            if (mParserResult == null || !mParserResult.isDone()) {
                errors++;
            }

            return errors == 0;
        }
    }

}
