package org.example.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String removeDiacritics(String inputString) {
        if (inputString == null) {
            return "";
        }

        String normalized = Normalizer.normalize(inputString, Normalizer.Form.NFD);
        return DIACRITICS_PATTERN.matcher(normalized).replaceAll("");
    }

    public static double parseDoubleOrDefault(String inputStringParameter, double defaultValue) {
        if (inputStringParameter == null || inputStringParameter.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(inputStringParameter);
    }
}
