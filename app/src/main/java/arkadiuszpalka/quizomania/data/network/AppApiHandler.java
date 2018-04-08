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
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_CATEGORIES_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_CATEGORIES_NAME;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ORDER;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_QUIZ_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_TEXT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_CONTENT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_TITLE;

class AppApiHandler implements ApiHandler {

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
    public ArrayList<Long> downloadQuizzes() {
        String result = null;
        try {
            result = new HttpHandler().request("http://quiz.o2.pl/api/v1/quizzes/0/100");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result != null) {
            ArrayList<HashMap<String, String>> quizzesList = new ArrayList<>();
            ArrayList<String> categoriesList = new ArrayList<>();
            ArrayList<Long> ids = new ArrayList<>();
            try {
                JSONArray items = new JSONObject(result).getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject jsonObject = items.getJSONObject(i);
                    String categoryName = jsonObject.getJSONArray("categories")
                            .getJSONObject(0)
                            .getString("name");
                    long quizId = (long) jsonObject.get("id");
                    String quizTitle = jsonObject.getString("title");
                    String quizContent = jsonObject.getString("content");

                    if (db.getCountOfQuizzesById(quizId) == 0) {
                        HashMap<String, String> quiz = new HashMap<>();
                        quiz.put(KEY_QUIZZES_ID, Long.toString(quizId));
                        quiz.put(KEY_QUIZZES_TITLE, quizTitle);
                        quiz.put(KEY_QUIZZES_CONTENT, quizContent);
                        quiz.put(KEY_CATEGORIES_NAME, categoryName);

                        categoriesList.add(categoryName);
                        quizzesList.add(quiz);
                        ids.add(quizId);
                    }
                }
                if (categoriesList.size() > 0) {
                    db.addCategories(categoriesList);
                }
                if (quizzesList.size() > 0) {
                    for (HashMap<String, String> map : quizzesList) {
                        map.put(KEY_CATEGORIES_ID,
                                Integer.toString(
                                        db.getCategoryIdByName(
                                                map.get(KEY_CATEGORIES_NAME))));
                    }
                    db.addQuizzes(quizzesList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("MAIN", "Quizzes was inserted");
            return ids;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void downloadQuestions(ArrayList<Long> ids) {
        if (ids.size() > 0) {
            Log.d("MAIN", "Inserting "+ ids.size() +" questions");
            for (Long id : ids) {
                try {
                    String result = new HttpHandler().request("http://quiz.o2.pl/api/v1/quiz/" + id + "/0");
                    if (result != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        long quizId = (long) jsonObject.get("id");
                        JSONArray questions = jsonObject.getJSONArray("questions");
                        for (int i = 0; i < questions.length(); i++) {
                            ArrayList<HashMap<String, String>> answersList = new ArrayList<>();
                            JSONObject question = questions.getJSONObject(i);
                            JSONArray answers = question.getJSONArray("answers");
                            String questionText = question.getString("text");
                            int questionOrder = question.getInt("order");

                            HashMap<String, String> questionHashMap = new HashMap<>();

                            questionHashMap.put(KEY_QUESTIONS_QUIZ_ID, Long.toString(quizId));
                            questionHashMap.put(KEY_QUESTIONS_TEXT, questionText);
                            questionHashMap.put(KEY_QUESTIONS_ORDER, Integer.toString(questionOrder));


                            db.addQuestion(questionHashMap);
                            int questionId = db.getQuestionIdByQuizIdOrder(quizId, questionOrder);
                            for (int j = 0; j < answers.length(); j++) {
                                HashMap<String, String> answersHashMap = new HashMap<>();
                                JSONObject answer = answers.getJSONObject(j);
                                int answerOrder = answer.getInt("order");
                                String answerText = answer.getString("text");
                                if (answer.has("isCorrect")) {
                                    answersHashMap.put(KEY_ANSWERS_IS_CORRECT, Integer.toString(
                                            answer.getInt("isCorrect")
                                    ));
                                }
                                answersHashMap.put(KEY_ANSWERS_ORDER, Integer.toString(answerOrder));
                                answersHashMap.put(KEY_ANSWERS_TEXT, answerText);
                                answersHashMap.put(KEY_QUESTIONS_ID, Integer.toString(questionId));

                                answersList.add(answersHashMap);
                            }
                            if (answersList.size() > 0) {
                                db.addAnswers(answersList);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("MAIN", "Questions was inserted");
        } else Log.d("MAIN", "0 questions was inserted");
    }
}
