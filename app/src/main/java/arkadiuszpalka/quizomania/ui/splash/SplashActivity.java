package arkadiuszpalka.quizomania.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.activity.QuizzesActivity;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;


public class SplashActivity extends BaseActivity implements SplashMvp.View {

    private TextView stateText;
    private ProgressBar progressBar;
    private SplashPresenter<SplashMvp.View> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        stateText = findViewById(R.id.text_splash_state);
        progressBar = findViewById(R.id.progressBar_splash);

        presenter.onAttach(SplashActivity.this);
        presenter.doUpdateDatabase();
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
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openQuizzesActivity() {
        startActivity(new Intent(this, QuizzesActivity.class));
        finish();
    }
}
