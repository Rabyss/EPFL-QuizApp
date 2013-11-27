package epfl.sweng.cache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import epfl.sweng.quizquestions.QuizQuestion;

public class SQLiteCache extends SQLiteOpenHelper implements CacheInterface {

	// Database version
	private static final int DATABASE_VERSION = 1;
	// Database name
	private static final String DATABASE_NAME = "QuizQuestion_Cache";

	// Question table name
	private static final String TABLE_QUESTION = "table_Question";

	// Question table columns name
	private static final String COL_ID = "id";
	private static final String COL_QUESTION = "questions";
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
			+ " INTEGER PRIMARY KEY" + ");";
	private static final String CREATE_TAG_TABLE = "CREATE TABLE" + TABLE_TAG
			+ "(" + COL_ID_TAG + " INTEGER PRIMARY KEY," + COL_TAG + " TEXT"
			+ ");";
	private static final String CREATE_ANSWER_TABLE = "CREATE TABLE"
			+ TABLE_ANSWER + "(" + COL_ID_ANSWER + " INTEGER PRIMARY KEY,"
			+ COL_ANSWER + " TEXT," + COL_INDEX + " INTEGER PRIMARY KEY" + ");";

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

	@Override
	public Set<QuizQuestion> getQuestionSetByTag(String tagRegExp) {
		Set<QuizQuestion> questionMatching = new HashSet<QuizQuestion>();
		//TODO A completer la demande!!
		String selectQuery = " SELECT *FROM " + TABLE_QUESTION + " INNER JOIN "
				+ TABLE_TAG + " On " + COL_ID_TAG + " WHERE " + COL_TAG + " = "
				+ tagRegExp;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				// TODO : initialiser les variables: faut-il faire plusieurs demandes?
				
				String question = null;
				List<String> answers = null;
				int solutionIndex = 0;
				Set<String> tags = null;
				int id = 0;
				String owner = null;
				
				QuizQuestion quizQuestion = new QuizQuestion(question, answers,
						solutionIndex, tags, id, owner);
				questionMatching.add(quizQuestion);
			} while (cursor.moveToNext());
		}
		return questionMatching;
	}

	@Override
	public void clearCache() {
		SQLiteDatabase db= this.getWritableDatabase();
		db.delete(TABLE_QUESTION, null, null);
		db.delete(TABLE_TAG, null, null);
		db.delete(TABLE_ANSWER, null, null);

	}

}
