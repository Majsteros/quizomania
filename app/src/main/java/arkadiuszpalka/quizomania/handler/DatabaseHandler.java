package arkadiuszpalka.quizomania.handler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler instance;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATABASE_NAME = "quizomania";
    private static final int DATABASE_VERSION = 1;

    //Tables name
    private static final String TABLE_QUIZZES = "quizzes";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ANSWERS = "answers";
    private static final String TABLE_RATES = "rates";

    private static final String[] ALL_TABLES = {TABLE_QUIZZES, TABLE_CATEGORIES, TABLE_QUESTIONS, TABLE_ANSWERS, TABLE_RATES};

    //Quizzes table columns names
    private static final String KEY_QUIZZES_ID = "quiz_id";
    private static final String KEY_QUIZZES_CATEGORY_ID = "category_id";
    private static final String KEY_QUIZZES_TITLE = "title";
    private static final String KEY_QUIZZES_CONTENT = "content";

    //Categories table columns names
    private static final String KEY_CATEGORIES_ID = "category_id";
    private static final String KEY_CATEGORIES_NAME = "name";

    //Questions table columns names
    private static final String KEY_QUESTIONS_ID = "question_id";
    private static final String KEY_QUESTIONS_QUIZ_ID = "quiz_id";
    private static final String KEY_QUESTIONS_TEXT = "text";
    private static final String KEY_QUESTIONS_ORDER = "order";

    //Answers table columns names
    private static final String KEY_ANSWERS_QUESTIONS_ID = "question_id";
    private static final String KEY_ANSWERS_TEXT = "text";
    private static final String KEY_ANSWERS_IS_CORRECT = "is_correct";
    private static final String KEY_ANSWERS_ORDER = "order";

    //Rates table columns names
    private static final String KEY_RATES_QUIZ_ID = "quiz_id";
    private static final String KEY_RATES_FROM = "from";
    private static final String KEY_RATES_TO = "to";
    private static final String KEY_RATES_CONTENT = "content";

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUIZZES_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_QUIZZES + "` (" +
                "`" + KEY_QUIZZES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`" + KEY_QUIZZES_CATEGORY_ID + "` INTEGER NOT NULL," +
                "`" + KEY_QUIZZES_TITLE + "` TEXT," +
                "`" + KEY_QUIZZES_CONTENT + "` TEXT);";
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_CATEGORIES + "` (" +
                "`" + KEY_CATEGORIES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`" + KEY_CATEGORIES_NAME + "` TEXT);";
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_QUESTIONS + "` (" +
                "`" + KEY_QUESTIONS_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`" + KEY_QUESTIONS_QUIZ_ID + "` INTEGER NOT NULL," +
                "`" + KEY_QUESTIONS_TEXT + "` TEXT," +
                "`" + KEY_QUESTIONS_ORDER + "` INTEGER);";
        String CREATE_ANSWERS_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_ANSWERS + "` (" +
                "`" + KEY_ANSWERS_QUESTIONS_ID + "` INTEGER NOT NULL," +
                "`" + KEY_ANSWERS_TEXT + "` TEXT," +
                "`" + KEY_ANSWERS_IS_CORRECT + "` INTEGER," +
                "`" + KEY_ANSWERS_ORDER + "` INTEGER);";
        String CREATE_RATES_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_RATES + "` (" +
                "`" + KEY_RATES_QUIZ_ID + "` INTEGER NOT NULL," +
                "`" + KEY_RATES_FROM + "` INTEGER," +
                "`" + KEY_RATES_TO + "` TEXT," +
                "`" + KEY_RATES_CONTENT + "` INTEGER);";
        String[] queries = {CREATE_QUIZZES_TABLE, CREATE_CATEGORIES_TABLE, CREATE_QUESTIONS_TABLE, CREATE_ANSWERS_TABLE, CREATE_RATES_TABLE};
        for (String query : queries) {
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for (String table : ALL_TABLES) {
            db.execSQL("DROP TABLE IF EXIST " + table);
        }
        onCreate(db);
    }
}
