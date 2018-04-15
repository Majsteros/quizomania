package arkadiuszpalka.quizomania.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static arkadiuszpalka.quizomania.data.database.AppDatabaseHandler.KEY_QUESTIONS_ORDER;
import static arkadiuszpalka.quizomania.utils.AppConstants.KEY_QUIZ_IS_SOLVED;
import static arkadiuszpalka.quizomania.utils.AppConstants.KEY_QUIZ_PROGRESS;
import static arkadiuszpalka.quizomania.utils.AppConstants.KEY_QUIZ_SCORE;

public final class SeedHandler {

    public static String generateSeed(byte order, byte score, boolean isSolved, byte progress) {
        Log.d("generateSeed", "generateSeed:\norder: " + order +
                "\nscore: " + score +
                "\nisSolved: " + isSolved +
                "\nprogress: " + progress
        );
        return "o" + order + 's' + score + 'e' + isSolved + 'p' + progress;
    }

    public static HashMap<String, String> readSeed(String seed) {
        Log.d("readSeed", "input seed: " + seed);
        HashMap<String, String> state = new HashMap<>(3);
        Pattern pattern = Pattern.compile("o([0-9]{1,4})s([0-9]{1,4})e((true)?(false)?)p([0-9]{1,3})");
        Matcher matcher = pattern.matcher(seed);
        if (matcher.find()) {
            Log.d("matcher", "find:\no: " + matcher.group(1) + " | s: " + matcher.group(2) + " | e: " + matcher.group(3) + " | p: " + matcher.group(6));
            state.put(KEY_QUESTIONS_ORDER, matcher.group(1));
            state.put(KEY_QUIZ_SCORE, matcher.group(2));
            state.put(KEY_QUIZ_IS_SOLVED, matcher.group(3));
            state.put(KEY_QUIZ_PROGRESS, matcher.group(6));
        }
        Log.d("readSeed", "readSeed:\norder: " + state.get(KEY_QUESTIONS_ORDER) +
                "\nscore: " + state.get(KEY_QUIZ_SCORE) +
                "\nisSolved: " + state.get(KEY_QUIZ_IS_SOLVED) +
                "\nprogress: " + state.get(KEY_QUIZ_PROGRESS)
        );
        return state;
    }
}
