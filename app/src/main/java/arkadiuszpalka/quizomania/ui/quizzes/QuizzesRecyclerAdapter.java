package arkadiuszpalka.quizomania.ui.quizzes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.ui.quiz.QuizActivity;
import arkadiuszpalka.quizomania.utils.AppConstants;


public class QuizzesRecyclerAdapter extends RecyclerView.Adapter<QuizzesRecyclerAdapter.QuizzesViewHolder> {

    private final QuizzesListPresenter presenter;

    QuizzesRecyclerAdapter(QuizzesListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public QuizzesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuizzesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.quizzes_item, parent, false));
    }

    @Override
    public void onBindViewHolder(QuizzesViewHolder holder, int position) {
        presenter.onBindQuizzesRowViewAtPosition(position, holder);
    }

    @Override
    public int getItemCount() {
        return presenter.getQuizzesRowsCount();
    }

    public class QuizzesViewHolder extends RecyclerView.ViewHolder implements QuizzesMvp.RowView {

        private TextView quizTitle, quizState;
        private long id;
        private boolean isSolved;
        private final Context context;

        QuizzesViewHolder(final View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.text_quiz_item_title);
            quizState = itemView.findViewById(R.id.text_quiz_item_state);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.setStateQuizActivity(id);
                    Intent intent = QuizActivity.getStartIntent(context);
                    intent.putExtra(AppConstants.EXTRA_QUIZ_ID, id);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        }

        @Override
        public void setQuizTitle(String quizTitle) {
            this.quizTitle.setText(quizTitle);
        }

        @Override
        public void setQuizState(String quizState) {
            this.quizState.setText(quizState);
        }

        @Override
        public void setQuizState(int resId) {
            setQuizState(context.getString(resId));
        }

        @Override
        public void setQuizState(String quizState, byte progress) {
            setQuizState(String.format(Locale.getDefault(), quizState + " %d%%", progress));
        }

        @Override
        public void setQuizState(int resId, byte progress) {
            setQuizState(String.format(Locale.getDefault(), context.getString(resId) + " %d%%", progress));
        }

        @Override
        public boolean isSolved() {
            return isSolved;
        }

        @Override
        public void setSolved(boolean solved) {
            isSolved = solved;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public void setId(long id) {
            this.id = id;
        }

    }
}
