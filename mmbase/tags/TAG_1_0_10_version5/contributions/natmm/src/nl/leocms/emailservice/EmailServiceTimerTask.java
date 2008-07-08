/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.emailservice;

import nl.leocms.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.TreeSet;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.module.EmailServiceModule;
import nl.leocms.util.PaginaHelper;

import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import org.apache.axis.Constants;
import javax.mail.MessagingException;
import javax.xml.rpc.ParameterMode;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

/**
 * This class will run from the Timer class initialized in EmailServiceModule
 * @author Ronald Kramp
 */
public class EmailServiceTimerTask extends TimerTask {
   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(EmailServiceTimerTask.class.getName());

   PaginaHelper paginaHelper;
   private String webserviceURL;
   private String webmasterEmail;
   private String webmasterName;
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");


    /**
     * Constructor
     * @param webserviceURL the url of the webservice where the users can be retrieved
     */
    public EmailServiceTimerTask(String webserviceURL) {
       this.webserviceURL = webserviceURL;
       this.webmasterEmail = PropertiesUtil.getProperty("mail.sender.email");
       this.webmasterName = PropertiesUtil.getProperty("mail.sender.name");
    }

    /**
     * This method will be called by the Timer class.
     * It will search for published articles with a relation mailijst to thema.
     * A webserivce will be called for getting all users with certain themas
     * For every user a mail will be send (text or html) with links to the articles in the live cloud
     * After succesfull process all maillijst relations will be removed and status field of the article
     * will be set to 2.
     */
    public void run() {
       String status = PropertiesUtil.getProperty("emailservice.status");
       if ((status != null) && (status.equals("active"))) {
          Cloud cloud = CloudFactory.getCloud();
          paginaHelper = new PaginaHelper(cloud);
          NodeList emailTemplates = cloud.getNodeManager("emailtemplate").getList("[active]=1", null, null);
       NodeList themas = cloud.getNodeManager("thema").getList(null, "naam", null);
       TreeSet articlesSet = new TreeSet();

       // only 1 emailtempalte maybe active
       if ((emailTemplates.size() == 1) && (themas.size() > 0)) {
          Node emailTemplate = emailTemplates.getNode(0);
          Hashtable usersWithArticles = new Hashtable();
          HashMap users = new HashMap();
          long dateInMillis = (new Date()).getTime() / 1000; // divide by 1000 for mmbase;
          for (int i = 0; i < themas.size(); i++) {
             Node themaNode = themas.getNode(i);
             String thema = themaNode.getStringValue("naam");
               log.debug("Thema : " + thema);

             NodeList artikels = cloud.getList(""+themaNode.getNumber(), "thema,maillijst,artikel", "artikel.number", "artikel.embargo < " + dateInMillis, null, null, "SOURCE", true) ;

             if (artikels.size() > 0) {
                try {
                    Service service = new Service();
                     Call call = (Call) service.createCall();

                     call.setTargetEndpointAddress(new java.net.URL(webserviceURL));
                     call.setOperationName("getUsers");
                     call.addParameter("thema", Constants.SOAP_STRING, ParameterMode.IN);
                     call.setReturnType(Constants.SOAP_MAP);

                     HashMap ret = (HashMap) call.invoke(new Object[]{thema});

                     if ((ret != null) && (ret.size() > 0) && (!ret.containsKey(new Integer(0)))) {
                        users.putAll(ret);
                        Iterator usersIt = ret.keySet().iterator();

                        // the email from the webservice starts with text: or html: indicating the type of mail to send.
                        while (usersIt.hasNext()) {
                           String email = (String) usersIt.next();
                           String fullName = (String) ret.get(email);

                           List articleList = (ArrayList) usersWithArticles.get(email);
                           if (articleList == null) {
                              articleList = new ArrayList(artikels.size());
                           }

                           for (int j = 0; j < artikels.size(); j++) {
                              String articleNodeNumber = artikels.getNode(j).getStringValue("artikel.number");
                              articlesSet.add(articleNodeNumber);
                              if (!articleList.contains(articleNodeNumber)) {
                                 articleList.add(articleNodeNumber);
                              }
                           }
                           usersWithArticles.put(email, articleList);
                        }
                     }
                  }
                  catch (Exception e) {
                     log.error("Webservice exception getusers with thema " + thema + ": " + e);
                  }
             }
               else {
                  log.debug("No articles found to email");
               }
          }

          //sendEmails
          Iterator usersWithArticlesIt = usersWithArticles.keySet().iterator();
          try {
              while (usersWithArticlesIt.hasNext()) {
                 String email = (String) usersWithArticlesIt.next();
                 List articles = (List) usersWithArticles.get(email);

                 if (articles.size() > 0) {
                     NodeList articleNodeList = getArticleNodeList(articles, "titel");
                    String name = (String)users.get(email);

                     name = (name != null) ? name : "";

                     if (email.indexOf("html:") >= 0) {
                        String realEmail = email.substring(email.indexOf("html:") + "html:".length(), email.length());
                        sendHtmlEmail(realEmail, name, emailTemplate, articleNodeList);
                     }
                     else if (email.indexOf("text:") >= 0) {
                        String realEmail = email.substring(email.indexOf("text:") + "text:".length(), email.length());
                        sendTextEmail(realEmail, name, emailTemplate, articleNodeList);
                     }
                 }
               }

               // remove all maillijst relations with the articles that are send.
               log.debug("Removing relations maillijst (size = " + articlesSet.size() + ")");
               if (!PropertiesUtil.getProperty("emailservice.notdeletemail").equals("true")) {
                  Iterator articlesIt = articlesSet.iterator();
                  while (articlesIt.hasNext()) {
                     String articleNodeNumber = (String) articlesIt.next();
                     Node articleNode = cloud.getNode(articleNodeNumber);
                     articleNode.setIntValue("status", 2);
                     articleNode.deleteRelations("maillijst");
                     articleNode.commit();
                  }
               }
            }
            catch (Exception e) {
               log.debug(e);
               // the message is already logged in de sendEmail method
               // if there is an exception it will be an exception that the mailhost is not reachable
            }
       }
       else {
          log.warn("Er zijn geen themas in het systeem, of er is geen emailtemplate actief");
       }
       }

       // schedule the new email maillijst one week later
       GregorianCalendar cal = (GregorianCalendar) EmailServiceModule.getDatumTijd();
       if (cal != null) {
       cal.add(Calendar.DAY_OF_WEEK, 7);
       Date date = new Date(cal.getTimeInMillis());
       PropertiesUtil.setProperty("emailservice.datumtijd", simpleDateFormat.format(date));
      }
    }

    /**
     * This method will return a comma separated list of articles ordered by the given orderby param
     * @param articles the list of article node numbers
     * @orderby the column name to order by
     */
    private NodeList getArticleNodeList(List articles, String orderby) {
       StringBuffer sb = new StringBuffer();
       for (int i = 0; i < articles.size(); i++) {
          if (i > 0) {
             sb.append(",");
          }
          sb.append((String) articles.get(i));
       }

       Cloud cloud = CloudFactory.getCloud();
       return cloud.getNodeManager("artikel").getList("number in (" + sb.toString() + ")", orderby, null);
    }

    /**
     * This method will send an html email to the given user with the given emailtemplate layout and
     * the given article list
     * @param email the emailaddress to send this email to
     * @param name the name of the user belonging to the email
     * @param emailTemplate the layout of the email
     * @param articleNodeList articleNodeList the list of article node numbers
     */
    private void sendHtmlEmail(String email, String name, Node emailTemplate, NodeList articleNodeList) throws Exception {
       StringBuffer sb = new StringBuffer();

       for (int i = 0; i < articleNodeList.size(); i++) {
          Node article = articleNodeList.getNode(i);

          try {
          String url = paginaHelper.createUrlForContentElement(article, "/live");
          if (url != null) {
               sb.append("<p><b>Titel:</b> <a href=\"");
               sb.append(url);
               sb.append("\">");               
               sb.append(article.getStringValue("titel"));               
               sb.append("</a>");
               String intro = article.getStringValue("intro");
               if ((intro != null) && (!intro.trim().equals(""))) { 
                  sb.append("<br>");
                  sb.append(intro);
               }
               sb.append("<br><b>Thema(s):</b> ");
               sb.append(getThemaList(article));
               sb.append("<hr></p>");
            }
         }
         catch (MalformedURLException mue) {
            log.warn("cannot create url: " + mue);
         }
         catch (NullPointerException e) {
            // this can happen when an article is removed from it's page
            log.warn("cannot create url: " + e);
         }
       }
       StringBuffer sbTotal = new StringBuffer();
       sbTotal.append(emailTemplate.getStringValue("htmlprecode"));
       sbTotal.append(emailTemplate.getStringValue("htmlaanhef"));
       sbTotal.append(" ");
       if ((name != null) && (!name.equals(""))) {
          sbTotal.append(name);
       }
       else {
          sbTotal.append("lezer");
       }
       sbTotal.append(",");
       sbTotal.append(emailTemplate.getStringValue("htmlintro"));
       sbTotal.append(sb);
       sbTotal.append("<br><br>");
       sbTotal.append(emailTemplate.getStringValue("htmlhandtekening"));
       sbTotal.append(emailTemplate.getStringValue("htmlpostcode"));

        Cloud cloud = CloudFactory.getCloud();
        Node emailNode = cloud.getNodeManager("email").createNode();
        emailNode.setValue("to", email);
        emailNode.setValue("from", webmasterEmail);
        emailNode.setValue("subject", emailTemplate.getStringValue("onderwerp"));
        emailNode.setValue("replyto", webmasterEmail);
        emailNode.setValue("body","<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\"></multipart>"
                        + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                        + "<html>" + sbTotal.toString() + "</html>"
                        + "</multipart>");
        emailNode.commit();
        emailNode.getValue("mail(oneshot)");
    }

    /**
     * This method will send a text email to the given user with the given emailtemplate layout and
     * the given article list
     * @param email the emailaddress to send this email to
     * @param name the name of the user belonging to the email
     * @param emailTemplate the layout of the email
     * @param articleNodeList articleNodeList the list of article node numbers
     */
    private void sendTextEmail(String email, String name, Node emailTemplate, NodeList articleNodeList) throws Exception {
       StringBuffer sb = new StringBuffer();

       for (int i = 0; i < articleNodeList.size(); i++) {
          Node article = articleNodeList.getNode(i);
          try {
          String url = paginaHelper.createUrlForContentElement(article, "/live");
          if (url != null) {
               sb.append("\n\nTitel: ");
              sb.append(article.getStringValue("titel"));
              sb.append("\nLink: ");
               sb.append(url);
               String intro = article.getStringValue("intro");
               if ((intro != null) && (!intro.trim().equals(""))) { 
                  intro = intro.replaceAll("<br>", "\n");
                  intro = intro.replaceAll("<br/>", "\n");
                  intro = intro.replaceAll("<br />", "\n");
                  intro = intro.replaceAll("<b>", "");
                  intro = intro.replaceAll("</b>", "");
                  sb.append("\n");
                  sb.append(intro);
               }
               sb.append("\nThema(s): ");
               sb.append(getThemaList(article));
            }
         }
         catch (MalformedURLException mue) {
            log.warn("cannot create url: " + mue);
         }
         catch (NullPointerException e) {
            // this can happen when an article is removed from it's page
            log.warn("cannot create url: " + e);
         }
       }
       StringBuffer sbTotal = new StringBuffer();
       sbTotal.append(emailTemplate.getStringValue("plataanhef"));
       sbTotal.append(" ");
       if ((name != null) && (!name.equals(""))) {
          sbTotal.append(name);
       }
       else {
          sbTotal.append("lezer");
       }
       sbTotal.append(",");
       sbTotal.append("\n\n");
       sbTotal.append(emailTemplate.getStringValue("platintro"));
       sbTotal.append(sb);
       sbTotal.append("\n\n");
       sbTotal.append(emailTemplate.getStringValue("plathandtekening"));

       Cloud cloud = CloudFactory.getCloud();
       Node emailNode = cloud.getNodeManager("email").createNode();
       emailNode.setValue("to", email);
       emailNode.setValue("from", webmasterEmail);
       emailNode.setValue("subject", emailTemplate.getStringValue("onderwerp"));
       emailNode.setValue("replyto", webmasterEmail);
       emailNode.setValue("body",sbTotal.toString());
       emailNode.commit();
       emailNode.getValue("mail(oneshot)");
    }

    /**
     * This method will return a comma separated String of thema names
     * @param article the article for getting the related themas from
     */
    private String getThemaList(Node article) {
       StringBuffer sb = new StringBuffer();

       NodeList themas = article.getRelatedNodes("thema", "maillijst", "DESTINATION");
       if (themas.size() > 0) {
       for (int i = 0; i < themas.size(); i++) {
          if (i > 0) {
             sb.append(", ");
          }
          sb.append(themas.getNode(i).getStringValue("naam"));
       }
       }
       return sb.toString();
    }
}
