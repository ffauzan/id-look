package uk.haku.idlook.utils;

import emu.grasscutter.utils.Language;

public class LanguageTools {
    private static String[] langList = Language.TextStrings.ARR_LANGUAGES;

    public static boolean IsLanguageExist(String selectedLangCode) {
        for (String langCode : langList) {
            if (langCode.equalsIgnoreCase(selectedLangCode)) {
                return true;
            }
        }
        return false;
    }
}
