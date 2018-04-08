package arkadiuszpalka.quizomania.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.database.AppDatabaseHandler;

import static arkadiuszpalka.quizomania.activity.QuizActivity.EXTRA_QUESTION_COUNT;
import static arkadiuszpalka.quizomania.activity.QuizActivity.EXTRA_QUIZ_SCORE;
import static arkadiuszpalka.quizomania.adapter.QuizzesRecyclerViewAdapter.EXTRA_QUIZ_ID;

public class SummaryActivity extends AppCompatActivity {
    private long quizId;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        context = getApplicationContext();

        TextView resultText = findViewById(R.id.text_summary_result);

        Intent extras = getIntent();
        if (extras != null) {
            quizId = extras.getLongExtra(EXTRA_QUIZ_ID, 0);
            int questionCount = extras.getIntExtra(EXTRA_QUESTION_COUNT, 0);
            int score = extras.getIntExtra(EXTRA_QUIZ_SCORE, 0);
            if (quizId != 0 || questionCount != 0) {
                resultText.setText(String.format(Locale.getDefault(), "%d%%", (int) ((score / (float) questionCount) * 100)));
            }
        } else {
            Toast.makeText(context, context.getString(R.string.occurred_unknown_error), Toast.LENGTH_LONG).show();
            startActivity(new Intent(context, QuizzesActivity.class));
            finish();
            return;
        }

        findViewById(R.id.button_to_quizzes_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, QuizzesActivity.class));
                finish();
            }
        });

        findViewById(R.id.button_start_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabaseHandler.getInstance(context.getApplicationContext()).removeSeed(quizId);
                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra(EXTRA_QUIZ_ID, quizId);
                startActivity(intent);
                finish();
            }
        });
    }
}
