package arkadiuszpalka.quizomania.ui.summary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;
import arkadiuszpalka.quizomania.ui.quiz.QuizActivity;
import arkadiuszpalka.quizomania.ui.quizzes.QuizzesActivity;

import static arkadiuszpalka.quizomania.utils.AppConstants.*;

public class SummaryActivity extends BaseActivity implements SummaryMvp.View {

    private long quizId;
    private SummaryPresenter<SummaryMvp.View> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        TextView resultText = findViewById(R.id.text_summary_result);

        presenter = new SummaryPresenter<>(AppDataManager.getInstance(getApplicationContext()));
        presenter.onAttach(this);

        Intent extras = getIntent();
        if (extras != null) {
            quizId = extras.getLongExtra(EXTRA_QUIZ_ID, 0);
            byte questionCount = extras.getByteExtra(EXTRA_QUESTION_COUNT, (byte) 0);
            byte score = extras.getByteExtra(EXTRA_QUIZ_SCORE, (byte) 0);

            if (quizId != 0 || questionCount != 0) {
                resultText.setText(String.format(Locale.getDefault(), "%d%%", (byte) ((score / (float) questionCount) * 100)));
            } else {
                openQuizzesActivity();
                onError(R.string.occurred_unknown_error);
                return;
            }
        } else {
            openQuizzesActivity();
            onError(R.string.occurred_unknown_error);
            return;
        }

        findViewById(R.id.button_to_quizzes_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQuizzesActivity();
            }
        });

        findViewById(R.id.button_start_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQuizActivity();
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void openQuizzesActivity() {
        startActivity(QuizzesActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void openQuizActivity() {
        presenter.onStartAgain(quizId);
        Intent intent = QuizActivity.getStartIntent(this);
        intent.putExtra(EXTRA_QUIZ_ID, quizId);
        startActivity(intent);
        finish();
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SummaryActivity.class);
    }
}
