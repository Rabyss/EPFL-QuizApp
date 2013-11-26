package epfl.sweng.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteCache extends SQLiteOpenHelper {

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
	
	
	private static final String CREATE_QUESTION_TABLE = "CREATE TABLE" + TABLE_QUESTION + "("
			+ COL_ID + " INTEGER PRIMARY KEY," + COL_QUESTION + " TEXT,"
			+ COL_OWNER + " TEXT," + COL_SOLUTION + " INTEGER PRIMARY KEY"
			+ ");";
	private static final String CREATE_TAG_TABLE = "CREATE TABLE" + TABLE_TAG + "(" + COL_ID_TAG
			+ " INTEGER PRIMARY KEY," + COL_TAG + " TEXT" + ");";
	private static final String CREATE_ANSWER_TABLE = "CREATE TABLE" + TABLE_ANSWER + "("
			+ COL_ID_ANSWER + " INTEGER PRIMARY KEY," + COL_ANSWER
			+ " TEXT," + COL_INDEX + " INTEGER PRIMARY KEY" + ");";
	
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
		// SELECT *FROM Questions INNER JOIN Tags On Tags.question_ID = ID ...
		// WHERE Tags.tag = A ou B et C
	}

}
