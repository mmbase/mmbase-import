/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.maintenance.richtext;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.datatypes.DataType;

import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.richtext.RichText;
import com.finalist.cmsc.services.publish.Publish;


public class RepairStagingUrls {
   
   private Cloud cloud;
   private PageContext ctx;
   
   public RepairStagingUrls(Cloud cloud, PageContext pageContext) {
      this.cloud = cloud;
      this.ctx =  pageContext;
   }
   
   public void execute(boolean repair) throws IOException {
      NodeManager urlManager = cloud.getNodeManager(ResourcesUtil.URLS);
      NodeQuery urlQuery = urlManager.createQuery();
      SearchUtil.addLikeConstraint(urlQuery, urlManager.getField("url"), "%staging%");
      
      NodeList urlNodes = urlManager.getList(urlQuery);
      ctx.getOut().write(urlNodes.size() + " Urls found to repair <br>");
      
      for (Iterator<Node> iterator = urlNodes.iterator(); iterator.hasNext();) {
         Node urlNode = iterator.next();
         repairUrlNode(urlNode, repair);
      }
      
      List<NodeManager> contentTypes = ContentElementUtil.getContentTypes(cloud);
      for (NodeManager contentManager : contentTypes) {
         NodeQuery contentQuery = contentManager.createQuery();
         List<Field> fields = contentManager.getFields();
         for (Field field : fields) {
            if (isRichtextField(field)) {
               SearchUtil.addLikeConstraint(contentQuery, field, "%staging%");
            }
         }
         NodeList contentNodes = contentManager.getList(contentQuery);
         for (Iterator<Node> iterator = contentNodes.iterator(); iterator.hasNext();) {
            Node contentNode = iterator.next();
            repairContentNode(contentNode, repair);
         }
      }
   }

   private void repairUrlNode(Node urlNode, boolean repair) throws IOException {
      int number = urlNode.getNumber();
      String url = urlNode.getStringValue("url");

      NodeList relatedContent = urlNode.getRelatedNodes(ContentElementUtil.CONTENTELEMENT, RichText.INLINEREL_NM, "SOURCE");
      if (relatedContent.isEmpty()) {
         if (repair) {
            if (Publish.isPublished(urlNode)) {
               Publish.unpublish(urlNode);
            }
            urlNode.delete(true);
         }
         ctx.getOut().write("Staging url deleted: " + number + " " + url + "<br>");
      }
      else {
         String inlineNumber = extractInlineNumber(url);
         if (inlineNumber == null) {
            ctx.getOut().write("Staging url found without inlineNumber: " + number + " " + url + "<br>");
         }
         else {
            for (Iterator<Node> iterator = relatedContent.iterator(); iterator.hasNext();) {
               Node contentElement = iterator.next();
               List<Field> fields = contentElement.getNodeManager().getFields();
               for (Field field : fields) {
                  if (isRichtextField(field)) {
                     String richValue = contentElement.getStringValue(field.getName());
                     String newRichValue = richValue.replaceAll("destination=\"("+number+")\"", "destination=\""+inlineNumber+"\"");
                     if (!richValue.equals(newRichValue)) {
                        contentElement.setStringValue(field.getName(), newRichValue);
                     }
                  }
               }
               if (contentElement.isChanged()) {
                  if (repair) {
                     contentElement.commit();
                     if (Publish.isPublished(contentElement)) {
                        Publish.publish(contentElement);
                     }
                  }
                  ctx.getOut().write(
                        "Staging url " + url + " repaired in " + contentElement.getNumber() + " "
                              + contentElement.getStringValue("title") + "<br>");
               }
            }
            if (repair) {
               if (Publish.isPublished(urlNode)) {
                  Publish.unpublish(urlNode);
               }
               urlNode.delete(true);
            }
         }
      }
   }
   
   private boolean isRichtextField(Field field) {
      DataType dataType = field.getDataType();
      while(StringUtils.isEmpty(dataType.getName())) {
          dataType = dataType.getOrigin();
      }
      return RichText.RICHTEXT_TYPE.equals(dataType.getName());
   }

   private static String extractInlineNumber(String url) {
      String inline = url.replaceAll(".*/(\\d+)($|/.*)", "$1");
      if (!inline.equals(url)) {
         return inline;
      }
      return null;
   }
   

   private void repairContentNode(Node contentNode, boolean repair) throws IOException {
      ctx.getOut().write(
            "Staging url repaired in " + contentNode.getNumber() + " "
                  + contentNode.getStringValue("title") + "<br>");
      
   }
}
