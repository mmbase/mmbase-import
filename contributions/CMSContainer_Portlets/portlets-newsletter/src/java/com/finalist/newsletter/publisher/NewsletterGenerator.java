package com.finalist.newsletter.publisher;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.mail.MessagingException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsletterGenerator {

   private static Logger log = Logging.getLoggerInstance(NewsletterGenerator.class.getName());


   public static String generate(String urlPath, String mimeType) throws MessagingException {

      log.debug("generate newsletter from url:"+urlPath);
      
        String inputString = "";
      try {

         log.debug("Try to get content from URL:"+urlPath);

         URL url = new URL(urlPath);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         connection.setRequestMethod("GET");
         connection.setDoInput(true);
         connection.setRequestProperty("Content-Type", mimeType);

         Reader reader = new InputStreamReader(connection.getInputStream());

         StringBuffer buffer = new StringBuffer();

         int c;
         while ((c = reader.read()) != -1) {
            char character = (char) c;
            buffer.append("").append(character);
         }

         reader.close();

         inputString = buffer.toString().trim();
         return (inputString);
      } catch (Exception e) {
         log.debug("Error when try to get content from"+urlPath,e);
      }

      return inputString;
   }
}