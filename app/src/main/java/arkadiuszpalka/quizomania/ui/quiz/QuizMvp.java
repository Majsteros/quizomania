package arkadiuszpalka.quizomania.ui.quiz;

import arkadiuszpalka.quizomania.ui.base.BaseMvp;

public interface QuizMvp {

    interface View extends BaseMvp.View {
        void setProgress(byte progress);

        void setTitle(String title);

        void setTitle(int resId);

        void addRadioButton(long quizId, byte questionCount, byte questionOrder, byte quizScore, String answerText, byte answerOrder, byte answerIsCorrect);

        void openQuizzesActivity();

        void openSummaryActivity(long quizId, byte questionCount, byte quizScore);

        void openQuizActivity(long quizId, byte questionCount, byte quizScore, byte questionOrder);

    }
    interface Presenter<V extends BaseMvp.View> extends BaseMvp.Presenter<V> {

        void decideState(long quizId, byte questionOrder, byte questionCount, byte quizScore);

        void nextQuestion(long quizId, byte questionCount, byte questionOrder, byte quizScore, boolean addScore);
    }

}
