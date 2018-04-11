package arkadiuszpalka.quizomania.ui.quizzes;

import java.util.ArrayList;
import java.util.HashMap;

import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;

public class QuizzesPresenter<V extends QuizzesMvp.View> extends BasePresenter<V> implements QuizzesMvp.Presenter<V> {

    public QuizzesPresenter(AppDataManager dataManager) {
        super(dataManager);
    }

    @Override
    public ArrayList<HashMap<String, String>> getQuizzes() {
        return getAppDataManager().getQuizzes();
    }

}
