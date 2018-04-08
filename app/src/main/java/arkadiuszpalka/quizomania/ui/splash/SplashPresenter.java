package arkadiuszpalka.quizomania.ui.splash;

import arkadiuszpalka.quizomania.data.DataManager;
import arkadiuszpalka.quizomania.ui.base.BasePresenter;

public class SplashPresenter extends BasePresenter implements SplashMvp.Presenter {


    SplashPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onSuccessUpdate() {

    }

    @Override
    public void onErrorUpdate() {

    }
}
