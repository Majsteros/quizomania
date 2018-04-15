package arkadiuszpalka.quizomania.ui.summary;

import arkadiuszpalka.quizomania.ui.base.BaseMvp;

public interface SummaryMvp {

    interface View extends BaseMvp.View {

        void openQuizzesActivity();

        void openQuizActivity();
    }

    interface Presenter<V extends SummaryMvp.View> extends BaseMvp.Presenter<V> {

        void onStartAgain(long quizId);
    }

}
