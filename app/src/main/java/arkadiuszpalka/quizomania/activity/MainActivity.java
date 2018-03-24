package arkadiuszpalka.quizomania.activity;

import android.app.Activity;
import android.content.Context;
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
        final DatabaseHandler db = DatabaseHandler.getInstance(getApplicationContext());

        findViewById(R.id.debug_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.debug_quizzes)).setText(db.getTableAsString("quizzes"));
                ((TextView)findViewById(R.id.debug_categories)).setText(db.getTableAsString("categories"));
                ((TextView)findViewById(R.id.debug_questions)).setText(db.getTableAsString("questions"));
                ((TextView)findViewById(R.id.debug_answers)).setText(db.getTableAsString("answers"));
                ((TextView)findViewById(R.id.debug_rates)).setText(db.getTableAsString("rates"));
            }
        });
    }

    public static class GetQuestions extends AsyncTask<Long, Void, ArrayList<String>> {
        private final WeakReference<Activity> weakReference;
        private ArrayList<Long> quizzesIdsList;
        private DatabaseHandler db;

        public GetQuestions(Activity activity) {
            weakReference = new WeakReference<>(activity);
            if (activity == null || activity.isFinishing()) {
                return;
            }
            db = DatabaseHandler.getInstance(activity.getApplicationContext());
            quizzesIdsList = db.getQuizzesIds();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<String> doInBackground(Long... longs) {
            ArrayList<String> results = new ArrayList<>();
            if (quizzesIdsList.size() > 0) {
                for (Long id : longs) {
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
            if (results.size() > 0 ) {
                for (String result : results) {
                    try {
                        JSONArray questions = new JSONObject(result).getJSONArray("items");
                        ArrayList<HashMap<String, String>> questionsList = new ArrayList<>();
                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject question = questions.getJSONObject(i);
                            JSONArray answers = question.getJSONArray("answers");
                            String questionText = question.getString("text");
                            int questionOrder = question.getInt("order");
                            long quizId = (long) question.get("id");
                            for (int j = 0; j < answers.length(); j++) {
                                JSONObject answer = answers.getJSONObject(j);
                                int answerOrder = answer.getInt("order");
                                String answerText = answer.getString("text");
                                if (answer.has("isCorrect")) {
                                    int answerIsCorrect = answer.getInt("isCorrect");
                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static class GetQuizzes extends AsyncTask<Void, Void, String> {
        private final WeakReference<Activity> weakReference;

        public GetQuizzes(Activity activity) {
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
            Log.d("MAIN", "Result = " + result);
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
        }
    }
}
