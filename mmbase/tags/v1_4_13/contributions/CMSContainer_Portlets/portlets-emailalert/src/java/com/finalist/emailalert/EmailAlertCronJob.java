package com.finalist.emailalert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.search.PageInfo;
import com.finalist.cmsc.services.search.Search;

public class EmailAlertCronJob implements CronJob {

   private static Logger log = Logging.getLoggerInstance(EmailAlertCronJob.class.getName());
   private static final String PAGINAURL = "#URL#";
   private static final String AFMELDENPAGINA = "#AFMELDENPAGINA#";
   private static final String AFMELDEN = "#AFMELDEN#";
   private static final String BEGINTEMPLATE = "#BEGIN#";
   private static final String ENDTEMPLATE = "#EIND#";


   public void init(CronEntry cronEntry) {
      // empty
   }


   public void stop() {
      // empty
   }


   public void run() {
      sendEmailAlerts();
   }


   private void sendEmailAlerts() {
      log.info("EmailAlert thread started");
      String liveHost = PropertiesUtil.getProperty("host.live");
      if (liveHost == null) {
         log.error("System property host.live not available, email alerts not sent");
         return;
      }

      if (!ServerUtil.isLive()) {
         log.error("Email alerts should run only on the live cloud, email alerts not sent");
         return;
      }

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Map<String, Set<PageInfo>> alerts = new HashMap<String, Set<PageInfo>>();

      // get the articles marked for alerts
      NodeList articles = SearchUtil.findNodeList(cloud, "article", "alert", true);
      log.debug("articles " + articles.size());
      for (int i = 0; i < articles.size(); i++) {
         Node article = articles.getNode(i);
         // get all the pages which contain the article
         List<PageInfo> pages = Search.findAllDetailPagesForContent(article);
         log.debug("pages " + pages.size());
         for (int j = 0; j < pages.size(); j++) {
            PageInfo page = pages.get(j);
            log.debug("valid subscribers for page  " + page.getPageNumber());
            // get the valid subscribers for a page
            NodeList subscribers = SearchUtil.findRelatedNodeList(cloud.getNode(page.getPageNumber()), "subscriber",
                  "subscriberel", "valid", true);
            log.debug("subscribers " + subscribers.size());
            for (int k = 0; k < subscribers.size(); k++) {
               Node subscriber = subscribers.getNode(k);
               String emailAddress = subscriber.getStringValue("emailaddress");
               Set<PageInfo> alertPages = alerts.get(emailAddress);
               if (alertPages == null) {
                  alertPages = new HashSet<PageInfo>();
               }
               alertPages.add(page);
               alerts.put(emailAddress, alertPages);
            }
         }
         // unmark the article for alerts
         article.setBooleanValue("alert", false);
         article.commit();
      }

      if (alerts.size() > 0) {
         sendEmails(cloud, alerts, liveHost);
      }

      log.info("EmailAlert thread finished");
   }


   private void sendEmails(Cloud cloud, Map<String, Set<PageInfo>> alerts, String liveHost) {
      // get the emailalert to be used for the email templates and settings
      NodeList emailalerts = SearchUtil.findNodeList(cloud, "emailalert");
      Node emailAlert;
      if (emailalerts.size() > 0) {
         emailAlert = emailalerts.getNode(0);
         if (emailalerts.size() > 1) {
            log.error("found " + emailalerts.size() + " emailalert nodes; first one will be used");
            return;
         }
      }
      else {
         log.error("Node emailalert not available");
         return;
      }

      String nameFrom = emailAlert.getStringValue("sendername");
      String emailFrom = emailAlert.getStringValue("senderemailaddress");
      String subject = emailAlert.getStringValue("alertemailsubject");
      String emailTemplate = emailAlert.getStringValue("alertemailbody");

      int indexBeginUrlTemplate = emailTemplate.indexOf(BEGINTEMPLATE);
      int indexEndUrlTemplate = emailTemplate.indexOf(ENDTEMPLATE);
      String urlTemplate = "";
      String beginEmailBody = emailTemplate;
      String endEmailBody = "";
      if (indexBeginUrlTemplate > -1 && indexEndUrlTemplate > -1) {
         beginEmailBody = emailTemplate.substring(0, indexBeginUrlTemplate - 1);
         urlTemplate = emailTemplate.substring(indexBeginUrlTemplate + BEGINTEMPLATE.length(), indexEndUrlTemplate - 1)
               .trim();
         urlTemplate = urlTemplate.trim();
         endEmailBody = emailTemplate.substring(indexEndUrlTemplate + ENDTEMPLATE.length());
      }
      else {
         log.info("unexpected format for alertemailbody template");
      }

      Set<String> emailAddresses = alerts.keySet();
      for (String emailAddress : emailAddresses) {
         log.debug("sending email to: " + emailAddress);
         Set<PageInfo> pages = alerts.get(emailAddress);
         String body = processTemplate(pages, beginEmailBody, urlTemplate, endEmailBody, emailAddress, liveHost);
         EmailUtil.send(cloud, null, emailAddress, nameFrom, emailFrom, subject, body);
      }
   }


   private String processTemplate(Set<PageInfo> pages, String beginEmailBody, String urlTemplate, String endEmailBody,
         String emailAddress, String liveHost) {
      String emailBodyText;
      String urlFragment;
      StringBuffer emailBody = new StringBuffer();

      String paginaUrlTemplate = liveHost + "/content/%d";
      String unsubscribePageTemplate = liveHost + "/alert/unsubscribe.do?p=%d&s=%s";
      String unsubscribeAllTemplate = liveHost + "/alert/unsubscribe.do?p=all&s=%s";

      emailBody.append(beginEmailBody);
      for (PageInfo page : pages) {
         urlFragment = urlTemplate.replaceAll(PAGINAURL, String.format(paginaUrlTemplate, page.getPageNumber()));
         urlFragment = urlFragment.replaceAll(AFMELDENPAGINA, String.format(unsubscribePageTemplate, page
               .getPageNumber(), emailAddress));
         emailBody.append(urlFragment);
         emailBody.append("\n\n");
      }
      emailBody.append(endEmailBody);

      emailBodyText = emailBody.toString();
      emailBodyText = emailBodyText.replaceAll(AFMELDEN, String.format(unsubscribeAllTemplate, emailAddress));
      return emailBodyText;
   }

}
