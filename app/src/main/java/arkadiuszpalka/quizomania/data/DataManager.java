package arkadiuszpalka.quizomania.data;

import arkadiuszpalka.quizomania.data.database.DatabaseHandler;
import arkadiuszpalka.quizomania.data.network.ApiHandler;
import arkadiuszpalka.quizomania.ui.splash.SplashPresenter;

public interface DataManager extends DatabaseHandler, ApiHandler {

    void syncApiData(SplashPresenter presenter);
}
