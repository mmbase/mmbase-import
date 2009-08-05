/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.util.*;

public class KeywordUtil {

    private static final String TOKENIZER_PATTERN = " \t\n\r\f.,!?;&-|";

    private static final String GERMAN = "de";
    private static final String ENGLISH = "en";
    private static final String DUTCH = "nl";

    private static String[] COMMON_ENGLISH = new String[] { "a", "about", "after", "again", "all",
            "also", "always", "am", "an", "and", "any", "anyone", "are", "around", "as", "at",
            "back", "be", "because", "been", "before", "being", "both", "brother", "but", "by",
            "can", "click", "do", "does", "down", "during", "early", "ensure", "except", "few",
            "following", "for", "from", "go", "had", "has", "have", "he", "her", "here", "him",
            "his", "hour", "hours", "how", "i", "if", "if", "in", "into", "is", "it", "its",
            "just", "km", "know", "later", "like", "long", "look", "lot", "many", "may", "me",
            "months", "more", "most", "must", "my", "needed", "no", "not", "of", "often", "on",
            "one", "only", "or", "other", "others", "our", "out", "over", "per", "pm", "quite",
            "see", "she", "she", "should", "since", "so", "some", "something", "still", "such",
            "sure", "than", "that", "the", "their", "them", "then", "there", "these",
            "they", "this", "through", "time", "to", "two", "up", "us", "very", "want", "was",
            "we", "well", "were", "what", "when", "which", "while", "who", "whom", "will", "with",
            "within", "without", "would", "you", "your" };
    
    private static String[] COMMON_DUTCH = new String[] { "aan", "aangaande", "aangezien",
            "achter", "achterna", "afgelopen", "al", "aldaar", "aldus", "alhoewel", "alias",
            "alle", "allebei", "alleen", "als", "alsnog", "altijd", "altoos", "ander", "andere",
            "anders", "anderszins", "behalve", "behoudens", "beide", "beiden", "ben", "beneden",
            "bent", "bepaald", "betreffende", "bij", "binnen", "binnenin", "boven", "bovenal",
            "bovendien", "bovengenoemd", "bovenstaand", "bovenvermeld", "buiten", "co", "corp",
            "could", "daar", "daardoor", "daarheen", "daarin", "daarna", "daarnaast", "daarnet",
            "daarmee", "daarom", "daarop", "daarvanlangs", "dan", "dankzij", "dat", "de", "den",
            "der", "des", "deze", "die", "dikwijls", "dit", "dl", "door", "doorgaand", "dr", "dus",
            "echter", "ed", "een", "eer", "eerdat", "eerder", "eerlang", "eerst", "elk", "elke",
            "en", "enig", "enige", "enigszins", "enkel", "enkele", "enz", "er", "erdoor", "ervoor",
            "etc", "even", "eveneens", "evenwel", "gauw", "gedurende", "geen", "gehad", "gekund",
            "geleden", "gelijk", "gemoeten", "gemogen", "geweest", "gewoon", "gewoonweg", "haar",
            "hadden", "hare", "heb", "hebben", "hebt", "heeft", "hem", "hen", "het", "hierbeneden",
            "hierboven", "hierin", "hij", "hoe", "hoewel", "hun", "hunne", "ik", "ikzelf", "in",
            "inc", "inmiddels", "inzake", "is", "je", "jezelf", "jij", "jijzelf", "jou", "jouw",
            "jouwe", "juist", "jullie", "kan", "klaar", "kon", "konden", "krachtens", "kunnen",
            "kunt", "last", "liever", "maar", "mag", "meer", "met", "mezelf", "mij", "mijn",
            "mijnent", "mijner", "mijzelf", "misschien", "mocht", "mochten", "moest", "moesten",
            "moet", "moeten", "mogen", "mr", "mrs", "ms", "mz", "na", "naar", "nabij", "nadat",
            "net", "niet", "noch", "nog", "nogal", "nu", "of", "ofschoon", "om", "omdat", "omhoog",
            "omlaag", "omstreeks", "omtrent", "omver", "onder", "ondertussen", "ongeveer", "ons",
            "onszelf", "onze", "ook", "op", "opnieuw", "opzij", "over", "overeind", "overigens",
            "pas", "precies", "prof", "publ", "reeds", "rond", "rondom", "s", "says", "sedert",
            "sinds", "sindsdien", "sl", "slechts", "sommige", "spoedig", "st", "steeds",
            "tamelijk", "te", "tegen", "ten", "tenzij", "ter", "terwijl", "thans", "tijdens",
            "toch", "toe", "toen", "toenmaals", "toenmalig", "tot", "totdat", "tussen", "uit",
            "uitg", "uitgezonderd", "vaak", "vaker", "van", "vanaf", "vandaan", "vanuit",
            "vanwege", "veeleer", "verder", "vert", "vervolgens", "vol", "volgens", "voor",
            "vooraf", "vooral", "vooralsnog", "voorbij", "voordat", "voordezen", "voordien",
            "voorheen", "voorop", "vooruit", "vrij", "vroeg", "waar", "waarbij", "waarom",
            "wanneer", "waren", "wat", "weer", "weg", "wegens", "wel", "weldra", "welk", "welke",
            "wie", "wiens", "wier", "wij", "wijzelf", "word", "worden", "wordt", "zal", "ze",
            "zelfs", "zich", "zichzelf", "zij", "zijn", "zijne", "zo", "zodra", "zonder", "zou",
            "zouden", "zowat", "zulke", "zullen", "zult" };

    private static String[] COMMON_GERMAN = new String[] { "am", "als", "an", "auf", "aufl", "aus",
            "bei", "beim", "bis", "das", "dem", "den", "der", "des", "die", "dr", "du", "durch",
            "ein", "eine", "einem", "einen", "einer", "eines", "einige", "fuer", "ihr", "ihre",
            "ihrer", "im", "in", "mich", "mit", "nach", "of", "ohne", "prof", "seine", "sowie",
            "tl", "ueber", "um", "und", "unter", "vert", "vom", "von", "zu", "zum", "zur" };

    
    /**
     * Convert list of keywords to comma-separated string
     * @param keywords - list of keywords
     * @return string of keywords
     */
    public static String keywordsToString(List<String> keywords) {
        if (!keywords.isEmpty()) {
            StringBuilder keywordsStr = new StringBuilder();
            for (String keyword : keywords) {
                keywordsStr.append(keyword).append(", ");
            }
            return keywordsStr.substring(0, keywordsStr.length() - 2);
        }
        return "";
    }


    /**
     * Get keywords from a text. This methods tries to detect which language it is in.
     * When the language is not detected then all words are eligable to be a keyword.
     * @param text - full text from which the keywords should be extracted
     * @return keywords found in text
     */
    public static List<String> getKeywords(String text) {
        return getKeywords(text, Integer.MAX_VALUE);
    }

    /**
     * Get keywords from a text
     * @param text - full text from which the keywords should be extracted
     * @param language - When the language is not recognized then all words 
     *                  are eligable to be a keyword.
     * @return keywords found in text
     */
    public static List<String> getKeywords(String text, String language) {
        return getKeywords(text, language, Integer.MAX_VALUE);
    }

    /**
     * Get keywords from a text
     * @param text - full text from which the keywords should be extracted
     * @param language - When the language is not recognized then all words 
     *                  are eligable to be a keyword.
     * @param max - maximum number of keywords returned
     * @return keywords found in text
     */
    public static List<String> getKeywords(String text, String language, int max) {
        if (ENGLISH.equalsIgnoreCase(language)) {
            return getKeywordsInternal(text, COMMON_ENGLISH, max);
        }
        if (DUTCH.equalsIgnoreCase(language)) {
            return getKeywordsInternal(text, COMMON_DUTCH, max);
        }
        if (GERMAN.equalsIgnoreCase(language)) {
            return getKeywordsInternal(text, COMMON_GERMAN, max);
        }
        return getKeywordsInternal(text, new String[0], max);
    }
    
    /**
     * Get keywords from a text. This methods tries to detect which language it is in.
     * When the language is not detected then all words are eligable to be a keyword.
     * @param text - full text from which the keywords should be extracted
     * @param max - maximum number of keywords returned
     * @return keywords found in text
     */
    public static List<String> getKeywords(String text, int max) {
        String[] ignoreWords = getCommonWords(text);
        return getKeywordsInternal(text, ignoreWords, max);
    }

    /**
     * Get keywords from a text
     * @param text - full text from which the keywords should be extracted
     * @param ignoreWords - ignore the words in this array
     * @param max - maximum number of keywords returned
     * @return keywords found in text
     */
    public static List<String> getKeywords(String text, String[] ignoreWords, int max) {
        Arrays.sort(ignoreWords);
        return getKeywordsInternal(text, ignoreWords, max);
    }

    /**
     * Get keywords from the strings. This methods tries to detect which language it is in.
     * When the language is not detected then all words are eligable to be a keyword.
     * @param textStrings - strings from which the keywords should be extracted
     * @return keywords found in strings
     */
    public static List<String> getKeywords(List<String> textStrings) {
        return getKeywords(textStrings, Integer.MAX_VALUE);
    }

    /**
     * Get keywords from the strings
     * @param textStrings - strings from which the keywords should be extracted
     * @param language - When the language is not recognized then all words 
     *                  are eligable to be a keyword.
     * @return keywords found in strings
     */
    public static List<String> getKeywords(List<String> textStrings, String language) {
        return getKeywords(textStrings, language, Integer.MAX_VALUE);
    }

    /**
     * Get keywords from the strings
     * @param textStrings - strings from which the keywords should be extracted
     * @param language - When the language is not recognized then all words 
     *                  are eligable to be a keyword.
     * @param max - maximum number of keywords returned
     * @return keywords found in strings
     */
    public static List<String> getKeywords(List<String> textStrings, String language, int max) {
        if (ENGLISH.equalsIgnoreCase(language)) {
            return getKeywordsInternal(textStrings, COMMON_ENGLISH, max);
        }
        if (DUTCH.equalsIgnoreCase(language)) {
            return getKeywordsInternal(textStrings, COMMON_DUTCH, max);
        }
        if (GERMAN.equalsIgnoreCase(language)) {
            return getKeywordsInternal(textStrings, COMMON_GERMAN, max);
        }
        return getKeywordsInternal(textStrings, new String[0], max);
    }
    
    /**
     * Get keywords from the strings. This methods tries to detect which language it is in.
     * When the language is not detected then all words are eligable to be a keyword.
     * @param textStrings - strings from which the keywords should be extracted
     * @param max - maximum number of keywords returned
     * @return keywords found in strings
     */
    public static List<String> getKeywords(List<String> textStrings, int max) {
        String[] ignoreWords = null;
        for (String text : textStrings) {
            ignoreWords = getCommonWords(text);
            if (ignoreWords.length > 0) {
                break;
            }
        }
        return getKeywordsInternal(textStrings, ignoreWords, max);
    }

    /**
     * Get keywords from the strings
     * @param textStrings - strings from which the keywords should be extracted
     * @param ignoreWords - ignore the words in this array
     * @param max - maximum number of keywords returned
     * @return keywords found in strings
     */
    public static List<String> getKeywords(List<String> textStrings, String[] ignoreWords, int max) {
        Arrays.sort(ignoreWords);
        return getKeywordsInternal(textStrings, ignoreWords, max);
    }
    
    /**
     * Get keywords from a text
     * @param text - full text from which the keywords should be extracted
     * @param ignoreWords - ignore the words in this array
     * @param max - maximum number of keywords returned
     * @return keywords found in text
     */
    private static List<String> getKeywordsInternal(String text, String[] ignoreWords, int max) {
        List<String> textStrings = new ArrayList<String>();
        textStrings.add(text);
        return getKeywordsInternal(textStrings, ignoreWords, max);
    }

    /**
     * Get keywords from the strings
     * @param textStrings - strings from which the keywords should be extracted
     * @param ignoreWords - ignore the words in this array
     * @param max - maximum number of keywords returned
     * @return keywords found in text
     */
    private static List<String> getKeywordsInternal(List<String> textStrings, String[] ignoreWords, int max) {
        Map<String,Keyword> keywords = new HashMap<String,Keyword>();

        for (String text : textStrings) {
            StringTokenizer tokenizer = new StringTokenizer(text, TOKENIZER_PATTERN);
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().toLowerCase();
                if (word.length() > 1 && Arrays.binarySearch(ignoreWords, word) < 0) {
                    Keyword keyword = null;
                    if (keywords.containsKey(word)) {
                        keyword = keywords.get(word);
                    }
                    else {
                        keyword = new Keyword(word);
                        keywords.put(word, keyword);
                    }
                    keyword.inc();
                }
            }
        }
        
        List<Keyword> sortList = new ArrayList<Keyword>(keywords.values());
        Collections.sort(sortList);
        List<String> words = new ArrayList<String>(sortList.size());
        
        int end = Math.min(max, sortList.size());
        for (int i = 0; i < end; i++) {
            words.add(sortList.get(i).key);
        }
        return words; 
    }

    public static String[] getCommonWords(String text) {
        int dutch = 0;
        int english = 0;
        int german = 0;
        
        StringTokenizer tokenizer = new StringTokenizer(text, TOKENIZER_PATTERN);
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().toLowerCase();
            if (Arrays.binarySearch(COMMON_DUTCH, word) >= 0) {
                dutch++;
            }
            if (Arrays.binarySearch(COMMON_ENGLISH, word) >= 0) {
                english++;
            }
            if (Arrays.binarySearch(COMMON_GERMAN, word) >= 0) {
                german++;
            }
        }
        if (dutch > english && dutch > german) {
            return COMMON_DUTCH;
        }
        if (english > dutch && english > german) {
            return COMMON_ENGLISH;
        }
        if (german > dutch && german > english) {
            return COMMON_ENGLISH;
        }
        if (dutch > 0) {
            // We are mainly a dutch community. 
            return COMMON_DUTCH;
        }
        return new String[0];
    }
    
    public static String detectLanguage(String text) {
        String[] commonWords = getCommonWords(text);
        if (commonWords == COMMON_DUTCH) {
            return DUTCH;
        }
        if (commonWords == COMMON_ENGLISH) {
            return ENGLISH;
        }
        if (commonWords == COMMON_GERMAN) {
            return GERMAN;
        }
        return null;
    }
    
    private static class Keyword implements Comparable<Keyword> {
        String key;
        int count;
        
        Keyword(String key) {
            if (key == null) {
                throw new IllegalArgumentException("key is empty");
            }
            this.key = key;
            this.count = 0;
        }
        
        public void inc() {
            this.count++;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (obj instanceof Keyword) {
                return key.equals(((Keyword)obj).key);
            }
            return false;
        }
        
        public int compareTo(Keyword o) {
            return o.count - count;
        }
    }

}
