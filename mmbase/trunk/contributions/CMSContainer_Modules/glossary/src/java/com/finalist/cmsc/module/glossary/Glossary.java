// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Glossary.java

package com.finalist.cmsc.module.glossary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Glossary
{

    private Glossary()
    {
    }

    void addTerm(String term, String description)
    {
        TERMS.put(term, description);
    }

    public String mark(String material)
    {
        Iterator i$ = TERMS.keySet().iterator();
label0:
        do
        {
            if(!i$.hasNext())
                break;
            String word = (String)i$.next();
            Pattern pattern = Pattern.compile((new StringBuilder()).append("\\b").append(word).append("\\b").toString());
            Pattern inLinkPattern = Pattern.compile((new StringBuilder()).append("<a[^<]*").append(word).append("[^>]*>").toString());
            Matcher matcher = pattern.matcher(material);
            Matcher linkMatcher = inLinkPattern.matcher(material);
            do
                if(!matcher.find())
                    continue label0;
            while(linkMatcher.find() && matcher.start() > linkMatcher.start() && matcher.end() < linkMatcher.end());
            String highlight = String.format("<a href=\"#\" title=\"%s\" id=\"_glossary_%s\">%s</a>", new Object[] {
                TERMS.get(word), word, word
            });
            material = (new StringBuilder()).append(material.substring(0, matcher.start())).append(highlight).append(material.substring(matcher.end(), material.length())).toString();
        } while(true);
        return material;
    }

    public static synchronized Glossary instance()
    {
        if(null == glossary)
        {
            Glossary glossary = new Glossary();
            glossary = glossary;
        }
        return glossary;
    }

    public Map getTerms()
    {
        return TERMS;
    }

    public static final String GLOSSARY = "glossary";
    private final Map TERMS = new HashMap();
    private static Glossary glossary = null;

}
