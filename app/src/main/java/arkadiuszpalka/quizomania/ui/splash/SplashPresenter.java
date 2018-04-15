package arkadiuszpalka.quizomania.ui.splash;

import arkadiuszpalka.quizomania.R;
import arkadiuszpalka.quizomania.data.AppDataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;

public class SplashPresenter<V extends SplashMvp.View> extends BasePresenter<V> implements SplashMvp.Presenter<V> {

    SplashPresenter(AppDataManager appDataManager) {
        super(appDataManager);
    }

    @Override
    public void onSuccessUpdate() {
        getActivityView().openQuizzesActivity();
    }

    @Override
    public void onErrorUpdate() {
        getActivityView().onError(R.string.occurred_update_error);
    }

    @Override
    public void doUpdateDatabase() {
        if (getActivityView().isNetworkConnected()) {
            getActivityView().showProgress();
            getActivityView().hideButtons();
            getAppDataManager().syncApiData(SplashPresenter.this);
        } else {
            getActivityView().hideProgress();
            getActivityView().onError(R.string.no_connection_error);
            getActivityView().showButtons();
        }
    }
}
