package arkadiuszpalka.quizomania.handler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler instance;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATABASE_NAME = "quizomania";
    private static final int DATABASE_VERSION = 5;

    //Tables name
    private static final String TABLE_QUIZZES = "quizzes";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ANSWERS = "answers";
    private static final String TABLE_RATES = "rates";

    private static final String[] ALL_TABLES = {TABLE_QUIZZES, TABLE_CATEGORIES, TABLE_QUESTIONS, TABLE_ANSWERS, TABLE_RATES};

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
        String[] queries = {CREATE_QUIZZES_TABLE, CREATE_CATEGORIES_TABLE, CREATE_QUESTIONS_TABLE, CREATE_ANSWERS_TABLE, CREATE_RATES_TABLE};
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

    public void addQuizzes(ArrayList<HashMap<String, String>> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO `"+ TABLE_QUIZZES +"`(`"+ KEY_QUIZZES_ID +"`,`"+ KEY_QUIZZES_TITLE +"`,`"+ KEY_QUIZZES_CONTENT +"`,`"+ KEY_CATEGORIES_ID +"`) VALUES(?,?,?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        for (HashMap<String, String>  map : arrayList) {
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

    public int getQuestionIdByQuizId(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, new String[] {KEY_QUESTIONS_ID}, KEY_QUESTIONS_QUIZ_ID + " = ?", new String[] {Long.toString(id)}, null, null, null, "1");
        cursor.moveToNext();
        int value = cursor.getInt(0);
        cursor.close();
        db.close();
        return value;
    }

    public ArrayList<Long> getQuizzesIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUIZZES, new String[] {KEY_QUIZZES_ID}, null, null, null, null, null, null);
        ArrayList<Long> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            ids.add(cursor.getLong(0));
        }
        cursor.close();
        db.close();
        return ids;
    }

    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[] {KEY_CATEGORIES_ID}, KEY_CATEGORIES_NAME + " = ?", new String[] {name}, null, null, null, "1");
        cursor.moveToNext();
        int value = cursor.getInt(0);
        cursor.close();
        db.close();
        return value;
    }

    public int getCountOfQuizzesById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM `" + TABLE_QUIZZES + "` WHERE `" + KEY_QUIZZES_ID + "` = "+ Long.toString(id) +";", null);
        cursor.moveToNext();
        int value = cursor.getInt(0);
        cursor.close();
        db.close();
        return value;
    }

    //method for debug only
    public String getTableAsString(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        allRows.close();
        return tableString;
    }
}
