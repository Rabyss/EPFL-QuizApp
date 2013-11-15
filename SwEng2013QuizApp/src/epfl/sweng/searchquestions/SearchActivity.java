package epfl.sweng.searchquestions;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivity extends Activity {

    private EditText mEditQuery; //the edit text field where the user type the request
    private Button mSearchButton; //the button the user press when he wants to submit its search query
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        mEditQuery = (EditText) findViewById(R.id.editSearchQuery);
        mEditQuery.addTextChangedListener(new QueryWatcher());
        
        mSearchButton = (Button) findViewById(R.id.submitSearchButton);
        
        TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
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
            if (SearchQuery.auditQueryStr(editable.toString()) == 0) {
                mSearchButton.setEnabled(true);
            } else {
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
