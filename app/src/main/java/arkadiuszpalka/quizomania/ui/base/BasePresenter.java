package arkadiuszpalka.quizomania.ui.base;


import arkadiuszpalka.quizomania.data.AppDataManager;

public class BasePresenter<V extends BaseMvp.View> implements BaseMvp.Presenter<V> {

    private final AppDataManager appDataManager;
    private V activityView;

    public BasePresenter(AppDataManager dataManager) {
        this.appDataManager = dataManager;
    }

    public AppDataManager getAppDataManager() {
        return appDataManager;
    }

    public V getActivityView() {
        return activityView;
    }

    public boolean isViewAttached() {
        return activityView != null;
    }

    @Override
    public void onAttach(V view) {
        this.activityView = view;
    }

    @Override
    public void onDetach() {
        activityView = null;
    }
}
