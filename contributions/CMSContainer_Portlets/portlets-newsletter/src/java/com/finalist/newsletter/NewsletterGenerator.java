package com.finalist.newsletter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.internet.MimeMultipart;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;

public abstract class NewsletterGenerator {

   private static Logger log = Logging.getLoggerInstance(NewsletterGenerator.class.getName());

   private String publicationNumber;

   public NewsletterGenerator(String publicationNumber) {
      this.publicationNumber = publicationNumber;
   }

   protected abstract MimeMultipart generateNewsletterMessage(String userName);
   
   

   protected String getContent(String userName) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);

      String newsletterUrl = "";
      newsletterUrl += PropertiesUtil.getProperty("host.live");
      newsletterUrl += NavigationUtil.getPathToRootString(publicationNode, true);

      try {
         log.debug("Creating URL");
         URL url = new URL(newsletterUrl);
         log.debug("Opening connection");
         URLConnection connection = url.openConnection();
         ((HttpURLConnection) connection).setRequestMethod("GET");
         log.debug("Set doInput");
         connection.setDoInput(true);
         log.debug("Set content type");
         connection.setRequestProperty("Content-Type", "text/html");
         connection.setRequestProperty("username", userName);
         log.debug("Getting inputstream");
         InputStream input = connection.getInputStream();
         log.debug("Creating inputstream reader");
         Reader reader = new InputStreamReader(input);
         log.debug("Creating buffer");
         StringBuffer buffer = new StringBuffer();

         int c;
         while ((c = reader.read()) != -1) {
            char character = (char) c;
            buffer.append("" + character);
         }
         reader.close();
         String inputString = buffer.toString();
         inputString = inputString.trim();
         log.debug("Input = " + inputString);
         return (inputString);
      } catch (Exception e) {
         log.debug("Error");
      }
      return (null);
   }
}