package com.finalist.newsletter.publisher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.module.Module;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.publisher.cache.CacheFactory;
import com.finalist.newsletter.publisher.cache.ICache;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublisher {

   private static Logger log = Logging
            .getLoggerInstance(NewsletterPublisher.class.getName());

   private static String personaliser;

   public static void setPersonaliser(String personaliser) {
      NewsletterPublisher.personaliser = personaliser;
   }

   enum MimeType {
      image, attachment
   }


   public void deliver(Publication publication, Subscription subscription) {
      try {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterEditionNode = cloud.getNode(publication.getId());
         // if needed to prompt user this validate will be remove to Action
         String originalBody  = "";
         String static_html = null;
         if (newsletterEditionNode.getValueWithoutProcess("static_html") != null)
            static_html = (String)newsletterEditionNode.getValueWithoutProcess("static_html");
         if (StringUtils.isEmpty(static_html)) {
            originalBody = getBody(publication, subscription);
         }
         else {
            if("text/plain".equals(subscription.getMimeType())){
               OnlyText onlyText = new OnlyText();
               originalBody = onlyText.html2Text(static_html);
            }
            else {
               originalBody = static_html;
            }

         }
         

         // Newsletter newsletter = service.getNewsletterBySubscription(subscription.getId());
         Newsletter newsletter = publication.getNewsletter();
         String replyAddress = newsletter.getReplyAddress();
         String toEmail = subscription.getEmail();
         Message message = new MimeMessage(getMailSession(toEmail, replyAddress));
         setSenderInfomation(message, newsletter.getFromAddress(), newsletter.getFromName(), replyAddress, newsletter
               .getReplyName());
         setContent(message, publication, subscription, originalBody);
         setRecipient(message, subscription.getEmail());
         // setBody(publication, subscription, message);
         setTitle(message, newsletter.getTitle());
         // setMIME(message, subscription.getMimeType());
         Transport.send(message);

         log.debug(String.format("mail send! publication %s to %s in %s format", publication.getId(), subscription
               .getId(), subscription.getMimeType()));
      } catch (MessagingException e) {
         log.error(e);
         throw new NewsletterSendFailException(e);
      } catch (UnsupportedEncodingException e) {
         log.error(e);
         throw new NewsletterSendFailException(e);
      }
   }

   private void setContent(Message message, Publication publication,Subscription subscription,String originalBody) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterPublicationNode = cloud.getNode(publication.getId());
      NodeList attachmentNodes = newsletterPublicationNode.getRelatedNodes("attachments");
      Multipart multipart = new MimeMultipart();
      if("text/plain".equals(subscription.getMimeType())){
         setBody(publication, subscription, multipart,originalBody);
      }
      else {
         setMultipartBody(publication, subscription, multipart,originalBody);
      }
      setAttachment(multipart, attachmentNodes, MimeType.attachment);
      NodeList imageNodes = newsletterPublicationNode.getRelatedNodes("images");
      setAttachment(multipart, imageNodes, MimeType.image);
      try {
         message.setContent(multipart);
      }
      catch (MessagingException e) {
         log.error(e);
      }
   }

   private void setBody(Publication publication, Subscription subscription, Multipart multipart,String originalBody) {
      MimeBodyPart mdp = new MimeBodyPart();
      try {
         String type=subscription.getMimeType();
         mdp.setContent(originalBody, type+";charset=utf-8");
         mdp.addHeader("Content-Transfer-Encoding", "quoted-printable");
         multipart.addBodyPart(mdp);
      }
      catch (MessagingException e) {
         log.error(e);
      }
   }

   private void setMultipartBody(Publication publication, Subscription subscription, Multipart multipart,String originalBody) {
      MimeBodyPart mdp = new MimeBodyPart();
      Multipart subMultipart = new MimeMultipart("alternative");
      MimeBodyPart textMdp = new MimeBodyPart();
      MimeBodyPart htmlMdp = new MimeBodyPart();
      try {
         
         String url = NewsletterUtil.getTermURL(publication.getUrl(), subscription.getTerms(), publication.getId());
         textMdp.addHeader("Content-Transfer-Encoding", "quoted-printable");
         textMdp.setText(url+"\n"+originalBody, "utf-8");
         subMultipart.addBodyPart(textMdp);
         
         String type=subscription.getMimeType();
         htmlMdp.addHeader("Content-Transfer-Encoding", "quoted-printable");
         htmlMdp.setContent(originalBody, type+";charset=utf-8");
         subMultipart.addBodyPart(htmlMdp);
         
         mdp.setContent(subMultipart);
         
         multipart.addBodyPart(mdp);
      }
      catch (MessagingException e) {
         log.error(e);
      }
   }

   private void setAttachment(Multipart multipart, NodeList attachmentNodes,
                              MimeType mimeType) {
      if (attachmentNodes.size() > 0) {

         try {
            for (int i = 0; i < attachmentNodes.size(); i++) {
               Node node = attachmentNodes.getNode(i);
               DataHandler dh = null;
               byte[] bytes = node.getByteValue("handle");
               ByteArrayDataSource bads = new ByteArrayDataSource(bytes, null);

               MimeBodyPart messageBodyPart = new MimeBodyPart();
               if (mimeType.compareTo(MimeType.image) == 0) {
                  bads = new ByteArrayDataSource(bytes, "image/"
                           + node.getStringValue("itype"));
               } else if (mimeType.compareTo(MimeType.attachment) == 0) {
                  bads = new ByteArrayDataSource(bytes, node
                           .getStringValue("mimetype"));
               }
               dh = new DataHandler(bads);
               messageBodyPart.setFileName(node.getStringValue("filename"));
               messageBodyPart.setDataHandler(dh);
               multipart.addBodyPart(messageBodyPart);
            }
         }
         catch (MessagingException e) {
            log.error(e);
         }
      }
   }

   private String getBody(Publication publication, Subscription subscription) throws MessagingException {

      String url = NewsletterUtil.getTermURL(publication.getUrl(), subscription.getTerms(), publication.getId());
      ICache cache = null;
      String expiration = PropertiesUtil.getProperty("publication.cache.expiration");
      if (StringUtils.isEmpty(expiration)) {
         cache = CacheFactory.getDefaultCache();
      } else {
         cache = CacheFactory.getDefaultCache(Long.parseLong(expiration));
      }
      String content = " ";
      if ((subscription.getTerms() == null) || (subscription.getTerms().size() == 0) || !cache.contains(url)) {
         if (null != getPersonalise()) {
            content = getPersonalise().personalise(content, subscription, publication);
            log.info("the content sended is Personalised :" + content);
         }else {
            log.info("url---->" + url);
            content = NewsletterGenerator.generate(url, subscription.getMimeType());
         }
         cache.add(url, content);
      } else {
         content = (String) cache.get(url);
         log.info("the content sended is from the cache" + content);
      }
      return content + "\n";
   }

   private void setSenderInfomation(Message message, String fromAddress, String fromName, String replyAddress, String replyName)
            throws MessagingException, UnsupportedEncodingException {

      String emailFrom = getHeaderProperties(fromAddress, "newsletter.default.fromaddress");
      String nameFrom = getHeaderProperties(fromName, "newsletter.default.fromname");
      String emailReplyTo = getHeaderProperties(replyAddress, "newsletter.default.replytoadress");
      String nameReplyTo = getHeaderProperties(replyName, "newsletter.default.replyto");

      log.debug("set header property:<" + nameFrom + ">" + emailFrom + "<" + nameReplyTo + ">" + emailReplyTo);


      InternetAddress replyToAddress = new InternetAddress(emailReplyTo);
      replyToAddress.setPersonal(nameReplyTo);
      message.setReplyTo(new InternetAddress[]{replyToAddress});

      InternetAddress senderAddress = new InternetAddress(emailFrom);
      senderAddress.setPersonal(nameFrom);
      message.setFrom(senderAddress);
   }

   private void setMIME(Message message, String mime) throws MessagingException {
      message.setHeader("MIME-Version", "1.0");
      message.setHeader("Content-Type", mime);
      message.setHeader("X-Mailer", "Recommend-It Mailer V2.03c02");
      message.setSentDate(new Date());
   }

   private void setTitle(Message message, String title)
            throws MessagingException {
      message.setSubject(title);
   }

   private void setRecipient(Message message, String email)
            throws MessagingException {
      InternetAddress toAddress = new InternetAddress(email);
      message.setRecipient(MimeMessage.RecipientType.TO, toAddress);
   }

   private String getHeaderProperties(String property, String defaultKey) {
      if (StringUtils.isBlank(property)) {
         property = PropertiesUtil.getProperty(defaultKey);

         log.debug("get property:" + defaultKey + " from system property got:"
                  + property);
      }

      if (StringUtils.isBlank(property)) {
         property = "newslettermodule@cmscontainer.org";

         log.debug("get property:" + defaultKey
                  + " from system property failed use default:" + property);
      }

      return property;
   }

   protected Session getMailSession(String toEmail, String senderEmail) {

      Session session = null;
      String datasource = getParameter("datasource");
      String context = getParameter("context");

      try {

         Context initCtx = new InitialContext();
         Context envCtx = (Context) initCtx.lookup(context);
         session = (javax.mail.Session) envCtx.lookup(datasource);

         Properties properties = new Properties();
         if (senderEmail == null || senderEmail.indexOf("@") < 1) {
            senderEmail = "admin@finalist.com";
         }
         String[] sender = StringUtils.split(senderEmail, "@");

         String verpFrom = String.format("%s-%s@%s", sender[0], toEmail.replaceAll("@", "="), sender[1]);
         properties.put("mail.smtp.from", verpFrom);
//         properties.put("mail.smtp.from", "dguo-mark.guo=gmail.com@cpier.pku.edu.cn");
//         properties.put("mail.smtp.from", senderEmail);
         properties.putAll(session.getProperties());

/*
session = Session.getInstance(properties,
new javax.mail.Authenticator() {
   protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication("dguo@cpier.pku.edu.cn", "lgs9000");
   }
});
*/
      }
      catch (NamingException e) {
         log.fatal("Configured dataSource '" + datasource + "' of context '" + context + "' is not a Session ");
      }
      return session;
   }
   private static String getParameter(String name) {

      Module sendmailModule = Module.getModule("sendmail");
      if (sendmailModule == null) {
         log
                  .fatal("Sendmail module not installed which is required for newsletter generation");
         return null;
      }

      String parameter = sendmailModule.getInitParameter(name);
      if (parameter == null) {
         parameter = "java:comp/env";
         log.warn("The property " + parameter + " is missing, taking default "
                  + parameter);
      }
      return parameter;
   }

   private Personaliser getPersonalise() {
      Personaliser ps = null;
      if (null == personaliser) {
         personaliser = PropertiesUtil.getProperty("newsletter.personaliser");
      }

      if (StringUtils.isNotEmpty(personaliser)) {
         try {
            ps = (Personaliser) Class.forName(personaliser).newInstance();
         }
         catch (ClassNotFoundException e) {
            log.error("No specified personaliser found:" + personaliser, e);
         }
         catch (IllegalAccessException e) {
            log.error(e);
         }
         catch (InstantiationException e) {
            log.error(e);
         }
      }
      return ps;

   }

   public class ByteArrayDataSource implements DataSource {
      private byte[] data; // data
      private String type; // content-type

      /* Create a DataSource from an input stream */
      public ByteArrayDataSource(InputStream is, String type) {
         this.type = type;
         try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1)
               os.write(ch);
            data = os.toByteArray();
         }
         catch (IOException ioex) {
         }
      }

      /* Create a DataSource from a byte array */
      public ByteArrayDataSource(byte[] data, String type) {
         this.data = data;
         this.type = type;
      }

      /* Create a DataSource from a String */
      public ByteArrayDataSource(String data, String type) {
         try {
            this.data = data.getBytes("iso-8859-1");
         }
         catch (UnsupportedEncodingException uex) {
         }
         this.type = type;
      }

      /**
       * Return an InputStream for the data. Note - a new stream must be
       * returned each time.
       */
      public InputStream getInputStream() throws IOException {
         if (data == null) {
            throw new IOException("no data");
         }
         return new ByteArrayInputStream(data);
      }

      public OutputStream getOutputStream() throws IOException {
         throw new IOException("cannot do this");
      }

      public String getContentType() {
         return type;
      }

      public String getName() {
         return "dummy";
      }
   }
}