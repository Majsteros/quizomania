package arkadiuszpalka.quizomania.ui.splash;

public interface SplashMvp {

    interface View {

        void setStateText(String message);

        void setStateText(int resId);

        void openQuizzesActivity();
    }

    interface Presenter{

        void onSuccessUpdate();

        void onErrorUpdate();
    }
}
