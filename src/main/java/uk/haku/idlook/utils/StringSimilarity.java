package uk.haku.idlook.utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;


public class StringSimilarity {
    public static double Fuzzy(String x, String y) {
        return (FuzzySearch.tokenSetRatio(x.toLowerCase(), y.toLowerCase()) + FuzzySearch.ratio(x.toLowerCase(), y.toLowerCase()))/2;
    }
}

