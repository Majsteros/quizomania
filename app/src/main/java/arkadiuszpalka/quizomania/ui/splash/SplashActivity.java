package arkadiuszpalka.quizomania.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.ui.quizzes.QuizzesActivity;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;


public class SplashActivity extends BaseActivity implements SplashMvp.View {

    private TextView stateText;
    private ProgressBar progressBar;
    SplashPresenter<SplashMvp.View> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        stateText = findViewById(R.id.text_splash_state);
        progressBar = findViewById(R.id.progressBar_splash);

        presenter = new SplashPresenter<>(AppDataManager.getInstance(getApplicationContext()));
        presenter.onAttach(this);
        presenter.doUpdateDatabase();
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void setStateText(String message) {
        stateText.setText(message);
    }

    @Override
    public void setStateText(int resId) {
        setStateText(getString(resId));
    }

    @Override
    public void openQuizzesActivity() {
        startActivity(new Intent(this, QuizzesActivity.class));
        finish();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
