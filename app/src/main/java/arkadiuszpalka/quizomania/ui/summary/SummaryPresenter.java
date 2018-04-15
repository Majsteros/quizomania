package arkadiuszpalka.quizomania.ui.summary;

import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;

public class SummaryPresenter<V extends SummaryMvp.View> extends BasePresenter<V> implements SummaryMvp.Presenter<V> {

    SummaryPresenter(AppDataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onStartAgain(long quizId) {
        getAppDataManager().removeSeed(quizId);
    }
}
