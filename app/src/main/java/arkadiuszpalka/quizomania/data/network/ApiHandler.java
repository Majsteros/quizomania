package arkadiuszpalka.quizomania.data.network;

import java.util.ArrayList;

public interface ApiHandler {

    ArrayList<Long> downloadQuizzes();

    void downloadQuestions(ArrayList<Long> ids);
}
