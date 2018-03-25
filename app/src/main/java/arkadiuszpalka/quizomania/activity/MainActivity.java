package arkadiuszpalka.quizomania.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.handler.DatabaseHandler;
import arkadiuszpalka.quizomania.handler.HttpHandler;

import static arkadiuszpalka.quizomania.handler.DatabaseHandler.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetQuizzes(this).execute();
        new GetQuestions(this).execute();
    }

    public static class GetQuestions extends AsyncTask<Long, Void, ArrayList<String>> {
        private DatabaseHandler db;

        private GetQuestions(Activity activity) {
            if (activity == null || activity.isFinishing()) {
                return;
            }
            db = DatabaseHandler.getInstance(activity.getApplicationContext());
        }

        @Override
        protected ArrayList<String> doInBackground(Long... params) {
            ArrayList<String> results = new ArrayList<>();
            ArrayList<Long> ids = db.getQuizzesIds();
            if (ids.size() > 0) {
                for (Long id : ids) {
                    try {
                        results.add(
                                new HttpHandler().request("http://quiz.o2.pl/api/v1/quiz/" + id + "/0")
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(isCancelled()) break;
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<String> results) {
            super.onPostExecute(results);
            Log.d("MAIN", "Inserting questions");
            if (results.size() > 0 ) {
                for (String result : results) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray questions = jsonObject.getJSONArray("questions");
                        ArrayList<HashMap<String, String>> questionsList = new ArrayList<>();
                        ArrayList<HashMap<String, String>> answersList = new ArrayList<>();
                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject question = questions.getJSONObject(i);
                            JSONArray answers = question.getJSONArray("answers");
                            String questionText = question.getString("text");
                            int questionOrder = question.getInt("order");
                            long quizId = (long) jsonObject.get("id");
                            for (int j = 0; j < answers.length(); j++) {
                                JSONObject answer = answers.getJSONObject(j);
                                HashMap<String, String> answersHashMap = new HashMap<>();
                                int answerOrder = answer.getInt("order");
                                String answerText = answer.getString("text");
                                if (answer.has("isCorrect")) {
                                    answersHashMap.put(KEY_ANSWERS_IS_CORRECT, Integer.toString(
                                            answer.getInt("isCorrect")
                                    ));
                                }

                                answersHashMap.put(KEY_ANSWERS_ORDER, Integer.toString(answerOrder));
                                answersHashMap.put(KEY_ANSWERS_TEXT, answerText);
                                answersHashMap.put(KEY_QUESTIONS_QUIZ_ID, Long.toString(quizId));

                                answersList.add(answersHashMap);
                            }
                            HashMap<String, String> questionHashMap = new HashMap<>();

                            questionHashMap.put(KEY_QUESTIONS_QUIZ_ID, Long.toString(quizId));
                            questionHashMap.put(KEY_QUESTIONS_TEXT, questionText);
                            questionHashMap.put(KEY_QUESTIONS_ORDER, Integer.toString(questionOrder));

                            questionsList.add(questionHashMap);
                        }
                        if (questionsList.size() > 0) {
                            db.addQuestions(questionsList);
                        }
                        if (answersList.size() > 0) {
                            for (HashMap<String, String> map : answersList) {
                                map.put(KEY_QUESTIONS_ID,
                                        Integer.toString(
                                                db.getQuestionIdByQuizId(
                                                        Long.parseLong(
                                                                map.get(KEY_QUESTIONS_QUIZ_ID)))));
                            }
                            db.addAnswers(answersList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("MAIN", "Questions was inserted");
        }
    }


    public static class GetQuizzes extends AsyncTask<Void, Void, String> {
        private final WeakReference<Activity> weakReference;

        private GetQuizzes(Activity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                 return new HttpHandler().request("http://quiz.o2.pl/api/v1/quizzes/0/100");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("MAIN", "Inserting quizzes");
            Activity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            Context context = activity.getApplicationContext();

            if (result != null) {
                ArrayList<HashMap<String, String>> quizzesList = new ArrayList<>();
                ArrayList<String> categoriesList = new ArrayList<>();
                try {
                    JSONArray items = new JSONObject(result).getJSONArray("items");
                    DatabaseHandler db = DatabaseHandler.getInstance(context);
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject jsonObject = items.getJSONObject(i);
                        String categoryName = jsonObject.getJSONArray("categories")
                                .getJSONObject(0)
                                .getString("name");
                        long quizId = (long) jsonObject.get("id");
                        String quizTitle = jsonObject.getString("title");
                        String quizContent = jsonObject.getString("content");

                        HashMap<String, String> quiz = new HashMap<>();

                        if (db.getCountOfQuizzesById(quizId) == 0) {
                            quiz.put(KEY_QUIZZES_ID, Long.toString(quizId));
                            quiz.put(KEY_QUIZZES_TITLE, quizTitle);
                            quiz.put(KEY_QUIZZES_CONTENT, quizContent);
                            quiz.put(KEY_CATEGORIES_NAME, categoryName);

                            categoriesList.add(categoryName);
                            quizzesList.add(quiz);
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
            } else {
                Toast.makeText(context, "Brak danych", Toast.LENGTH_LONG).show();
            }
            Log.d("MAIN", "Quizzes was inserted");
        }
    }
}
