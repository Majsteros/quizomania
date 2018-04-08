package arkadiuszpalka.quizomania.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class AppDatabaseHandler extends SQLiteOpenHelper implements DatabaseHandler {

    private static volatile AppDatabaseHandler instance;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATABASE_NAME = "quizomania";
    private static final int DATABASE_VERSION = 1;

    //Tables name
    private static final String TABLE_QUIZZES = "quizzes";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ANSWERS = "answers";
    private static final String TABLE_RATES = "rates";
    private static final String TABLE_SEEDS = "seeds";

    private static final String[] ALL_TABLES = {TABLE_QUIZZES, TABLE_CATEGORIES, TABLE_QUESTIONS, TABLE_ANSWERS, TABLE_RATES, TABLE_SEEDS};

    //Quizzes table columns names
    public static final String KEY_QUIZZES_ID = "quiz_id";
    private static final String KEY_QUIZZES_CATEGORY_ID = "category_id";
    public static final String KEY_QUIZZES_TITLE = "title";
    public static final String KEY_QUIZZES_CONTENT = "content";

    //Categories table columns names
    public static final String KEY_CATEGORIES_ID = "category_id";
    public static final String KEY_CATEGORIES_NAME = "name";

    //Questions table columns names
    public static final String KEY_QUESTIONS_ID = "question_id";
    public static final String KEY_QUESTIONS_QUIZ_ID = "quiz_id";
    public static final String KEY_QUESTIONS_TEXT = "text";
    public static final String KEY_QUESTIONS_ORDER = "order";

    //Answers table columns names
    private static final String KEY_ANSWERS_QUESTIONS_ID = "question_id";
    public static final String KEY_ANSWERS_TEXT = "text";
    public static final String KEY_ANSWERS_IS_CORRECT = "is_correct";
    public static final String KEY_ANSWERS_ORDER = "order";

    //Rates table columns names
    private static final String KEY_RATES_QUIZ_ID = "quiz_id";
    private static final String KEY_RATES_FROM = "from";
    private static final String KEY_RATES_TO = "to";
    private static final String KEY_RATES_CONTENT = "content";

    //Seeds table columns names
    private static final String KEY_SEEDS_QUIZ_ID = "quiz_id";
    private static final String KEY_SEEDS_SEED = "seed";

    private AppDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static AppDatabaseHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabaseHandler.class) {
                if (instance == null) instance = new AppDatabaseHandler(context.getApplicationContext());
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_CATEGORIES + "` (" +
                "`" + KEY_CATEGORIES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`" + KEY_CATEGORIES_NAME + "` TEXT UNIQUE" +
                ");";
        String CREATE_QUIZZES_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_QUIZZES + "` (" +
                "`" + KEY_QUIZZES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`" + KEY_QUIZZES_CATEGORY_ID + "` INTEGER NOT NULL," +
                "`" + KEY_QUIZZES_TITLE + "` TEXT," +
                "`" + KEY_QUIZZES_CONTENT + "` TEXT," +
                "FOREIGN KEY (`" + KEY_QUIZZES_CATEGORY_ID + "`) " +
                "REFERENCES `" + TABLE_CATEGORIES + "`(`"+ KEY_CATEGORIES_ID +"`)" +
                ");";
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_QUESTIONS + "` (" +
                "`" + KEY_QUESTIONS_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`" + KEY_QUESTIONS_QUIZ_ID + "` INTEGER NOT NULL," +
                "`" + KEY_QUESTIONS_TEXT + "` TEXT," +
                "`" + KEY_QUESTIONS_ORDER + "` INTEGER," +
                "FOREIGN KEY (`" + KEY_QUESTIONS_QUIZ_ID + "`) " +
                "REFERENCES `" + TABLE_QUIZZES + "`(`"+ KEY_QUIZZES_ID +"`)" +
                ");";
        String CREATE_ANSWERS_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_ANSWERS + "` (" +
                "`" + KEY_ANSWERS_QUESTIONS_ID + "` INTEGER NOT NULL," +
                "`" + KEY_ANSWERS_TEXT + "` TEXT," +
                "`" + KEY_ANSWERS_IS_CORRECT + "` INTEGER," +
                "`" + KEY_ANSWERS_ORDER + "` INTEGER," +
                "FOREIGN KEY (`" + KEY_ANSWERS_QUESTIONS_ID + "`) " +
                "REFERENCES `" + TABLE_QUESTIONS + "`(`"+ KEY_QUESTIONS_ID +"`)" +
                ");";
        String CREATE_RATES_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_RATES + "` (" +
                "`" + KEY_RATES_QUIZ_ID + "` INTEGER NOT NULL," +
                "`" + KEY_RATES_FROM + "` INTEGER," +
                "`" + KEY_RATES_TO + "` TEXT," +
                "`" + KEY_RATES_CONTENT + "` INTEGER," +
                "FOREIGN KEY (`" + KEY_RATES_QUIZ_ID + "`) " +
                "REFERENCES `" + TABLE_QUIZZES + "`(`"+ KEY_QUIZZES_ID +"`)" +
                ");";
        String CREATE_SEEDS_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_SEEDS + "` (" +
                "`" + KEY_SEEDS_QUIZ_ID + "` INTEGER UNIQUE ON CONFLICT IGNORE NOT NULL," +
                "`" + KEY_SEEDS_SEED + "` VARCHAR(18)," +
                "FOREIGN KEY (`" + KEY_SEEDS_QUIZ_ID + "`) " +
                "REFERENCES `" + TABLE_QUIZZES + "`(`"+ KEY_QUIZZES_ID +"`)" +
                ");";
        String[] queries = {CREATE_QUIZZES_TABLE, CREATE_CATEGORIES_TABLE, CREATE_QUESTIONS_TABLE, CREATE_ANSWERS_TABLE, CREATE_RATES_TABLE, CREATE_SEEDS_TABLE};
        for (String query : queries) {
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for (String table : ALL_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }

    @Override
    public void addAnswers(ArrayList<HashMap<String, String>> answers) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO `"+ TABLE_ANSWERS +"`(`"+ KEY_ANSWERS_QUESTIONS_ID +"`,`"+ KEY_ANSWERS_IS_CORRECT +"`,`"+ KEY_ANSWERS_ORDER +"`,`"+ KEY_ANSWERS_TEXT +"`) VALUES(?,?,?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        for (HashMap<String, String> map : answers) {
            stmt.bindString(1, map.get(KEY_ANSWERS_QUESTIONS_ID));
            stmt.bindString(2,
                    map.get(KEY_ANSWERS_IS_CORRECT) == null ? "0" : map.get(KEY_ANSWERS_IS_CORRECT)
            );
            stmt.bindString(3, map.get(KEY_ANSWERS_ORDER));
            stmt.bindString(4, map.get(KEY_ANSWERS_TEXT));
            stmt.executeInsert();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void addSeed(long quizId, String seed) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO `"+ TABLE_SEEDS +"`(`"+ KEY_SEEDS_QUIZ_ID +"`,`"+ KEY_SEEDS_SEED +"`) VALUES(?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        stmt.bindLong(1, quizId);
        stmt.bindString(2, seed);
        stmt.executeInsert();
        stmt.clearBindings();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void addQuestion(HashMap<String, String> question) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO `"+ TABLE_QUESTIONS +"`(`"+ KEY_QUESTIONS_QUIZ_ID +"`,`"+ KEY_QUESTIONS_TEXT +"`,`"+ KEY_QUESTIONS_ORDER +"`) VALUES(?,?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        stmt.bindString(1, question.get(KEY_QUESTIONS_QUIZ_ID));
        stmt.bindString(2, question.get(KEY_QUESTIONS_TEXT));
        stmt.bindString(3, question.get(KEY_QUESTIONS_ORDER));
        stmt.executeInsert();
        stmt.clearBindings();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void addQuestions(ArrayList<HashMap<String, String>> questions) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO `"+ TABLE_QUESTIONS +"`(`"+ KEY_QUESTIONS_QUIZ_ID +"`,`"+ KEY_QUESTIONS_TEXT +"`,`"+ KEY_QUESTIONS_ORDER +"`) VALUES(?,?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        for (HashMap<String, String> map : questions) {
            stmt.bindString(1, map.get(KEY_QUESTIONS_QUIZ_ID));
            stmt.bindString(2, map.get(KEY_QUESTIONS_TEXT));
            stmt.bindString(3, map.get(KEY_QUESTIONS_ORDER));
            stmt.executeInsert();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void addQuizzes(ArrayList<HashMap<String, String>> quizzes) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO `"+ TABLE_QUIZZES +"`(`"+ KEY_QUIZZES_ID +"`,`"+ KEY_QUIZZES_TITLE +"`,`"+ KEY_QUIZZES_CONTENT +"`,`"+ KEY_CATEGORIES_ID +"`) VALUES(?,?,?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        for (HashMap<String, String>  map : quizzes) {
            stmt.bindString(1, map.get(KEY_QUIZZES_ID));
            stmt.bindString(2, map.get(KEY_QUIZZES_TITLE));
            stmt.bindString(3, map.get(KEY_QUIZZES_CONTENT));
            stmt.bindString(4, map.get(KEY_CATEGORIES_ID));
            stmt.executeInsert();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void addCategories(ArrayList<String> categories) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT OR IGNORE INTO `"+ TABLE_CATEGORIES +"`(`"+ KEY_CATEGORIES_NAME +"`) VALUES(?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        for (String categoryName : categories) {
            stmt.bindString(1, categoryName);
            stmt.executeInsert();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public int getQuestionIdByQuizId(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_QUESTIONS_ID + "` FROM `" + TABLE_QUESTIONS + "` WHERE `" + KEY_QUESTIONS_QUIZ_ID + "` = '" + Long.toString(id) + "' LIMIT 1", null);
        int value = 0;
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public int getQuestionIdByQuizIdOrder(long id, int order) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_QUESTIONS_ID + "` FROM `" + TABLE_QUESTIONS + "` WHERE `" + KEY_QUESTIONS_QUIZ_ID + "` = '" + Long.toString(id) + "' AND `" + KEY_QUESTIONS_ORDER + "` = '" + Integer.toString(order) + "' LIMIT 1", null);
        int value = 0;
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public HashMap<String, Object> getQuestionByQuizIdOrder(long id, int order) {
        HashMap<String, Object> question = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_QUESTIONS_ID + "`,`" + KEY_QUESTIONS_TEXT + "`,`" + KEY_QUESTIONS_ORDER + "` FROM `" + TABLE_QUESTIONS + "` WHERE `" + KEY_QUESTIONS_QUIZ_ID + "` = '"+ Long.toString(id) + "' AND `" + KEY_QUESTIONS_ORDER + "` = '" + Integer.toString(order) + "' LIMIT 1", null);
        while (cursor.moveToNext()) {
            question.put(KEY_QUESTIONS_ID, cursor.getInt(0));
            question.put(KEY_QUESTIONS_TEXT, cursor.getString(1));
            question.put(KEY_QUESTIONS_ORDER, cursor.getInt(2));
        }
        cursor.close();
        db.close();
        return question;
    }

    @Override
    public ArrayList<TreeMap<String, Object>> getAnswersByQuestionId(int id) {
        ArrayList<TreeMap<String, Object>> answers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_ANSWERS_TEXT + "`,`" + KEY_ANSWERS_ORDER + "`,`" + KEY_ANSWERS_IS_CORRECT + "` FROM `" + TABLE_ANSWERS + "` WHERE `" + KEY_ANSWERS_QUESTIONS_ID + "` = '" + Integer.toString(id) + "' ORDER BY `" + KEY_ANSWERS_ORDER +"`",null);
        while (cursor.moveToNext()) {
            TreeMap<String, Object> answer = new TreeMap<>();
            answer.put(KEY_ANSWERS_TEXT, cursor.getString(0));
            answer.put(KEY_ANSWERS_ORDER, cursor.getInt(1));
            answer.put(KEY_ANSWERS_IS_CORRECT, cursor.getInt(2));
            answers.add(answer);
        }
        cursor.close();
        db.close();
        return answers;
    }

    @Override
    public String getSeed(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_SEEDS_SEED + "` FROM `" + TABLE_SEEDS + "` WHERE `"+ KEY_SEEDS_QUIZ_ID +"` = '"+ id + "' LIMIT 1", null);
        String value = "";
        while (cursor.moveToNext()) {
            value = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public ArrayList<HashMap<String, String>> getQuizzes() {
        ArrayList<HashMap<String, String>> quizzes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_QUIZZES_ID + "`,`" + KEY_QUIZZES_TITLE + "` FROM `" + TABLE_QUIZZES + "` ORDER BY `" + KEY_QUIZZES_ID +"`", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> quiz = new HashMap<>();
            quiz.put(KEY_QUIZZES_ID, Long.toString(cursor.getLong(0)));
            quiz.put(KEY_QUIZZES_TITLE, cursor.getString(1));
            quizzes.add(quiz);
        }
        cursor.close();
        db.close();
        return quizzes;
    }

    @Override
    public ArrayList<Long> getQuizzesIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUIZZES, new String[] {KEY_QUIZZES_ID}, null, null, null, null, null);
        ArrayList<Long> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            ids.add(cursor.getLong(0));
        }
        cursor.close();
        db.close();
        return ids;
    }

    @Override
    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `" + KEY_CATEGORIES_ID + "` FROM `" + TABLE_CATEGORIES + "` WHERE `"+ KEY_CATEGORIES_NAME +"` = '"+ name + "' LIMIT 1", null);
        int value = 0;
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public int getCountOfQuestionsById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM `" + TABLE_QUESTIONS + "` WHERE `" + KEY_QUESTIONS_QUIZ_ID + "` = '"+ Long.toString(id) +"'", null);
        int value = 0;
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public int getCountOfQuizzesById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM `" + TABLE_QUIZZES + "` WHERE `" + KEY_QUIZZES_ID + "` = '"+ Long.toString(id) +"'", null);
        int value = 0;
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public int getCountOfSeedsById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM `" + TABLE_SEEDS + "` WHERE `" + KEY_SEEDS_QUIZ_ID + "` = '"+ Long.toString(id) +"'", null);
        int value = 0;
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public void updateSeed(long id, String seed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SEEDS_SEED, seed);
        db.update(TABLE_SEEDS, values, KEY_SEEDS_QUIZ_ID + " = '" + Long.toString(id) + "'", null);
        db.close();
    }

    @Override
    public void removeSeed(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEEDS, KEY_SEEDS_QUIZ_ID + " = " + Long.toString(id), null);
        db.close();
    }
}
