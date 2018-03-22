package arkadiuszpalka.quizomania.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.handler.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHandler db = DatabaseHandler.getInstance(getApplicationContext());
    }
}
