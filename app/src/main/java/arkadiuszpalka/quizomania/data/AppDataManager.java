package arkadiuszpalka.quizomania.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import arkadiuszpalka.quizomania.data.database.AppDatabaseHandler;
import arkadiuszpalka.quizomania.data.database.DatabaseHandler;
import arkadiuszpalka.quizomania.data.network.ApiHandler;
import arkadiuszpalka.quizomania.data.network.AppApiHandler;
import arkadiuszpalka.quizomania.ui.splash.SplashPresenter;

import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_CATEGORIES_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_CATEGORIES_NAME;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ORDER;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_QUIZ_ID;

public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

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
    public byte getCountOfQuestionsById(long id) {
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
    public ArrayList<Long> checkNewQuizzes(ArrayList<Long> dbIds, String apiResult) {
        return apiHandler.checkNewQuizzes(dbIds, apiResult);
    }

    @Override
    public String request(String url) {
        return apiHandler.request(url);
    }

    @Override
    public ArrayList<HashMap<String, String>> downloadQuizzes(ArrayList<Long> apiIds, String apiResult) {
       return apiHandler.downloadQuizzes(apiIds, apiResult);
    }

    @Override
    public ArrayList<HashMap<String, String>> downloadQuestions(ArrayList<Long> ids) {
        return apiHandler.downloadQuestions(ids);
    }

    @Override
    public ArrayList<HashMap<String, String>> downloadAnswers(long quizId, int questionId, int questionOrder) {
        return apiHandler.downloadAnswers(quizId, questionId, questionOrder);
    }

    public void syncApiData(final SplashPresenter presenter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "syncing started...");
                String request = request("http://quiz.o2.pl/api/v1/quizzes/0/30");
                ArrayList<Long> apiIds = checkNewQuizzes(getQuizzesIds(),
                        request);
                ArrayList<HashMap<String, String>> quizzes = downloadQuizzes(apiIds,
                        request);
                if (quizzes.size() > 0) {
                    ArrayList<String> categories = new ArrayList<>();
                    for (HashMap<String, String> quiz : quizzes) {
                        categories.add(quiz.get(KEY_CATEGORIES_NAME));
                    }
                    addCategories(categories);
                    for (HashMap<String, String> quiz : quizzes) {
                        quiz.put(KEY_CATEGORIES_ID,
                                Integer.toString(getCategoryIdByName(quiz.get(KEY_CATEGORIES_NAME))));
                    }
                    addQuizzes(quizzes);
                }
                ArrayList<HashMap<String, String>> questions = downloadQuestions(apiIds);
                addQuestions(questions);
                for (HashMap<String, String> map : questions) {
                    long quizId = Long.parseLong(map.get(KEY_QUESTIONS_QUIZ_ID));
                    int questionOrder = Integer.parseInt(map.get(KEY_QUESTIONS_ORDER));
                    int questionId = getQuestionIdByQuizIdOrder(quizId, questionOrder);
                    Log.d(TAG, "downloading answers for quiz ID: " + quizId + "\nquestion ID: " + questionId);
                    addAnswers(downloadAnswers(quizId,
                                            questionId,
                                            questionOrder));
                }
                Log.i(TAG, "syncing was finished...");
                presenter.onSuccessUpdate();
            }
        }).start();
    }
}
