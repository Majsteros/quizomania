package arkadiuszpalka.quizomania.ui.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;
import arkadiuszpalka.quizomania.ui.quizzes.QuizzesActivity;
import arkadiuszpalka.quizomania.ui.summary.SummaryActivity;

import static arkadiuszpalka.quizomania.utils.AppConstants.EXTRA_QUESTION_COUNT;
import static arkadiuszpalka.quizomania.utils.AppConstants.EXTRA_QUESTION_ORDER;
import static arkadiuszpalka.quizomania.utils.AppConstants.EXTRA_QUIZ_ID;
import static arkadiuszpalka.quizomania.utils.AppConstants.EXTRA_QUIZ_SCORE;

public class QuizActivity extends BaseActivity implements QuizMvp.View{

    private QuizPresenter<QuizMvp.View> presenter;
    private TextView title;
    private ProgressBar progressBar;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        title = findViewById(R.id.text_quiz_title);
        progressBar = findViewById(R.id.progressbar_quiz);
        radioGroup = findViewById(R.id.radiogroup_quiz_answers);

        presenter = new QuizPresenter<>(AppDataManager.getInstance(getApplicationContext()));
        presenter.onAttach(this);

        final Intent extras = getIntent();
        if (extras != null) {
            final long quizId = extras.getLongExtra(EXTRA_QUIZ_ID, 0);

            if (quizId != 0) {
                byte questionOrder = extras.getByteExtra(EXTRA_QUESTION_ORDER, (byte) 1);
                byte quizScore = extras.getByteExtra(EXTRA_QUIZ_SCORE, (byte) 0);
                byte questionCount = extras.getByteExtra(EXTRA_QUESTION_COUNT, (byte) 0);
                presenter.decideState(quizId, questionOrder, questionCount, quizScore);
            }

        } else {
            openQuizzesActivity();
            onError(getString(R.string.occurred_unknown_error));
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(QuizzesActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void setProgress(byte progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    @Override
    public void addRadioButton(final long quizId, final byte questionCount, final byte questionOrder, final byte quizScore, String answerText, byte answerOrder, byte answerIsCorrect) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(answerOrder);
        radioButton.setText(answerText);
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

        if (answerIsCorrect == 1) {
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.nextQuestion(quizId, questionCount, questionOrder, quizScore, true);
                }
            });
        } else {
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.nextQuestion(quizId, questionCount, questionOrder, quizScore, false);
                }
            });
        }

        radioGroup.addView(radioButton, 0, layoutParams);
    }

    @Override
    public void openQuizzesActivity() {
        startActivity(QuizzesActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void openSummaryActivity(long quizId, byte questionCount, byte quizScore) {
        Intent intent = SummaryActivity.getStartIntent(this);
        intent.putExtra(EXTRA_QUIZ_ID, quizId);
        intent.putExtra(EXTRA_QUESTION_COUNT, questionCount);
        intent.putExtra(EXTRA_QUIZ_SCORE, quizScore);
        startActivity(intent);
        finish();
    }

    @Override
    public void openQuizActivity(long quizId, byte questionCount, byte quizScore, byte questionOrder) {
        Intent intent = QuizActivity.getStartIntent(this);
        intent.putExtra(EXTRA_QUIZ_ID, quizId);
        intent.putExtra(EXTRA_QUESTION_COUNT, questionCount);
        intent.putExtra(EXTRA_QUESTION_ORDER, questionOrder);
        intent.putExtra(EXTRA_QUIZ_SCORE, quizScore);
        startActivity(intent);
        finish();
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, QuizActivity.class);
    }
}
