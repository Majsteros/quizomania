package arkadiuszpalka.quizomania.ui.splash;

import arkadiuszpalka.quizomania.data.DataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;

public class SplashPresenter<V extends SplashMvp.View> extends BasePresenter<V> implements SplashMvp.Presenter<V> {

    SplashPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onSuccessUpdate() {
        getActivityView().openQuizzesActivity();
    }

    @Override
    public void onErrorUpdate() {
        getActivityView().onError("Test");
    }

    @Override
    public void doUpdateDatabase() {
        getDataManager().syncApiData();
    }
}
