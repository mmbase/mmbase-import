package com.finalist.newsletter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.Message;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.module.Module;
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

   protected abstract Message generateNewsletterMessage(String userName);

   protected String getContent(String userName) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);

      String hostUrl = PropertiesUtil.getProperty("host.live");
      // TODO : Check if last char is a /
      String newsletterPath = NavigationUtil.getPathToRootString(publicationNode, true);
      String newsletterUrl = "".concat(hostUrl).concat(newsletterPath);

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

   public static Session getMailSession() {
      Module sendmailModule = Module.getModule("sendmail");
      if (sendmailModule == null) {
         log.fatal("Sendmail module not installed which is required for newsletter generation");
         return null;
      } else {
         String context = sendmailModule.getInitParameter("context");
         if (context == null) {
            context = "java:comp/env";
            log.warn("The property 'context' is missing, taking default " + context);
         }
         String dataSource = sendmailModule.getInitParameter("datasource");
         if (dataSource == null) {
            dataSource = "mail/Session";
            log.warn("The property 'datasource' is missing, taking default " + dataSource);
         }

         Session session = null;
         try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup(context);
            Object o = envCtx.lookup(dataSource);
            if (o instanceof Session) {
               session = (javax.mail.Session) o;
            } else {
               log.fatal("Configured dataSource '" + dataSource + "' of context '" + context + "' is not a Session but "
                     + (o == null ? "NULL" : "a " + o.getClass().getName()));
               return null;
            }
         } catch (NamingException e) {
            log.fatal("Configured dataSource '" + dataSource + "' of context '" + context + "' is not a Session ");
            return null;
         }
         log.debug("Email session obtained");
         return session;
      }
   }
}