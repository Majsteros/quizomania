package arkadiuszpalka.quizomania.data.network;

import java.util.ArrayList;
import java.util.HashMap;

public interface ApiHandler {

    String request(String url);

    ArrayList<Long> checkNewQuizzes(ArrayList<Long> dbIds, String apiResult);

    ArrayList<HashMap<String, String>> downloadQuizzes(ArrayList<Long> apiIds, String apiResult);

    ArrayList<HashMap<String, String>> downloadQuestions(ArrayList<Long> ids);

    ArrayList<HashMap<String, String>> downloadAnswers(long quizId, int questionId, int questionOrder);
}
