package arkadiuszpalka.quizomania.ui.quizzes;

import java.util.ArrayList;
import java.util.HashMap;

import arkadiuszpalka.quizomania.ui.base.BaseMvp;

public interface QuizzesMvp {

    interface View extends BaseMvp.View {
    }

    interface Presenter<V extends QuizzesMvp.View> extends BaseMvp.Presenter<V> {

        ArrayList<HashMap<String, String>> getQuizzes();
    }

    interface RowView {

        void setQuizTitle(String quizTitle);

        void setQuizState(String quizState);

        void setQuizState(int resId);

        void setQuizState(String quizState, byte progress);

        void setQuizState(int resId, byte progress);

        boolean isSolved();

        void setSolved(boolean solved);

        long getId();

        void setId(long id);

    }
}
