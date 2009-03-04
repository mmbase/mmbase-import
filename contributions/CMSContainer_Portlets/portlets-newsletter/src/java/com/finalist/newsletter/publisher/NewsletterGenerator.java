package com.finalist.newsletter.publisher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterGenerator {

   private static Logger log = Logging.getLoggerInstance(NewsletterGenerator.class.getName());


   public static String generate(String urlPath, String mimeType) {

      log.debug("generate newsletter from url:" + urlPath);

      String inputString = "";
      String errorInfo = "please check the system live-path variable, ";

      try {
         log.debug("Try to get content from URL:" + urlPath);

         URL url = new URL(urlPath);
         if (urlPath.indexOf("http") < 0) {
            errorInfo += "the path does not start with http: ";
         }
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         connection.setRequestMethod("GET");
         connection.setDoInput(true);
         connection.setRequestProperty("Content-Type", mimeType);

         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

         StringBuilder buffer = new StringBuilder();

         String line;
         while ((line = in.readLine()) != null) {
            buffer.append(line);
         }
         in.close();

         inputString = buffer.toString().trim();

         if ("text/plain".equalsIgnoreCase(mimeType)) {
            inputString = getContentFromPage(inputString);
         }
         inputString = NewsletterUtil.calibrateRelativeURL(inputString);

         return (inputString);
         
      } catch (FileNotFoundException e) {
         log.error("Error when try to get content from:" + urlPath+errorInfo, e);
         
      } catch (IOException e) {
         e.printStackTrace();
         log.error("Error when reading input data from URL:" + urlPath+errorInfo, e);
      }

      return inputString;
   }

   public static String getContentFromPage(String inputString) {
      Parser myParser;
      myParser = Parser.createParser(inputString, "utf-8");

      HtmlPage visitor = new HtmlPage(myParser);

      try {
         myParser.visitAllNodesWith(visitor);
      } catch (ParserException e) {
         e.printStackTrace();
      }

      inputString = visitor.getBody().asHtml().trim();
      inputString = inputString.replaceAll("(?m)^\\s*\r\n+", "").replaceAll("(?m)^\\s*\r+", "").replaceAll("(?m)^\\s*\n+", "");
      inputString = inputString.replaceAll("(?m)\r\n+", "").replaceAll("(?m)\r+", "").replaceAll("(?m)\n+", "");
      inputString = inputString.replaceAll("<br/>", "\r\n");

      return inputString;
   }


}