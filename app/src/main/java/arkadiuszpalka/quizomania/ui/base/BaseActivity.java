package arkadiuszpalka.quizomania.ui.base;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import arkadiuszpalka.quizomania.R;

public abstract class BaseActivity extends AppCompatActivity implements BaseMvp.View {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showProgress() {
        //TODO open dialog for every activity
    }

    @Override
    public void hideProgress() {
        //TODO close dialog for every activity
    }

    @Override
    public void onError(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
        dialog.setTitle(getString(R.string.default_title_error));
        dialog.setNeutralButton(R.string.dialog_ok, null);
        dialog.create().show();
    }

    @Override
    public void onError(int resId) {
        onError(getString(resId));
    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.occurred_unknown_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(int resId) {
        showMessage(getString(resId));
    }

    @Override
    public boolean isNetworkConnected() {
        return false; //TODO make Utils method to check connection
    }
}
