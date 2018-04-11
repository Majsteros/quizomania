package arkadiuszpalka.quizomania.ui.quizzes;

import java.util.ArrayList;
import java.util.HashMap;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.utils.SeedHandler;

import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUIZZES_TITLE;
import static arkadiuszpalka.quizomania.utils.SeedHandler.KEY_QUIZ_IS_SOLVED;
import static arkadiuszpalka.quizomania.utils.SeedHandler.KEY_QUIZ_PROGRESS;

public class QuizzesListPresenter extends QuizzesPresenter {

    private final ArrayList<HashMap<String, String>> quizzes;

    public QuizzesListPresenter(AppDataManager dataManager, ArrayList<HashMap<String, String>> quizzes) {
        super(dataManager);
        this.quizzes = quizzes;
    }

    public void onBindQuizzesRowViewAtPosition(int position, QuizzesMvp.RowView rowView) {
        HashMap<String, String> quiz = quizzes.get(position);
        rowView.setQuizTitle(quiz.get(KEY_QUIZZES_TITLE));
        rowView.setId(Long.parseLong(quiz.get(KEY_QUIZZES_ID)));
        HashMap<String, String> state = SeedHandler.readSeed(getAppDataManager().getSeed(rowView.getId()));

        if (state.get(KEY_QUIZ_PROGRESS ) != null) {
            byte progress = Byte.parseByte(state.get(KEY_QUIZ_PROGRESS));
            boolean isSolved = Boolean.parseBoolean(state.get(KEY_QUIZ_IS_SOLVED));

            if (progress == 0) {
                rowView.setQuizState(R.string.quiz_tap_to_start);
            } else if (isSolved) {
                rowView.setQuizState(R.string.quiz_last_score, progress);
            } else {
                rowView.setQuizState(R.string.quiz_progress_solve, progress);
            }
        } else {
            rowView.setQuizState(R.string.quiz_tap_to_start);
        }
    }

    public int getQuizzesRowsCount() {
        return quizzes.size();
    }
}
