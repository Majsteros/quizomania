package arkadiuszpalka.quizomania.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;
import arkadiuszpalka.quizomania.ui.quizzes.QuizzesActivity;
import arkadiuszpalka.quizomania.utils.NetworkUtils;


public class SplashActivity extends BaseActivity implements SplashMvp.View {

    private TextView stateText;
    private ProgressBar progressBar;
    private Button buttonRetry, buttonOffline;
    SplashPresenter<SplashMvp.View> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        stateText = findViewById(R.id.text_splash_state);
        progressBar = findViewById(R.id.progressBar_splash);
        buttonRetry = findViewById(R.id.button_start_update);
        buttonOffline = findViewById(R.id.button_play_offline);

        presenter = new SplashPresenter<>(AppDataManager.getInstance(getApplicationContext()));
        presenter.onAttach(this);

        presenter.doUpdateDatabase();

        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.doUpdateDatabase();
            }
        });

        buttonOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQuizzesActivity();
            }
        });
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
        stateText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        stateText.setVisibility(View.GONE);
    }

    @Override
    public void showButtons() {
        buttonRetry.setVisibility(View.VISIBLE);
        buttonOffline.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideButtons() {
        buttonRetry.setVisibility(View.GONE);
        buttonOffline.setVisibility(View.GONE);
    }
}
