package arkadiuszpalka.quizomania.data.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public interface DatabaseHandler {

    void addAnswers(ArrayList<HashMap<String, String>> answers);

    void addCategories(ArrayList<String> categories);

    void addSeed(long quizId, String seed);

    void addQuestion(HashMap<String, String> question);

    void addQuestions(ArrayList<HashMap<String, String>> questions);

    void addQuizzes(ArrayList<HashMap<String, String>> quizzes);

    ArrayList<TreeMap<String, Object>> getAnswersByQuestionId(int id);

    int getCategoryIdByName(String name);

    int getCountOfSeedsById(long id);

    int getCountOfQuizzesById(long id);

    String getSeed(long id);

    HashMap<String, Object> getQuestionByQuizIdOrder(long id, int order);

    byte getCountOfQuestionsById(long id);

    int getQuestionIdByQuizId(long id);

    int getQuestionIdByQuizIdOrder(long id, int order);

    ArrayList<HashMap<String, String>> getQuizzes();

    ArrayList<Long> getQuizzesIds();

    void updateSeed(long id, String seed);

    void removeSeed(long id);
}
