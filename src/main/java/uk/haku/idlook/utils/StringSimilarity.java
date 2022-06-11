package uk.haku.idlook.utils;

import org.apache.commons.lang3.StringUtils;;


public class StringSimilarity {
    public static double LevenshteinDistance(String x, String y) {
        double maxLength = Double.max(x.length(), y.length());
        if (maxLength > 0) {
            // optionally ignore case if needed
            return (maxLength - StringUtils.getLevenshteinDistance(x, y)) / maxLength;
        }
        return 0.1;
    }
}