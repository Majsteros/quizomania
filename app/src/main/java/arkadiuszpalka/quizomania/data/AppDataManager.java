package arkadiuszpalka.quizomania.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import arkadiuszpalka.quizomania.data.database.AppDatabaseHandler;
import arkadiuszpalka.quizomania.data.database.DatabaseHandler;
import arkadiuszpalka.quizomania.data.network.ApiHandler;
import arkadiuszpalka.quizomania.data.network.AppApiHandler;

public class AppDataManager implements DataManager {

    private static volatile AppDataManager instance;
    private final DatabaseHandler dbHandler;
    private final ApiHandler apiHandler;

    private AppDataManager(Context context) {
        this.dbHandler = AppDatabaseHandler.getInstance(context);
        this.apiHandler = AppApiHandler.getInstance();
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get single instance of this class.");
        }
    }

    public static AppDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDataManager.class) {
                if (instance == null) instance = new AppDataManager(context);
            }
        }
        return instance;
    }

    public DatabaseHandler getDbHandler() {
        return dbHandler;
    }

    public ApiHandler getApiHandler() {
        return apiHandler;
    }

    @Override
    public void addAnswers(ArrayList<HashMap<String, String>> answers) {
        dbHandler.addAnswers(answers);
    }

    @Override
    public void addCategories(ArrayList<String> categories) {
        dbHandler.addCategories(categories);
    }

    @Override
    public void addSeed(long quizId, String seed) {
        dbHandler.addSeed(quizId, seed);
    }

    @Override
    public void addQuestion(HashMap<String, String> question) {
        dbHandler.addQuestion(question);
    }

    @Override
    public void addQuestions(ArrayList<HashMap<String, String>> questions) {
        dbHandler.addQuestions(questions);
    }

    @Override
    public void addQuizzes(ArrayList<HashMap<String, String>> quizzes) {
        dbHandler.addQuizzes(quizzes);
    }

    @Override
    public ArrayList<TreeMap<String, Object>> getAnswersByQuestionId(int id) {
        return dbHandler.getAnswersByQuestionId(id);
    }

    @Override
    public int getCategoryIdByName(String name) {
        return dbHandler.getCategoryIdByName(name);
    }

    @Override
    public int getCountOfSeedsById(long id) {
        return dbHandler.getCountOfSeedsById(id);
    }

    @Override
    public int getCountOfQuizzesById(long id) {
        return dbHandler.getCountOfQuizzesById(id);
    }

    @Override
    public String getSeed(long id) {
        return dbHandler.getSeed(id);
    }

    @Override
    public HashMap<String, Object> getQuestionByQuizIdOrder(long id, int order) {
        return dbHandler.getQuestionByQuizIdOrder(id, order);
    }

    @Override
    public int getCountOfQuestionsById(long id) {
        return dbHandler.getCountOfQuestionsById(id);
    }

    @Override
    public int getQuestionIdByQuizId(long id) {
        return dbHandler.getQuestionIdByQuizId(id);
    }

    @Override
    public int getQuestionIdByQuizIdOrder(long id, int order) {
        return dbHandler.getQuestionIdByQuizIdOrder(id, order);
    }

    @Override
    public ArrayList<HashMap<String, String>> getQuizzes() {
        return dbHandler.getQuizzes();
    }

    @Override
    public ArrayList<Long> getQuizzesIds() {
        return dbHandler.getQuizzesIds();
    }

    @Override
    public void updateSeed(long id, String seed) {
        dbHandler.updateSeed(id, seed);
    }

    @Override
    public void removeSeed(long id) {
        dbHandler.removeSeed(id);
    }

    @Override
    public ArrayList<Long> downloadQuizzes() {
        return apiHandler.downloadQuizzes();
    }

    @Override
    public void downloadQuestions(ArrayList<Long> ids) {
        apiHandler.downloadQuestions(ids);
    }

    public void syncApiData() {
        new Runnable() {
            @Override
            public void run() {
                ArrayList<Long> ids = downloadQuizzes();
                downloadQuestions(ids);
            }
        };
    }
}
