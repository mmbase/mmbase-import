package com.finalist.newsletter.publisher;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication;

public class NewsletterGenerator {

   private static Logger log = Logging.getLoggerInstance(NewsletterGenerator.class.getName());

   private static String personaliser;

   public void setPersonaliser(String personaliser) {
      NewsletterGenerator.personaliser = personaliser;
   }


   public static void generate(Message message, Publication publication, Subscription subscription) throws MessagingException {

      log.debug("generate newsletter:"+publication.getNewsletter().getTitle());
      
      String rawHtmlContent = getContent(publication, subscription.getMimeType());
      rawHtmlContent = personalise(rawHtmlContent, subscription);
      message.setText(rawHtmlContent + "\n");
   }

   private static String personalise(String rawHtmlContent, Subscription subscription) {
      String result = rawHtmlContent;

      if (null == personaliser) {
         personaliser = PropertiesUtil.getProperty("newsletter.personaliser");
      }

      if (StringUtils.isNotEmpty(personaliser)) {
         try {
            Personaliser ps = (Personaliser) Class.forName(personaliser).newInstance();
            result = ps.personalise(rawHtmlContent, subscription);
         } catch (ClassNotFoundException e) {
            log.error("No specified personaliser found:" + personaliser, e);
         } catch (IllegalAccessException e) {
            log.error(e);
         } catch (InstantiationException e) {
            log.error(e);
         }
      }
      return result;

   }


   public static String getContent(Publication publication, String type) {
      String inputString = "";
      try {

         log.debug("Try to get content from URL:"+publication.getUrl());
         
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
            buffer.append("").append(character);
         }

         reader.close();

         inputString = buffer.toString().trim();
         return (inputString);
      } catch (Exception e) {
         log.debug("Error when try to get content from"+publication.getUrl(),e);
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