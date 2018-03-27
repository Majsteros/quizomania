package arkadiuszpalka.quizomania.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    public static final String EXTRA_QUESTION_ORDER = "arkadiuszpalka.quizomania.adapter.QUESTION_ORDER";
    public static final String EXTRA_QUESTION_COUNT = "arkadiuszpalka.quizomania.adapter.QUESTION_COUNT";
    public static final String EXTRA_QUIZ_SCORE = "arkadiuszpalka.quizomania.adapter.QUIZ_SCORE";

    private int questionOrder, questionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        TextView title = findViewById(R.id.text_quiz_title);
        ProgressBar progressBar = findViewById(R.id.progressbar_quiz);
        RadioGroup radioGroup = findViewById(R.id.radiogroup_quiz_answers);

        final Context context = getApplicationContext();
        DatabaseHandler db = DatabaseHandler.getInstance(context);

        final Intent extras = getIntent();
        if (extras != null) {
            final long quizId = extras.getLongExtra(EXTRA_QUIZ_ID, 0);
            if (quizId != 0) {
                questionOrder = extras.getIntExtra(EXTRA_QUESTION_ORDER, 1);
                questionCount = extras.getIntExtra(EXTRA_QUESTION_COUNT, 0);
                final int quizScore = extras.getIntExtra(EXTRA_QUIZ_SCORE, 0);
                HashMap<String, Object> question = db.getQuestionByQuizIdOrder(quizId, questionOrder);

                int id = (int) question.get(KEY_QUESTIONS_ID);
                title.setText((String) question.get(KEY_QUESTIONS_TEXT));
                questionOrder = (int) question.get(KEY_QUESTIONS_ORDER);

                if (questionCount == 0) {
                    questionCount = db.getCountOfQuestionsById(quizId);
                    Log.d("QUIZ", "Setting question count");
                }

                progressBar.setProgress((int) ((questionOrder / (float) questionCount) * 100));

                ArrayList<TreeMap<String, Object>> answers = db.getAnswersByQuestionId(id);
                for (TreeMap<String, Object> answer : answers) {
                    String answerText = (String) answer.get(KEY_ANSWERS_TEXT);
                    int answerOrder = (Integer) answer.get(KEY_ANSWERS_ORDER);
                    int answerIsCorrect = (Integer) answer.get(KEY_ANSWERS_IS_CORRECT);

                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setId(answerOrder);
                    radioButton.setText(answerText);

                    if (answerIsCorrect == 1) {
                        radioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nextQuestion(context, quizId, questionCount, questionOrder, quizScore, true);
                            }
                        });
                    } else {
                        radioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nextQuestion(context, quizId, questionCount, questionOrder, quizScore, false);

                            }
                        });
                    }
                    radioGroup.addView(radioButton);
                }
                Log.d("QUIZ", "Quiz activity:" +
                        "\nquizId: " + quizId +
                        "\nquestionOrder: " + questionOrder +
                        "\nquestionCount: " + questionCount +
                        "\nquizScore: " + quizScore +
                        "\nprogress value: " + (int) ((questionOrder / (float) questionCount) * 100)
                );
            }
        } else {
            Toast.makeText(context, "Wystąpił nieznany błąd", Toast.LENGTH_LONG).show();
            startActivity(new Intent(context, QuizzesActivity.class));
            finish();
        }
    }
    public void nextQuestion(Context context, long quizId, int questionCount, int questionOrder, int quizScore, boolean addScore) {
        if (questionOrder == questionCount) {
            startActivity(new Intent(context, QuizzesActivity.class));
            finish();
            return;
        }
        Intent intent = new Intent(context, QuizActivity.class);
        intent.putExtra(EXTRA_QUIZ_ID, quizId);
        intent.putExtra(EXTRA_QUESTION_COUNT, questionCount);
        intent.putExtra(EXTRA_QUESTION_ORDER, ++questionOrder);
        intent.putExtra(EXTRA_QUIZ_SCORE, addScore ? ++quizScore : quizScore);
        startActivity(intent);
        finish();
    }

}
