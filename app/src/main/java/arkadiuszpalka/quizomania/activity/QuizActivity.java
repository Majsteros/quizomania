package arkadiuszpalka.quizomania.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.handler.DatabaseHandler;
import static arkadiuszpalka.quizomania.handler.DatabaseHandler.*;
import static arkadiuszpalka.quizomania.adapter.QuizzesRecyclerViewAdapter.EXTRA_QUIZ_ID;

public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        TextView title = findViewById(R.id.text_quiz_title);
        ProgressBar progressBar = findViewById(R.id.progressbar_quiz);

        Context context = getApplicationContext();
        DatabaseHandler db = DatabaseHandler.getInstance(context);

        long quizId;
        Intent intent = getIntent();
        if (intent != null) {
            quizId = intent.getLongExtra(EXTRA_QUIZ_ID, 0);
            if (quizId != 0) {
                HashMap<String, Object> question = db.getQuestionByQuizId(quizId);

                title.setText((String) question.get(KEY_QUESTIONS_TEXT));
                int id = (Integer) question.get(KEY_QUESTIONS_ID);
                int order = (Integer) question.get(KEY_QUESTIONS_ORDER);

                ArrayList<TreeMap<String, Object>> answers = db.getAnswersByQuestionId(id);
                for (TreeMap<String, Object> answer : answers) {
                    //tu wszystkie odp
                }
            }
        } else {
            Toast.makeText(context, "Wystąpił nieznany błąd", Toast.LENGTH_LONG).show();
            startActivity(new Intent(context, QuizzesActivity.class));
            finish();
        }
    }
}
