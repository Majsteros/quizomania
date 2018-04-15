package arkadiuszpalka.quizomania.data.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_ANSWERS_IS_CORRECT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_ANSWERS_ORDER;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_ANSWERS_TEXT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_CATEGORIES_NAME;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ORDER;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_QUIZ_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_TEXT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_CONTENT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_TITLE;

public class AppApiHandler implements ApiHandler {

    private static final String TAG = "AppApiHandler";
    private static volatile AppApiHandler instance;

    private AppApiHandler() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get single instance of this class.");
        }
    }

    public static AppApiHandler getInstance() {
        if (instance == null) {
            synchronized (AppApiHandler.class) {
                if (instance == null) instance = new AppApiHandler();
            }
        }
        return instance;
    }

    @Override
    public String request(String url) {
        String result = "";
        try {
            result = new HttpHandler().request(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ArrayList<Long> checkNewQuizzes(ArrayList<Long> dbIds, String apiResult) {
        ArrayList<Long> apiIds = new ArrayList<>();
        try {
            JSONArray items = new JSONObject(apiResult).getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonObject = items.getJSONObject(i);
                long quizId = (long) jsonObject.get("id");
                apiIds.add(quizId);
            }
            ArrayList<Long> toRemove = new ArrayList<>();
            for (long dbId : dbIds) {
                for (long apiId : apiIds) {
                    if (dbId == apiId) {
                        toRemove.add(apiId);
                    }
                }
            }
            apiIds.removeAll(toRemove);
            return apiIds;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return apiIds;
    }

    @Override
    public ArrayList<HashMap<String, String>> downloadQuizzes(ArrayList<Long> apiIds, String apiResult) {
            ArrayList<HashMap<String, String>> quizzesList = new ArrayList<>();
            try {
                JSONArray items = new JSONObject(apiResult).getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject jsonObject = items.getJSONObject(i);
                    long quizId = (long) jsonObject.get("id");
                    for (long apiId : apiIds) {
                        if (apiId == quizId) {
                            String quizTitle = jsonObject.getString("title");
                            String quizContent = jsonObject.getString("content");
                            String categoryName = jsonObject.getJSONArray("categories")
                                    .getJSONObject(0)
                                    .getString("name");

                            HashMap<String, String> quiz = new HashMap<>();
                            quiz.put(KEY_QUIZZES_ID, Long.toString(quizId));
                            quiz.put(KEY_QUIZZES_TITLE, quizTitle);
                            quiz.put(KEY_QUIZZES_CONTENT, quizContent);
                            quiz.put(KEY_CATEGORIES_NAME, categoryName);

                            quizzesList.add(quiz);
                        }
                    }
                }
                return quizzesList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("MAIN", "Quizzes was inserted");
        return quizzesList;
    }

    @Override
    public ArrayList<HashMap<String, String>> downloadQuestions(ArrayList<Long> ids) {
        ArrayList<HashMap<String, String>> questionsList = new ArrayList<>();

        if (ids.size() > 0) {
            Log.d(TAG, "Inserting "+ ids.size() +" questions");

            for (Long id : ids) {
                try {
                    String result = new HttpHandler().request("http://quiz.o2.pl/api/v1/quiz/" + id + "/0");

                    if (result != null && result.length() > 0) {
                        JSONObject jsonObject = new JSONObject(result);
                        long quizId = (long) jsonObject.get("id");
                        JSONArray questions = jsonObject.getJSONArray("questions");

                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject question = questions.getJSONObject(i);
                            String questionText = question.getString("text");
                            int questionOrder = question.getInt("order");

                            HashMap<String, String> questionHashMap = new HashMap<>();
                            questionHashMap.put(KEY_QUESTIONS_QUIZ_ID, Long.toString(quizId));
                            questionHashMap.put(KEY_QUESTIONS_TEXT, questionText);
                            questionHashMap.put(KEY_QUESTIONS_ORDER, Integer.toString(questionOrder));
                            questionsList.add(questionHashMap);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "Questions was inserted");
        } else Log.d(TAG, "0 questions was inserted");
        return questionsList;
    }

    @Override
    public ArrayList<HashMap<String, String>> downloadAnswers(long quizId, int questionId, int questionOrder) {
        ArrayList<HashMap<String, String>> answersList = new ArrayList<>();
        try {
            String result = new HttpHandler().request("http://quiz.o2.pl/api/v1/quiz/" + Long.toString(quizId) + "/0");

            if (result != null && result.length() > 0) {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray questions = jsonObject.getJSONArray("questions");
                JSONObject question = questions.getJSONObject(questionOrder - 1);

                if (question.optInt("order") == questionOrder) {
                    JSONArray answers = question.getJSONArray("answers");

                    for (int j = 0; j < answers.length(); j++) {
                        HashMap<String, String> answersHashMap = new HashMap<>();
                        JSONObject answer = answers.getJSONObject(j);

                        int answerOrder = answer.getInt("order");
                        String answerText = answer.getString("text");
                        if (answer.has("isCorrect")) {
                            answersHashMap.put(KEY_ANSWERS_IS_CORRECT,
                                    Integer.toString(answer.getInt("isCorrect")));
                        }

                        answersHashMap.put(KEY_ANSWERS_ORDER, Integer.toString(answerOrder));
                        answersHashMap.put(KEY_ANSWERS_TEXT, answerText);
                        answersHashMap.put(KEY_QUESTIONS_ID, Integer.toString(questionId));

                        answersList.add(answersHashMap);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answersList;
    }
}
