package arkadiuszpalka.quizomania.ui.quizzes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BaseActivity;

public class QuizzesActivity extends BaseActivity implements QuizzesMvp.View {

    private QuizzesPresenter<QuizzesMvp.View> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        AppDataManager appDataManager = AppDataManager.getInstance(getApplicationContext());

        presenter = new QuizzesPresenter<>(appDataManager);
        QuizzesListPresenter listPresenter = new QuizzesListPresenter(appDataManager, presenter.getQuizzes());

        presenter.onAttach(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_quizzes);
        RecyclerView.LayoutManager layoutManagerRecyclerView = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerRecyclerView);
        RecyclerView.Adapter recyclerViewAdapter = new QuizzesRecyclerAdapter(listPresenter);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
