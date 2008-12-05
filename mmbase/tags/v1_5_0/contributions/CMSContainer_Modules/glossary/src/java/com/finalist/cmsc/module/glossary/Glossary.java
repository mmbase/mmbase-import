package com.finalist.cmsc.module.glossary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Glossary {
   public static final String LINKPATTERN = "<a href=\"#\" title=\"%s\" id=\"_glossary_%s\" onclick=\"return false;\">%s</a>";
   public static final String GLOSSARY = "glossary";
   private final Map<String, String> TERMS = new HashMap<String, String>();
   private static Glossary glossary = null;


   void addTerm(String term, String description) {
      TERMS.put(term, description);
   }


   public String mark(String material) {
      Set<String> keyWords = TERMS.keySet();

      for (String word : keyWords) {

         Pattern pattern = Pattern.compile(String.format("\\b%s\\b", word));
         Matcher matcher = pattern.matcher(material);

         while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (!isInFormatedFragment(material, word, start)) {
               String highlight = String.format(LINKPATTERN, TERMS.get(word), word, word);
               material = (new StringBuilder()).append(material.substring(0, start)).append(highlight).append(
                     material.substring(end, material.length())).toString();
               break;
            }
         }
      }
      return material;

   }


   private boolean isInFormatedFragment(String material, String keywords, int keywordStartPosition) {
      Pattern pattern = Pattern.compile(String.format("<[abh][^</]*%s[^>]*>", keywords));
      Matcher matcher = pattern.matcher(material);

      while (matcher.find()) {
         int start = matcher.start();
         int end = matcher.end();

         if (keywordStartPosition + keywords.length() < end && keywordStartPosition > start)
            return true;
      }
      return false;
   }


   public static synchronized Glossary instance() {
      if (null == glossary) {
         glossary = new Glossary();
      }
      return glossary;
   }


   public Map<String, String> getTerms() {
      return TERMS;
   }

}
