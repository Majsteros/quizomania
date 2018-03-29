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
import java.util.Locale;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.activity.QuizActivity;
import arkadiuszpalka.quizomania.handler.DatabaseHandler;
import arkadiuszpalka.quizomania.handler.SeedHandler;

import static arkadiuszpalka.quizomania.handler.DatabaseHandler.*;
import static arkadiuszpalka.quizomania.handler.SeedHandler.*;

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
        private boolean isSolved;

        ViewHolder(View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.text_quiz_item_title);
            quizState = itemView.findViewById(R.id.text_quiz_item_state);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = DatabaseHandler.getInstance(context);
                    HashMap<String, String> state = SeedHandler.readSeed(db.getSeed(id));
                    isSolved = Boolean.parseBoolean(state.get(KEY_QUIZ_IS_SOLVED));
                    if (isSolved) {
                        db.removeSeed(id);
                    }
                    Intent intent = new Intent(context, QuizActivity.class);
                    intent.putExtra(EXTRA_QUIZ_ID, id);
                    context.startActivity(intent);
                }
            });
        }

        public Context getContext() {
            return context;
        }

        public long getId() {
            return id;
        }

        private boolean isSolved() {
            return isSolved;
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
        holder.setId(Long.parseLong(quizzesList.get(position).get(KEY_QUIZZES_ID)));

        Context context = holder.getContext();
        HashMap<String, String> state = SeedHandler.readSeed(
                DatabaseHandler.getInstance(context)
                        .getSeed(holder.getId()));
        if (state.get(KEY_QUIZ_PROGRESS) != null) {
            int progress = Integer.parseInt(state.get(KEY_QUIZ_PROGRESS));
            if (holder.isSolved()) {
                holder.quizState.setText(String.format(
                        Locale.getDefault(),
                        context.getString(R.string.quiz_last_score) + " %d%%",
                        progress
                ));
            } else if (progress == 0) {
                holder.quizState.setText(context.getString(R.string.quiz_tap_to_start));
            } else {
                holder.quizState.setText(String.format(
                        Locale.getDefault(),
                        context.getString(R.string.quiz_progress_solve) + " %d%%",
                        progress
                ));
            }
        } else {
            holder.quizState.setText(context.getString(R.string.quiz_tap_to_start));
        }

    }

    @Override
    public int getItemCount() {
        return quizzesList.size();
    }
}
