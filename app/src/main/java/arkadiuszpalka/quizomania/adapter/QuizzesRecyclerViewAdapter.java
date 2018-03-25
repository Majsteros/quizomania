package arkadiuszpalka.quizomania.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.activity.QuizActivity;

import static arkadiuszpalka.quizomania.handler.DatabaseHandler.*;

public class QuizzesRecyclerViewAdapter extends RecyclerView.Adapter<QuizzesRecyclerViewAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> quizzesList;
    public static final String EXTRA_QUIZ_ID = "arkadiuszpalka.quizomania.adapter.QUIZ_ID";

    public QuizzesRecyclerViewAdapter(ArrayList<HashMap<String, String>> quizzesList) {
        this.quizzesList = quizzesList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quizTitle, quizState;
        private final Context context;
        private long id;

        ViewHolder(View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.text_quiz_item_title);
            quizState = itemView.findViewById(R.id.text_quiz_item_state);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, QuizActivity.class);
                    intent.putExtra(EXTRA_QUIZ_ID, id);
                    context.startActivity(intent);
                }
            });
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Override
    public QuizzesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizzes_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuizzesRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.quizTitle.setText(quizzesList.get(position).get(KEY_QUIZZES_TITLE));
        holder.quizState.setText(quizzesList.get(position).get(KEY_QUIZZES_ID));
        holder.setId(
                Long.parseLong(
                        quizzesList.get(position).get(KEY_QUIZZES_ID))
        );
    }

    @Override
    public int getItemCount() {
        return quizzesList.size();
    }
}
