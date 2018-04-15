package arkadiuszpalka.quizomania.ui.quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;
import arkadiuszpalka.quizomania.utils.SeedHandler;

import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_ANSWERS_IS_CORRECT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_ANSWERS_ORDER;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_ANSWERS_TEXT;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ID;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ORDER;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_TEXT;
import static arkadiuszpalka.quizomania.utils.AppConstants.KEY_QUIZ_SCORE;

public class QuizPresenter<V extends QuizMvp.View> extends BasePresenter<V> implements QuizMvp.Presenter<V> {

    private static final String TAG = "QuizPresenter";

    QuizPresenter(AppDataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void decideState(long quizId, byte questionOrder, byte questionCount, byte quizScore) {
        if (questionOrder == 1 &&
                getAppDataManager().getCountOfSeedsById(quizId) == 0) {

            getAppDataManager().addSeed(quizId,
                    SeedHandler.generateSeed(questionOrder,
                            (byte) 0,
                            false,
                            (byte) 0));

        } else {
            HashMap<String, String> state =
                    SeedHandler.readSeed(getAppDataManager().getSeed(quizId));

            questionOrder = Byte.parseByte(state.get(KEY_QUESTIONS_ORDER));
            quizScore = Byte.parseByte(state.get(KEY_QUIZ_SCORE));
        }

        HashMap<String, Object> question =
                getAppDataManager().getQuestionByQuizIdOrder(quizId, questionOrder);

        questionCount = getAppDataManager().getCountOfQuestionsById(quizId);

        int id = (int) question.get(KEY_QUESTIONS_ID);
        questionOrder = (byte) question.get(KEY_QUESTIONS_ORDER);

        getActivityView().setTitle((String) question.get(KEY_QUESTIONS_TEXT));
        getActivityView().setProgress((byte) ((questionOrder / (float) questionCount) * 100));

        ArrayList<TreeMap<String, Object>> answers =
                getAppDataManager().getAnswersByQuestionId(id);

        for (TreeMap<String, Object> answer : answers) {
            String answerText = (String) answer.get(KEY_ANSWERS_TEXT);
            byte answerOrder = (byte) answer.get(KEY_ANSWERS_ORDER);
            byte answerIsCorrect = (byte) answer.get(KEY_ANSWERS_IS_CORRECT);

            getActivityView().addRadioButton(quizId, questionCount, questionOrder, quizScore, answerText, answerOrder, answerIsCorrect);
        }
    }

    @Override
    public void nextQuestion(long quizId, byte questionCount, byte questionOrder, byte quizScore, boolean addScore) {
        if (addScore) {
            quizScore++;
            getActivityView().showMessage(R.string.answer_good);
        } else {
            getActivityView().showMessage(R.string.answer_bad);
        }

        if (questionOrder == questionCount) {
            getAppDataManager().updateSeed(quizId,
                    SeedHandler.generateSeed(questionOrder,
                            quizScore,
                            true,
                            (byte) ((quizScore / (float) questionCount) * 100)));
            getActivityView().openSummaryActivity(quizId, questionCount, quizScore);
        } else {
            questionOrder++;
            getAppDataManager().updateSeed(quizId,
                    SeedHandler.generateSeed(questionOrder,
                            quizScore,
                            false,
                            (byte) ((questionOrder / (float) questionCount) * 100)));
            getActivityView().openQuizActivity(quizId, questionCount, quizScore, questionOrder);
        }
    }
}
