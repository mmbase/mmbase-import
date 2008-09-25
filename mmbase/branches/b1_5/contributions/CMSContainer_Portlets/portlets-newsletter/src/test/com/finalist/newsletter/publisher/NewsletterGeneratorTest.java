package com.finalist.newsletter.publisher;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class NewsletterGeneratorTest extends TestCase {

   public void testGetContentFromPage(){

      String resource = "rawplainEmail.txt";
      String letter = getResource(resource);

      String result = NewsletterGenerator.getContentFromPage(letter);
      assertEquals(getResource("plainemail.txt"),result);
   }

   private String getResource(String resource) {
      String letter;
      StringBuffer buffer = new StringBuffer();

      InputStream stream  = NewsletterGeneratorTest.class.getResourceAsStream(resource);
      BufferedReader in = new BufferedReader(new InputStreamReader(stream));


      String line;
      try {
         while ((line = in.readLine()) != null) {
           buffer.append(line);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      letter = buffer.toString();
      return letter;
   }

}
