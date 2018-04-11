package arkadiuszpalka.quizomania.ui.splash;

import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;

public class SplashPresenter<V extends SplashMvp.View> extends BasePresenter<V> implements SplashMvp.Presenter<V> {

    public SplashPresenter(AppDataManager appDataManager) {
        super(appDataManager);
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
        getAppDataManager().syncApiData(SplashPresenter.this);
    }
}
