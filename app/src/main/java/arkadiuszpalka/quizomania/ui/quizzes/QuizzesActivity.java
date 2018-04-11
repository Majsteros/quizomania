package arkadiuszpalka.quizomania.ui.quizzes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.adapter.QuizzesRecyclerViewAdapter;
import arkadiuszpalka.quizomania.data.database.AppDatabaseHandler;


public class QuizzesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_quizzes);
        RecyclerView.LayoutManager layoutManagerRecyclerView = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerRecyclerView);
        RecyclerView.Adapter recyclerViewAdapter = new QuizzesRecyclerViewAdapter(
                AppDatabaseHandler.getInstance(getApplicationContext())
                        .getQuizzes()
        );
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
