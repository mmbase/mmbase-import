package com.finalist.newsletter.generator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
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
         return session;
      }
   }

   private int  publicationNumber;

   public NewsletterGenerator(int  publicationNumber) {
      this.publicationNumber = publicationNumber;
   }

   protected String checkUrls(String input) {
      String hostUrl = getLiveHostUrl();
      String appName = getApplicationName(hostUrl);

      String output = input;
      output = output.replaceAll("\"/" + appName, "\"/");
      output = output.replaceAll("\"/", hostUrl);
      return (output);
   }

   public  Message generateNewsletterMessage(String userName) {
      Session session = getMailSession();
      Message message = new MimeMessage(session);
      String rawHtmlContent = getContent(userName,getType());

      if (rawHtmlContent != null) {
         // BodyPart htmlBodyPart = new MimeBodyPart();
         // Multipart content = new MimeMultipart();
         try {
            // content.addBodyPart(htmlBodyPart);
            // message.setContent(content);
            message.setText(rawHtmlContent + "\n");
            message.setHeader("Content-type", getType());
         } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return (message);
   }
   
   protected abstract String getType();

   private String getApplicationName(String hostUrl) {
      String[] hostUrlParts = hostUrl.split("/");
      String appName = hostUrlParts[hostUrlParts.length - 1];
      return (appName);
   }

   protected String getContent(String userName,String type) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);

      String hostUrl = getLiveHostUrl();
      String newsletterPath = NavigationUtil.getPathToRootString(publicationNode, true);
      String newsletterUrl = "".concat(hostUrl).concat(newsletterPath);

      if (newsletterUrl != null && newsletterUrl.startsWith("http")) {
         try {
            URL url = new URL(newsletterUrl);
            URLConnection connection = url.openConnection();
            ((HttpURLConnection) connection).setRequestMethod("GET");
            connection.setDoInput(true);
            if(type == null || type.trim().length() == 0) {
               connection.setRequestProperty("Content-Type", "text/html");
            }
            else {
               connection.setRequestProperty("Content-Type", type);  
            }
            connection.setRequestProperty("username", userName);
            InputStream input = connection.getInputStream();
            Reader reader = new InputStreamReader(input);
            StringBuffer buffer = new StringBuffer();

            int c;
            while ((c = reader.read()) != -1) {
               char character = (char) c;
               buffer.append("" + character);
            }
            reader.close();
            String inputString = buffer.toString();
            inputString = inputString.trim();
            inputString = checkUrls(inputString);
            return (inputString);
         } catch (Exception e) {
            log.debug("Error");
         }
      }
      return (null);
   }

   private String getLiveHostUrl() {
      String hostUrl = PropertiesUtil.getProperty("host.live");
      if (hostUrl != null) {
         if (!hostUrl.endsWith("/")) {
            hostUrl += "/";
         }
      }
      return hostUrl;
   }
}