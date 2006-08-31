package org.mmbase.www;

import java.util.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.http.*;
import org.apache.commons.lang.util.Validate;
import org.apache.commons.validator.UrlValidator;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.RelationManagerList;


/**
 * @author mdemare
 * Date: 11-Dec-2003
 * Time: 11:51:49
 */
public class PartnerHandler {
   /**
    * Expects email attribute in params. Looks up mail in user builder,
    * if there's exactly result, mail username + password to mail.
    * @param cloud
    * @param request
    * @throws IllegalArgumentException with message if anything goes wrong.
    */
   public static void addProject(Cloud cloud, HttpServletRequest request) {
      String title = request.getParameter("title");
      String subtitle = request.getParameter("subtitle");
      String intro = request.getParameter("intro");
      String body = request.getParameter("body");
      String name = request.getParameter("name");
      String url = request.getParameter("url");
      String description = request.getParameter("description");
      Validate.notNull(url, "No url specified.");
      try {
         new URL(url);
      } catch (MalformedURLException ex) {
         Validate.isTrue(false, "'"+url+"' is not a valid url.");
      }
      Node homepage = getHomePage(cloud);
      Node news = cloud.getNodeManager("news").createNode();
      news.setStringValue("title", title);
      news.setStringValue("subtitle", subtitle);
      news.setStringValue("intro", intro);
      news.setStringValue("body", body);
      news.commit();
      Node urlnode = cloud.getNodeManager("urls").createNode();
      urlnode.setStringValue("name", name);
      urlnode.setStringValue("url", url);
      urlnode.setStringValue("description", description);
      urlnode.commit();
      homepage.createRelation(news, getRelationManager(cloud, "pages", "news")).commit();
      news.createRelation(urlnode, getRelationManager(cloud, "news", "urls")).commit();

   }

   private static RelationManager getRelationManager(Cloud cloud, String src, String target) {
      // I shouldn't have to do this...
      RelationManagerList relationManagers = cloud.getRelationManagers();
      for (Iterator iterator = relationManagers.iterator(); iterator.hasNext();) {
         RelationManager rm = (RelationManager) iterator.next();
         if (rm.getSourceManager().getName().equals(src)) {
            if (rm.getDestinationManager().getName().equals(target)) {
               return rm;
            }
         }
      }
      throw new IllegalArgumentException("No relationManager found."+src+":"+target);
   }

   private static Node getHomePage(Cloud cloud) {
      return cloud.getNodeByAlias("page_homepage");
   }
}
