package com.finalist.newsletter.generator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication;

public abstract class NewsletterGenerator {

   private static Logger log = Logging.getLoggerInstance(NewsletterGenerator.class.getName());

   public static void generate(Message message, Publication publication, Subscription subscription) throws MessagingException {
      String rawHtmlContent = getContent(publication, subscription.getMimeType());
      message.setText(rawHtmlContent + "\n");
   }

   protected static String checkUrls(String input) {
      String hostUrl = getLiveHostUrl();
      String appName = getApplicationName(hostUrl);

      String output = input;
      output = output.replaceAll("\"/" + appName, "\"/");
      output = output.replaceAll("\"/", hostUrl);
      return output;
   }

   private static String getApplicationName(String hostUrl) {
      String[] hostUrlParts = hostUrl.split("/");
      String appName = hostUrlParts[hostUrlParts.length - 1];
      return (appName);
   }

   protected static String getContent(Publication publication, String type) {


      String inputString = "";
      try {
         URL url = new URL(publication.getUrl());
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         connection.setRequestMethod("GET");
         connection.setDoInput(true);
         connection.setRequestProperty("Content-Type", type);

         Reader reader = new InputStreamReader(connection.getInputStream());

         StringBuffer buffer = new StringBuffer();

         int c;
         while ((c = reader.read()) != -1) {
            char character = (char) c;
            buffer.append("" + character);
         }

         reader.close();

         inputString = buffer.toString().trim();
         inputString = checkUrls(inputString);
         return (inputString);
      } catch (Exception e) {
         log.debug("Error");
      }
      return inputString;
   }

   private static String getLiveHostUrl() {
      String hostUrl = PropertiesUtil.getProperty("host.live");
      if (hostUrl != null && !hostUrl.endsWith("/")) {
         hostUrl += "/";
      }
      return hostUrl;
   }


}