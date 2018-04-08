package arkadiuszpalka.quizomania.data;

import arkadiuszpalka.quizomania.data.database.DatabaseHandler;
import arkadiuszpalka.quizomania.data.network.ApiHandler;

public interface DataManager extends DatabaseHandler, ApiHandler {

    void syncApiData();
}
