package arkadiuszpalka.quizomania.ui.splash;

import arkadiuszpalka.quizomania.ui.base.BaseMvp;

public interface SplashMvp {

    interface View extends BaseMvp.View {

        void setStateText(String message);

        void setStateText(int resId);

        void openQuizzesActivity();

        void hideProgress();

        void showButtons();

        void hideButtons();
    }

    interface Presenter<V extends SplashMvp.View> extends BaseMvp.Presenter<V> {

        void onSuccessUpdate();

        void onErrorUpdate();

        void doUpdateDatabase();
    }
}
