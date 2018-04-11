package arkadiuszpalka.quizomania.handler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static arkadiuszpalka.quizomania.ui.quiz.QuizActivity.KEY_QUIZ_SCORE;
import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ORDER;

public class SeedHandler {
    public static final String KEY_QUIZ_IS_SOLVED = "is_solved";
    public static final String KEY_QUIZ_PROGRESS = "progress";

    public static String generateSeed(int order, int score, boolean isSolved, int progress) {
        return "o" + order + 's' + score + 'e' + isSolved + 'p' + progress;
    }

    public static HashMap<String, String> readSeed(String seed) {
        HashMap<String, String> state = new HashMap<>(3);
        Pattern pattern = Pattern.compile("o([0-9]{1,4})s([0-9]{1,4})e((true)?(false)?)p([0-9]{1,4})");
        Matcher matcher = pattern.matcher(seed);
        if (matcher.find()) {
            state.put(KEY_QUESTIONS_ORDER, matcher.group(1));
            state.put(KEY_QUIZ_SCORE, matcher.group(2));
            state.put(KEY_QUIZ_IS_SOLVED, matcher.group(3));
            state.put(KEY_QUIZ_PROGRESS, matcher.group(6));
        }
        return state;
    }
}
