package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.mail.MessagingException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.newsletter.publisher.MIMEType;
import com.finalist.newsletter.publisher.NewsletterGenerator;

public abstract class NewsletterPublicationUtil {

   private static void copyOtherRelations(Node newsletterNode, Node publicationNode, boolean copyContent) {
      copyPageRelations(newsletterNode, publicationNode, copyContent);
      copyImageAndAttachmentRelations(newsletterNode, publicationNode);
   }

   public static Node copyPageRelations(Node sourcePage, Node newPage, boolean copyContent) {
      CloneUtil.cloneRelations(sourcePage, newPage, PagesUtil.LAYOUTREL, PagesUtil.LAYOUT);
      if (copyContent) {
         PortletUtil.copyPortlets(sourcePage, newPage);
      }
      Node popupinfo = PagesUtil.getPopupinfo(sourcePage);
      if (popupinfo != null) {
         Node newPopupinfo = PagesUtil.copyPopupinfo(popupinfo);
         PagesUtil.addPopupinfo(newPage, newPopupinfo);
      }
      return newPage;
   }

   private static void copyImageAndAttachmentRelations(Node newsletterNode, Node publicationNode) {
      CloneUtil.cloneRelations(newsletterNode, publicationNode, "namedrel", "images");
      CloneUtil.cloneRelations(newsletterNode, publicationNode, "posrel", "attachments");
   }

   public static Node getNewsletterByPublicationNumber(int publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterPublicationNode = cloud.getNode(publicationNumber);
      List<Node> relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");
      return relatedNewsletters.get(0);
   }

   public static Node createPublication(int newsletterNumber, boolean copyContent) {
      if (newsletterNumber > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);
         Node publicationNode = CloneUtil.cloneNode(newsletterNode, "newsletterpublication");
         if (publicationNode != null) {
            String urlFragment = String.valueOf(publicationNode.getNumber());
            publicationNode.setStringValue("urlfragment", urlFragment);
            publicationNode.setStringValue("publishdate", "null");
            publicationNode.setStringValue("status", Publication.STATUS.INITIAL.toString());
            publicationNode.commit();

            //  copyContent(newsletterNode, publicationNode);
            copyOtherRelations(newsletterNode, publicationNode, copyContent);
            NavigationUtil.appendChild(newsletterNode, publicationNode);
            Node layoutNode = PagesUtil.getLayout(publicationNode);
            if (copyContent == true) {
               PagesUtil.linkPortlets(publicationNode, layoutNode);
            }
            return (publicationNode);
         }
      }
      return (null);
   }

   public static void createDefaultTerm(Node newsletterNode) {

      if (!hasDefaultTerm(newsletterNode)) {
         Node defaultTerm = newsletterNode.getCloud().getNodeManager("term").createNode();
         defaultTerm.setStringValue("name", "default");
         defaultTerm.setStringValue("subject", newsletterNode.getStringValue("title"));
         defaultTerm.commit();
         newsletterNode.createRelation(defaultTerm, newsletterNode.getCloud().getRelationManager("posrel")).commit();
      }
   }

   private static boolean hasDefaultTerm(Node newsletterNode) {
      NodeManager termNodeManager = newsletterNode.getCloud().getNodeManager("term");
      NodeList terms = newsletterNode.getRelatedNodes(termNodeManager);
      boolean hasDefaultTerm = false;
      for (int i = 0; i < terms.size(); i++) {
         Node term = terms.getNode(i);
         if (term.getStringValue("name") != null && term.getStringValue("name").equals("default")) {
            hasDefaultTerm = true;
            break;
         }
      }
      return hasDefaultTerm;
   }

   // Delete a publication, only if not yet published
   public static void deletePublication(int publicationNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node publicationNode = cloud.getNode(publicationNumber);

      NavigationUtil.deleteItem(publicationNode);
   }

   public static List<String> getAllTermsForPublication(int publicationNumber) {
      List<String> themes = new ArrayList<String>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(publicationNumber);
      NodeList themeList = newsletterNode.getRelatedNodes("newslettertheme");
      for (int i = 0; i < themeList.size(); i++) {
         Node themeNode = themeList.getNode(i);
         String theme = themeNode.getStringValue("number");
         themes.add(theme);
      }
      return (themes);
   }

   public static Publication getPublication(Cloud cloud,int number) {
      Node newsletterPublicationNode = cloud.getNode(number);

      List<Node> relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");
      Publication pub = new Publication();
      pub.setId(newsletterPublicationNode.getNumber());
      pub.setStatus(Publication.STATUS.valueOf(newsletterPublicationNode.getStringValue("status")));
      pub.setUrl(getPublicationURL(cloud,number));
      Newsletter newsletter = new Newsletter();

      Node node = relatedNewsletters.get(0);
      new POConvertUtils<Newsletter>().convert(newsletter, node);
      newsletter.setReplyAddress(node.getStringValue("replyto_mail"));
      newsletter.setReplyName(node.getStringValue("replyto_name"));
      newsletter.setFromAddress(node.getStringValue("from_mail"));
      newsletter.setFromName(node.getStringValue("from_name"));
      pub.setNewsletter(newsletter);

      return pub;
   }
   
   public static String getPublicationURL(Cloud cloud, int publicationId) {
      Node publicationNode = cloud.getNode(publicationId);
      String hostUrl = NewsletterUtil.getServerURL();
      String newsletterPath = getNewsletterPath(publicationNode);
      return "".concat(hostUrl).concat(newsletterPath);
   }
   
   public static String getNewsletterPath(Node newsletterPublicationNode) {
      return NavigationUtil.getPathToRootString(newsletterPublicationNode, true);
   }
   
   public static STATUS getStatus(Cloud cloud, int publicationId) {
      return getPublication(cloud,publicationId).getStatus();
   }
   
   public static void publish(Node node) {
      if(ServerUtil.isStaging() && !ServerUtil.isSingle()) {
         Publish.publish(node);
      }
   }
   public static void publish(Cloud cloud, int number) {
      Node node = cloud.getNode(number);
      publish(node);
   }
   
   /**
    * Freeze a edition
    * @throws MessagingException 
    */
   public static void freezeEdition(Node edition) throws MessagingException {
      //publish(edition);
      String static_html = getStaticHtml(edition.getNumber());
      edition.setStringValue("process_status", EditionStatus.FROZEN.value());
//      edition.setValue("static_html", StringEscapeUtils.escapeHtml(static_html));
      edition.setValueWithoutProcess("static_html", static_html);
      edition.setStringValue("approved_by", null);
      edition.commit();
   }
   
   /**
    * Defrost a edition
    */
   public static void defrostEdition(Node edition) {
      edition.setStringValue("process_status", EditionStatus.INITIAL.value());
      edition.setStringValue("static_html", null);
      edition.commit();
   }
   
   /**
    * Approve a edition
    */
   public static void approveEdition(Node edition) {
      edition.setStringValue("process_status", EditionStatus.APPROVED.value());
      String user=edition.getCloud().getUser().getIdentifier();
      edition.setStringValue("approved_by", user);
      edition.commit();
   }
   
   /**
    * Revoke approval of a edition
    */
   public static void revokeEdition(Node edition) {
      edition.setStringValue("process_status", EditionStatus.FROZEN.value());
      edition.commit();
   }
   
   /**
    * change the status of the edition to be beingsend
    */
   public static void setBeingSend(Node edition) {
      edition.setStringValue("process_status", EditionStatus.BEING_SENT.value());
      edition.commit();
   }
   /**
    * change the status of the edition to be beingsend
    */
   public static void setBeingSend(int number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node edition = cloud.getNode(number);    
      setBeingSend(edition);
   }
   /**
    * change the status  of a edition to be issent
    */
   public static void setIsSent(Node edition) {
      edition.setStringValue("process_status", EditionStatus.IS_SENT.value());
      edition.commit();
   }
   
   /**
    * get the process status  of a edition
    */
   public static String getEditionStatus(Node edition) {
      return edition.getStringValue("process_status");
   }
   
   /**
    * get the process status of an edition
    */
   public static String getEditionStatus(int number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node edition = cloud.getNode(number);      
      return edition.getStringValue("process_status");
   }

   public static String getStaticHtml(int publicationId) throws MessagingException {

      NewsletterPublicationCAOImpl publicationCAO = new NewsletterPublicationCAOImpl();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      publicationCAO.setCloud(cloud);
      Publication publication = publicationCAO.getPublication(publicationId);
      Subscription subscription = new Subscription();
      subscription.setTerms(new HashSet<Term>());
      subscription.setMimeType(MIMEType.HTML.type());
      return getBody(publication, subscription);
   }
   
   private static String getBody(Publication publication, Subscription subscription)
            throws MessagingException {     
      String url = NewsletterUtil.getTermURL(publication.getUrl(), subscription
               .getTerms(), publication.getId());
      String content = " ";
      if ((subscription.getTerms() == null) || (subscription.getTerms().size() == 0)) {
         content = NewsletterGenerator.generate(url, subscription.getMimeType());
      } 
      return content + "\n";
   }

}