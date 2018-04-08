package arkadiuszpalka.quizomania.ui.splash;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;


public class SplashActivity extends BaseActivity implements SplashMvp.View {

    private TextView stateText;
    private ProgressBar progressBar;
    private SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        stateText = findViewById(R.id.text_splash_state);
        progressBar = findViewById(R.id.progressBar_splash);

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

    }
}
