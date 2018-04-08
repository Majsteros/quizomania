package arkadiuszpalka.quizomania.ui.base;


import arkadiuszpalka.quizomania.data.DataManager;

public abstract class BasePresenter<V extends BaseMvp.View> implements BaseMvp.Presenter<V> {

    private final DataManager dataManager;
    private V activityView;

    protected BasePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public V getActivityView() {
        return activityView;
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
