package com.finalist.cmsc.module.glossary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Glossary {
    public static final String GLOSSARY = "glossary";
    private final Map<String, String> TERMS = new HashMap<String, String>();
    private static Glossary glossary = null;

    private Glossary() {
    }

    void addTerm(String term, String description) {
        TERMS.put(term, description);
    }

    public String mark(String material) {
        Set<String> keyWords = TERMS.keySet();

        for (String word : keyWords) {

            Pattern pattern = Pattern.compile((new StringBuilder()).append("\\b").append(word).append("\\b").toString());

            Pattern inLinkPattern = Pattern.compile((new StringBuilder()).append("<a[^<]*").append(word).append("[^>]*>").toString());
            Matcher matcher = pattern.matcher(material);
            Matcher linkMatcher = inLinkPattern.matcher(material);
            do
                if (!matcher.find())
                    continue;

            while (linkMatcher.find() && matcher.start() > linkMatcher.start() && matcher.end() < linkMatcher.end());

            String highlight = String.format("<a href=\"#\" title=\"%s\" id=\"_glossary_%s\">%s</a>", TERMS.get(word), word, word);

            material = (new StringBuilder()).append(material.substring(0, matcher.start())).append(highlight).append(material.substring(matcher.end(), material.length())).toString();

        }
        return material;

    }

    public static synchronized Glossary instance() {
        if (null == glossary) {
            Glossary glossary = new Glossary();
        }
        return glossary;
    }

    public Map getTerms() {
        return TERMS;
    }

}
