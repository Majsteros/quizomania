package arkadiuszpalka.quizomania.ui.base;

public interface BaseMvp {

    interface View{
        void showProgress();

        void hideProgress();

        void onError(String message);

        void onError(int resId);

        void showMessage(String message);

        void showMessage(int resId);

        boolean isNetworkConnected();
    }

    interface Presenter<V extends View>{

        void onAttach(V view);

        void onDetach();
    }
}
