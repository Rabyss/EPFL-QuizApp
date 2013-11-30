package epfl.sweng.cache;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.SQLQueryCompiler;
import epfl.sweng.searchquestions.parser.tree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SQLiteCache extends SQLiteOpenHelper implements CacheInterface {

	public static final String TAGS_SEL = "tagsSel";
	
	// Database version
	private static final int DATABASE_VERSION = 1;
	// Database name
	private static final String DATABASE_NAME = "QuizQuestion_Cache";

	// Question table name
	private static final String TABLE_QUESTION = "table_Question";

	// Question table columns name
	private static final String COL_ID = "id";
	private static final String COL_QUESTION = "question";
	private static final String COL_SOLUTION = "solution";
	private static final String COL_OWNER = "owner";

	// Tab table name
	private static final String TABLE_TAG = "table_tag";
	private static final String COL_ID_TAG = "id";
	private static final String COL_TAG = "tag";

	// Answer table name
	private static final String TABLE_ANSWER = "table_answer";

	private static final String COL_ID_ANSWER = "id";
	private static final String COL_ANSWER = "answer";
	private static final String COL_INDEX = "index";
	
	private static final String CREATE_QUESTION_TABLE = "CREATE TABLE"
			+ TABLE_QUESTION + "(" + COL_ID + " INTEGER PRIMARY KEY,"
			+ COL_QUESTION + " TEXT," + COL_OWNER + " TEXT," + COL_SOLUTION
			+ " INTEGER" + ");";
	private static final String CREATE_TAG_TABLE = "CREATE TABLE" + TABLE_TAG
			+ "(" + COL_ID_TAG + " INTEGER," + COL_TAG + " TEXT"
			+ ");";
	private static final String CREATE_ANSWER_TABLE = "CREATE TABLE"
			+ TABLE_ANSWER + "(" + COL_ID_ANSWER + " INTEGER,"
			+ COL_ANSWER + " TEXT," + COL_INDEX + " INTEGER" + ");";
	
	private static final int MAX_SQL_CACHE_SIZE = 100;
	private static final long MAX_SQL_SIZE = 1024 * 1024 * 1024;
	
	public SQLiteCache(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// change de size of the cache of the sqlite
		db.setMaxSqlCacheSize(MAX_SQL_CACHE_SIZE);

		// change de size of the sqlite : 1 GB
		db.setMaximumSize(MAX_SQL_SIZE);

		db.execSQL(CREATE_QUESTION_TABLE);
		db.execSQL(CREATE_TAG_TABLE);
		db.execSQL(CREATE_ANSWER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);

		onCreate(db);

	}

	@Override
	public void cacheQuestion(QuizQuestion question) {
		SQLiteDatabase db = this.getWritableDatabase();

		// Insert elements in the question table
		ContentValues valuesQuestion = new ContentValues();
		valuesQuestion.put(COL_ID, question.getId());
		valuesQuestion.put(COL_QUESTION, question.getQuestion());
		valuesQuestion.put(COL_OWNER, question.getOwner());
		valuesQuestion.put(COL_SOLUTION, question.getSolution());
		db.insert(TABLE_QUESTION, null, valuesQuestion);

		// Insert elements in the tags table
		ContentValues valuesTags = new ContentValues();
		Set<String> tagSet = question.getTags();

		for (String tag : tagSet) {
			valuesTags.put(COL_ID_TAG, question.getId());
			valuesTags.put(COL_TAG, tag);
		}
		db.insert(TABLE_TAG, null, valuesTags);

		// Insert elements in the answers table
		ContentValues valuesAnswers = new ContentValues();
		List<String> answerList = question.getAnswers();
		int index = 0;
		for (String answer : answerList) {
			valuesAnswers.put(COL_ID_ANSWER, question.getId());
			valuesAnswers.put(COL_ANSWER, answer);
			valuesAnswers.put(COL_INDEX, index);
			index++;
		}
		db.insert(TABLE_ANSWER, null, valuesAnswers);

		db.close();
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public Set<QuizQuestion> getQuestionSetByTag(TreeNode AST) {
		Map<Integer, QuizQuestion> questionMatching = new HashMap<Integer, QuizQuestion>();

		//TODO A completer la demande!!
		
		SQLiteDatabase db = this.getReadableDatabase();
        SQLQueryCompiler compiler = new SQLQueryCompiler();
		// | INT id | STR question | STR owner | INT solution | STR tag | STR answer | INT index |
		Cursor cursor = db.rawQuery("SELECT "+
				TABLE_QUESTION+"."+COL_ID+
				TABLE_QUESTION+"."+COL_QUESTION+
				TABLE_QUESTION+"."+COL_OWNER+
				TABLE_QUESTION+"."+COL_SOLUTION+
				"tagsShow."+COL_TAG+
				TABLE_ANSWER+"."+COL_ANSWER+
				TABLE_ANSWER+"."+COL_INDEX+
				" FROM "+TABLE_QUESTION+" INNER JOIN "+TABLE_TAG+
				" AS tagsSel ON tagsSel."+COL_ID_TAG+"="+COL_ID+
				" INNER JOIN "+TABLE_TAG+" AS tagsShow ON tagsShow."+
				COL_ID_TAG+"="+COL_ID+" WHERE "+
                compiler.toSQL(AST)
                +" SORT ORDER BY "+COL_ID+", "+COL_INDEX+" ASC;", new String[0]);
		
		if (cursor.moveToFirst()) {
			do {
				// TODO : initialiser les variables: faut-il faire plusieurs demandes?
				int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
				
				if (!questionMatching.containsKey(id)) {
					Set<String> tags = new HashSet<String>();
					ArrayList<String> answers = new ArrayList<String>();
					
					String question = cursor.getString(cursor.getColumnIndex(COL_QUESTION));
					String owner = cursor.getString(cursor.getColumnIndex(COL_OWNER));
					int solutionIndex = cursor.getInt(cursor.getColumnIndex(COL_SOLUTION));
					tags.add(cursor.getString(cursor.getColumnIndex(COL_TAG)));
					answers.add(cursor.getString(cursor.getColumnIndex(COL_ANSWER)));
					
					QuizQuestion quizQuestion = new QuizQuestion(question, answers,
							solutionIndex, tags, id, owner);

					questionMatching.put(id, quizQuestion);
				} else {
					QuizQuestion quizQuestion = questionMatching.get(id);
					
					quizQuestion.addTag(cursor.getString(cursor.getColumnIndex(COL_TAG)));
					quizQuestion.addAnswer(cursor.getString(cursor.getColumnIndex(COL_ANSWER)));
				}
				
				
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return new HashSet<QuizQuestion>(questionMatching.values());
	}

	@Override
	public void clearCache() {
		SQLiteDatabase db= this.getWritableDatabase();
		db.delete(TABLE_QUESTION, null, null);
		db.delete(TABLE_TAG, null, null);
		db.delete(TABLE_ANSWER, null, null);

	}

}
